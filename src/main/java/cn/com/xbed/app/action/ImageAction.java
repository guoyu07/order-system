package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.service.CleanSvRecService;
/**
 * 【作废】
 * @author Administrator
 *
 */
@Controller
public class ImageAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ImageAction.class));
	@Resource
	private CleanSvRecService cleanSvRecService;
	
	private static String path = "";

	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/images/{fileName}/{pixel}")
	public String redirectImage(@PathVariable String pixel, @PathVariable String fileName,
			HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			AssertHelper.notNullEmptyBlank(pixel, "pixel不能为空");
			AssertHelper.notNullEmptyBlank(fileName, "fileName不能为空");
			
			if (path.length() == 0) {
				path = req.getScheme() + "://" + req.getLocalName()+":"+req.getLocalPort()+req.getContextPath(); 
			}
//			path = req.getScheme() + "://" + "192.168.0.111"+":"+req.getLocalPort()+req.getContextPath(); 
			if (pixel.equals("700")) {
				String redirectPath = path + "/upload/images/700/" + fileName;
				return "redirect:" + redirectPath;
			} else if (pixel.equals("400")) {
				String redirectPath = path + "/upload/images/400/" + fileName;
				return "redirect:" + redirectPath;
			}
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		}
		return "redirect:";
	}
	
	
	@RequestMapping(value = "/queryimagepath")
	public String getImagePath(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			
			
			
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		}
		return "redirect:";
	}

}
