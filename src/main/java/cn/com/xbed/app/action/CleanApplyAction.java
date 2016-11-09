package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.service.CleanSvRecService;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/sv")
public class CleanApplyAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanApplyAction.class));
	@Resource
	private CleanSvRecService cleanSvRecService;
	@Resource
	private LodgerService lodgerService;

	
	/**
	 * 申请清洁房间的服务 {checkinId:13,mobile:"3423423",period: 1 9:00~10:00,2 10:00~11:00,3 14:00~15:00, 4 15:00~16:00}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cleanv2", method = RequestMethod.POST)
	public Map<String, Object> applyCleanServiceV2(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			
			Integer checkinId = entity.getInteger("checkinId");
			Integer period = entity.getInteger("period");
			String mobile = entity.getString("mobile");
			AssertHelper.notNull("传参错误", checkinId, period, mobile);
			cleanSvRecService.applyCleanServiceV2(checkinId, mobile, period);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
}
