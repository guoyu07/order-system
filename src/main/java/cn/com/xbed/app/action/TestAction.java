package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.listener.OverstayListener;
import cn.com.xbed.app.commons.util.Base64ImageUtil;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.TestService;
import cn.com.xbed.app.service.syncin.SyncInScaner;
import cn.com.xbed.app.service.timer.TimerTasks;

@Controller
@RequestMapping("/test")
public class TestAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TestAction.class));
	@Resource
	private TestService testService;

	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private SyncInScaner syncInScaner;
	@Resource
	private TimerTasks timerTasks;

	
	@ResponseBody
	@RequestMapping(value = "/trans2LocalRoom")
	public Map<String, Object> trans2LocalRoom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer tmpRoomId = entity.getInteger("tmpRoomId");
			
			testService.trans2LocalRoom(tmpRoomId);
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
	 * 测试模拟扫描
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/test")
	public Map<String, Object> test(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			//timerTasks.scanOverstayCheckin();
			
			
			
			
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/testReqFromJsp")
	public Object testReqFromJsp(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);

			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/debugsuperman")
	public Map<String, Object> debugSuperMan(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/testprecheckouthint")
	public Map<String, Object> testPreCheckoutHint(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			timerTasks.scanPreCheckoutHint();
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
	 * 测试同步到去呼呼，浏览器中调用一次表示扫描一次
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/testsyncqhh")
	public Map<String, Object> testsyncqhh(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			syncInScaner.scanSyncQhh();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/autocheckout")
	public Map<String, Object> testAutoCheckout(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			timerTasks.scanCheckoutTime();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/scanwarning")
	public Map<String, Object> testScanWarning(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			timerTasks.scanWarningOrder();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/scancancel")
	public Map<String, Object> testScanCancel(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			timerTasks.scanCancelOrder();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/tongji", method = RequestMethod.POST)
	public Map<String, Object> tongji(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			// testService.tongji();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	// @ResponseBody
	@RequestMapping(value = "/initopercode", method = RequestMethod.GET)
	public String initOperateCode(HttpServletRequest req, HttpServletResponse resp) {
		try {
			List<Map<String, Object>> operateCodeList = testService.queryOperateCode();
			req.setAttribute("operateCodeList", operateCodeList);
		} catch (Exception e) {
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return "forward:/WEB-INF/jsp/room_state_tool.jsp";
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param json_data
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/roomstate", method = RequestMethod.POST)
	public Map<String, Object> updateRoomState(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			Integer operateCode = entity.getInteger("operateCode");
			String date = entity.getString("date");
			AssertHelper.notNull("不能为空", roomId, operateCode, date);
			testService.updateRoomState(roomId, operateCode, date);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/sync")
	public Map<String, Object> qhhSync(HttpServletRequest req, HttpServletResponse resp, String json_data) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String datagram = Base64ImageUtil.readFileContent(json_data);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 获得有效的同步实体,入参是去呼呼的报文,过滤掉sp,剩下qhh的订单
	 * 
	 * @param req
	 * @param resp
	 * @param json_data
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getusefulsyncentity")
	public Map<String, Object> getUsefulSyncEntity(HttpServletRequest req, HttpServletResponse resp, String json_data) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String datagram = Base64ImageUtil.readFileContent(json_data);
			String uuid = RandomUtil.getUUID32Uppercase();

		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	@RequestMapping(value = "/adminlogin")
	public String adminLogin(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		String retmsg = "";
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String name = entity.getString("name");
			String password = entity.getString("password");
			AssertHelper.notNullEmptyBlank(name, password);
			int ret = testService.adminLogin(name, password);
			if (ret == 1) {
				return "forward:/WEB-INF/jsp/xsadmin.jsp";
			}
		} catch (Exception e) {
			RestCallHelper.fillParams(resultMap, -1, null, retmsg);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return "forward:/WEB-INF/jsp/error.jsp";
	}

	/**
	 * 转发到对应的JSP
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/forward/{page}")
	public String forward(HttpServletRequest req, HttpServletResponse resp, @PathVariable String page) {
		try {
			return "forward:/WEB-INF/jsp/" + page + ".jsp";
		} catch (Exception e) {
			throw exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/jsonrpc")
	public Map<String, Object> testJsonRpc(String key1) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			System.out.println(key1);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		}
		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/mod")
	public Map<String, Object> mod(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			testService.mod(jsonStr);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询某个房间某个日期到某个日期之间有什么订单 {start:"2016-01-07",end:"2016-01-08",roomId:12}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryorderbyperiod")
	public Map<String, Object> queryOrderByPeriodAndRoomId(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String start = entity.getString("start");
			String end = entity.getString("end");
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull(start, end, roomId, "传参错误");

			RestCallHelper.fillSuccessParams(resultMap, testService.queryOrderByPeriodAndRoomId(start, end, roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 增加一条日志信息 {logLevel:"warn|error|info",msg:""}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/testLog", method = RequestMethod.GET)
	public Object testLog(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String logLevel = entity.getString("logLevel");
			String msg = entity.getString("msg");
			AssertHelper.notNullNotEmpty(logLevel, msg, "参数错误");

			if (logLevel.equalsIgnoreCase("warn")) {

				exceptionHandler.getLog().warn(msg);

			} else if (logLevel.equalsIgnoreCase("error")) {

				exceptionHandler.getLog().error(msg);

			} else if (logLevel.equalsIgnoreCase("info")) {

				exceptionHandler.getLog().info(msg);

			} else {
				throw new RuntimeException("logLevel错误");
			}
			exceptionHandler.getLog().info("");
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

}
