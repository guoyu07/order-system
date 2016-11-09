package cn.com.xbed.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.OperOverstay;
import cn.com.xbed.app.bean.XbAddition;
import cn.com.xbed.app.bean.XbCalendarDaily;
import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbFacility;
import cn.com.xbed.app.bean.XbInvoiceRec;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderOper;
import cn.com.xbed.app.bean.XbPicture;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.bean.vo.CheckinInputVo;
import cn.com.xbed.app.commons.enu.IdCardType;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.pager.ListPage;
import cn.com.xbed.app.commons.pager.PageHelper;
import cn.com.xbed.app.commons.pager.Pager;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.util.SqlCreatorTool;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.ljh.CleanSystemService;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.CancelType;
import cn.com.xbed.app.service.ordermodule.impl.CheckoutType;
import cn.com.xbed.app.service.ordermodule.impl.FromType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.roomstatemodule.CouponCardTool;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;
import cn.com.xbed.app.service.smsmodule.CommonSms;

@Service
@Transactional
public class OrderMgntService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private ImageCommon imageCommon;
	@Resource
	private CommonService commonService;
	@Resource
	private LodgerService lodgerService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private OmsUserService omsUserService;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private RoomStateTool roomStateTool;
	@Resource
	private CouponCardTool couponCardTool;
	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private CommonSms commonSms;
	@Resource
	private EventService eventService;
	@Resource
	private CommonDbLogService commonDbLogService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderMgntService.class));

	
	
	//续住
	public Map<String, Object> overstay(int oriCheckinId, int lodgerId, String overstayEndDay, int source, int payType, int actualPriceFromWebCentUnit,
			Integer discountPriceFromWebCentUnit, String cardCode, XbInvoiceRec invoice) {
		String inputParams = String.format(
				"checkinId:%s,lodgerId:%s,overstayEndDay:%s,source:%s,cardCode:%s,payType:%s,actualPrice:%s,discountPrice:%s,invoice:%s",
				oriCheckinId, lodgerId, overstayEndDay, source, cardCode, payType, actualPriceFromWebCentUnit, discountPriceFromWebCentUnit,
				invoice);
		exceptionHandler.getLog().info("续住接口,传参" + inputParams);//续住接口,传参checkinId:
		try {
			// 【1】业务可行性判断
			if (!(source == AppConstants.Order_source.WECHAT_0 || source == AppConstants.Order_source.IOS_2
					|| source == AppConstants.Order_source.ANDROID_3 || source == AppConstants.Order_source.OMS_4)) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误,source:" + source);
			}
			if (!(payType == AppConstants.Order_payType.WECHAT_1 || payType == AppConstants.Order_payType.UNKNOW_2)) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误,payType:" + payType);
			}
			if (StringUtils.isNotBlank(cardCode) && discountPriceFromWebCentUnit == null) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误,传了卡券但没传优惠额度参数");
			}
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			if (lodgerInfo == null) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误,找不到用户信息,lodgerId:" + lodgerId);
			}
			
			
			XbCheckin oriCheckinInfo = daoUtil.checkinDao.findByPk(oriCheckinId);
			exceptionHandler.getLog().info(oriCheckinInfo);
			if (oriCheckinInfo.getStat() != AppConstants.Checkin_stat.CHECKIN_1) {
				throw exceptionHandler.newErrorCodeException("-3", "原单非入住状态不能办理续住,oriCheckinId:" + oriCheckinId);
			}
			XbOrder oriOrderInfo = daoUtil.orderMgntDao.findByPk(oriCheckinInfo.getOrderId());
			
			
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(oriCheckinId);// 已经过滤掉删除的入住人了
			boolean contain = false;
			for (XbCheckiner xbCheckiner : checkinerList) {
				exceptionHandler.getLog().info(String.format("判断能否申请续住,要过滤掉下单人,原订单oriCheckinId:%s,当前checkinerId:%s,当前对象:%s", oriCheckinId,
						xbCheckiner.getCheckinerId(), xbCheckiner));
				if (xbCheckiner.getIsMain() != -1 && xbCheckiner.getLodgerId().intValue() == lodgerId) {// -1表示是下单人,是冗余在xb_checkiner表的,只为了查询"我的订单"接口复杂度简化之用
					contain = true;
					break;
				}
			}
			if (!contain) {
				throw exceptionHandler.newErrorCodeException("-5", "该用户不能申请续住,lodgerId:" + lodgerId);
			}
			
			
			Date checkinTime = DateUtil.addMinutes(oriCheckinInfo.getCheckoutTime(), 1);// 续住单从结束下一分钟开始
			if (DateUtil.compareDate(DateUtil.parseDate(overstayEndDay), checkinTime) <= 0) {
				throw exceptionHandler.newErrorCodeException("-7", "续住结束日期错误,overstayEndDay:" + overstayEndDay);
			}
			
			
			// 房间不能被锁定
			Date checkoutTime = DateUtil.parseDateTime(DateUtil.getYearMonDayStr_(DateUtil.parseDate(overstayEndDay)) + " 12:00:00");
			exceptionHandler.getLog().info("续住检查房间不能被锁定," + inputParams + ",入离时间:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime) + "~" + DateUtil.getYearMonDayHrMinSecStr_(checkoutTime));
			
			int roomId = oriCheckinInfo.getRoomId();
			String orderNo = RandomUtil.getBookOrderNo();
			exceptionHandler.getLog().info("准备产生续住订单:" + orderNo);
			orderMgntCommon.lockCouponCard(orderNo, lodgerId, cardCode, checkinTime, checkoutTime, roomId, discountPriceFromWebCentUnit == null ? 0 : discountPriceFromWebCentUnit, actualPriceFromWebCentUnit);// 卡券接口抛什么就是什么异常
			
			
			
			
			// 【2】下订单
			exceptionHandler.getLog().info("组装新建续住订单的参数");
			int stat = AppConstants.Order_stat.NEW_0;// 未支付
			String otaOrderNo = null;
			int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
			boolean isTestAcct = orderMgntCommon.isTestAccount(lodgerInfo.getMobile());
			int realPriceCentUnit = actualPriceFromWebCentUnit;
			if (isTestAcct) {
				realPriceCentUnit = 100;// 1元
				orderType = AppConstants.Order_orderType.TEST_5;
			}
			
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, realPriceCentUnit, lodgerInfo.getLodgerId(),// 下单人是当前申请续住的入住人
					lodgerInfo.getLodgerName(), lodgerInfo.getMobile(), checkinTime, checkoutTime, otaOrderNo, discountPriceFromWebCentUnit);
			orderInput.setOrderNo(orderNo);// 由外部产生
			List<CheckinInput> checkinInputList = new ArrayList<>();
			int chkinPriceCentUnit = realPriceCentUnit;
			String otaStayId = null;
			int defaultCheckinLodgerId = lodgerInfo.getLodgerId();
			String defaultCheckinName = lodgerInfo.getLodgerName();
			String defaultCheckinMobile = lodgerInfo.getMobile();// 
			CheckinInput checkinInput = new CheckinInput(roomId, defaultCheckinLodgerId, defaultCheckinName, defaultCheckinMobile , checkinTime, checkoutTime,
					chkinPriceCentUnit, otaStayId);
			checkinInput.setSaveXbCheckiner(false);// 不要保存xb_checkiner,下面会手动保存
			checkinInputList.add(checkinInput);

			
			// 保存邮寄发票信息(原来有就保存,没就不保存)
			Invoice invoiceInfo = null;
			if (invoice != null) {
				invoiceInfo = new Invoice(invoice.getInvoiceTitle(), invoice.getMailName(), invoice.getContactNo(), invoice.getMailAddr(),
						invoice.getPostCode(), realPriceCentUnit);
			}
			
			
			// 新建订单(★)
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, FromType.FROM_LOCAL);
			int newOrderId = out.getOrderInfo().getOrderId();
			int newCheckinId = out.getNewCheckinList().get(0).getCheckinInfo().getCheckinId();
			exceptionHandler.getLog().info("生成订单完毕:" + orderNo);
			// 修改overstay_flag标记
			maintainModuleBase.chgOverstayFlag(oriCheckinId, newCheckinId);

			
			
			// 【3】原来的入住人原样继承过来
			List<XbCheckiner> notContainOrderManCheckinerList = new ArrayList<>();// 不包括下单人的xb_checkiner有效记录(is_del不为删除状态)
			for (XbCheckiner xbCheckiner : checkinerList) {
				if (xbCheckiner.getIsMain() != -1) {// 过滤掉原单的下单人
					notContainOrderManCheckinerList.add(xbCheckiner);
				}
			}
			List<XbCheckiner> newCheckinerList = new ArrayList<>();
			for (XbCheckiner oriCheckiner : notContainOrderManCheckinerList) {
				XbCheckiner newCheckiner = new XbCheckiner();
				newCheckiner.setCheckinId(newCheckinId);
				newCheckiner.setCreateDtm(DateUtil.getCurDateTime());
				newCheckiner.setRemark("续住从旧单拷贝过来的,oriCheckinerId:" + oriCheckiner.getCheckinerId());
				newCheckiner.setMobile(oriCheckiner.getMobile());
				newCheckiner.setLodgerId(oriCheckiner.getLodgerId());
				newCheckiner.setName(oriCheckiner.getName());
				newCheckiner.setIdcardNo(oriCheckiner.getIdcardNo());
				newCheckiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);// 发门锁密码
				if (newCheckiner.getLodgerId() == lodgerId) {// 下单人
					newCheckiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);
				} else {
					newCheckiner.setIsMain(AppConstants.Checkiner_isMain.OTHER);
				}
				Long checkinerId = (Long) daoUtil.checkinerDao.addAndGetPk(newCheckiner);
				newCheckiner.setCheckinerId(checkinerId.intValue());
				newCheckinerList.add(newCheckiner);
			}
			// 新的下单人
			XbCheckiner orderCheckiner = new XbCheckiner();
			orderCheckiner.setCheckinId(newCheckinId);
			orderCheckiner.setLodgerId(out.getOrderInfo().getLodgerId());
			orderCheckiner.setMobile(out.getOrderInfo().getLodgerMobile());
			orderCheckiner.setName(out.getOrderInfo().getLodgerName());
			orderCheckiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);// 发门锁密码
			orderCheckiner.setRemark("下订单人,冗余数据");
			orderCheckiner.setIsMain(-1);
			orderCheckiner.setCreateDtm(DateUtil.getCurDateTime());
			Long checkinerId = (Long) daoUtil.checkinerDao.addAndGetPk(orderCheckiner);
			orderCheckiner.setCheckinerId(checkinerId.intValue());
			newCheckinerList.add(orderCheckiner);
			
			// 【4】记录到操作记录表
			String remark = String.format("用户%s,手机%s,续住roomId是%s的房间,续住入离时间是%s~%s", lodgerInfo.getLodgerName(), lodgerInfo.getMobile(),
					oriCheckinInfo.getRoomId(), DateUtil.getYearMonDayHrMinSecStr_(checkinTime), DateUtil.getYearMonDayHrMinSecStr_(checkoutTime));
			OperOverstay operOverstay = new OperOverstay(oriCheckinInfo.getCheckinId(), newCheckinId, oriCheckinInfo.getOrderId(), newOrderId, oriCheckinInfo.getRoomId(), oriCheckinInfo.getRoomId(), lodgerId, DateUtil.getCurDateTime(), remark);
			daoUtil.operOverstayDao.addAndGetPk(operOverstay);
			
			Map<String, Object> valueMap = new HashMap<>();
			valueMap.put("orderInfo", out.getOrderInfo());
			valueMap.put("checkinerList", newCheckinerList);
			return valueMap;
		} catch (Exception e) {
			exceptionHandler.getLog().info("查询某天退房数异常: " + e.getMessage());
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	
	public XbInvoiceRec queryInvoice(Integer invoiceRecId, Integer checkinId, Integer orderId) {
		String inputParams = String.format("invoiceRecId:%s,checkinId:%s,orderId:%s", invoiceRecId, checkinId, orderId);
		exceptionHandler.getLog().info("续住接口,传参" + inputParams);
		try {
			if (invoiceRecId != null) {
				return daoUtil.invoiceRecDao.findByPk(invoiceRecId);
			}
			if (checkinId != null) {
				return daoUtil.invoiceRecDao.findByCheckinId(checkinId);
			}
			if (orderId != null) {
				return daoUtil.invoiceRecDao.findByOrderId(orderId);
			}
			return null;
		} catch (Exception e) {
			exceptionHandler.getLog().info("查询某天退房数异常: " + e.getMessage());
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public Map<String, Object> checkOverstay(int oriCheckinId, int lodgerId) {
		String inputParams = String.format("检查是否能够续住oriCheckinId:%s,lodgerId:%s", oriCheckinId, lodgerId);
		exceptionHandler.getLog().info("续住接口,传参" + inputParams);
		Map<String, Object> result = new HashMap<>();
		try {
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			if (lodgerInfo == null) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误,找不到用户信息,lodgerId:" + lodgerId, result);
			}
			
			XbCheckin oriCheckinInfo = daoUtil.checkinDao.findByPk(oriCheckinId);
			exceptionHandler.getLog().info(oriCheckinInfo);

			
			Date checkoutTime = oriCheckinInfo.getCheckoutTime();
			Date newCheckinTime = DateUtil.addMinutes(checkoutTime, 1);
			result.put("invoiceInfo", this.queryInvoice(null, oriCheckinId, null));
			result.put("newOrderCheckinTime", newCheckinTime);
			
			
			if (oriCheckinInfo.getStat() != AppConstants.Checkin_stat.CHECKIN_1) {
				throw exceptionHandler.newErrorCodeException("-3", "原单非入住状态不能办理续住,oriCheckinId:" + oriCheckinId, result);
			}
			
			
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(oriCheckinId);// 已经过滤掉删除的入住人了
			boolean contain = false;
			for (XbCheckiner xbCheckiner : checkinerList) {
				exceptionHandler.getLog().info(String.format("判断能否申请续住,要过滤掉下单人,原订单oriCheckinId:%s,当前checkinerId:%s,当前对象:%s", oriCheckinId,
						xbCheckiner.getCheckinerId(), xbCheckiner));
				if (xbCheckiner.getIsMain() != -1 && xbCheckiner.getLodgerId().intValue() == lodgerId) {// -1表示是下单人,是冗余在xb_checkiner表的,只为了查询"我的订单"接口复杂度简化之用
					contain = true;
					break;
				}
			}
			if (!contain) {
				throw exceptionHandler.newErrorCodeException("-5", "该用户不能申请续住,lodgerId:" + lodgerId, result);
			}
			
			
			// 查询旧单离店时间当天的房态情况,这一天至少是可以预订的才返回可预订
			Date newCheckoutTime = DateUtil.parseDateTime(DateUtil.getYearMonDayStr_(DateUtil.addDays(newCheckinTime, 1)) + " 12:00:00");
			List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(oriCheckinInfo.getRoomId(), newCheckinTime, newCheckoutTime);// 每天的房态和价格
			if (!calendarCommon.checkCanBook(roomCalList)) {
				throw exceptionHandler.newErrorCodeException("-7", "不能预订,房间被占", result);
			}
			
		} catch (Exception e) {
			exceptionHandler.getLog().info("查询某天退房数异常: " + e.getMessage());
			throw exceptionHandler.logServiceException(e);
		}
		return result;
	}
	
	
	/**
	 * 查询当天预计退房数
	 * 
	 * @param orderNo
	 * @param today
	 *            传入当天的日期
	 * @return
	 */
	public Map<String, Object> queryPreCheckout(Date today) {
		exceptionHandler.getLog().info("查询某天退房数" + DateUtil.getYearMonDayHrMinSecStr_(today));
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<String> li = new ArrayList<>();// 记录已经存在的地区,这些地区就不需要初始化为0了
			
			/*
			 * 正常单逻辑://
			 * ①已支付
			 * ②已入住
			 * ③非换房原单(作废单)
			 * ④离店时间是当天
			 * 
			 * 停用单:
			 * ①正在进行的
			 * ②当天退房的
			 */
			String sql = "SELECT t.district,SUM(total) sumOfEachDistrict FROM"+ 
							"("+
							"SELECT c.district,COUNT(*) total FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id INNER JOIN xb_room c ON b.room_id=c.room_id "+ 
							"WHERE a.stat=1 AND b.stat=1 AND b.chg_room_flag!=1 AND DATE_FORMAT(b.checkout_time,'%Y-%m-%d')=? GROUP BY c.district"+
				
							" UNION ALL " + 
				
							"SELECT b.district,COUNT(*) FROM xb_order_stop a INNER JOIN xb_room b ON a.room_id=b.room_id WHERE a.stop_stat=1 AND DATE_FORMAT(a.stop_end,'%Y-%m-%d')=? GROUP BY b.district"+
							") t GROUP BY t.district";
			
			
			
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { DateUtil.getYearMonDayStr_(today), DateUtil.getYearMonDayStr_(today) });
			for (Map<String, Object> map : mapList) {
				String district = (String) map.get("district");
				BigDecimal count = (BigDecimal) map.get("sumOfEachDistrict");
				int total = count.intValue();
				Map<String, Object> m = new HashMap<>();
				m.put("district", district);
				m.put("total", total);
				list.add(m);
				li.add(district);
			}

			String sqlAll = "SELECT district FROM xb_room GROUP BY district";// WHERE
																				// flag=1
																				// 这个不要,查出来无影响
			List<Map<String, Object>> mapListAll = daoUtil.orderMgntDao.queryMapList(sqlAll);
			for (Map<String, Object> map : mapListAll) {
				String district = (String) map.get("district");
				if (!li.contains(district)) {
					Map<String, Object> m = new HashMap<>();
					m.put("district", district);
					m.put("total", 0);
					list.add(m);
				}
			}

			// 计算总退房数
			int totalChkout = 0;
			for (Map<String, Object> map : list) {
				Integer total = (Integer) map.get("total");
				totalChkout += (total == null ? 0 : total);
			}

			Map<String, Object> param = new HashMap<>();
			param.put("totalChkout", totalChkout);
			param.put("zone", list);

			exceptionHandler.getLog().info("查询某天退房数:出参" + JsonHelper.toJSONString(param));
			return param;
		} catch (Exception e) {
			exceptionHandler.getLog().info("查询某天退房数异常: " + e.getMessage());
			throw exceptionHandler.logServiceException(e);
		}
	}

	private static Map<String, Object> transEntity2Map(Collection<Entity> list) {
		if (list != null) {
			Map<String, Object> map = new HashMap<>();
			for (Entity entity : list) {
				map.put("district", entity.getDistrict());
				map.put("total", entity.getTotal());
			}
			return map;
		}
		return null;
	}

	private static class Entity {
		private String district;
		private int total;

		public Entity(String district, int total) {
			this.district = district;
			this.total = total;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		@Override
		public String toString() {
			return "Entity [district=" + district + ", total=" + total + "]";
		}
	}

	// OMS系统查看订单信息和入住信息(就是类似"所有订单"中的一行)
	public List<Map<String, Object>> qryOrderInfoCheckinInfo(String orderNo, int qryType, Integer roomId) {
		try {
			List<Object> params = new ArrayList<>();
			String sql = "SELECT b.chg_room_flag chgRoomFlag,b.night_count nightCount,b.parent_checkin_id parentCheckinId,b.child_checkin_id childCheckinId,b.actual_checkin_time actualCheckinTime,b.actual_checkout_time actualCheckoutTime,f.ext2 isRoomCtrlOpenPwdSmsSend,b.room_id roomId,a.lodger_id bookLodgerId,e.ext2 isBookSmsSend,f.ext3 isCheckinSmsSend,b.checkout_type checkoutType,d.is_follow isFollow,d.lodger_id checkinLodgerId,"
					+ "b.contact_phone mobile,b.lodger_name lodgerName,a.order_id orderId,a.order_no orderNo,a.create_time createTime,a.order_type orderType,a.source source,"
					+ "a.stat orderStat,b.stat checkinStat,b.checkin_time checkinTime,b.checkout_time checkoutTime,b.checkin_id checkinId,a.room_count roomCount,"
					+ "a.discount_price discountPrice,a.total_price totalPrice,c.chain_id chainId,c.chain_name chainName,c.room_name roomName,a.invoice_rec_id invoiceRecId"
					+ " FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id INNER JOIN xb_room c ON b.room_id=c.room_id LEFT JOIN xb_lodger d ON d.lodger_id=b.lodger_id LEFT JOIN xb_order_ext e ON a.order_id=e.order_id LEFT JOIN xb_checkin_ext f ON"
					+ " b.checkin_id=f.checkin_id WHERE a.order_no=?";// 需要再用b.checkout_time再过滤一次吗
			params.add(orderNo);
			if (roomId != null) {
				sql += " AND b.room_id=?";
				params.add(roomId);
			}

			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sql, params.toArray());
			int resultSize = listMap.size();
			for (int i = 0; i < resultSize; i++) {
				Map<String, Object> m = listMap.get(i);
				Map<String, Object> statusMap = new HashMap<>();
				String isBookSmsSend = (String) m.get("isBookSmsSend");
				String isCheckinSmsSend = (String) m.get("isCheckinSmsSend");
				String isRoomCtrlOpenPwdSmsSend = (String) m.get("isRoomCtrlOpenPwdSmsSend");
				Integer isFollow = (Integer) m.get("isFollow");
				statusMap.put("isBookSmsSend", isBookSmsSend != null && isBookSmsSend.length() > 0 ? 1 : 0);// 扩展字段2:预定发送给首个入住人的短信ID
				statusMap.put("isCheckinSmsSend", isCheckinSmsSend != null && isCheckinSmsSend.length() > 0 ? 1 : 0);// 扩展字段3:办理入住,发送给首个入住人的入住短信ID
				statusMap.put("isLogined", isFollow != null && isFollow == AppConstants.Lodger_isFollow.FOLLOW_2
						? AppConstants.Lodger_isFollow.FOLLOW_2 : AppConstants.Lodger_isFollow.NOT_FOLLOW_1);// 兼容前台
				statusMap.put("checkoutType", m.get("checkoutType"));
				statusMap.put("isRoomCtrlOpenPwdSmsSend", isRoomCtrlOpenPwdSmsSend != null && isRoomCtrlOpenPwdSmsSend.length() > 0 ? 1 : 0);
				m.put("smsStatus", statusMap);
				if (qryType == 1) {
					int checkinId = (int) m.get("checkinId");
					int canChgRoomFlag = this.canChgRoom(checkinId);
					m.put("canChgRoomFlag", canChgRoomFlag == 1 ? 1 : 0);
				}
			}
			return listMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}


	
	// 是否已经入住(总是担心那种异常数据对系统造成破坏,例如:其实已经通过去呼呼管家办理了入住,但是我们系统还是0的状态,已经离店了还是0的状态...)
	public boolean isAlreadyCheckin(XbCheckin checkinInfo) {
		try {
			int stat = checkinInfo.getStat();
			if (stat == AppConstants.Checkin_stat.NEW_0) {// 需要再判断入住时间和当前日期吗?
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得换房后新房的时间段
	public Map<String, Date> getChgRoomPeriod(XbCheckin oldCheckinInfo) {
		Map<String, Date> resultMap = new HashMap<>();
		try {
			Date now = DateUtil.getCurDateTime();
			Date startTime = null;
			Date endTime = null;
			if (!this.isAlreadyCheckin(oldCheckinInfo)) {// 已支付未入住
				startTime = oldCheckinInfo.getCheckinTime();
				endTime = oldCheckinInfo.getCheckoutTime();
			} else {// 已支付已入住
				startTime = now;
				endTime = oldCheckinInfo.getCheckoutTime();
			}
			resultMap.put("startTime", startTime);
			resultMap.put("endTime", endTime);
			exceptionHandler.getLog().info("旧checkinInfo:" + oldCheckinInfo);
			exceptionHandler.getLog().info("获得换房后新房的时间段,startTime:" + startTime + ",endTime:" + endTime);
			return resultMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 查询更换该入住单的目标房间列表
	public Map<String, Object> qryDestRoomList(int checkinId) {
		Map<String, Object> resultMap = new HashMap<>();
		exceptionHandler.getLog().info("查询能换房的房间列表checkinId[" + checkinId + "]");
		try {
			Date now = DateUtil.getCurDateTime();
			exceptionHandler.getLog().info("目前时间:" + now);

			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			exceptionHandler.getLog().info("换房原来的入住单信息" + checkinInfo);
			exceptionHandler.getLog().info("换房原来的订单信息" + orderInfo);

			// 只有已支付未入住或已支付已入住两种情况可以换房
			int orderStat = orderInfo.getStat();
			int checkinStat = checkinInfo.getStat();
			int canChgRoomFlag = this.canChgRoom(checkinId);
			if (canChgRoomFlag != 1) {
				throw exceptionHandler.newErrorCodeException("-1", "不能换房,orderStat:" + orderStat + ",checkinStat:" + checkinStat + ",checkinTime:"
						+ checkinInfo.getCheckinTime() + ",checkoutTime:" + checkinInfo.getCheckoutTime());
			}

			// 查出这段时间内每一天都能预占的(不包括结尾天)
			Map<String, Date> d = this.getChgRoomPeriod(checkinInfo);
			Date startTime = d.get("startTime");
			Date endTime = d.get("endTime");
			List<Map<String, Object>> freeRoomList = calendarCommon.queryFreeRoomContainNotFreeRoom(startTime, endTime);
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("fromTime", startTime);
			subMap.put("toTime", endTime);
			subMap.put("value", freeRoomList);
			resultMap.put("retcode", 1);
			resultMap.put("retval", subMap);
			return resultMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 判断能否换房,返回1可以换房
	public int canChgRoom(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			int source = orderInfo.getSource();
			int orderStat = orderInfo.getStat();
			int checkinStat = checkinInfo.getStat();
			Date checkoutTime = checkinInfo.getCheckoutTime();
			Date now = DateUtil.getCurDateTime();
			if (!(orderStat == AppConstants.Order_stat.PAYED_1
					&& (checkinStat == AppConstants.Checkin_stat.NEW_0 || checkinStat == AppConstants.Checkin_stat.CHECKIN_1))) {
				return -1;// 订单或入住单状态错误
			}
			int compareChkoutTime = DateUtil.compareDate(now, checkoutTime);
			if (compareChkoutTime > 0) {
				return -2;// 过期的垃圾单,理论不会出现这种情况,但是不排除垃圾数据(如在去呼呼后台帮忙办理入住,则在我们这边的入住单的状态一直都是0)
			}
			if (source == AppConstants.Order_source.QUNAR_1 && checkinStat == AppConstants.Checkin_stat.NEW_0) {
				return -3;// 去哪儿来的订单,若还未入住不能办理换房(存在失败风险:未入住的去哪儿订单不能由我们取消订单)
			}
			if (checkinInfo.getChgRoomFlag() != AppConstants.Checkin_chgRoomFlag.NOMAL_0) {
				return -4;
			}
			return 1;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// OMS用,查询出能换房的订单列表(其实是入住单列表)
	public ListPage qryCanChgRoomOrderList(int thisPage, int pageSize) {
		try {
			if (thisPage <= 0 || pageSize <= 0) {
				throw new RuntimeException("非法入参thisPage[" + pageSize + "],pageSize[" + pageSize + "]");
			}
			ListPage listPage = new ListPage();
			String sql = "SELECT e.ext2 isBookSmsSend,f.ext3 isCheckinSmsSend,b.checkout_type checkoutType,d.is_follow isFollow,d.lodger_id lodgerId,"
					+ "b.contact_phone mobile,b.lodger_name lodgerName,a.order_id orderId,a.order_no orderNo,a.create_time createTime,a.order_type orderType,a.source source,"
					+ "a.stat orderStat,b.stat checkinStat,b.checkin_time checkinTime,b.checkout_time checkoutTime,b.checkin_id checkinId,a.room_count roomCount,"
					+ "a.total_price totalPrice,c.chain_id chainId,c.chain_name chainName,c.room_name roomName,a.invoice_rec_id invoiceRecId"
					+ " FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id INNER JOIN xb_room c ON b.room_id=c.room_id LEFT JOIN xb_lodger d ON d.lodger_id=b.lodger_id LEFT JOIN xb_order_ext e ON a.order_id=e.order_id LEFT JOIN xb_checkin_ext f ON"
					+ " b.checkin_id=f.checkin_id WHERE a.stat=1 AND b.stat IN (0,1) ORDER BY a.create_time DESC LIMIT ?,?";// 需要再用b.checkout_time再过滤一次吗

			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sql,
					new Object[] { PageHelper.getStartIndex(thisPage, pageSize), pageSize });
			int resultSize = listMap.size();
			for (int i = 0; i < resultSize; i++) {
				Map<String, Object> m = listMap.get(i);
				Map<String, Object> statusMap = new HashMap<>();
				String isBookSmsSend = (String) m.get("isBookSmsSend");
				String isCheckinSmsSend = (String) m.get("isCheckinSmsSend");
				Integer isFollow = (Integer) m.get("isFollow");
				statusMap.put("isBookSmsSend", isBookSmsSend != null && isBookSmsSend.length() > 0 ? 1 : 0);// 扩展字段2:预定发送给首个入住人的短信ID
				statusMap.put("isCheckinSmsSend", isCheckinSmsSend != null && isCheckinSmsSend.length() > 0 ? 1 : 0);// 扩展字段3:办理入住,发送给首个入住人的入住短信ID
				statusMap.put("isLogined", isFollow != null && isFollow == AppConstants.Lodger_isFollow.FOLLOW_2
						? AppConstants.Lodger_isFollow.FOLLOW_2 : AppConstants.Lodger_isFollow.NOT_FOLLOW_1);// 兼容前台
				statusMap.put("checkoutType", m.get("checkoutType"));
				m.put("smsStatus", statusMap);
			}
			Pager pager = new Pager();
			pager.setPageSize(pageSize);
			pager.setThisPage(thisPage);
			// pager.setTotal(resultSize);
			// pager.setPageCount(PageHelper.getTotalPage(resultSize,
			// pageSize));
			listPage.setPager(pager);
			listPage.setList(listMap);
			return listPage;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	
	public void sendOpenDoorSms(String mobile, int checkinId) {
		try {
			// 1.新的短信模版,按照这个模版组装短信,
			// 2.要从checkin表的open_pwd中获取当时的开门密码(同时加上保存管家密码的字段)
			// 3.目前生产上xb_checkin.open_pwd字段,所有是NULL值的都是未办理入住的,有两条是历史数据(在记录open_pwd之前创建的)而导致NULL的,通过去呼呼办理入住的有2条记录,无法获得开门密码
			// 4.发送门锁密码需要验证lodger的mobile和密码,同时验证这个mobile是住户的一员(在checkiner中),同时该入住单的状态正常(是入住状态)
			// 5.建立checkin的扩展表xb_checkin_ext,和xb_order_ext类似,记录发送的门锁密码短信ID

			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			if (checkinInfo.getStat() != AppConstants.Checkin_stat.CHECKIN_1) {// 入住单状态错误
				throw exceptionHandler.newErrorCodeException("-3", "入住单状态错误");
			}
			String openPwd = checkinInfo.getOpenPwd();
			if (openPwd == null || openPwd.length() == 0 || !openPwd.matches("\\d+")) {
				throw exceptionHandler.newErrorCodeException("-6", "开门密码值问题,原因可能是去呼呼后台办理入住无法知道密码; 或数据问题. openPwd[" + openPwd + "]");
			}

			// 抛事件
			eventService.throwResendOpenPwdEvent(checkinId, mobile);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public ListPage queryNotCheckinOrders(int thisPage, int pageSize) {
		try {
			if (thisPage <= 0 || pageSize <= 0) {
				throw new RuntimeException("非法入参thisPage[" + pageSize + "],pageSize[" + pageSize + "]");
			}
			ListPage listPage = new ListPage();
			// 是不是要采取左连接?忽略xb_lodger的数据问题?
			String sql = "SELECT a.create_time AS orderCreateDtm,b.lodger_id AS lodgerId,b.lodger_name AS lodgerName,b.contact_phone AS mobile,a.order_id AS orderId,a.order_no AS orderNo,a.stat AS orderStat,a.lodger_id AS orderLodgerId,a.total_price AS totalPrice,a.order_type AS orderType,a.ota_order_no AS otaOrderNo,a.remark AS orderRemark,"
					+ "b.checkin_id AS checkinId,b.lodger_id AS checkinLodgerId,b.room_id AS roomId,b.stat AS checkinStat,b.checkin_time AS checkinTime,b.checkout_time AS checkoutTime,b.open_pwd AS openPwd,b.remark AS checkinRemark,b.ota_stay_id AS otaStayId"
					+ "  FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id WHERE b.stat=? AND a.stat IN (0,1,4) AND a.source!=1 AND a.order_type!=? ORDER BY a.create_time DESC LIMIT ?,?";
			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { AppConstants.Checkin_stat.NEW_0,
					AppConstants.Order_orderType.SELF_FORBID_2, PageHelper.getStartIndex(thisPage, pageSize), pageSize });
			int resultSize = listMap.size();
			for (int i = 0; i < resultSize; i++) {
				Map<String, Object> m = listMap.get(i);
				// 入住人信息
				Map<String, Object> lodgerInfo = new HashMap<>(5);
				lodgerInfo.put("lodgerId", m.get("lodgerId"));
				lodgerInfo.put("lodgerName", m.get("lodgerName"));
				lodgerInfo.put("mobile", m.get("mobile"));
				m.put("lodgerInfo", lodgerInfo);
				int orderStat = (int) m.get("orderStat");
				boolean isPaid = orderStat == AppConstants.Order_stat.PAYED_1 || orderStat == AppConstants.Order_stat.REFUND_APPLY_4 ? true : false;// 已经支付或正申请退款,都是表示已经支付
				m.put("isPaid", isPaid);
				// 未付款和已付款都显示"取消",申请退款中则显示"退款"按钮
				String oper = orderStat == AppConstants.Order_stat.NEW_0 || orderStat == AppConstants.Order_stat.PAYED_1 ? "cancel" : "refund";
				m.put("oper", oper);
			}
			Pager pager = new Pager();
			pager.setPageSize(pageSize);
			pager.setThisPage(thisPage);
			// pager.setTotal(resultSize);
			// pager.setPageCount(PageHelper.getTotalPage(resultSize,
			// pageSize));
			listPage.setPager(pager);
			listPage.setList(listMap);
			return listPage;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}


	/**
	 * 返回 -1 表示 订单状态异常,可能是管理员取消了,然后微信端的界面依然停留在可以取消掉的界面,或反过来.后台这里会再检查一遍.
	 * 
	 * @param orderId
	 * @return
	 */
	public int cancelOrder(int orderId) {
		try {
			exceptionHandler.getLog().info("用户主动取消,orderId:" + orderId);
			Date now = DateUtil.getCurDateTime();
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull("订单不存在:orderId[" + orderId + "]", orderInfo);
			if (orderInfo.getStat() != AppConstants.Order_stat.NEW_0) {
				throw exceptionHandler.newErrorCodeException("-1", "订单状态异常");
			}
			// 不能取消去哪儿的订单(无论是未支付还是已支付)
			if (orderInfo.getSource() == AppConstants.Order_source.QUNAR_1) {
				throw exceptionHandler.newErrorCodeException("-2", "不能取消去哪儿订单");
			}

			// 修改订单状态
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderInfo.getOrderId());
			newOrder.setStat(AppConstants.Order_stat.CANCEL_2);
			String orderRemark = orderInfo.getRemark();
			String remark = orderRemark == null || orderRemark.equals("none") ? "" : orderRemark;
			remark += (remark.length() == 0 ? "用户取消订单" : ",用户取消订单");
			newOrder.setRemark(remark);
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);

			orderUtil.cancel(orderId, CancelType.CUST);

			// 写操作记录表
			XbOrderOper orderOper = new XbOrderOper();
			orderOper.setCreateDtm(now);
			orderOper.setIsRefund(AppConstants.OrderOper_isRefund.NO_REFUND_1);// 无需退款
			orderOper.setOperType(AppConstants.OrderOper_operType.SELF_CANCEL_1);// 自己取消未支付订单
			orderOper.setOrderId(orderId);
			orderOper.setRefund(0);
			orderOper.setRefundDtm(null);
			orderOper.setRemark("自己取消订单");
			orderOper.setUserId(-1);
			daoUtil.orderOperDao.add(orderOper);

			return 1;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public int finishRefund(int orderId) {
		try {
			Date now = DateUtil.getCurDateTime();
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull("订单不存在:orderId[" + orderId + "]", orderInfo);
			int orderStat = orderInfo.getStat();
			if (!(orderStat == AppConstants.Order_stat.REFUND_APPLY_4)) {// 【申请退款】才可以取消
				return -1;
			}

			// 修改订单状态
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderInfo.getOrderId());
			newOrder.setStat(AppConstants.Order_stat.REFUND_5);// 已经退款
			String orderRemark = orderInfo.getRemark();
			String remark = orderRemark == null || orderRemark.equals("none") ? "" : orderRemark;
			remark += (remark.length() == 0 ? "管理员已经退款" : ",管理员已经退款");
			newOrder.setRemark(remark);
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);

			// 写操作记录表
			XbOrderOper orderOper = new XbOrderOper();
			orderOper.setCreateDtm(now);
			orderOper.setIsRefund(AppConstants.OrderOper_isRefund.DONE_REFUND_3);
			orderOper.setOperType(AppConstants.OrderOper_operType.ADMIN_CANCEL_PAID_3);
			orderOper.setOrderId(orderId);
			orderOper.setRefund(orderInfo.getTotalPrice());
			orderOper.setRefundDtm(null);
			orderOper.setRemark("管理员已经退款");
			orderOper.setUserId(-1);
			daoUtil.orderOperDao.add(orderOper);
			return 1;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// OMS的取消订单,独立出来(可以取消未支付或已支付的)
	public void cancelOrderFromOMS(int orderId, String account) {
		try {
			exceptionHandler.getLog().info("oms取消订单,orderId:" + orderId);
			Date now = DateUtil.getCurDateTime();
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			exceptionHandler.getLog().info("订单存在:" + orderInfo);
			
			int orderStat = orderInfo.getStat();
			if (!(orderStat == AppConstants.Order_stat.NEW_0 || orderStat == AppConstants.Order_stat.PAYED_1)) {// 未支付和已支付才可以取消
				throw exceptionHandler.newErrorCodeException("-1", "订单状态异常");
			}
			// 不能取消去哪儿的订单(无论是未支付还是已支付)
			if (orderInfo.getSource() == AppConstants.Order_source.QUNAR_1) {
				throw exceptionHandler.newErrorCodeException("-2", "不能取消去哪儿订单");
			}

			// 取消订单(★)
			boolean isPaid = orderStat == AppConstants.Order_stat.NEW_0 ? false : true;
			orderUtil.cancel(orderId, isPaid ? CancelType.OMS_PAID : CancelType.OMS_UNPAID);
			
			// 修改订单状态
			exceptionHandler.getLog().info("修改订单状态为2或4");
			XbOrder newOrder = new XbOrder();
			int newOrderStat = isPaid ? AppConstants.Order_stat.REFUND_APPLY_4 : AppConstants.Order_stat.CANCEL_2;
			newOrder.setOrderId(orderInfo.getOrderId());
			newOrder.setStat(newOrderStat);
			String orderRemark = orderInfo.getRemark();
			String remark = orderRemark == null || orderRemark.equals("none") ? "" : orderRemark;
			remark += (remark.length() == 0 ? "管理员OMS取消订单" : ",管理员OMS取消订单");
			newOrder.setRemark(remark);
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);
			

			// 写操作记录表
			exceptionHandler.getLog().info("写操作记录表...");
			int isRefund = isPaid ? AppConstants.OrderOper_isRefund.NOT_YET_REFUND_2// 未退款
					: AppConstants.OrderOper_isRefund.NO_REFUND_1;// 无需退款
			XbOrderOper orderOper = new XbOrderOper();
			orderOper.setCreateDtm(now);
			orderOper.setIsRefund(isRefund);
			int operType = isPaid ? AppConstants.OrderOper_operType.ADMIN_CANCEL_PAID_3// 管理员取消已支付订单
					: AppConstants.OrderOper_operType.ADMIN_CANCEL_NOT_PAID_2;// 管理员取消未支付订单
			orderOper.setOperType(operType);
			orderOper.setOrderId(orderId);
			int refund = isPaid ? orderInfo.getTotalPrice() : 0;
			orderOper.setRefund(refund);
			orderOper.setRefundDtm(null);
			orderOper.setRemark("操作员取消订单");
			int userId = -1;
			if (StringUtils.isNotBlank(account)) {
				omsUserService.queryOmsUserId(account);
			}
			orderOper.setUserId(userId);
			daoUtil.orderOperDao.add(orderOper);
			
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// OMS用
	public ListPage queryOrders(int thisPage, int pageSize, Integer roomId, String orderNo, Integer orderId) {
		try {
			if (thisPage <= 0 || pageSize <= 0) {
				throw new RuntimeException("非法入参thisPage[" + pageSize + "],pageSize[" + pageSize + "]");
			}
			ListPage listPage = new ListPage();
			StringBuffer sql = new StringBuffer("SELECT b.actual_checkin_time actualCheckinTime,b.actual_checkout_time actualCheckoutTime,b.chg_room_flag chgRoomFlag,e.ext2 isBookSmsSend,f.ext3 isCheckinSmsSend,b.checkout_type checkoutType,d.is_follow isFollow,d.lodger_id lodgerId,").
					append("a.lodger_mobile mobile,a.lodger_name lodgerName,a.order_id orderId,a.order_no orderNo,a.create_time createTime,a.order_type orderType,a.source source,").
					append("a.stat orderStat,b.stat checkinStat,b.checkin_time checkinTime,b.checkout_time checkoutTime,b.checkin_id checkinId,a.room_count roomCount,").
					append("a.discount_price discountPrice,a.total_price totalPrice,c.chain_id chainId,c.chain_name chainName,c.room_name roomName,a.invoice_rec_id invoiceRecId").
					append(" FROM xb_order a INNER JOIN xb_checkin b ON a.order_id=b.order_id INNER JOIN xb_room c ON b.room_id=c.room_id LEFT JOIN xb_lodger d ON d.lodger_id=b.lodger_id LEFT JOIN xb_order_ext e ON a.order_id=e.order_id LEFT JOIN xb_checkin_ext f ON").
					append(" b.checkin_id=f.checkin_id WHERE b.chg_room_flag!=1 ");
			List<Object> params = new ArrayList<>();
			if (roomId != null) {
				sql.append(" AND b.room_id=?");
				params.add(roomId);
			}
			if (StringUtils.isNotBlank(orderNo)) {
				sql.append(" AND a.order_no=?");
				params.add(orderNo);
			}
			if (orderId != null) {
				sql.append(" AND a.order_id=?");
				params.add(orderId);
			}
			sql.append(" ORDER BY a.create_time DESC LIMIT ?,?");
			params.add(PageHelper.getStartIndex(thisPage, pageSize));
			params.add(pageSize);
			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sql.toString(),
					params.toArray());
			int resultSize = listMap.size();
			for (int i = 0; i < resultSize; i++) {
				Map<String, Object> m = listMap.get(i);
				Map<String, Object> statusMap = new HashMap<>();
				String isBookSmsSend = (String) m.get("isBookSmsSend");
				String isCheckinSmsSend = (String) m.get("isCheckinSmsSend");
				Integer isFollow = (Integer) m.get("isFollow");
				statusMap.put("isBookSmsSend", isBookSmsSend != null && isBookSmsSend.length() > 0 ? 1 : 0);// 扩展字段2:预定发送给首个入住人的短信ID
				statusMap.put("isCheckinSmsSend", isCheckinSmsSend != null && isCheckinSmsSend.length() > 0 ? 1 : 0);// 扩展字段3:办理入住,发送给首个入住人的入住短信ID
				statusMap.put("isLogined", isFollow != null && isFollow == AppConstants.Lodger_isFollow.FOLLOW_2
						? AppConstants.Lodger_isFollow.FOLLOW_2 : AppConstants.Lodger_isFollow.NOT_FOLLOW_1);// 兼容前台
				statusMap.put("checkoutType", m.get("checkoutType"));
				m.put("smsStatus", statusMap);
				m.put("checkInData", daoUtil.checkinerDao.findBycheckInId((Integer)m.get("checkinId")));
			}
			Pager pager = new Pager();
			pager.setPageSize(pageSize);
			pager.setThisPage(thisPage);
			// pager.setTotal(resultSize);
			// pager.setPageCount(PageHelper.getTotalPage(resultSize,
			// pageSize));
			listPage.setPager(pager);
			listPage.setList(listMap);
			return listPage;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 查询可以预定的房间
	 * 
	 * @return
	 */
	public List<Map<String, Object>> queryRoomsToBeOrdered(String jsonStr) {
		Date now = DateUtil.getCurDateTime();
		try {
			StringBuffer sql = new StringBuffer();
			List<Object> params = new ArrayList<>();
			// 有查询条件
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(jsonStr == null || jsonStr.length() == 0 ? "{}" : jsonStr);
			String province = obj.getString("province");
			String city = obj.getString("city");
			String district = obj.getString("district");
			Integer chainId = obj.getInteger("chainId");
			JSONObject time = obj.getJSONObject("time");

			if (time != null) {
				String start = time.getString("start");
				String end = time.getString("end");
				sql.append(
						"SELECT r.room_detail roomDetail,r.province province,r.city city,r.district district,r.lodger_count AS lodgerCount,r.locate AS locate,r.room_id AS roomId,r.chain_id AS chainId,r.room_type_name AS roomTypeName,")
						.append("r.room_name AS roomName,r.title AS title,r.addr AS addr,r.price AS defaultPrice,r.descri AS descri,c.price AS actualPrice,r.pic_id AS picId FROM xb_room r INNER JOIN xb_calendar_daily c ON r.room_id=c.room_id INNER JOIN (")
						.append("SELECT r.room_id FROM xb_room r INNER JOIN xb_calendar_daily c ON r.room_id=c.room_id WHERE c.stat=1 AND r.flag=1 AND c.cur_date>=? AND c.cur_date<? GROUP BY room_id HAVING COUNT(*)=DATEDIFF(?,?)")
						.append(") t ON r.room_id=t.room_id WHERE c.stat=1 AND r.flag=1 AND c.cur_date=?");

				params.add(start);
				params.add(end);
				params.add(end);
				params.add(start);
				params.add(start);
				if (province != null && province.length() > 0) {
					sql.append(" AND r.province=?");
					params.add(province);
				}
				if (city != null && city.length() > 0) {
					sql.append(" AND r.city=?");
					params.add(city);
				}
				if (district != null && district.length() > 0) {
					sql.append(" AND r.district=?");
					params.add(district);
				}
				if (chainId != null) {
					sql.append(" AND r.chain_id=?");
					params.add(chainId);
				}
			} else {
				sql.append(
						"SELECT r.room_detail roomDetail,r.province province,r.city city,r.district district,r.lodger_count AS lodgerCount,r.locate AS locate,r.room_id AS roomId,r.chain_id AS chainId,r.room_type_name AS roomTypeName,")
						.append("r.room_name AS roomName,r.title AS title,r.addr AS addr,r.price AS defaultPrice,r.descri AS descri,c.price AS actualPrice,r.pic_id AS picId")
						.append(" FROM xb_room r,xb_calendar_daily c WHERE r.room_id=c.room_id AND r.flag=1 AND c.cur_date=?");
				params.add(DateUtil.getYearMonDayStr_(now));
				if (province != null && province.length() > 0) {
					sql.append(" AND r.province=?");
					params.add(province);
				}
				if (city != null && city.length() > 0) {
					sql.append(" AND r.city=?");
					params.add(city);
				}
				if (district != null && district.length() > 0) {
					sql.append(" AND r.district=?");
					params.add(district);
				}
				if (chainId != null) {
					sql.append(" AND r.chain_id=?");
					params.add(chainId);
				}
			}

			List<Map<String, Object>> resultList = daoUtil.orderMgntDao.queryMapList(sql.toString(), params.toArray());

			// 价格和封面图片
			if (resultList != null && resultList.size() > 0) {
				int sz = resultList.size();
				for (int i = 0; i < sz; i++) {
					Map<String, Object> tmp = resultList.get(i);
					int roomId = (int) tmp.get("roomId");
					// 封面
					XbPicture picture = daoUtil.pictureDao.findCoverRoomPicByRoomId(roomId);
					if (picture == null) {
						List<XbPicture> listPics = daoUtil.pictureDao.findRoomPicByRoomId(roomId);
						if (listPics != null && listPics.size() > 0) {
							picture = listPics.get(0);
						}
					}
					tmp.put("coverPicUrl", picture == null || picture.getFilePath() == null ? "" : picture.getFilePath());
				}
			}
			return resultList;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 查询某个房间的详情
	 * 
	 * @param roomId
	 * @return
	 */
	public Map<String, Object> queryRoomDetail(int roomId) {
		Map<String, Object> subMap = new HashMap<>();
		try {
			// 房间信息
			XbRoom roomInfo = daoUtil.roomMgntDao.findValidByPk(roomId);
			AssertHelper.notNull(roomInfo);

			// 设施信息,列出所有,并且标识哪些是房间具有的
			List<XbFacility> baseFaci = daoUtil.roomFacilityRelDao.findValidFacilityByRoomId(roomId, AppConstants.Facility_facilityType.BASE_FACILITY_1);
			List<XbFacility> provServ = daoUtil.roomFacilityRelDao.findValidFacilityByRoomId(roomId, AppConstants.Facility_facilityType.PROV_SERVICE_2);

			// 额外信息,一般是单个
			List<XbAddition> additionInfos = daoUtil.additionDao.findByRoomId(roomId);

			// 图片
			List<XbPicture> pictures = daoUtil.pictureDao.findRoomPicByRoomId(roomId);
			List<XbPicture> naviPic = daoUtil.pictureDao.findNaviPicByRoomId(roomId);

			// 当天价格信息
			XbCalendarDaily xbCalendarDaily = daoUtil.calendarDailyDao.findEntityByRoomIdAndCurDate(roomId, DateUtil.getCurDateStr());

			// 返回值
			subMap.put("roomInfo", roomInfo);
			subMap.put("baseFaci", baseFaci);
			subMap.put("curDatePrice", xbCalendarDaily == null ? 0 : xbCalendarDaily.getPrice());
			subMap.put("provServ", provServ);
			subMap.put("additionInfos", additionInfos);
			subMap.put("pictures", pictures);
			subMap.put("naviPic", naviPic);

			return subMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	@Transactional
	public Map<String, Object> bookRoom2(int lodgerId, int roomId, int payType, String beginDateStr, String endDateStr, int totalPriceFromWebCentUnit,Integer discountPriceFromWebCentUnit,
			XbInvoiceRec invoice, int source, List<Map<String, String>> checkIns, String cardCode) {
		String checkInUserJson = JSON.toJSONString(checkIns);
		String defaultCheckInName = null;
		String defaultCheckInMobile = null;

		exceptionHandler.getLog().info("新建订单入参:" + lodgerId + ",roomId:" + roomId + ",beginDateStr:" + beginDateStr + "," + endDateStr + ",source:"
				+ source + ",payType:" + payType + ",totalPriceFromWebCentUnit:" + totalPriceFromWebCentUnit + "checkInData" + checkInUserJson);
		try {
			// 【1】 检查业务是否允许
			if (!(payType == AppConstants.Order_payType.WECHAT_1 || payType == AppConstants.Order_payType.UNKNOW_2)) {
				throw exceptionHandler.newErrorCodeException("-4", "传参错误");
			}
			if (!(source == AppConstants.Order_source.WECHAT_0 || source == AppConstants.Order_source.QUNAR_1
					|| source == AppConstants.Order_source.IOS_2 || source == AppConstants.Order_source.ANDROID_3
					|| source == AppConstants.Order_source.OMS_4 || source == AppConstants.Order_source.CTRIP_5
					|| source == AppConstants.Order_source.OWNER_6)) {
				throw exceptionHandler.newErrorCodeException("-4", "传参错误");
			}
			if (StringUtils.isNotBlank(cardCode) && discountPriceFromWebCentUnit == null) {
				throw exceptionHandler.newErrorCodeException("-4", "传参错误");
			}
			if (checkIns.size() == 0) {
				throw exceptionHandler.newErrorCodeException("-4", "传参错误");
			}
			// 验证手机号码格式, 不允许有重复手机号码的情况
			for (Map<String, String> map : checkIns) {
				String mobile = map.get("checkInMobile");
				if (StringUtils.isBlank(mobile) || StringUtils.isBlank(map.get("checkInName")) || mobile.length() != 11 || !mobile.matches("\\d*")) {
					throw exceptionHandler.newErrorCodeException("-4", "传参错误");//入住人参数错误
				}
				for (Map<String, String> map1 : checkIns) {
					if (!map.equals(map1) && map.get("checkInMobile").equals(map1.get("checkInMobile"))) {
						throw exceptionHandler.newErrorCodeException("-4", "传参错误");//入住人手机号码不能相同
					}
				}
			}
			// 将前端传入的日期转换成要发给去呼呼的带有时间的形式
			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(beginDateStr, endDateStr);
			Date checkinTime = result.get("checkinTime");
			Date checkoutTime = result.get("checkoutTime");

			// 判断是否能预定,判断价格是否有变动
			exceptionHandler.getLog().info("检查是否  房间被占或实付价格传错,或优惠额度传错");
			String orderNo = RandomUtil.getBookOrderNo();
			exceptionHandler.getLog().info("准备产生订单:" + orderNo);
			Map<String,Object> resultMap = orderMgntCommon.lockCouponCard(orderNo, lodgerId, cardCode, checkinTime, checkoutTime, roomId, discountPriceFromWebCentUnit == null ? 0 : discountPriceFromWebCentUnit, totalPriceFromWebCentUnit);
//			int retcode = (int) resultMap.get("retcode");
//			String retmsg = (String) resultMap.get("retmsg");
//			if (retcode != 1) {
//				throw exceptionHandler.newErrorCodeException(retcode+"", retmsg);//"【卡券系统】房间被占或实付价格传错,或优惠额度传错"
//			}
			
			exceptionHandler.getLog().info("检查卡券、实付、优惠等完毕:" + orderNo);
			
			defaultCheckInName = checkIns.get(0).get("checkInName");
			defaultCheckInMobile = checkIns.get(0).get("checkInMobile");

			
			
			// 【2】可以预定
			// 如果是测试帐号就把价格改成1块钱// 事实上该非业务的逻辑不应该这么侵入正常的业务逻辑,有其他更好的方法么?
			XbLodger loginLodger = daoUtil.lodgerDao.findByPk(lodgerId);
			AssertHelper.notNull(loginLodger, "不存在该用户,lodgerId:" + lodgerId);

			boolean isTestAcct = orderMgntCommon.isTestAccount(loginLodger.getMobile());
			int realPriceCentUnit = totalPriceFromWebCentUnit;
			int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
			if (isTestAcct) {
				realPriceCentUnit = 100;// 1元
				orderType = AppConstants.Order_orderType.TEST_5;
			}

			// 判断是否要创建入住人
			int checkInLodgerId = loginLodger.getLodgerId();
			for (Map<String, String> map : checkIns) {
				Integer userId = lodgerService.findByMobile(map.get("checkInMobile").trim());
				if (userId == null) {
					exceptionHandler.getLog()
							.info(String.format("订单需要创建新的入住人,checkInMobile: %s,checkInName: %s", map.get("checkInMobile"), map.get("checkInName")));
					Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(map.get("checkInMobile").trim(),
							map.get("checkInName").trim(), "", AppConstants.Lodger_source.BOOK_1,  RandomUtil.getRandomStr(6), AppConstants.Lodger_hasChgPwd.NOT_YET_1,
							true);
					userId = (int) lodgerMap.get("lodgerId");
					boolean isNewCreate = (boolean) lodgerMap.get("isNewCreate");
				}
				if (map.get("checkInMobile").equals(defaultCheckInMobile)) {
					checkInLodgerId = userId;
				}
				map.put("checkInUserId", userId.toString());
			}

			int stat = AppConstants.Order_stat.NEW_0;
			String otaOrderNo = null;
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, realPriceCentUnit, loginLodger.getLodgerId(),
					loginLodger.getLodgerName(), loginLodger.getMobile(), checkinTime, checkoutTime, otaOrderNo, discountPriceFromWebCentUnit);
			orderInput.setOrderNo(orderNo);// 由外部产生
			List<CheckinInput> checkinInputList = new ArrayList<>();
			int chkinPriceCentUnit = realPriceCentUnit;
			String otaStayId = null;

			CheckinInput checkinInput = new CheckinInput(roomId, checkInLodgerId, defaultCheckInName, defaultCheckInMobile, checkinTime, checkoutTime,
					chkinPriceCentUnit, otaStayId);
			checkinInput.setSaveXbCheckiner(false);// 不要保存xb_checkiner,下面会手动保存
			checkinInputList.add(checkinInput);

			// 保存邮寄发票信息
			Invoice invoiceInfo = null;// 无需发票
			if (invoice != null) {
				invoiceInfo = new Invoice(invoice.getInvoiceTitle(), invoice.getMailName(), invoice.getContactNo(), invoice.getMailAddr(),
						invoice.getPostCode(), realPriceCentUnit);
			}
			
			// 新建订单(★)
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, FromType.FROM_LOCAL);
			int orderId = out.getOrderInfo().getOrderId();
			int checkinId = out.getNewCheckinList().get(0).getCheckinInfo().getCheckinId();

			// 依次填充入住人信息
			XbCheckiner checkiner = null;
			for (Map<String, String> map : checkIns) {
				checkiner = new XbCheckiner();
				checkiner.setCheckinId(checkinId);
				checkiner.setLodgerId(Integer.valueOf(map.get("checkInUserId")));
				checkiner.setMobile(map.get("checkInMobile"));
				checkiner.setName(map.get("checkInName"));
				checkiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);// 发门锁密码
				checkiner.setRemark("办理入住,订单阶段创建");
				checkiner.setCreateDtm(DateUtil.getCurDateTime());
				daoUtil.checkinerDao.addAndGetPk(checkiner);
			}
			checkiner = new XbCheckiner();
			checkiner.setCheckinId(checkinId);
			checkiner.setLodgerId(out.getOrderInfo().getLodgerId());
			checkiner.setMobile(out.getOrderInfo().getLodgerMobile());
			checkiner.setName(out.getOrderInfo().getLodgerName());
			checkiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);// 发门锁密码
			checkiner.setRemark("下订单人,冗余数据");
			checkiner.setIsMain(-1);
			checkiner.setCreateDtm(DateUtil.getCurDateTime());
			daoUtil.checkinerDao.addAndGetPk(checkiner);

			
			Map<String, Object> valueMap = new HashMap<>();
			valueMap.put("orderInfo", out.getOrderInfo());
			return valueMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	// 用户预定天数包括开始日期不包括结束日期(日期格式2015-10-13)
	public Map<String, Integer> calculateOrderPrice(int roomId, String beginDateStr, String endDateStr, String cardCode, int lodgerId) {
		try {

			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(beginDateStr, endDateStr);
			Date checkinTime = result.get("checkinTime");
			Date checkoutTime = result.get("checkoutTime");
			Map<String, Object> resultMap = couponCardTool.calculateOrderPrice(checkinTime, checkoutTime, roomId, cardCode, lodgerId);
			Map<String, Object> data = (Map<String, Object>) resultMap.get("retval");
			Map<String, Integer> map = new HashMap<>();
			map.put("actualPrice", (Integer) data.get("actualPrice"));
			map.put("discountPrice", (Integer) data.get("discountPrice"));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 支付成功接口
	public void paySuccess(String orderNo,String platformSn) {
		boolean flag = true;
		int orderId = -1;
		int paymentId = -1;
		String errorMsg = "";
		try {
			exceptionHandler.getLog().info("支付成功,orderNo[" + orderNo + "]");
			XbOrder orderInfo = daoUtil.orderMgntDao.findByOrderNo(orderNo);
			orderId = orderInfo.getOrderId();
			if (orderInfo.getStat() == AppConstants.Order_stat.NEW_0) {
				exceptionHandler.getLog().info(String.format("支付成功,订单编号:%s", orderNo));

				// 增加xb_payment表
				paymentId = daoUtil.paymentDao.updateByPlatformSnAndGetPk(platformSn);
				orderMgntCommon.transOrder2PaidStatus(orderInfo.getOrderId());
				maintainModuleBase.markHasSendBookSms(orderInfo.getOrderId());

				// 查询关联的卡ID,并核销(有的话)
				orderMgntCommon.destroyCouponCard(orderNo, orderInfo.getLodgerId());

				eventService.throwBookAfterPaySuccEvent(orderInfo.getOrderId());// 抛事件
			}
		} catch (Exception e) {
			flag = false;
			errorMsg = e.getMessage();
			throw exceptionHandler.logServiceException(e);
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logPaySucc("调用支付成功接口", paymentId, orderId, succFlag, errorMsg);
		}
	}


	/**
	 * 返回-1必须在当天才能退房 -2订单或入住单状态异常 -3别的用户尝试退房别人的入住单 ★目前只有第一个入住人才可以办理退房
	 * 
	 * @param checkinId
	 * @param roomId
	 * @param checkinLodgerId
	 *            退房的人，只有入住者才可以退
	 * @return
	 */
	public void checkout(int checkinId) {
		exceptionHandler.getLog().info("客户主动办理退房,checkinId:" + checkinId);
		try {
			// 检查能否退房
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			AssertHelper.notNull("数据错误", checkinInfo, orderInfo);

			int orderStat = orderInfo.getStat();
			int checkinStat = checkinInfo.getStat();
			if (orderStat != AppConstants.Order_stat.PAYED_1 || checkinStat != AppConstants.Checkin_stat.CHECKIN_1) {// 状态不对不能退
				throw exceptionHandler.newErrorCodeException("-3", "订单或入住单状态异常,orderStat:" + orderStat + ",checkinStat:" + checkinStat);
			}
			if (orderMgntCommon.hasPaidOrUnpaidOverstay(checkinInfo)) {

				throw orderStat == AppConstants.Order_stat.PAYED_1
						? exceptionHandler.newErrorCodeException("-5", "该单不能手动退房,有已支付的续住单。checkinId:" + checkinId + ",orderStat:" + orderStat)
						: exceptionHandler.newErrorCodeException("-7", "该单不能手动退房,有未支付的续住单。checkinId:" + checkinId + ",orderStat:" + orderStat);
			}
			
			
			// 判断该住户退的是不是他的单【多入住人修改】
//			Integer ch = checkinInfo.getLodgerId();
//			if (checkinLodgerId != ch.intValue()) {
//				throw exceptionHandler.newErrorCodeException("-4", "该入住单不能由该用户退房");
//			}
			// 办理退房(同步)
			exceptionHandler.getLog().info("办理退房 BEGIN...");
			int checkoutType = AppConstants.Checkin_checkoutType.MANUAL_CHECKOUT_1;
			orderUtil.checkout(checkinId, checkoutType, CheckoutType.ACTIVE);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	
	public Map<String, Object> checkinForOverstay(String jsonStr) {
		Map<String, Object> retMap = new HashMap<>();
		try {
			exceptionHandler.getLog().info("======================续住单办理入住 begin======================");
			
			
			exceptionHandler.getLog().info("办理入住:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			exceptionHandler.getLog().info("办理入住,checkinId:" + checkinId);
			JSONObject checkiner = entity.getJSONObject("checkiner");
			AssertHelper.notNull("入参错误", checkiner);
			JSONObject main = checkiner.getJSONObject("main");
			AssertHelper.notNull("传参错误", checkinId, main);
			JSONArray other = checkiner.getJSONArray("other");// 可选

			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			Date now = DateUtil.getCurDateTime();
			
			// 检查能否办理入住
			if (DateUtil.compareDate(now, checkinInfo.getCheckinTime()) < 0) {
				throw exceptionHandler.newErrorCodeException("-1", "未到办理入住的时间");
			}
			if (DateUtil.compareDate(now, checkinInfo.getCheckoutTime()) > 0) {
				throw exceptionHandler.newErrorCodeException("-4", "过期的单不能入住");
			}
			if (checkinInfo.getStat() != AppConstants.Checkin_stat.NEW_0) {
				throw exceptionHandler.newErrorCodeException("-2", "入住单不是可以办理入住的状态");
			}
			if (orderInfo.getStat() != AppConstants.Order_stat.PAYED_1) {
				throw exceptionHandler.newErrorCodeException("-3", "未支付订单不能入住");
			}
			// 这个不要阻止,原房间是已入住,也可以办理入住
//			if (!orderMgntCommon.isCleanRoom(checkinInfo.getRoomId())) {
//				throw exceptionHandler.newErrorCodeException("-5", "该房间还在清洁中,暂不能办理入住");
//			}
			if (checkinInfo.getChgRoomFlag() == AppConstants.Checkin_chgRoomFlag.ORI_ROOM_1) {
				throw exceptionHandler.newErrorCodeException("-6", "原换房订单不能操作");
			}

			String mainMobile = main.getString("mobile");
			String mainName = main.getString("name");
			String mainIdcardNo = main.getString("idcardNo");
			XbLodger mainLodger = daoUtil.lodgerDao.findByMobile(mainMobile);// 主入住人
			AssertHelper.notNull("该手机号没用户信息mobile[" + mainMobile + "]", mainLodger);
			Integer mainDoesSendPin = main.getInteger("doesSendPin");
			String cardPicName = null;//上传身份证名称
			
			// 保存主入住人的身份证号
			daoUtil.lodgerDao.updateEntityByPk(new XbLodger(mainLodger.getLodgerId(), mainIdcardNo));

			// 将订单的入住人改一下。改为主入住人的信息
			checkinInfo.setLodgerName(mainName);
			checkinInfo.setContactPhone(mainMobile);
			checkinInfo.setLodgerId(mainLodger.getLodgerId());
			daoUtil.checkinDao.updateEntityByPk(checkinInfo);
			if(StringUtils.isNotBlank(main.getString("idCardPic"))){
				cardPicName = lodgerService.uploadIdCardPic(mainLodger.getLodgerId(), "1".equals(main.getString("idCardPicType")) ? IdCardType.FRONT : IdCardType.BACK, main.getString("idCardPic"), mainIdcardNo);
			}
			// 保存主入住人登记信息
			List<XbCheckiner> tmpCheckiners = daoUtil.checkinerDao.findByCheckinIdAndByMobile(checkinInfo.getCheckinId(), mainMobile);
			AssertHelper.notEmpty(tmpCheckiners, "主入住人订单未找到");
			XbCheckiner mainCheckiner = tmpCheckiners.get(0);
			mainCheckiner.setIdcardNo(mainIdcardNo);
			int mainSendFlag = mainDoesSendPin == null || mainDoesSendPin == 1 ? AppConstants.Checkiner_sendFlag.SEND_1
					: AppConstants.Checkiner_sendFlag.NOT_SEND_2;
			mainCheckiner.setSendFlag(mainSendFlag);
			mainCheckiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);
			daoUtil.checkinerDao.updateEntityByPk(mainCheckiner);

			String lockPassword = null;
			exceptionHandler.getLog().info("办理入住,发去呼呼办理入住 begin=============================");
			try {
				lockPassword = orderUtil.checkin(checkinId, null);
			} catch (Exception e) {
				throw exceptionHandler.newErrorCodeException("-7", "去呼呼办理入住失败");
			}
			exceptionHandler.getLog().info("办理入住,发去呼呼办理入住 end=============================");


			// 保存从入住人登记信息(入住的时候添加的) 因为要发送短信，所以放到去呼呼后面执行
			this.saveOtherCheckiner(other, checkinInfo.getCheckinId());

			// 找出订单未填写身份证的数据，标记为删除
			List<String> mobiles = new ArrayList<String>();
			mobiles.add(mainMobile);
			if(other != null){
				for (int i = 0; i < other.size(); i++) {
					JSONObject tmp = other.getJSONObject(i);
					if (StringUtils.isNotBlank(tmp.getString("mobile"))) {
						mobiles.add(tmp.getString("mobile"));
					}
				}
			}
			// 续住的 不需要此逻辑
//			List<XbCheckiner> delCheckiners = daoUtil.checkinerDao.findByCheckinIdAndByNotMobile(checkinInfo.getCheckinId(), mobiles);
//			for (XbCheckiner otherCheckiner : delCheckiners) {
//				if(otherCheckiner.getIsMain() != -1){
//					otherCheckiner.setIsDel(AppConstants.Checkiner_isDel.DEL);
//					daoUtil.checkinerDao.updateEntityByPk(otherCheckiner);
//				}
//			}

			exceptionHandler.getLog().info("办理入住,准备抛事件,checkinId:" + checkinId);
			eventService.throwCheckinHintEvent(checkinInfo.getCheckinId());
			maintainModuleBase.markHasSendCheckinSms(checkinInfo.getCheckinId());
			
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("gateLockPassword", lockPassword);// 门锁密码
			subMap.put("cardPicName", cardPicName);
			retMap.put("retcode", "1");
			retMap.put("retval", subMap);
			
			
			exceptionHandler.getLog().info("======================续住单办理入住 end======================");
			return retMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	// 兼容下,以前的代码就不改了
	public Map<String, Object> checkin(CheckinInputVo checkinInputVo) {
		return this.checkinForOverstay(JsonHelper.toJSONString(checkinInputVo));
	}
	// 
	public Map<String, Object> checkin(String jsonStr) {
		Map<String, Object> retMap = new HashMap<>();
		try {
			exceptionHandler.getLog().info("办理入住:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			exceptionHandler.getLog().info("办理入住,checkinId:" + checkinId);
			JSONObject checkiner = entity.getJSONObject("checkiner");
			AssertHelper.notNull("入参错误", checkiner);
			JSONObject main = checkiner.getJSONObject("main");
			AssertHelper.notNull("传参错误", checkinId, main);
			JSONArray other = checkiner.getJSONArray("other");// 可选

			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			Date now = DateUtil.getCurDateTime();
			
			// 检查能否办理入住
			if (DateUtil.compareDate(now, checkinInfo.getCheckinTime()) < 0) {
				throw exceptionHandler.newErrorCodeException("-1", "未到办理入住的时间");
			}
			if (DateUtil.compareDate(now, checkinInfo.getCheckoutTime()) > 0) {
				throw exceptionHandler.newErrorCodeException("-4", "过期的单不能入住");
			}
			if (checkinInfo.getStat() != AppConstants.Checkin_stat.NEW_0) {
				throw exceptionHandler.newErrorCodeException("-2", "入住单不是可以办理入住的状态");
			}
			if (orderInfo.getStat() != AppConstants.Order_stat.PAYED_1) {
				throw exceptionHandler.newErrorCodeException("-3", "未支付订单不能入住");
			}
			if (!orderMgntCommon.isCleanRoom(checkinInfo.getRoomId())) {
				throw exceptionHandler.newErrorCodeException("-5", "该房间还在清洁中,暂不能办理入住");
			}
			if (checkinInfo.getChgRoomFlag() == AppConstants.Checkin_chgRoomFlag.ORI_ROOM_1) {
				throw exceptionHandler.newErrorCodeException("-6", "原换房订单不能操作");
			}

			String mainMobile = main.getString("mobile");
			String mainName = main.getString("name");
			String mainIdcardNo = main.getString("idcardNo");
			XbLodger mainLodger = daoUtil.lodgerDao.findByMobile(mainMobile);// 主入住人
			AssertHelper.notNull("该手机号没用户信息mobile[" + mainMobile + "]", mainLodger);
			Integer mainDoesSendPin = main.getInteger("doesSendPin");
			String cardPicName = null;//上传身份证名称
			
			// 保存主入住人的身份证号
			daoUtil.lodgerDao.updateEntityByPk(new XbLodger(mainLodger.getLodgerId(), mainIdcardNo));

			// 将订单的入住人改一下。改为主入住人的信息
			checkinInfo.setLodgerName(mainName);
			checkinInfo.setContactPhone(mainMobile);
			checkinInfo.setLodgerId(mainLodger.getLodgerId());
			daoUtil.checkinDao.updateEntityByPk(checkinInfo);
			if(StringUtils.isNotBlank(main.getString("idCardPic"))){
				cardPicName = lodgerService.uploadIdCardPic(mainLodger.getLodgerId(), "1".equals(main.getString("idCardPicType")) ? IdCardType.FRONT : IdCardType.BACK, main.getString("idCardPic"), mainIdcardNo);
			}
			// 保存主入住人登记信息
			List<XbCheckiner> tmpCheckiners = daoUtil.checkinerDao.findByCheckinIdAndByMobile(checkinInfo.getCheckinId(), mainMobile);
			AssertHelper.notEmpty(tmpCheckiners, "主入住人订单未找到");
			XbCheckiner mainCheckiner = tmpCheckiners.get(0);
			mainCheckiner.setIdcardNo(mainIdcardNo);
			int mainSendFlag = mainDoesSendPin == null || mainDoesSendPin == 1 ? AppConstants.Checkiner_sendFlag.SEND_1
					: AppConstants.Checkiner_sendFlag.NOT_SEND_2;
			mainCheckiner.setSendFlag(mainSendFlag);
			mainCheckiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);
			daoUtil.checkinerDao.updateEntityByPk(mainCheckiner);

			String lockPassword = null;
			exceptionHandler.getLog().info("办理入住,发去呼呼办理入住 begin=============================");
			try {
				lockPassword = orderUtil.checkin(checkinId, null);
			} catch (Exception e) {
				throw exceptionHandler.newErrorCodeException("-7", "去呼呼办理入住失败");
			}
			exceptionHandler.getLog().info("办理入住,发去呼呼办理入住 end=============================");


			// 保存从入住人登记信息(入住的时候添加的) 因为要发送短信，所以放到去呼呼后面执行
			this.saveOtherCheckiner(other, checkinInfo.getCheckinId());

			// 找出订单未填写身份证的数据，标记为删除
			List<String> mobiles = new ArrayList<String>();
			mobiles.add(mainMobile);
			if(other != null){
				for (int i = 0; i < other.size(); i++) {
					JSONObject tmp = other.getJSONObject(i);
					if (StringUtils.isNotBlank(tmp.getString("mobile"))) {
						mobiles.add(tmp.getString("mobile"));
					}
				}
			}
			List<XbCheckiner> delCheckiners = daoUtil.checkinerDao.findByCheckinIdAndByNotMobile(checkinInfo.getCheckinId(), mobiles);
			for (XbCheckiner otherCheckiner : delCheckiners) {
				if(otherCheckiner.getIsMain() != -1){
					otherCheckiner.setIsDel(AppConstants.Checkiner_isDel.DEL);
					daoUtil.checkinerDao.updateEntityByPk(otherCheckiner);
				}
			}

			exceptionHandler.getLog().info("办理入住,准备抛事件,checkinId:" + checkinId);
			eventService.throwCheckinHintEvent(checkinInfo.getCheckinId());
			maintainModuleBase.markHasSendCheckinSms(checkinInfo.getCheckinId());
			
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("gateLockPassword", lockPassword);// 门锁密码
			subMap.put("cardPicName", cardPicName);
			retMap.put("retcode", "1");
			retMap.put("retval", subMap);
			return retMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 为入住订单添加入住人信息
	 * 
	 * @return
	 */
	public Map<String, Object> appendToCheckiner(String jsonStr) {
		try {
			exceptionHandler.getLog().info("额外添加入住:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			exceptionHandler.getLog().info("额外添加入住,checkinId:" + checkinId);
			JSONArray other = entity.getJSONArray("other");
			AssertHelper.notNull("找不到额外添加入住人信息", other);

			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);

			int roomState = roomStateTool.queryCurentRoomStat(checkinInfo.getRoomId());
			if (roomState != AppConstants.RoomState_stat.CHECKIN_3) {
				throw exceptionHandler.newErrorCodeException("-1", "房间处于非入住状态,暂不能添加入住人");
			}
			// 判断当前状态必须是入住状态。才可以添加入住人信息
			saveOtherCheckiner(other, checkinInfo.getCheckinId());

			Map<String, Object> retMap = new HashMap<>();
			retMap.put("retcode", "1");
			return retMap;	
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 删除入住人
	 * 
	 * @param jsonStr
	 * @return
	 */
	public Map<String, Object> deleteCheckiner(String jsonStr) {
		try {
			exceptionHandler.getLog().info("删除入住人:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			String mobile = entity.getString("mobile");
			AssertHelper.notNull("找不到删除入住人电话", mobile);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			int roomState = roomStateTool.queryCurentRoomStat(checkinInfo.getRoomId());
			if (roomState != AppConstants.RoomState_stat.CHECKIN_3) {
				throw exceptionHandler.newErrorCodeException("-1", "房间处于非入住状态,暂不能删除入住人");
			}
			List<XbCheckiner> lists = daoUtil.checkinerDao.findByCheckinIdAndByMobile(checkinId, mobile);
			if (lists == null || lists.size() == 0) {
				throw exceptionHandler.newErrorCodeException("-1", "未找到删除入住人单");
			}
			XbCheckiner checkiner = lists.get(0);
			if (checkiner.getIsMain() == 1) {
				throw exceptionHandler.newErrorCodeException("-1", "主入住人不允许被删除");
			}
			checkiner.setIsDel(AppConstants.Checkiner_isDel.DEL);

			Map<String, Object> retMap = new HashMap<>();
			retMap.put("retcode", "-1");
			if (daoUtil.checkinerDao.updateEntityByPk(checkiner) > 0) {
				retMap.put("retcode", "1");
			}
			return retMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 保存多个入住人信息 可能存在的情况 1、正常添加 2、修改身份证或其它信息 3、删除后再次添加
	 */
	private void saveOtherCheckiner(JSONArray checkiners, int checkinId) {
		if (checkiners != null && checkiners.size() > 0) {
			for (int i = 0; i < checkiners.size(); i++) {
				JSONObject tmp = checkiners.getJSONObject(i);
				String name = tmp.getString("name");
				String mobile = tmp.getString("mobile");
				String idcardNo = tmp.getString("idcardNo");
				boolean doesSendPin = String.valueOf(AppConstants.Checkiner_sendFlag.SEND_1).equals(tmp.getString("doesSendPin"));

				int sendFlag = doesSendPin ? AppConstants.Checkiner_sendFlag.SEND_1 : AppConstants.Checkiner_sendFlag.NOT_SEND_2;
				// 如果是未注册的用户，执行注册用户操作
				String randomPwd = RandomUtil.getRandomStr(6);
				Map<String, Object> result = commonService.judgeIfNeedCreateLodger(mobile, name, "", AppConstants.Lodger_source.CHECKIN_3, randomPwd,
						AppConstants.Lodger_hasChgPwd.NOT_YET_1, true);
				int otherLodgerId = (int) result.get("lodgerId");
				boolean isNewCreate = (boolean) result.get("isNewCreate");

				// 查询XbCheckiner对象是否存在
				XbCheckiner checkiner = null;
				List<XbCheckiner> lists = daoUtil.checkinerDao.findByCheckinIdAndByMobile(checkinId, mobile);
				if (lists != null && lists.size() > 0) {
					checkiner = lists.get(0);
				}
				// 修改用户的身份证
				daoUtil.lodgerDao.updateEntityByPk(new XbLodger(otherLodgerId, idcardNo));

				if (checkiner == null) {
					checkiner = new XbCheckiner();
					checkiner.setCheckinId(checkinId);
					checkiner.setCreateDtm(new Date());
					checkiner.setIdcardNo(idcardNo);
					checkiner.setLodgerId(otherLodgerId);
					checkiner.setMobile(mobile);
					checkiner.setName(name);
					checkiner.setRemark("办理入住,从住户[办理入住阶段添加]");
					checkiner.setSendFlag(sendFlag);
					daoUtil.checkinerDao.addAndGetPk(checkiner);
				} else {
					checkiner.setName(name);
					checkiner.setIdcardNo(idcardNo);
					checkiner.setSendFlag(sendFlag);
					checkiner.setIsDel(AppConstants.Checkiner_isDel.NORMAL);
					daoUtil.checkinerDao.updateEntityByPk(checkiner);
				}
				if(StringUtils.isNotBlank(tmp.getString("idCardPic"))){
					lodgerService.uploadIdCardPic(otherLodgerId, "1".equals(tmp.getString("idCardPicType")) ? IdCardType.FRONT : IdCardType.BACK, tmp.getString("idCardPic"), idcardNo);
				}
			}
		}
	}

	// 【我的房间】
	public List<Map<String, Object>> queryCheckinList(int lodgerId) {// 入住人的lodgerId,注意下单人不能查出
		Date now = DateUtil.getCurDateTime();
		try {
			// 查xb_order和xb_checkin
			String sql = "SELECT a.total_price totalPrice,b.chkin_price chkinPrice,a.create_time orderCreateTime, b.room_id AS roomId,a.order_id AS orderId,b.checkin_id AS checkinId,a.order_no AS orderNo,a.stat AS orderStat,b.stat AS checkinStat,"
					+ "b.checkin_time AS checkinTime,b.checkout_time AS checkoutTime,a.night_count AS nightCount,b.lodger_id AS checkinLodgerId,"
					+ "a.lodger_id AS bookLodgerId FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND a.stat=1 AND b.stat IN (0,1,2) AND b.lodger_id=? ORDER BY b.checkin_time ASC";
			List<Map<String, Object>> resultList = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { lodgerId });
			if (!resultList.isEmpty()) {
				int sz = resultList.size();
				for (int i = 0; i < sz; i++) {
					Map<String, Object> unit = resultList.get(i);
					// 可办理入住,已入住,暂不能入住
					Date checkinTime = (Date) unit.get("checkinTime");
					Date checkoutTime = (Date) unit.get("checkoutTime");
					int checkinStat = (int) unit.get("checkinStat");
					int roomId = (int) unit.get("roomId");
					if (checkinStat == AppConstants.Checkin_stat.CHECKIN_1) {
						unit.put("status", 2);// 已入住
					} else if (checkinStat == AppConstants.Checkin_stat.CHECKOUT_2 || DateUtil.compareDateTime(now, checkoutTime) > 0) {
						unit.put("status", 4);// 已退房
					} else {
						int dateCompare = DateUtil.compareDate(now, checkinTime);
						if (dateCompare > 0) {// 理论上当天或推后都允许办理入住
							unit.put("status", 1);// 可办理入住
						} else if (dateCompare == 0) {
							int compareTime = DateUtil.compareDateTime(now, checkinTime);
							if (compareTime < 0) {
								XbCalendarDaily yestodayCal = daoUtil.calendarDailyDao.findEntityByRoomIdAndCurDate(roomId,
										DateUtil.getYearMonDayStr_(DateUtil.addDays(now, -1)));
								if (yestodayCal != null && yestodayCal.getStat() == AppConstants.CalendarDaily_stat.FREE_1) {// 再细分为:可以在前一天没人住的情况下,提前若干小时办理入住
									unit.put("status", 1);// 可办理入住
								} else {
									unit.put("status", 3);// 暂不能入住
								}
							} else {
								unit.put("status", 1);// 可办理入住
							}
						} else if (dateCompare < 0) {
							unit.put("status", 3);// 暂不能入住
						}
					}
					if ((int) unit.get("status") == 1) {
						if (orderMgntCommon.isDirtyRoom(roomId)) {
							unit.put("status", 5);// 房间还在清洁中;
						}
					}

					// 房间信息
					XbRoom room = daoUtil.roomMgntDao.findByPk(roomId);
					unit.put("roomInfo", room);
					unit.put("coverPic", imageCommon.getCoverPicName(roomId));
					unit.put("chainInfo", daoUtil.chainMgntDao.findByPk(room.getChainId()));
					unit.put("naviPic", imageCommon.getNaviPicName(roomId));
				}
			}
			return resultList.size() == 0 ? null : resultList;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	/**
	 * 查询订单信息
	 * @param lodgerId 用户 ID
	 * @param orderType self 当前订单 overdue过期订单
	 * @return
	 */
	public List<Map<String, Object>> queryLodgerOrders(int lodgerId, String orderType) {
		try {
			if (lodgerId <= 0) {
				throw new RuntimeException("非法入参lodgerId[" + lodgerId + "]");
			}
			List<Map<String, Object>> ordersMap = null;
			if("self".equals(orderType)){
				ordersMap = daoUtil.orderMgntDao.findByLodgerIdSelfOrder(lodgerId);
			}else{
				ordersMap = daoUtil.orderMgntDao.findByLodgerIdOverdueOrder(lodgerId);
			}
			
			List<Map<String, Object>> checkinsMap = null;
			List<Map<String, Object>> checkinersMap = null;
			List<Map<String, Object>> tmp = null;
			for(Map<String, Object> orderMap:ordersMap){
				Integer orderStat = (Integer)orderMap.get("orderStat");
				Integer source = (Integer)orderMap.get("source");
				Integer orderId = (Integer)orderMap.get("orderId");
				// 可否支付 0-不能 1-能
				if (orderStat == AppConstants.Order_stat.PAYED_1
						|| (orderStat == AppConstants.Order_stat.NEW_0 && source == AppConstants.Order_source.QUNAR_1)) {
					orderMap.put("canPay", 0);
				} else {
					orderMap.put("canPay", 1);
				}
				checkinsMap = daoUtil.roomMgntDao.findByOrderId(orderId);
				checkinersMap = daoUtil.checkinerDao.findByOrderId(orderId);
				Integer exitNumber = 0;
				for(Map<String, Object> checkinMap:checkinsMap){
					Date checkoutTime = (Date)checkinMap.get("checkoutTime");
					Date checkinTime = (Date)checkinMap.get("checkinTime");
					Integer roomId = (Integer)checkinMap.get("roomId");
					int checkinId = (int) checkinMap.get("checkinId");
					int overstayFlag = (int) checkinMap.get("overstayFlag");
					Integer checkinStat = (Integer)checkinMap.get("stat");
					checkinMap.put("checkinStatus", this.getRoomStat(roomId,checkinTime,checkoutTime,checkinStat) + (orderMgntCommon.isOverstayCheckin(overstayFlag) ? "2" : ""));//房间当前状态 
					checkinMap.put("coverPicName", imageCommon.getCoverPicName((Integer)checkinMap.get("roomId")));// 封面信息
					tmp = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> checkiner:checkinersMap){
						if(checkiner.get("checkinId").equals(checkinMap.get("checkinId"))){
							tmp.add(checkiner);
						}
					}
					if(checkinMap.get("stat") != null && "2".equals(checkinMap.get("stat").toString())){
						exitNumber += 1;
					}
					checkinMap.put("checkInfos", tmp);
				}
				orderMap.put("roomInfos", checkinsMap);
				orderMap.put("signOut", "overdue".equals(orderType) && orderStat == 1 && exitNumber == checkinsMap.size());//当订单是已支付情况下 才显示退房,并且入住状态
			}
			return ordersMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 返回某个房间的状态
	 * @param roomMap
	 * @return
	 */
	private Integer getRoomStat(int roomId, Date checkinTime, Date checkoutTime, int checkinStat) {
		Date now = new Date();
		if (checkinStat == AppConstants.Checkin_stat.CHECKIN_1){
			return 2;// 已入住
		}
		
		if (checkinStat == AppConstants.Checkin_stat.CHECKOUT_2 || DateUtil.compareDateTime(now, checkoutTime) > 0) {
			return 4;// 已退房
		}
		
		int dateCompare = DateUtil.compareDate(now, checkinTime);
		if (dateCompare < 0) {
			return 3;// 还未到办理入住的日期(例如2016-01-20 14:00:00入住,现在才2016-01-08号,不能办理)
		}
		
		if (dateCompare >= 0) {// 理论上当天或推后都允许办理入住
			if (orderMgntCommon.isCleanRoom(roomId)) {
				return 1;// 可以办理入住
			} else {
				if (orderMgntCommon.isDirtyRoom(roomId)) {
					return 5;// 房间还在清洁中
				} else {
					return -1;// 其他原因不能办理入住
				}
			}
		}
		return -1;
	}
	/**
	 * 暂时废弃
	 * @param lodgerId
	 * @return
	 */
	public List<Map<String, Object>> queryLodgerOrdersBack(int lodgerId) {
		try {
			if (lodgerId <= 0) {
				throw new RuntimeException("非法入参lodgerId[" + lodgerId + "]");
			}
			// 查xb_order和xb_checkin
			String orderField = SqlCreatorTool.getSelectFields(XbOrder.class, "xb_order");// status
			String sqlQryOrderInfo = "SELECT " + orderField + ",xb_order.stat orderStat FROM xb_order,(select checkin.order_id from xb_checkin checkin,xb_checkiner checkiner  where checkin.checkin_id=checkiner.checkin_id and checkiner.lodger_id=?) temp WHERE xb_order.order_id=temp.order_id ORDER BY create_time DESC";
			
			// 查入住单的信息
			String roomField = "b.room_id roomId,b.chain_id chainId,b.chain_name chainName,b.chain_addr chainAddr,b.room_type_id roomTypeId,b.room_type_name roomTypeName,b.room_name roomName,b.room_floor roomFloor,"
					+ "b.lodger_count lodgerCount,b.area AREA,b.house_type houseType,b.locate LOCATE,b.province province,b.city city,b.district district,b.addr addr,b.title title";
			String sqlQryChkinNRoomInfo = "SELECT a.checkin_id checkinId,a.order_id orderId,a.lodger_id lodgerId,a.lodger_name lodgerName,a.contact_phone contactPhone,a.stat checkinStat,a.create_time createTime,a.night_count nightCount,a.checkin_time checkinTime,a.checkout_time checkoutTime,"
					+ roomField + " FROM xb_checkin a,xb_room b WHERE a.room_id=b.room_id AND a.order_id=?";
			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sqlQryOrderInfo, new Object[] { lodgerId });
			int resultSize = listMap.size();
			System.out.println("需要处理条数据" + resultSize);
			for (int i = 0; i < resultSize; i++) {
				Map<String, Object> m = listMap.get(i);
				int source = (int) m.get("source");
				int orderStat = (int) m.get("orderStat");
				int orderId = (int) m.get("orderId");
				int invoiceRecId = (int) m.get("invoiceRecId");

				// 可否支付 0-不能 1-能
				if (orderStat == AppConstants.Order_stat.PAYED_1
						|| (orderStat == AppConstants.Order_stat.NEW_0 && source == AppConstants.Order_source.QUNAR_1)) {
					m.put("canPay", 0);
				} else {
					m.put("canPay", 1);
				}

				// 发票信息
				if (invoiceRecId > 0) {
					XbInvoiceRec invoiceInfo = daoUtil.invoiceRecDao.findByPk(invoiceRecId);
					m.put("invoiceInfo", invoiceInfo);
				} else {
					m.put("invoiceInfo", null);
				}

				// 入住和房间信息
				List<Map<String, Object>> checkinAndRoomList = daoUtil.orderMgntDao.queryMapList(sqlQryChkinNRoomInfo, new Object[] { orderId });
				Date now = DateUtil.getCurDateTime();
				for (int j = 0; j < checkinAndRoomList.size(); j++) {
					Map<String, Object> unit = checkinAndRoomList.get(j);
					int checkinStat = (int) unit.get("checkinStat");
					Date checkinTime = (Date) unit.get("checkinTime");
					Date checkoutTime = (Date) unit.get("checkoutTime");
					int roomId = (int) unit.get("roomId");
					int checkinId = (int) unit.get("checkinId");

					// 查
					List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(checkinId);
					unit.put("checkiner", checkinerList);

					if (checkinStat == AppConstants.Checkin_stat.CHECKIN_1) {
						unit.put("checkinStatus", 2);// 已入住
					} else if (checkinStat == AppConstants.Checkin_stat.CHECKOUT_2 || DateUtil.compareDateTime(now, checkoutTime) > 0) {
						unit.put("checkinStatus", 4);// 已退房
					} else {
						int dateCompare = DateUtil.compareDate(now, checkinTime);
						if (dateCompare > 0) {// 理论上当天或推后都允许办理入住
							unit.put("checkinStatus", 1);// 可办理入住
						} else if (dateCompare == 0) {
							int compareTime = DateUtil.compareDateTime(now, checkinTime);
							if (compareTime < 0) {
								XbCalendarDaily yestodayCal = daoUtil.calendarDailyDao.findEntityByRoomIdAndCurDate(roomId,
										DateUtil.getYearMonDayStr_(DateUtil.addDays(now, -1)));
								if (yestodayCal != null && yestodayCal.getStat() == AppConstants.CalendarDaily_stat.FREE_1) {// 再细分为:可以在前一天没人住的情况下,提前若干小时办理入住
									unit.put("checkinStatus", 1);// 可办理入住
								} else {
									unit.put("checkinStatus", 3);// 暂不能入住
								}
							} else {
								unit.put("checkinStatus", 1);// 可办理入住
							}
						} else if (dateCompare < 0) {
							unit.put("checkinStatus", 3);// 暂不能入住
						}
					}
					if ((int) unit.get("checkinStatus") == 1) {
						if (orderMgntCommon.isDirtyRoom(roomId)) {
							unit.put("checkinStatus", 5);// 房间还在清洁中;
						}
					}

					// 封面信息
					String coverPicName = imageCommon.getCoverPicName(roomId);
					unit.put("coverPicName", coverPicName);
				}
				m.put("checkinInfoRoomInfo", checkinAndRoomList);
			}
			return listMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public Map<String, String> queryCheckinInfo(int checkinId, int lodgerId) {
		Date now = DateUtil.getCurDate();
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			if (checkinInfo.getLodgerId() != lodgerId) {
				throw new RuntimeException("该用户无该入住单:lodgerId[" + lodgerId + "],checkinId[" + checkinId + "]");
			}
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			AssertHelper.notNull("数据问题,无lodgerInfo、orderInfo或roomInfo", lodgerInfo, orderInfo, roomInfo);
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			AssertHelper.notNull("数据问题,无chainInfo", chainInfo);

			Map<String, String> map = new LinkedHashMap<>();
			map.put("lodgerId", lodgerInfo.getLodgerId() + "");
			map.put("lodgerName", lodgerInfo.getLodgerName());
			map.put("mobile", lodgerInfo.getMobile());
			map.put("orderId", orderInfo.getOrderId() + "");
			map.put("orderNo", orderInfo.getOrderNo());
			map.put("orderStat", orderInfo.getStat() + "");
			map.put("checkinId", checkinInfo.getCheckinId() + "");
			map.put("checkinTime", DateUtil.dateToString(checkinInfo.getCheckinTime(), DateUtil.yrMonDayHrMinSec_));
			map.put("checkoutTime", DateUtil.dateToString(checkinInfo.getCheckoutTime(), DateUtil.yrMonDayHrMinSec_));
			map.put("checkinStat", checkinInfo.getStat() + "");
			map.put("roomId", roomInfo.getRoomId() + "");
			map.put("chainId", chainInfo.getChainId() + "");
			map.put("chainName", chainInfo.getName());
			map.put("province", roomInfo.getProvince());
			map.put("city", roomInfo.getCity());
			map.put("district", roomInfo.getDistrict());
			map.put("roomAddr", roomInfo.getAddr());
			map.put("roomNo", roomInfo.getRoomName());
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 根据checkin_id 查询出入住人信息
	 * 
	 * @param checkinId
	 * @return
	 */
	public List<Map<String, Object>> queryXbCheckiner(Integer checkInId) {
		return daoUtil.checkinerDao.findBycheckInId(checkInId);
	}
}
