package cn.com.xbed.app.commons.listener;

import java.util.List;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.vo.CheckinInputVo;
import cn.com.xbed.app.bean.vo.CheckinerUnit;
import cn.com.xbed.app.bean.vo.CheckinerVo;
import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.WarningSystemTool;

// 已经在spring.xml配置
@Transactional
public class OverstayListener implements MessageListener {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OverstayListener.class));

	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private OrderMgntService orderMgntService;
	@Resource
	private CommonDbLogService commonDbLogService;

	/**
	 * 接收消息
	 */
	@Override
	public void onMessage(Message message) {
		exceptionHandler.getLog().info("监听ordersytem.listener.overstayAutoCheckin,帮办理入住");
		try {
			if (message instanceof ActiveMQObjectMessage) {
				ActiveMQObjectMessage aMsg = (ActiveMQObjectMessage) message;
				Object obj = (Object) aMsg.getObject();
				String str = obj.toString();
				
				// 写你的处理逻辑
				this.handle(str);
			
			} else if (message instanceof ActiveMQTextMessage) {
				ActiveMQTextMessage amMessage = (ActiveMQTextMessage) message;
				String str = amMessage.getText();

				// 写你的处理逻辑
				this.handle(str);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public void handle(String str) {
		exceptionHandler.getLog().info("MQ监听消息,进入了handle方法:" + str);
		boolean flag = true;
		int oriCheckinId = -1;
		Integer newCheckinId = -1;
		String errorMsg = "";
		try {
			JSONObject jsonObj = JsonHelper.parseJSONStr2JSONObject(str);
			JSONObject parameter = jsonObj.getJSONObject("parameters");
			JSONObject checkinInfo = parameter.getJSONObject("checkinInfo");
			oriCheckinId = checkinInfo.getIntValue("checkinId");
			exceptionHandler.getLog().info(String.format("自动办理入住,oriCheckinId:%s", oriCheckinId));
			newCheckinId = orderMgntCommon.queryOverstayCheckinId(oriCheckinId);
			exceptionHandler.getLog().info(String.format("自动办理入住,newCheckinId:%s", newCheckinId));

			if (newCheckinId != null) {
				XbCheckin newCheckinInfo = daoUtil.checkinDao.findByPk(newCheckinId);
				XbLodger orderMan = daoUtil.lodgerDao.findByPk(newCheckinInfo.getLodgerId());
				List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(newCheckinId);
				XbCheckiner mainCheckiner = null;
				for (XbCheckiner xbCheckiner : checkinerList) {
					if (xbCheckiner.getIsMain() != -1) {// -1表示是下单人,是冗余在xb_checkiner表的,只为了查询"我的订单"接口复杂度简化之用
						if (xbCheckiner.getCheckinId() == newCheckinId.intValue()) {// 记得intValue()
							mainCheckiner = xbCheckiner;
						}
					}
				}
				String idcardNo = orderMan == null ? "" : orderMan.getIdCard();
				String mobile = orderMan == null ? "" : orderMan.getMobile();
				String name = orderMan == null ? "" : orderMan.getLodgerName();
				if (mainCheckiner != null) {
					idcardNo = mainCheckiner.getIdcardNo();
					mobile = mainCheckiner.getMobile();
					name = mainCheckiner.getName();
				}
				if (StringUtils.isBlank(idcardNo) || StringUtils.isBlank(mobile) || StringUtils.isBlank(name)) {
					throw new RuntimeException("续住异常,数据异常,找不到续住的身份证号或姓名或密码,newCheckinId:" + newCheckinId);
				}

				CheckinerUnit main = new CheckinerUnit(AppConstants.Checkiner_sendFlag.SEND_1, idcardNo, mobile, name);
				List<CheckinerUnit> other = null;// 入住阶段添加的其他入住人
				CheckinerVo checkiner = new CheckinerVo(main, other);
				CheckinInputVo checkinInputVo = new CheckinInputVo(newCheckinId, checkiner);

				exceptionHandler.getLog().info(String.format("自动办理入住BEGIN,newCheckinId:%s,oriCheckinId:%s,入参:%s", newCheckinId, oriCheckinId,
						JsonHelper.toJSONString(checkinInputVo)));

				orderMgntService.checkin(checkinInputVo);

				exceptionHandler.getLog().info(String.format("自动办理入住END,newCheckinId:%s,oriCheckinId:%s,入参:%s", newCheckinId, oriCheckinId,
						JsonHelper.toJSONString(checkinInputVo)));
			} else {
				errorMsg = String.format("找不到续住单,原单oriCheckinId:%s", oriCheckinId);
				throw new RuntimeException(errorMsg);
			}
		} catch (Exception e) {
			flag = false;
			errorMsg = String.format("【自动办理续住失败】,oriCheckinId:%s,newCheckinId:%s,errorMsg:%s", oriCheckinId, newCheckinId, e.getMessage());
			warningSystemTool.warningLogMultiThread(this.getWarningEntity(e.getMessage()));
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logOverstayAutoCheckin(oriCheckinId, newCheckinId, null, errorMsg, succFlag);
		}
	}

	
	private WarningEntity getWarningEntity(String errorMsg) {
		WarningEntity warningEntity = new WarningEntity();
		warningEntity.setLogType(2);
		warningEntity.setLogLevel(2);
		warningEntity.setCreateTime(DateUtil.getCurDateTime());
		warningEntity.setLogContent(errorMsg);
		warningEntity.setConfigItemCode("ORD_SYS.OVERSTAY_CHECKIN");
		return warningEntity;
	}
}
