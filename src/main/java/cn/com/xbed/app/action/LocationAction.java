package cn.com.xbed.app.action;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.service.BreakfastService;
import cn.com.xbed.app.service.ChainMgntService;
import cn.com.xbed.app.service.ValidService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/loc")
public class LocationAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LocationAction.class));
	@Resource
	private ChainMgntService chainMgntService;
	@Resource
	private BreakfastService breakfastService;

	
	/**
	 * {provinceCode:"123",cityCode:"446",districtCode:"555"}  都可选，都不传查全部
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrylocations", method = RequestMethod.POST)
	public Map<String, Object> qryLocations(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			String content = entity.getString("content");
			
			
			
			
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

