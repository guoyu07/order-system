package cn.com.xbed.app.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.commond.LogTool;
import cn.com.xbed.commond.XbedMQClient;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Component
public class WarningSystemTool {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(WarningSystemTool.class));
	@Resource
	private LogTool logTool;
	@Resource
	private XbedMQClient xbedMQClient;
	
	// 修改房间状态
	public void warningLog(WarningEntity warningEntity) {
		try {
			String configItemCode = warningEntity.getConfigItemCode();
			Integer logType = warningEntity.getLogType();
			Integer logLevel = warningEntity.getLogLevel();
			String logRemark = warningEntity.getLogRemark();
			String logContent = warningEntity.getLogContent();
			Date createTime = warningEntity.getCreateTime();
			String logCode = warningEntity.getLogCode();
			String lineNum = warningEntity.getLineNum();
			int result = logTool.createLog(configItemCode, logType, logLevel, lineNum, logContent, logRemark, logCode, createTime);
		} catch (Exception e) {
			// 不抛异常
		}
	}
	
	public void warningLogMultiThread(final WarningEntity warningEntity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				warningLog(warningEntity);
			}
		}).start();
	}
	
	/**
	 * 
	 * @param queueName
	 * @param jsonStr
	 */
	public void sendQueueMessage(String queueName, String jsonStr) {
		try {
			int result = xbedMQClient.sendMQMessage(queueName, jsonStr);
			if (result != 0) {
				throw new RuntimeException("发送清洁单消息失败");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 该方法可能没有适用的地方,因为对于发送清洁单失败,是比较严重的,不能使用多线程,否则调用者线程不会等到新开的线程返回值
	 * @param queueName
	 * @param jsonStr
	 */
	protected void sendQueueMessageMultiThread(final String queueName, final String jsonStr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendQueueMessage(queueName, jsonStr);
			}
		}).start();
	}
}
