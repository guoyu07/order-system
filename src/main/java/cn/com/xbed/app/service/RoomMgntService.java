package cn.com.xbed.app.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbAddition;
import cn.com.xbed.app.bean.XbCalendarDaily;
import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbFacility;
import cn.com.xbed.app.bean.XbPicture;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.XbRoomCleanServ;
import cn.com.xbed.app.bean.XbRoomFacilityRel;
import cn.com.xbed.app.bean.XbRoomState;
import cn.com.xbed.app.bean.vo.CalEditVo;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.bean.vo.XbFacilityFacadeVo;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.LocationUtil;
import cn.com.xbed.app.commons.util.SqlCreatorTool;
import cn.com.xbed.app.commons.util.UploadHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;

@Service
@Transactional
public class RoomMgntService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private ImageCommon imageCommon;
	@Resource
	private RoomStateTool roomStateTool;
	
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomMgntService.class));

	public int queryCurentRoomStat(int roomId) {
		try {
			return roomStateTool.queryCurentRoomStat(roomId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * OMS 查询房间列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> queryRoomList(Integer flag, Integer chainId, Integer roomId,boolean qryPreCheckout) {
		try {
			// 不用flag=正常条件,查出所有
			String sql = "SELECT r.room_type_name roomTypeName,r.chain_id chainId, r.flag AS flag,r.locate as locate,r.room_id as roomId,r.room_name as roomName,r.addr as addr,r.province as province,r.city as city,r.district as district,c.name as chainName FROM xb_room r,xb_chain c WHERE r.chain_id=c.chain_id";
			List<Object> params = new ArrayList<>();
			if (flag != null) {
				sql += " AND r.flag=?";
				params.add(flag);
			}
			if (chainId != null) {
				sql += " AND c.chain_id=?";
				params.add(chainId);
			}
			if (roomId != null) {
				sql += " AND r.room_id=?";
				params.add(roomId);
			}
			List<Map<String, Object>> mapList = daoUtil.roomMgntDao.queryMapList(sql, params.toArray());
			if (qryPreCheckout) {
				for (Map<String, Object> map : mapList) {// 可以考虑放到redis
					int curRoomId = (int) map.get("roomId");
					String preCheckoutSql = "SELECT b.checkin_id FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id WHERE a.stat=1 AND b.stat=1 AND b.chg_room_flag!=1 AND DATE_FORMAT(b.checkout_time,'%Y-%m-%d')=? AND b.room_id=?";
					String stopSql = "SELECT stop_id FROM xb_order_stop WHERE stop_stat=1 AND DATE_FORMAT(stop_end,'%Y-%m-%d')=? AND room_id=?";// 停用单   停用单状态 0新建 1开始 2结束 3停用取消(已开始) 4-未开始取消
					String union = preCheckoutSql + " UNION ALL " + stopSql;
					String curDate = DateUtil.getCurDateStr();
					// union提高了效率
					List<Map<String, Object>> orderList = daoUtil.orderMgntDao.queryMapList(union, new Object[] { curDate, curRoomId, curDate, curRoomId });
					if (!orderList.isEmpty()) {
						map.put("isPreCheckout", 1);// 有
					} else {
						map.put("isPreCheckout", 0);// 无
					}
				}
			}
			return mapList;
			//return daoUtil.roomMgntDao.findAllRoomsByFlag(AppConstants.Room_flag.NORMAL_1);// 原来只查出
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	public List<Map<String, Object>> queryRoomListByLocation(String location, double radius) {
		try {
			String fields = SqlCreatorTool.getSelectFields(XbRoom.class, "a");
			String sql = "SELECT "+fields+",b.price AS curDatePrice FROM xb_room a LEFT JOIN xb_calendar_daily b ON a.room_id=b.room_id WHERE b.cur_date=? AND a.flag=?";
			List<Map<String, Object>> list = daoUtil.roomMgntDao.queryMapList(sql, new Object[] { DateUtil.getCurDateStr(), AppConstants.Room_flag.NORMAL_1 });
			List<Map<String, Object>> resultList = new ArrayList<>(list.size());
			for (Map<String, Object> map : list) {
				String roomLocate = (String) map.get("locate");
				if (roomLocate != null && roomLocate.contains(",")) {
					double twoPointDistanct = LocationUtil.getDistance(roomLocate, location);
					if (twoPointDistanct < radius) {
						resultList.add(map);
					}
				}
			}
			// 查询当日价格
			for (Map<String, Object> map : resultList) {
				int roomId = (int) map.get("roomId");
				List<XbPicture> picList = daoUtil.pictureDao.findRoomPicByRoomIdCoverPicFirst(roomId);
				map.put("coverPic", picList.isEmpty() ? "" : picList.get(0).getFilePath());
			}
			return resultList;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 删除某个房间
	 * 
	 * @param roomId
	 * @return
	 */
	public void delRooms(int roomId) {
		try {
			XbRoom room = new XbRoom();
			room.setRoomId(roomId);
			room.setFlag(AppConstants.Room_flag.DELETED_2);
			int affect = daoUtil.roomMgntDao.updateRoomByPk(room);
			if (affect == 0) {
				throw exceptionHandler.newErrorCodeException("-1", "无记录可以更新 roomId[" + roomId + "]");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 删除某个房间
	 * 
	 * @param roomId
	 * @return
	 */
	public void resetRoom(int roomId) {
		try {
			XbRoom room = new XbRoom();
			room.setRoomId(roomId);
			room.setFlag(AppConstants.Room_flag.NORMAL_1);
			int affect =  daoUtil.roomMgntDao.updateRoomByPk(room);
			if (affect == 0) {
				throw exceptionHandler.newErrorCodeException("-1", "无记录可以更新 roomId[" + roomId + "]");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 查询出房间的信息
	 * 
	 * @param roomId
	 * @return
	 */
	public Map<String, Object> queryRoomInfo(int roomId) {
		Map<String, Object> subMap = new HashMap<>();
		try {
			// 房间信息
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
			AssertHelper.notNull(roomInfo);

			// 区域中心信息
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			AssertHelper.notNull(chainInfo);

			// 设施信息,列出所有,并且标识哪些是房间具有的
			List<XbFacilityFacadeVo> baseFacade = new ArrayList<>();
			List<XbFacility> allBase = daoUtil.facilityDao
					.findValidByFacilityType(AppConstants.Facility_facilityType.BASE_FACILITY_1);// 提供的所有基础设施
			if (allBase != null && allBase.size() > 0) {
				List<XbRoomFacilityRel> roomBase = daoUtil.roomFacilityRelDao.findValidByRoomId(roomId,
						AppConstants.Facility_facilityType.BASE_FACILITY_1);// 该房间的基础设施
				int roomBaseSize = roomBase.size();
				Set<Integer> baseFaciSet = new HashSet<>(roomBaseSize);
				for (int i = 0; i < roomBaseSize; i++) {
					baseFaciSet.add(roomBase.get(i).getFacilityId());
				}

				int allBaseFaciSize = allBase.size();
				for (int i = 0; i < allBaseFaciSize; i++) {
					XbFacilityFacadeVo vo = new XbFacilityFacadeVo();
					vo.setXbFacility(allBase.get(i));
					if (baseFaciSet.contains(allBase.get(i).getFacilityId())) {
						vo.setChecked(true);
					}
					baseFacade.add(vo);
				}
			}
			List<XbFacilityFacadeVo> servFacade = new ArrayList<>();
			List<XbFacility> allServ = daoUtil.facilityDao
					.findValidByFacilityType(AppConstants.Facility_facilityType.PROV_SERVICE_2);// 提供的所有服务
			if (allServ != null && allServ.size() > 0) {
				List<XbRoomFacilityRel> roomServ = daoUtil.roomFacilityRelDao.findValidByRoomId(roomId,
						AppConstants.Facility_facilityType.PROV_SERVICE_2);
				int roomServSize = roomServ.size();
				Set<Integer> provServSet = new HashSet<>(roomServSize);
				for (int i = 0; i < roomServSize; i++) {
					provServSet.add(roomServ.get(i).getFacilityId());
				}

				int allProvServSize = allServ.size();
				for (int i = 0; i < allProvServSize; i++) {
					XbFacilityFacadeVo vo = new XbFacilityFacadeVo();
					vo.setXbFacility(allServ.get(i));
					if (provServSet.contains(allServ.get(i).getFacilityId())) {
						vo.setChecked(true);
					}
					servFacade.add(vo);
				}
			}

			// 额外信息,一般是单个
			List<XbAddition> additionInfos = daoUtil.additionDao.findByRoomId(roomId);

			// 查询所有的区域管理中心
			List<XbChain> allChainInfo = daoUtil.chainMgntDao.findAll();

			// 返回值
			subMap.put("roomInfo", roomInfo);
			subMap.put("chainInfo", chainInfo);
			subMap.put("baseFacade", baseFacade);
			subMap.put("servFacade", servFacade);
			subMap.put("additionInfos", additionInfos);
			subMap.put("allChainInfo", allChainInfo);

			return subMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}



	// 改成了新的版本的从xb_calendar_daily查
	public Map<String, Object> queryCalendarV2(int roomId, int howManyMon, Boolean isIgnorePast) {
		Map<String, Object> subMap = new HashMap<>();

		Collection<Collection<CalendarUnit>> allLockedCalList = new ArrayList<>();// 存放锁定的房间,是一个数组,数组内是小数组,小数组内是键值对MAP
		Collection<Collection<CalendarUnit>> allSpecialPriceList = new ArrayList<>();// 存放特殊价格的房间
		try {
			boolean flag = true;// 是否忽略当前月份今天之前的日期
			if (isIgnorePast != null) {
				flag = isIgnorePast.booleanValue();
			}
			// 查询这个月份及其之后的两个月的记录
			Date now = DateUtil.getCurDateTime();
			Date yestoday = DateUtil.addDays(now, -1);
			// 房间的默认价格
			int defaultPrice = daoUtil.roomMgntDao.findRoomDefaultPriceByPk(roomId);
			for (int i = 0; i < howManyMon; i++) {
				Map<String, CalendarUnit> curMonthLockedCalMap = new LinkedHashMap<>();// 存放锁定状态的(禁售或预占)
				Map<String, CalendarUnit> curMonthSpecialPriceMap = new LinkedHashMap<>();// 存放价格非默认的
				
				Date tmpDate = DateUtil.addMonths(now, i);
				String yearMonStr_ = DateUtil.getYearMonStr_(tmpDate);// yyyy-MM
				List<XbCalendarDaily> calendarDailyList = daoUtil.calendarDailyDao.findByRoomIdAndMonths(roomId, yearMonStr_);// 某个月的所有日历
				for (XbCalendarDaily t : calendarDailyList) {
					int stat = t.getStat();
					int price = t.getPrice();
					String curDate = t.getCurDate();// yyyy-MM-dd
					CalendarUnit unit = new CalendarUnit(stat, price, curDate);
					if (stat == AppConstants.Calendar_stat.FIX_3 || stat == AppConstants.Calendar_stat.SALE_2 || stat == AppConstants.Calendar_stat.STOP_4) {
						if (!flag) {
							curMonthLockedCalMap.put(curDate, unit);
						} else {
							Date d = DateUtil.parseDate(curDate);
							if (DateUtil.compareDate(d, yestoday) >= 0) {// 昨天之后的才加入(包含昨天)
								curMonthLockedCalMap.put(curDate, unit);
							}
						}
					}
					if (t.getPrice() != defaultPrice) {
						if (!flag) {
							curMonthSpecialPriceMap.put(curDate, unit);
						} else {
							Date d = DateUtil.parseDate(curDate);
							if (DateUtil.compareDate(d, yestoday) >= 0) {// 昨天之后的才加入(包含昨天)
								curMonthSpecialPriceMap.put(curDate, unit);
							}
						}
					}
					
				}
				Collection<CalendarUnit> curMonthLockedCal = curMonthLockedCalMap.values();// 某月份的所有锁定的日历
				Collection<CalendarUnit> curMonthSpecialPrice = curMonthSpecialPriceMap.values();// 某月份的所有特殊价格的日历
				allLockedCalList.add(curMonthLockedCal);
				allSpecialPriceList.add(curMonthSpecialPrice);
			}

			subMap.put("lockedCal", allLockedCalList);
			subMap.put("specialPriceCal", allSpecialPriceList);
			subMap.put("defaultPrice", defaultPrice);
			subMap.put("dateBegin", DateUtil.dateToYrMonDay_(now));
			subMap.put("thisDate", DateUtil.getYearMonDayHrMinSecStr_(new Date()));
			subMap.put("dateEnd", DateUtil.dateToYrMonDay_(DateUtil.addMonths(now, howManyMon - 1)));
			return subMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	

	public void editCalendarStat(List<CalEditVo> listEdit) {
		try {
			for (CalEditVo calEditVo : listEdit) {
				int newStat = calEditVo.getStatOper();
				int roomId = calEditVo.getRoomId();
				String curDate = calEditVo.getDate();
				//int operateCode = AppConstants.RoomStateOperCode.
				//roomStateTool.editCalendar(checkinTime, checkoutTime, operateCode, roomId);
			}
			if (true) {
				throw new RuntimeException("不允许修改日历");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public void editCalendarPrice(List<CalEditVo> listEdit) {
		try {
			for (CalEditVo calEditVo : listEdit) {
				int newPrice = calEditVo.getNewPrice();
				int roomId = calEditVo.getRoomId();
				String curDate = calEditVo.getDate();
				roomStateTool.editRoomPrice(roomId, curDate, 1, newPrice);
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 查询所有的区域信息
	public List<XbChain> queryChainInfo() {
		try {
			return daoUtil.chainMgntDao.findAll();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 上传一个不存在的路径会?【抛异常】java.io.FileNotFoundException: K:\t.jpg (系统找不到指定的路径。)
	 * <br>
	 * 上传的文件与目标路径上存在相同文件名会?【替换】<br>
	 * 
	 * @param roomId
	 * @param tag
	 * @param isCover
	 * @param file
	 * @param req
	 * @return -1-表示不支持上传该图片格式 1-成功
	 */
	public XbPicture uploadFile(int roomId, String tag, boolean isCover, MultipartFile file, Integer picId,Integer picType,
			HttpServletRequest req) {
		Map<String, Object> subMap = new HashMap<>();
		try {
			// 检查扩展名
			String fileName = file.getOriginalFilename();// 如: abc.jpg
			String extension = UploadHelper.getFileExtLowercaseDot(fileName);
			if (!(extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png"))) {
				throw exceptionHandler.newErrorCodeException("-1", "不支持上传这种格式");
			}
			

			// 新文件名，  roomId_20150826_160001_332_ori.jpg
			Date now = DateUtil.getCurDateTime();
			String newFileName = imageCommon.getRoomPicName(roomId, now, extension);
			
			
			// 获得部署的web应用中用于存放上传文件的路径
			String oriPicPath = imageCommon.getRoomPicOriPath();
			File newPicture = new File(oriPicPath + "/" + newFileName);

			// 传送文件
			file.transferTo(newPicture);

			// 生成各种缩略图
			String imageRootPath = imageCommon.getRoomPicRootPath();
			int result = imageCommon.generateThumb(imageRootPath, newFileName);
			if (result == -1) {
				throw new RuntimeException("生成缩略图出错");
			}

			// 插入到pic表
			XbPicture pic = new XbPicture();
			pic.setCover(isCover ? AppConstants.Picture_cover.IS_COVER_1 : AppConstants.Picture_cover.NOT_COVER_0);
			pic.setRoomId(roomId);
			pic.setCreateDtm(now);
			pic.setTag(tag);
			pic.setFilePath(newFileName);// 存放原始图的文件名
			pic.setPicType(picType == null || picType == AppConstants.Picture_picType.ROOM_PIC_1
					? AppConstants.Picture_picType.ROOM_PIC_1 : AppConstants.Picture_picType.NAVI_MAP_PIC_2);
			int newGenPicId = ((Long) daoUtil.pictureDao.addAndGetPk(pic)).intValue();
			pic.setPicId(newGenPicId);
			
			return pic;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public XbPicture replaceFile(int roomId, String tag, boolean isCover, MultipartFile file, Integer picId, Integer picType,
			HttpServletRequest req) {
		try {
			XbPicture pic = uploadFile(roomId, tag, isCover, file, picId, picType, req);
			int affect = delPic(picId);// 删除图片记录
			if (affect <= 0) {// 抛异常回滚掉
				throw new RuntimeException("图片picId错误:" + picId);
			}
			return pic;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public XbPicture updatePicTag(int picId, String tag) {
		try {
			XbPicture pictureInfo = daoUtil.pictureDao.findByPk(picId);
			if (pictureInfo == null) {
				throw exceptionHandler.newErrorCodeException("-2", "找不到对应的图片对象,picId:" + picId);
			}
			XbPicture pic = new XbPicture();
			pic.setPicId(picId);
			pic.setTag(tag);
			daoUtil.pictureDao.updateRoomByPk(pic);
			
			pictureInfo.setTag(tag);// 要求返回新对象
			return pictureInfo;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 删除上传的照片
	 * 
	 * @param picId
	 * @return
	 */
	public int delPic(int picId) {
		try {
			// 删除关联关系
			int ret = daoUtil.pictureDao.deleteByPk(picId);
			if (ret > 0) {
				return 1;
			} else {
				return -1;
			}

			// 能不能远程删除上传的文件???

		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	

	

	

	public List<XbFacility> queryFacility(int facilityType) {
		try {
			return daoUtil.facilityDao.findValidByFacilityType(facilityType);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public Map<String, Object> addRoomInfo(String jsonStr) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			Date now = DateUtil.getCurDateTime();
			// {operType:1-新增,2-修改,roomId:23234,roomInfo:{roomName:"房间名",chainId:123,roomTypeName:"类型名称",roomTypeId:23,title:"房间个性标题",descri:"房间亮点描述",province:"省份中文",city:"城市",district:"区",
			// addr:"详细地址",houseType:"x室x厅x厨x卫x阳台",area:"面积",lodgerCount:"可住人数",bedCount:"床位数",tag:"房间特色标签",price:28800},faciInfo:[{operType:1,facilityId:123},{operType:3,facilityId:123},{operType:1,facilityId:123}],additionInfo:{traffic:"交通路线",guide:"入住指南"}}
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			// 校验该传的参数
			String roomInfoStr = entity.getString("roomInfo");
			String faciInfoStr = entity.getString("faciInfo");
			String additionInfoStr = entity.getString("additionInfo");
			XbRoom roomInfo = JsonHelper.parseJSONStr2T(roomInfoStr, XbRoom.class);
			XbAddition additionInfo = JsonHelper.parseJSONStr2T(additionInfoStr, XbAddition.class);
			
			// 补上xb_room冗余信息的字段
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			roomInfo.setChainName(chainInfo.getName());
			roomInfo.setChainAddr(chainInfo.getAddress());
			roomInfo.setCreateDtm(now);
			
			// 新增房间信息
			int roomId = ((Long) daoUtil.roomMgntDao.addAndGetPk(roomInfo)).intValue();
			roomInfo.setRoomId(roomId);
			
			// 新增xb_addtion信息
			additionInfo.setRoomId(roomId);
			daoUtil.additionDao.add(additionInfo);

			// 新增xb_room_facility_rel
			JSONArray faciOperArr = JsonHelper.parseJSONStr2JSONArray(faciInfoStr);
			int sz = faciOperArr.size();
			for (int i = 0; i < sz; i++) {
				JSONObject obj = faciOperArr.getJSONObject(i);
				XbRoomFacilityRel rel = new XbRoomFacilityRel(roomId, obj.getIntValue("facilityId"));
				daoUtil.roomFacilityRelDao.add(rel);
			}

			// 新增房间日历
			calendarCommon.generateCalendarV2(roomId, roomInfo.getPrice(), null);
			
			// 新增xb_room_state
			XbRoomState roomState = new XbRoomState();
			roomState.setRoomId(roomId);
			roomState.setStat(AppConstants.RoomState_stat.CLEAN_1);
			daoUtil.roomStateDao.addAndGetPk(roomState);
			
			// 第一阶段忘记设计清洁价格，临时补上默认
			addDefaultCleanPrice(roomId);
			
			
			
			result.put("roomInfo", roomInfo);
			result.put("result", true);
			return result;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("新增房间出错 jsonStr: " + jsonStr, e);
		}
	}

	// 默认价格
	public boolean addDefaultCleanPrice(int roomId) {
		try {
//			xb_room_clean_serv
			XbRoomCleanServ cleanServ = new XbRoomCleanServ();
			cleanServ.setCleanStepId(-1);
			cleanServ.setRoomId(roomId);
			cleanServ.setCleanPriceId(1);
			cleanServ.setGoodsSetId(1);
			daoUtil.roomCleanServDao.add(cleanServ);
			return true;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public boolean editRoomInfo(String jsonStr) {
		try {
			// {operType:1-新增,2-修改,roomId:23234,roomInfo:{roomName:"房间名",chainId:123,roomTypeName:"类型名称",roomTypeId:23,title:"房间个性标题",descri:"房间亮点描述",province:"省份中文",city:"城市",district:"区",
			// addr:"详细地址",houseType:"x室x厅x厨x卫x阳台",area:"面积",lodgerCount:"可住人数",bedCount:"床位数",tag:"房间特色标签",price:28800},faciInfo:[{operType:1,facilityId:123},{operType:3,facilityId:123},{operType:1,facilityId:123}],additionInfo:{addId:修改的时候才需要传,traffic:"交通路线",guide:"入住指南"}}
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");

			// 校验该传的参数

			// 修改房间信息
			String roomInfoStr = entity.getString("roomInfo");
			if (roomInfoStr != null && roomInfoStr.length() > 0) {
				XbRoom roomInfo = JsonHelper.parseJSONStr2T(roomInfoStr, XbRoom.class);
				roomInfo.setRoomId(roomId);
				daoUtil.roomMgntDao.updateEntityByPk(roomInfo, "roomId");
			}

			// 新增xb_addtion信息
			String additionInfoStr = entity.getString("additionInfo");
			if (additionInfoStr != null && additionInfoStr.length() > 0) {
				XbAddition additionInfo = JsonHelper.parseJSONStr2T(additionInfoStr, XbAddition.class);
				AssertHelper.notNull(additionInfo.getAddId(), "必须传入修改的addId");
				daoUtil.additionDao.updateEntityByPk(additionInfo, "addId");
			}
			
			
			// 新增xb_room_facility_rel
			String faciInfoStr = entity.getString("faciInfo");
			if (faciInfoStr != null && faciInfoStr.length() > 0) {
				JSONArray faciOperArr = JsonHelper.parseJSONStr2JSONArray(faciInfoStr);
				int sz = faciOperArr.size();
				for (int i = 0; i < sz; i++) {
					JSONObject obj = faciOperArr.getJSONObject(i);
					int facilityId = obj.getIntValue("facilityId");
					int operType = obj.getIntValue("operType");
					if (operType == 1) {// 新增
						XbRoomFacilityRel rel = new XbRoomFacilityRel(roomId, facilityId);
						daoUtil.roomFacilityRelDao.add(rel);
					} else if (operType == 3) {// 删除
						daoUtil.roomFacilityRelDao.delByRoomIdAndFaciId(roomId, facilityId);
					}
				}
			}
			
			return true;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 查房间图
	public List<XbPicture> queryRoomPic(int roomId) {
		try {
			return daoUtil.pictureDao.findRoomPicByRoomIdCoverPicFirst(roomId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 查导航图片
	public List<XbPicture> queryNaviPic(int roomId) {
		try {
			return daoUtil.pictureDao.findNaviPicByRoomId(roomId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
