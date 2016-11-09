package cn.com.xbed.app.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbPicture;
import cn.com.xbed.app.bean.vo.CalEditVo;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.service.OmsUserService;
import cn.com.xbed.app.service.RoomMgntService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/roommgnt")
public class RoomMgntAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomMgntAction.class));
	@Resource
	private RoomMgntService roomMgntService;
	@Resource
	private OmsUserService omsUserService;

	/**
	 * 查询所有的房间·暂时不分页 {}//无需入参  给OMS 上面的列表 {flag:1-全部2-删除,chainId:1,roomId:1,qryPreCheckout:1-查询预退,0-不查(默认)}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> queryRooms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);
			Integer flag = null;
			Integer chainId = null;
			Integer roomId = null;
			Integer qryPreCheckout = null;
			if (StringUtils.isNotBlank(jsonStr) && !jsonStr.equals("null")) {
				JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
				flag = entity.getInteger("flag");
				chainId = entity.getInteger("chainId");
				roomId = entity.getInteger("roomId");
				qryPreCheckout = entity.getInteger("qryPreCheckout");
			}
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryRoomList(flag, chainId, roomId, qryPreCheckout != null && qryPreCheckout == 1 ? true : false));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询当前房态 {roomId:123} 1 干净房 ，2 脏房 ， 3 已入住， 4 停用，5 维修
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querycurroomstat", method = RequestMethod.POST)
	public Map<String, Object> queryCurentRoomStat(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull(roomId, "必传roomId");
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryCurentRoomStat(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询某个经纬度附近多少米内的房间
	 * {location:"经度,纬度",radius:单位米}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/around", method = RequestMethod.POST)
	public Map<String, Object> queryAoundRooms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String location = entity.getString("location");
			Integer radius = entity.getInteger("radius");
			AssertHelper.notNullEmptyBlank("JSON入参错误", location);
			AssertHelper.notNull("JSON入参错误", radius);
			if (!location.contains(",")) {
				throw new RuntimeException("JSON入参错误");
			}
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryRoomListByLocation(location, radius));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * OMS上操作房间状态,删除房间 {roomId:1234}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	public Map<String, Object> delRoom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull("传参错误", roomId);
			
			roomMgntService.delRooms(roomId);
			RestCallHelper.fillSuccessParams(resultMap, null);
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * OMS上操作房间状态,改成恢复状态 {roomId:1234}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/resetroom", method = RequestMethod.POST)
	public Map<String, Object> resetRoom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull("传参错误", roomId);
			
			roomMgntService.resetRoom(roomId);
			RestCallHelper.fillSuccessParams(resultMap, null);
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询房间 {roomId:1234}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public Map<String, Object> queryRoomInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryRoomInfo(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询日历 {roomId:1234,howManyMon:3}---howManyMon参数可选,表示查询包括当前日期所在月份内,总共多少个月份的记录,例如今天2015-08-20,
	 * 3表示查询8月9月10月. 默认是3. version可以不传,不传就是精简格式,传2是简单格式
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/calendar", method = RequestMethod.POST)
	public Map<String, Object> queryCalendar(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");
			String howManyMon = entity.getString("howManyMon");
			int defaultMon = 3;
			if (howManyMon != null && howManyMon.length() > 0) {
				defaultMon = Integer.parseInt(howManyMon);
			}
			// RestCallHelper.fillSuccessParams(resultMap,
			// roomMgntService.queryCalendar2(roomId, defaultMon, true));
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryCalendarV2(roomId, defaultMon, true));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * OMS修改默认价格
	 * {account:"xbed400",password:"0d0cdfc3247673cdb3d300c925d83c22",data:[{roomId:123,date:"2015-09-21",newPrice:38800},{roomId:123,date:"2015-10-21",newPrice:28800}]}// 密码是密文
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/editcalprice", method = RequestMethod.POST)
	public Map<String, Object> editCalendarPrice(HttpServletRequest req, HttpServletResponse resp) {
 		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String account = entity.getString("account");
			String password = entity.getString("password");
			List<CalEditVo> toBeEdited = JsonHelper.parseJSONStr2TList(entity.getString("data"), CalEditVo.class);
			AssertHelper.notNullEmptyBlank("入参错误", account, password);
			AssertHelper.notEmpty(toBeEdited);
			for (int i = 0; i < toBeEdited.size(); i++) {
				CalEditVo t = toBeEdited.get(i);
				AssertHelper.notNull("入参错误", t.getRoomId(), t.getDate(), t.getNewPrice());
			}
			
			// 修改
			roomMgntService.editCalendarPrice(toBeEdited);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * OMS修改日历的状态,禁售或解禁 {account:"xbed400",password:"0d0cdfc3247673cdb3d300c925d83c22",data:[{roomId:123,date:"2015-09-21",statOper:2},{roomId:123,date:"2015-10-21",statOper:2}]}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/editcalstat", method = RequestMethod.POST)
	public Map<String, Object> editCalendarStat(HttpServletRequest req, HttpServletResponse resp) {
 		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String account = entity.getString("account");
			String password = entity.getString("password");
			List<CalEditVo> toBeEdited = JsonHelper.parseJSONStr2TList(entity.getString("data"), CalEditVo.class);
			AssertHelper.notNullEmptyBlank("入参错误", account, password);
			AssertHelper.notEmpty(toBeEdited);
			for (int i = 0; i < toBeEdited.size(); i++) {
				CalEditVo t = toBeEdited.get(i);
				int statOper = t.getStatOper();
				if (statOper != AppConstants.Calendar_stat.FREE_1 && statOper != AppConstants.Calendar_stat.SALE_2
						&& statOper != AppConstants.Calendar_stat.FIX_3) {
					throw new RuntimeException("statOper只能传1、2或者3, 1解锁(可订) 2(预占) 3锁定(禁售)");
				}
				AssertHelper.notNull("入参错误", t.getRoomId(), t.getDate(), t.getStatOper());
			}
			
			// 修改
			roomMgntService.editCalendarStat(toBeEdited);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查所有的区域中心
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querychain", method = RequestMethod.POST)
	public Map<String, Object> queryChainInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryChainInfo());
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询出所有的设施
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryfacility", method = RequestMethod.POST)
	public Map<String, Object> queryFacility(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("baseFacility",
					roomMgntService.queryFacility(AppConstants.Facility_facilityType.BASE_FACILITY_1));
			subMap.put("provService", roomMgntService.queryFacility(AppConstants.Facility_facilityType.PROV_SERVICE_2));
			RestCallHelper.fillSuccessParams(resultMap, subMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * {operType:1-新增,2-修改,roomId:23234,roomInfo:{roomName:"房间名",chainId:123,
	 * roomTypeName:"类型名称",roomTypeId:23,title:"房间个性标题",descri:"房间亮点描述",province
	 * :"省份中文",city:"城市",district:"区",addr:"详细地址",house_type:"x室x厅x厨x卫x阳台",
	 * area:"面积",lodgerCount:"可住人数",bedCount:"床位数",tag:"房间特色标签"},faciInfo:[123,
	 * 23,123],additionInfo:{traffic:"交通路线",guide:"入住指南"}}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/operroom", method = RequestMethod.POST)
	public Map<String, Object> addRoomInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("addRoomInfo新增房间入参: "+jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer operType = entity.getInteger("operType");
			if (operType == null || !(operType == 1 || operType == 2)) {
				RestCallHelper.fillFailParams(resultMap, "必传operType,值必须是1或2");
				return resultMap;
			}

			if (operType == 1) {// 新增
				Map<String, Object> result = roomMgntService.addRoomInfo(jsonStr);
				boolean flag = (boolean) result.get("result");
				if (flag) {
					
					RestCallHelper.fillSuccessParams(resultMap, result.get("roomInfo"));
				} else {
					RestCallHelper.fillFailParams(resultMap);
				}
			} else if (operType == 2) {// 修改
				Integer roomId = entity.getInteger("roomId");
				if (roomId == null) {
					RestCallHelper.fillFailParams(resultMap, "必传roomId");
					return resultMap;
				}
				if (roomMgntService.editRoomInfo(jsonStr)) {
					RestCallHelper.fillSuccessParams(resultMap, null);
				} else {
					RestCallHelper.fillFailParams(resultMap);
				}
			}

		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		exceptionHandler.getLog().info("addRoomInfo新增房间出参: "+JsonHelper.toJSONString(resultMap));
		return resultMap;
	}

	/**
	 * 上传一个不存在的路径会?【抛异常】java.io.FileNotFoundException: K:\t.jpg (系统找不到指定的路径。)
	 * <br>
	 * 上传的文件与目标路径上存在相同文件名会?【替换】<br>
	 * 
	 * {operType:1-新增 2-替换或改标签,picId:修改的时候必传,roomId:1,isCover:0否,1是,tag:"客房",picType:1-房间图片 2-导航图片}  picType可选,默认是1-房间图片
	 * 
	 * @param req
	 * @param resp
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public Object uploadFile(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(name="file_data",required=false) MultipartFile file) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int operType = entity.getIntValue("operType");
			int roomId = entity.getIntValue("roomId");
			int isCover = entity.getIntValue("isCover");
			Integer picId = entity.getInteger("picId");
			Integer picType = entity.getInteger("picType");
			String tag = entity.getString("tag");
			if (!(operType == 1 || operType == 2)) {
				throw new RuntimeException("入参错误");
			}
			
			if (operType == 2 && picId == null) {
				throw new RuntimeException("替换图片或更改TAG必须传原图片ID");
			}
			if (picType != null) {
				if (!(picType == 1 || picType == 2)) {
					throw new RuntimeException("JSON入参错误");
				}
			}
			
			XbPicture pictureInfo = null;
			if (operType == 1) {
				pictureInfo = roomMgntService.uploadFile(roomId, tag, isCover == 0 ? false : true, file, picId, picType, req);
			} else if (operType == 2) {
				if (file != null) {
					pictureInfo = roomMgntService.replaceFile(roomId, tag, isCover == 0 ? false : true, file, picId, picType, req);
				} else {
					pictureInfo = roomMgntService.updatePicTag(picId, tag);
				}
			}
			
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("picId", pictureInfo);
			RestCallHelper.fillSuccessParams(resultMap, subMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException("上传的路径找不到", e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * {picId:123}
	 * 
	 * @param req
	 * @param resp
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/delpic")
	public Map<String, Object> delPic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int picId = entity.getIntValue("picId");
			int ret = roomMgntService.delPic(picId);
			if (ret == -1) {
				RestCallHelper.fillFailParams(resultMap, "删除失败,无此图片");
			} else if (ret == 1) {
				RestCallHelper.fillSuccessParams(resultMap, null);
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException("上传的路径找不到或JSON格式错误", e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询某间房间的图片(非导航图片),首页图排在第一位{roomId:1}  
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/qryroompic")
	public Map<String, Object> queryRoomPic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryRoomPic(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException("上传的路径找不到", e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查导航的图片 {roomId:1}
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/qrynavipic")
	public Map<String, Object> qryNaviPic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");
			RestCallHelper.fillSuccessParams(resultMap, roomMgntService.queryNaviPic(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException("上传的路径找不到", e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
}
