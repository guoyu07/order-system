package cn.com.xbed.app.service.ljh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.service.eventsystem.EventCode;
import cn.com.xbed.app.service.smsmodule.CommonSms;

@Service
@Transactional
public class CleanSystemService {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanSystemService.class));

	@Resource
	private CommonDbLogService commonDbLogService;
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private CommonService commonService;
	@Resource
	private CommonSms commonSms;
	/**
	 * 发送退房清洁单
	 * 
	 * @param checkinId
	 * @param orderNo
	 * @param roomId
	 */
	public void sendCheckoutCleanSheet(Integer checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			this.sendCleanSheet(checkinId, orderInfo.getOrderNo(), AppConstants.CleanSystem.Plan.CHECKOUT_2, checkinInfo.getRoomId(), orderInfo.getOrderType());
		} catch (Exception e) {
			warningSystemTool.warningLogMultiThread(this.getWarningEntity("退房发送短信或清洁单失败,请检查XMonitor相关配置,checkinId:" + checkinId));
			String errorMsg = "【外系统交互·跟丽家会】退房发送短信或清洁单失败,请检查XMonitor相关配置,checkinId:" + checkinId;
			throw exceptionHandler.logServiceException(errorMsg, e);
		}
	}

	private WarningEntity getWarningEntity(String errorMsg) {
		WarningEntity warningEntity = new WarningEntity();
		warningEntity.setLogType(2);
		warningEntity.setLogLevel(2);
		warningEntity.setCreateTime(DateUtil.getCurDateTime());
		warningEntity.setLogContent(errorMsg);
		warningEntity.setConfigItemCode("ORD_SYS.CLEAN_DONE");
		return warningEntity;
	}
	/**
	 * 发送在住申请清洁单
	 * 
	 * @param checkinId
	 * @param orderNo
	 * @param roomId
	 */
	public void sendApplyCleanCleanSheet(Integer checkinId) {
		try {
			exceptionHandler.getLog().info("【MQ系统】在住申请打扫,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			this.sendCleanSheet(checkinId, orderInfo.getOrderNo(), AppConstants.CleanSystem.Plan.APPLY_CLEAN_1, checkinInfo.getRoomId(), orderInfo.getOrderType());
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("", e);
		}
	}

	// 发清洁单
	protected void sendCleanSheet(Integer checkinId, String orderNo, int plan, int roomId, int orderType) {
		boolean succFlag = true;
		String datagram = "";
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("cleanSource", AppConstants.CleanSystem.CleanSource.ORDER_SYSTEM_1 + "");
			parameters.put("createName", AppConstants.CleanSystem.CREATE_NAME + "");
			parameters.put("plan", plan + "");// 触发方案 1：在住清洁申请；2：退房；3：停用房清洁
			parameters.put("roomId", roomId + "");
			parameters.put("xbedCheckinId", checkinId);// 如果这个是null,json字符串将不会有这个参数,但接收方说要这样,那就这样吧
			parameters.put("xbedOrderNo", orderNo);
			parameters.put("xbedOrderType", orderType);
			
			Map<String, Object> mqParams =new HashMap<>();
			mqParams.put("eventCode", EventCode.SOME_ROOM_NEED_TO_CLEAN);
			mqParams.put("parameters", parameters);
			String input = JsonHelper.toJSONString(mqParams);
			exceptionHandler.getLog().info("【MQ系统】在住申请打扫,入参:" + input);
			warningSystemTool.sendQueueMessage("xbed.common.event", input);
		} catch (Exception e) {
			succFlag = false;
			throw exceptionHandler.logServiceException(e);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logCleanSheet(checkinId, orderNo, datagram, plan, roomId, enumSuccFlag);
		}
	}

	
	public List<String> getOperationManPhones() {
		List<String> mobiles = new ArrayList<>();
		Config config = this.getConfig();
		if (config == null) {
			return mobiles;
		}
		if (!config.isMainSwitch()) {
			return mobiles;
		}
		for (SubConfig subConfig : config.getMobiles()) {
			if (subConfig.isSubSwitch()) {
				mobiles.add(subConfig.getPhone());
			}
		}
		if (mobiles.isEmpty()) {
			return mobiles;
		}
		return mobiles;
	}
	
	public Config getConfig() {
		try {
			// {mainSwitch:1,mobiles:[{phone:"13xxxx",switch:1,remark:"Lisa的手机"},{phone:"13xxx",switch:0,remark:"Wendy的手机"}]}
			SvSysParam debugSysParam = daoUtil.sysParamDao.getByParamKey("CLEAN_TASK_NOTIFY_PHONE");
			String debugStr = debugSysParam.getParamValue();
			if (StringUtils.isBlank(debugStr)) {
				return null;
			}
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(debugStr);
			int mainSwitch = obj.getIntValue("mainSwitch");
			JSONArray jsonArr = obj.getJSONArray("mobiles");
			Config config = new Config();
			config.setMainSwitch(mainSwitch == 1 ? true : false);
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject tmp = jsonArr.getJSONObject(i);
				String phone = tmp.getString("phone");
				int subSwitch = tmp.getIntValue("switch");
				String remark = tmp.getString("remark");
				SubConfig subConfig = new SubConfig(subSwitch == 1 ? true : false, phone, remark);
				config.getMobiles().add(subConfig);
			}
			return config;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}

	}

	private static class Config {
		private boolean mainSwitch;
		private List<SubConfig> mobiles = new ArrayList<>();

		public boolean isMainSwitch() {
			return mainSwitch;
		}

		public void setMainSwitch(boolean mainSwitch) {
			this.mainSwitch = mainSwitch;
		}

		public List<SubConfig> getMobiles() {
			return mobiles;
		}

		public void setMobiles(List<SubConfig> mobiles) {
			this.mobiles = mobiles;
		}

		@Override
		public String toString() {
			return "Config [mainSwitch=" + mainSwitch + ", mobiles=" + mobiles + "]";
		}

	}

	private static class SubConfig {
		private boolean subSwitch;
		private String phone;
		private String remark;

		public SubConfig(boolean subSwitch, String phone, String remark) {
			super();
			this.subSwitch = subSwitch;
			this.phone = phone;
			this.remark = remark;
		}

		public boolean isSubSwitch() {
			return subSwitch;
		}

		public void setSubSwitch(boolean subSwitch) {
			this.subSwitch = subSwitch;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		@Override
		public String toString() {
			return "SubConfig [subSwitch=" + subSwitch + ", phone=" + phone + ", remark=" + remark + "]";
		}

	}

	public String getOrderTypeDesci(int orderType) {
		String orderTypeDescri = "";
		switch (orderType) {// 订单类型 1-客户订单 2-禁售单 5-测试订单 6-换房订单
		case 1:
			orderTypeDescri = "-客户订单";
			break;
		case 2:
			orderTypeDescri = "禁售单";
			break;
		case 5:
			orderTypeDescri = "测试订单";
			break;
		case 6:
			orderTypeDescri = "换房订单";
			break;

		default:
			orderTypeDescri = "其他类型";
			break;
		}
		return orderTypeDescri;
	}

	public String getCleanApplyPeriod(int period) {
		String periodDescri = "";
		switch (period) {// 申请在住打扫时间段 1 9:00~10:00,2 10:00~11:00,3 14:00~15:00,
							// 4 15:00~16:00
		case 1:
			periodDescri = "9:00~10:00";
			break;
		case 2:
			periodDescri = "10:00~11:00";
			break;
		case 3:
			periodDescri = "14:00~15:00";
			break;
		case 4:
			periodDescri = "15:00~16:00";
			break;

		default:
			periodDescri = "其他时段";
			break;
		}
		return periodDescri;
	}
}
