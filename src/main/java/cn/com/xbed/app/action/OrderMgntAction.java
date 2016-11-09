package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.com.xbed.app.bean.XbInvoiceRec;
import cn.com.xbed.app.bean.vo.ChgRoomInputVo;
import cn.com.xbed.app.bean.vo.ChgRoomUnitVo;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.OmsUserService;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.RoomMgntService;
import cn.com.xbed.app.service.chgroom.ChgRoomService;

@Controller
@RequestMapping("/app/ordmgnt")
public class OrderMgntAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderMgntAction.class));
	@Resource
	private OrderMgntService orderMgntService;

	@Resource
	private RoomMgntService roomMgntService;
	
	@Resource
	private LodgerService lodgerService;
	@Resource
	private OmsUserService omsUserService;
	@Resource
	private ChgRoomService chgRoomService;

	/**
	 * 续住  {checkinId:原入住单ID,lodgerId:申请人ID,overstayEndDay:"2016-01-27", source:"新单的渠道", payType:1,actualPrice:26800,discountPrice:8000,cardCode:"xxxxx",invoiceInfo:{invoiceTitle:"广州搜床网络科技有限公司",mailName:"王永锋",contactNo:"13062014179",mailAddr:"广州市荔湾区芳村信义路24号七喜创意园3栋103",postCode:"510640"}}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/overstay", method = RequestMethod.POST)
	public Map<String, Object> overstay(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			Integer lodgerId = entity.getInteger("lodgerId");
			Integer source = entity.getInteger("source");
			Integer payType = entity.getInteger("payType");
			Integer actualPrice = entity.getInteger("actualPrice");
			Integer discountPrice = entity.getInteger("discountPrice");
			String overstayEndDay = entity.getString("overstayEndDay");
			AssertHelper.notNull("JSON传参错误", lodgerId, lodgerId, source, overstayEndDay);
			
			String cardCode = entity.getString("cardCode");
			
			String invoiceStr = entity.getString("invoiceInfo");
			XbInvoiceRec invoice = null;
			if (invoiceStr != null && invoiceStr.length() > 0) {
				invoice = JsonHelper.parseJSONStr2T(invoiceStr, XbInvoiceRec.class);
				AssertHelper.notNullEmptyBlank("传参错误", invoice.getMailName(), invoice.getContactNo(),
						invoice.getInvoiceTitle());
			}

			
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.overstay(checkinId, lodgerId, overstayEndDay, source, payType, actualPrice, discountPrice, cardCode, invoice));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 检查是否能续住 {checkinId:旧入住单的ID,lodgerId:当前申请的用户ID}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkoverstay", method = RequestMethod.POST)
	public Map<String, Object> checkOverstay(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			Integer lodgerId = entity.getInteger("lodgerId");
			AssertHelper.notNull(checkinId, lodgerId, "传参错误");
			
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.checkOverstay(checkinId, lodgerId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 查询发票信息 {invoiceRecId:11,checkinId:112,orderId:1234} 均可选
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryinvoice", method = RequestMethod.POST)
	public Map<String, Object> queryInvoice(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer invoiceRecId = entity.getInteger("invoiceRecId");
			Integer checkinId = entity.getInteger("checkinId");
			Integer orderId = entity.getInteger("orderId");
			
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryInvoice(invoiceRecId, checkinId, orderId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询某个用户的订单 {lodgerId:2, orderType:"self"}  微信端点击个人中心的"我的订单",这里的订单有"待支付"的,可以操作重新支付;有"待入住"的,只是可以看到记录,但不可以办理入住;有"已入住"的,也仅仅能查看记录
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querylodgerorders", method = RequestMethod.POST)
	public Map<String, Object> queryLodgerOrders(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			String orderType = entity.getString("orderType");
			AssertHelper.notNull("JSON传参错误", lodgerId);
			AssertHelper.notNull("JSON传参错误", orderType);
			exceptionHandler.getLog().info(String.format("查询某个用户的订单,lodgerId:%s,orderType:%s", lodgerId, orderType));
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryLodgerOrders(lodgerId, orderType));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询可以办理入住的列表{lodgerId:登录的lodgerId},微信端点击个人中心的"我的房间"调用该方法【作废】
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querycheckin", method = RequestMethod.POST)
	public Map<String, Object> queryCheckinList(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("办理入住，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			AssertHelper.notNull("传参错误", lodgerId);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryCheckinList(lodgerId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询可以换的目标房间列表  {checkinId:原来要换房的入住单}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/destroomlist", method = RequestMethod.POST)
	public Map<String, Object> qryDestRoomList(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			AssertHelper.notNull("JSON传参错误", checkinId);
			Map<String, Object> result = orderMgntService.qryDestRoomList(checkinId);
			int retcode = (int) result.get("retcode");
			if (retcode == 1) {
				RestCallHelper.fillSuccessParams(resultMap, result.get("retval"));
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 换房接口 {chgOperList:[{checkinLodgerId:23,newRoomId:23232,oldCheckinId:123},{checkinLodgerId:23,newRoomId:666,oldCheckinId:665}],account:"xsdfasd"}// account哪个OMS操作员,lodgerId给谁换房,oldCheckinId旧单的checkinId
	 * <br>
	 * checkinLodgerId 没什么卵用
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/chgrooms", method = RequestMethod.POST)
	public Map<String, Object> chgRooms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			ChgRoomInputVo vo = JsonHelper.parseJSONStr2T(jsonStr, ChgRoomInputVo.class);
			String account = vo.getAccount();
			List<ChgRoomUnitVo> chgOperList = vo.getChgOperList();
			AssertHelper.notNull("入参错误", account);
			AssertHelper.notEmpty(chgOperList);
			
			chgRoomService.chgRoom(account, chgOperList);
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
	 * OMS 查询可以换房的订单列表(入住单列表)    {thisPage:1,pageSize:50} 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/canchgroomorderlist", method = RequestMethod.POST)
	public Map<String, Object> qryCanChgRoomOrderList(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer thisPage = entity.getInteger("thisPage");
			Integer pageSize = entity.getInteger("pageSize");
			AssertHelper.notNull("JSON传参错误", thisPage, pageSize);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.qryCanChgRoomOrderList(thisPage, pageSize));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * OMS系统搜索订单信息和入住信息(就是类似"所有订单"中的一行)   {orderNo:"2342342342",qryType:1-查询结果增加标记能否换房, roomId:1} roomId可选
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/orderinfocheckininfo", method = RequestMethod.POST)
	public Map<String, Object> qryOrderInfoCheckinInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String orderNo = entity.getString("orderNo");
			Integer qryType = entity.getInteger("qryType");
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull("入参错误", qryType);
			AssertHelper.notNullEmptyBlank("入参错误", orderNo);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.qryOrderInfoCheckinInfo(orderNo, qryType, roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 重新发送开门密码短信 {mobile:"13042144588",checkinId:123}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/opendoorsms", method = RequestMethod.POST)
	public Map<String, Object> sendOpenDoorSms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			Integer checkinId = entity.getInteger("checkinId");
			AssertHelper.notNullEmptyBlank("JSON传参错误", mobile);
			AssertHelper.notNull("JSON传参错误", checkinId);
			orderMgntService.sendOpenDoorSms(mobile, checkinId);
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
	 * OMS【取消订单】查询 OMS取消订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrynotchkinord", method = RequestMethod.POST)
	public Map<String, Object> queryCheckinOrders(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer thisPage = entity.getInteger("thisPage");
			Integer pageSize = entity.getInteger("pageSize");
			AssertHelper.notNull("JSON传参错误", thisPage, pageSize);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryNotCheckinOrders(thisPage, pageSize));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * OMS查询所有订单记录 {thisPage:1,pageSize:50,roomId:12,orderNo:"2015xxxx",orderId:12}  roomId可选,orderNo,orderId可选
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryorders", method = RequestMethod.POST)
	public Map<String, Object> queryOrders(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer thisPage = entity.getInteger("thisPage");
			Integer pageSize = entity.getInteger("pageSize");
			Integer roomId = entity.getInteger("roomId");
			String orderNo = entity.getString("orderNo");
			Integer orderId = entity.getInteger("orderId");
			AssertHelper.notNull("JSON传参错误", thisPage, pageSize);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryOrders(thisPage, pageSize, roomId, orderNo, orderId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * OMS的取消订单,独立出来 {orderId:791,account:"xbedadmin"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/omscancelorder", method = RequestMethod.POST)
	public Map<String, Object> cancelOrderFromOMS(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer orderId = entity.getInteger("orderId");
			AssertHelper.notNull("JSON传参错误", orderId);
			String account = entity.getString("account");
			orderMgntService.cancelOrderFromOMS(orderId, account);
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
	 * 退款完毕后点击 {orderId:791}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/finishrefund", method = RequestMethod.POST)
	public Map<String, Object> finishRefund(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer orderId = entity.getInteger("orderId");
			AssertHelper.notNull("JSON传参错误", orderId);
			if (orderMgntService.finishRefund(orderId) == -1) {
				RestCallHelper.fillParams(resultMap, -1, null, "订单状态异常");
			} else {
				RestCallHelper.fillSuccessParams(resultMap, null);
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 取消订单(微信端用) 用户只能取消未支付的订单 {orderId:3505}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cancelorder", method = RequestMethod.POST)
	public Map<String, Object> cancelOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer orderId = entity.getInteger("orderId");
			AssertHelper.notNull("JSON传参错误", orderId);
			orderMgntService.cancelOrder(orderId);
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
	 * 首页房间列表    查询可以预定的房间{province:"广东省",city:"广州市",district:"海珠区",time:{start:"2015-11-05",end:"2015-11-07"},chainId:1}// 所有参数可选,time传的话必须同时传start和end
	 * 
	 * @param req
	 * @param resp 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryrooms", method = RequestMethod.POST)
	public Map<String, Object> queryRooms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryRoomsToBeOrdered(jsonStr));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询房间的详细信息{roomId:1}  详情页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/roomdetail")
	public Map<String, Object> queryRoomDetail(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			int roomId = entity.getIntValue("roomId");
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryRoomDetail(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 预定房间 <br>
	 * {source:0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS,roomId:68,beginDate:"2015-11-15",endDate:"2015-11-16",actualPrice:100,discountPrice:20,lodgerId:2,checkInData:[{checkInName:"王永锋",checkInMobile:"13042014179"},{checkInName:"周会",checkInMobile:"13042014171"}],
	 * invoiceInfo:{invoiceTitle:"广州搜床网络科技有限公司",mailName:"王永锋",contactNo:"13062014179",mailAddr:"广州市荔湾区芳村信义路24号七喜创意园3栋103",postCode:"510640"},payType:1,cardCode:"111"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/book", method = RequestMethod.POST)
	public Map<String, Object> bookRoom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("预定房间，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			Integer roomId = entity.getInteger("roomId");
			Integer actualPrice = entity.getInteger("actualPrice");
			Integer discountPrice = entity.getInteger("discountPrice");
			Integer payType = entity.getInteger("payType");
			String beginDate = entity.getString("beginDate");
			String endDate = entity.getString("endDate");
			Integer source = entity.getInteger("source");
			List<Map<String, String>> checkIns = JSON.parseObject(entity.getString("checkInData"), new TypeReference<List<Map<String, String>>>(){});
			String cardCode = entity.getString("cardCode");
			AssertHelper.notNull("传参错误", checkIns);
			AssertHelper.notNull("传参错误", lodgerId, roomId, actualPrice, payType, source);
			AssertHelper.notNullEmptyBlank("传参错误", beginDate, endDate);

			// 邮寄发票信息
			String invoiceStr = entity.getString("invoiceInfo");
			XbInvoiceRec invoice = null;
			if (invoiceStr != null && invoiceStr.length() > 0) {
				invoice = JsonHelper.parseJSONStr2T(invoiceStr, XbInvoiceRec.class);
				AssertHelper.notNullEmptyBlank("传参错误", invoice.getMailName(), invoice.getContactNo(),
						invoice.getInvoiceTitle());
			}
			exceptionHandler.getLog().info("自有渠道新建订单:" + ",roomId:" + roomId + ",lodgerId:" + lodgerId + ",source:" + source + "jsonStr:" + jsonStr);
			//RestCallHelper.fillSuccessParams(resultMap, orderMgntService.bookRoom(lodgerId, roomId, payType, beginDate, endDate, totalPrice, invoice, source, checkIns));
			
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.bookRoom2(lodgerId, roomId, payType, beginDate, endDate, actualPrice,discountPrice, invoice, source, checkIns, cardCode));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 计算订单的价格 {roomId:1,beginDate:"2015-08-28",endDate:"2015-08-31",cardCode:"xxxx",lodgerId:1223}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qryprice", method = RequestMethod.POST)
	public Map<String, Object> calculateOrderPrice(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			Integer lodgerId = entity.getInteger("lodgerId");
			String beginDate = entity.getString("beginDate");
			String endDate = entity.getString("endDate");
			AssertHelper.notNull("传参错误", roomId, lodgerId);
			AssertHelper.notNullEmptyBlank("传参错误", beginDate, endDate);
			
			String cardCode = entity.getString("cardCode");
			
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.calculateOrderPrice(roomId, beginDate, endDate, cardCode, lodgerId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询房间入住人信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrycheckiner", method = RequestMethod.POST)
	public Map<String, Object> qryXbCheckiner(HttpServletRequest req, HttpServletResponse resp){
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try{
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("查询房间入住人信息，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkInId = entity.getInteger("checkInId");
			AssertHelper.notNull("传参错误", checkInId);
			RestCallHelper.fillSuccessParams(resultMap, orderMgntService.queryXbCheckiner(checkInId)); 
		}catch(Exception e){
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		}finally{
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 支付成功回调的接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/paysucc", method = RequestMethod.POST)
	public Map<String, Object> paySuccess(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("支付成功，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String orderNo = entity.getString("orderNo");
			String platformSn = entity.getString("platformSn");
			AssertHelper.notNullEmptyBlank("入参错误", orderNo, platformSn);
			orderMgntService.paySuccess(orderNo, platformSn);
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
	 * 退房接口{checkinId:123}
	 * @param req
	 * @param resp
	 * @return
	 * retcode -1用户名或密码错误  -2必须当天才能退房 -3订单状态有误(例如已经退过再退一次) -4不能退房因为传入用户非该入住单住户
	 */
	@ResponseBody
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public Map<String, Object> checkout(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("退房，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			AssertHelper.notNull("传参错误", checkinId);
			orderMgntService.checkout(checkinId);
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
	 * 办理入住
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkin", method = RequestMethod.POST)
	public Map<String, Object> checkin(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("办理入住，入参:" + jsonStr);
			Map<String,Object> result = orderMgntService.checkin(jsonStr);
			String s = (String) result.get("retcode");
			if (s.equals("1")) {
				RestCallHelper.fillSuccessParams(resultMap, result.get("retval"));
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	/**
	 * 入住状态添加入住人
	 * {checkinId:52, other:[{name:"王大锤",mobile:"13431091111",idcardNo:"3444433444",doesSendPin:1},{name:"王老娘",mobile:"13431091112",idcardNo:"34444334666",doesSendPin:2}]}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/appendToCheckiner", method = RequestMethod.POST)
	public Map<String, Object> appendToCheckiner(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("入住状态添加入住人，入参:" + jsonStr);
			Map<String,Object> result = orderMgntService.appendToCheckiner(jsonStr);
			String s = (String) result.get("retcode");
			if (s.equals("1")) {
				RestCallHelper.fillSuccessParams(resultMap, result.get("retval"));
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	/**
	 * 删除入住用户（软删除修改is_del字段）
	 * {mobile:13777777777, checkinId:0}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCheckiner", method = RequestMethod.POST)
	public Map<String, Object> deleteCheckiner(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("删除入住用户，入参:" + jsonStr);
			Map<String,Object> result = orderMgntService.deleteCheckiner(jsonStr);
			String s = (String) result.get("retcode");
			if (s.equals("1")) {
				RestCallHelper.fillSuccessParams(resultMap, result.get("retval"));
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 该方法好像没被前台调用,CleanApplyAction里有对应的  【作废】
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/applyclean", method = RequestMethod.POST)
	public Map<String, Object> applyClean(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("申请在住打扫，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			
			
			
			
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 【好像是作废的接口】
	 * 换房接口{newRoomId:123,oldOrderId:23423}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/chgroom", method = RequestMethod.POST)
	public Map<String, Object> changeRoom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// 初步检查入参
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("退房，入参:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			
			
			
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
}
