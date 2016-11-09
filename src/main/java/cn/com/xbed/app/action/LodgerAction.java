package cn.com.xbed.app.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.enu.IdCardType;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/lodger")
public class LodgerAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LodgerAction.class));
	@Resource
	private LodgerService lodgerService;
	
	@Resource
	private OrderMgntService orderMgntService;
	@Resource
	private CommonService commonService;

	
	/**
	 * OMS查询用户信息  {lodgerId:123,mobile:"13431114445",lodgerName:"马云",idCardNo:"44522xxxxx"}// 全部查询条件都是可选的
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/lodgerinfo")
	public Object queryLodgerInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			if (StringUtils.isBlank(jsonStr)) {
				jsonStr = "{}";
			}
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			String mobile = entity.getString("mobile");
			String lodgerName = entity.getString("lodgerName");
			String idCardNo = entity.getString("idCardNo");
			RestCallHelper.fillSuccessParams(resultMap, lodgerService.queryLodgerInfo(lodgerId, mobile, lodgerName, idCardNo));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	
	/**
	 * 上传身份证照片
	 * {"lodgerId":123,type:1-正面,2-背面,base64code:"xxxxxxx","cardNo":"445221......"}
	 * base64code可选,cardNo可选,两者必传其一,传谁就修改谁
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public Object uploadIdCardPic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			Integer type = entity.getInteger("type");
			String base64code = entity.getString("base64code");
			String cardNo = entity.getString("cardNo");
			AssertHelper.notNull("传参错误", lodgerId, type);
			
			IdCardType idCardType = type == 1 ? IdCardType.FRONT : IdCardType.BACK;
			RestCallHelper.fillSuccessParams(resultMap, lodgerService.uploadIdCardPic(lodgerId, idCardType, base64code, cardNo));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException("上传的路径找不到", e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 登录接口 入参:{"mobile":"登录手机号","password":"密码",from:1-微信登录 2-ISO 3-android,openId:"ksdjfasdf"} 出参:参考文档
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map<String, Object> login(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			String password = entity.getString("password");
			String openId = entity.getString("openId");
			
			AssertHelper.notNullEmptyBlank(mobile, password, "入参错误");
			exceptionHandler.getLog().info("登录mobile:" + mobile);
			exceptionHandler.getLog().info(jsonStr);
			
			XbLodger foundLodger = lodgerService.queryLodger(mobile, password);
			if (foundLodger != null) {
				// 获取个人信息、账户信息、区域中心信息和额外的统计信息
				Map<String, Object> subMap = new LinkedHashMap<>();
				subMap.put("lodgerInfo", foundLodger);
				RestCallHelper.fillSuccessParams(resultMap, subMap);
				
				
				// 记录是否关注微信
				Integer from = entity.getInteger("from");
				if (from != null && from == 1 && foundLodger.getIsFollow() == AppConstants.Lodger_isFollow.NOT_FOLLOW_1) {
					lodgerService.updateFollow(foundLodger.getLodgerId(), AppConstants.Lodger_isFollow.FOLLOW_2);
				}
				// 记录openId(不需要放到同一个事务)
				lodgerService.updateOpenId(foundLodger.getLodgerId(), foundLodger.getOpenId(), openId, foundLodger);
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
	 * 修改密码接口 入参: {"mobile":"登录手机号","password":"密码","passwordNew":"新密码"} 出参:参考文档
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/chgpwd", method = RequestMethod.POST)
	public Map<String, Object> chgPwd(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);

			String mobile = entity.getString("mobile");
			String password = entity.getString("password");
			String newPassword = entity.getString("passwordNew");
			if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password) || StringUtils.isBlank(newPassword)) {
				throw new RuntimeException("入参错误");
			}

			XbLodger foundLodger = lodgerService.queryLodger(mobile, password);
			if (foundLodger != null) {
				lodgerService.updatePwd(foundLodger.getLodgerId(), newPassword);
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
	 * 用户密码重置<br>
	 * {"mobile":"登录手机号","validId":"验证码ID","validValue":"验证码的值","passwordNew":"新密码"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
	public Map<String, Object> resetPwd(  HttpServletRequest req, HttpServletResponse resp ) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);

			String mobile = entity.getString("mobile");
			String validId = entity.getString("validId");
			String validValue = entity.getString("validValue");
			String passwordNew = entity.getString("passwordNew");
			AssertHelper.notNullEmptyBlank(mobile, validId, validValue, passwordNew, "入参错误");

			//密码重置业务操作
			lodgerService.resetPassword(mobile, validId, validValue, passwordNew);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	
	
	
	//{"mobile":"登录手机号","password":"密文密码","lodgerName":"新名字"}String mobile, String password, String lodgerName,
	/**
	 * 更改用户姓名<br>
	 * {"mobile":"登录手机号","lodgerName":"新名字"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateLogerName", method = RequestMethod.POST)
	public Map<String, Object> updateLogerName( HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);

			String mobile = entity.getString("mobile");
			String lodgerName = entity.getString("lodgerName");
			
			AssertHelper.notNullEmptyBlank(mobile, lodgerName, "入参错误");

			//修改名字操作
			lodgerService.updateLogerName(mobile, lodgerName);
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
	 * 用户注册 {name:"王大锤",mobile:"13445252225",password:"123456"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Map<String, Object> registerUser(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);

			String name = entity.getString("name");
			String mobile = entity.getString("mobile");
			String password = entity.getString("password");
			AssertHelper.notNullEmptyBlank("传参错误", mobile, password);
			
			if (lodgerService.existSameLodger(mobile)) {
				RestCallHelper.fillFailParams(resultMap, "该号码已被注册");
			} else {
				commonService.register(name, mobile, password, null,"", AppConstants.Lodger_source.REGISTER_0, AppConstants.Lodger_hasChgPwd.HAS_CHG_2);
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
	 * 检查某个号码是否注册过 {mobile:"13443339992"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkreg", method = RequestMethod.POST)
	public Map<String, Object> checkRegister(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			AssertHelper.notNullEmptyBlank("传参错误", mobile);
			RestCallHelper.fillSuccessParams(resultMap,lodgerService.existSameLodger(mobile));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 查询身份证信息  OMS使用   {checkinId:289}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/idcardinfo", method = RequestMethod.POST)
	public Map<String, Object> queryIdCardInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String checkinId = entity.getString("checkinId");
			AssertHelper.notNullEmptyBlank("JSON传参错误", checkinId);

			RestCallHelper.fillSuccessParams(resultMap, lodgerService.queryIdCardInfo(checkinId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
}
