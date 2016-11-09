package cn.com.xbed.app.commons.listener;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.WarningSystemTool;
// 已经在spring.xml配置
@Transactional
public class CleanDoneListener implements MessageListener {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanDoneListener.class));
	
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	
	/**
	 * 接收消息
	 */
	@Override
	public void onMessage(Message message) {
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
			//warningSystemTool.warningLogMultiThread(warningEntity);
		}
	}
	
//	private getWarningEntity() {
//		
//	}
	
	// 【作废方法】
	public void handle(String str) {
		// {"roomId":"1","xbedCheckinId":"2","xbedOrderNo":"2015-12-14"}
		JSONObject jsonObj = JsonHelper.parseJSONStr2JSONObject(str);
		Integer roomId = jsonObj.getInteger("roomId");
		
		List<Map<String, Object>> list = commonService.queryTodayCheckinOrderByRoomId(roomId, DateUtil.getCurDateTime());
		if (!list.isEmpty()) {
			int lodgerId = (int) list.get(0).get("lodgerId");
			int checkinId = (int) list.get(0).get("checkinId");
			String orderNo = (String) list.get(0).get("orderNo");
			JSONObject param = new JSONObject();
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			if (roomInfo != null && checkinInfo != null && lodgerInfo != null) {
				param.put("roomId", roomInfo.getRoomId());
				param.put("roomNo", roomInfo.getRoomName());
				param.put("roomAddr", roomInfo.getAddr());
				param.put("chainName", roomInfo.getChainName());
				param.put("checkinId", checkinInfo.getCheckinId());
				param.put("lodgerName", checkinInfo.getLodgerName());
				param.put("checkinTime", DateUtil.getYearMonDayHrMinStr_(checkinInfo.getCheckinTime()));
				param.put("checkoutTime", DateUtil.getYearMonDayHrMinStr_(checkinInfo.getCheckoutTime()));
				param.put("orderNo", orderNo);
				param.put("lodgerId", lodgerInfo.getLodgerId());
				param.put("lodgerName", lodgerInfo.getLodgerName());
				param.put("lodgerMobile", lodgerInfo.getMobile());
				String openId = lodgerInfo.getOpenId() == null ? "" : lodgerInfo.getOpenId();
				param.put("openId", openId);
				
				exceptionHandler.getLog().info("清洁完成通知XMonitor:" + param);
				try {
					warningSystemTool.sendQueueMessage(AppConstants.XMonitor.QUEUE_KEY_CLEAN_DONE, param.toJSONString());
				} catch (Exception e) {
					String errorMsg = "该房间打扫完毕,通知今天可以入住,通知失败.房间:" + roomInfo.getRoomName() + ",roomId:" + roomInfo.getRoomId() + ",lodgerId:"
							+ lodgerInfo.getLodgerId() + ",lodgerName:" + lodgerInfo.getLodgerName();
					warningSystemTool.warningLogMultiThread(this.getWarningEntity(errorMsg ));
				}
			} else {
				exceptionHandler.getLog().error("数据残缺,roomInfo:" + roomInfo);
				exceptionHandler.getLog().error("数据残缺,lodgerInfo:" + lodgerInfo);
				exceptionHandler.getLog().error("数据残缺,checkinInfo:" + checkinInfo);
			}
		} else {
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
			String roomName = roomInfo == null ? "" : roomInfo.getRoomName();
			exceptionHandler.getLog().info("房间清洁完了,但没有今天入住的,无需提醒可以办理入住,roomId:" + roomId + "roomName:" + roomName + ",日期:"
					+ DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
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
}
