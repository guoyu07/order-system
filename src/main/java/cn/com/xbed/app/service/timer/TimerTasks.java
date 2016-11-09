package cn.com.xbed.app.service.timer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.timer.TimerTasksService.OperType;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
public class TimerTasks {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TimerTasks.class));
	@Resource
	private TimerTasksService timerTasksService;

	@Transactional
	public void testQuart() {
		System.out.println(DateUtil.dateToString(DateUtil.getCurDateTime(), DateUtil.yrMonDayHrMinSec_));
	}

	/**
	 * 扫描需要自动办理入住的续住单
	 */
	@Transactional
	public void scanOverstayCheckin() {
		try {
			Date now = DateUtil.getCurDateTime();
			String nowDate = DateUtil.dateToYrMonDay_(now);
			String sql = "SELECT a.order_id orderId,b.checkin_id checkinId FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND a.stat=1 AND b.stat=0 AND b.overstay_flag=2 AND DATE_FORMAT(b.checkin_time,'%Y-%m-%d')=?";
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { nowDate });
			exceptionHandler.getLog().info("【办理续住进程】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int checkinId = (int) map.get("checkinId");
				try {
					timerTasksService.handleOverstayCheckin(checkinId);// 独立事务
				} catch (Exception e) {
					// 避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("扫描需要自动办理入住的续住单", e);// 要用logActionException而不能用logServiceException,否则记录不了报错日志
		}
	}
	
	
	@Transactional
	public void scanStopBeginOrEnd() {
		try {
			String sql = "SELECT stop_id FROM xb_order_stop WHERE stop_stat=0 AND stop_begin<NOW() AND stop_end>NOW()";
			List<Map<String, Object>> mapList = daoUtil.checkinDao.queryMapList(sql);

			exceptionHandler.getLog().info("【扫描停用单开始或结束·开始】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int stopId = (int) map.get("stop_id");
				try {
					timerTasksService.autoBeginAndEndStop(stopId, OperType.BEGIN);
				} catch (Exception e) {
					//避免被调用方法抛出后结束循环
				}
			}
			
			// 要结束的
			sql = "SELECT stop_id FROM xb_order_stop WHERE stop_stat=1 AND stop_end<NOW()";
			mapList = daoUtil.checkinDao.queryMapList(sql);
			exceptionHandler.getLog().info("【扫描停用单开始或结束·结束】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int stopId = (int) map.get("stop_id");
				try {
					timerTasksService.autoBeginAndEndStop(stopId, OperType.END);
				} catch (Exception e) {
					//避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("扫描过期要退房的单报错", e);// 要用logActionException而不能用logServiceException,否则记录不了报错日志
		}
	}
	
	/**
	 * 扫描需要自动退房的单，去呼呼来的单也扫描<br>
	 * 扫描条件: ①已支付 ②已入住 ③当天<br>
	 */
	@Transactional
	public void scanCheckoutTime() {
		try {
			Date now = DateUtil.getCurDateTime();
			String nowDate = DateUtil.dateToYrMonDay_(now);
			String nowHour = DateUtil.dateToString(now, DateUtil.hour);
			if (!nowHour.equals("12")) {
				return;
			}

			String sql = "SELECT b.checkin_id checkinId FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND a.stat=1 AND DATE_FORMAT(b.checkout_time,'%Y-%m-%d')=? AND b.stat=1";
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { nowDate });
			exceptionHandler.getLog().info("【自动退房进程】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int checkinId = (int) map.get("checkinId");
				try {
					timerTasksService.autoCheckout(checkinId);// 独立事务
				} catch (Exception e) {
					// 避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("扫描过期要退房的单报错", e);// 要用logActionException而不能用logServiceException,否则记录不了报错日志
		}
	}

	/**
	 * 扫描快要超时未支付的订单,短信提醒支付<br>
	 * 可能会存在的问题:<br>
	 * ① 新建的订单使用的是应用服务器的时间，而checkout使用的是数据库的时间而不是应用服务器的时间。可能存在隐患。
	 * 例如服务器时间快DB数据库时间10分钟，会导致过了第10分钟就开发发警告，而不是20分钟(30分钟超时)。要不要改？<br>
	 * ② 单线程效率问题<br>
	 */
	@Transactional
	public void scanWarningOrder() {
		try {
			// source 0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS // rice说不要扫描去呼呼过来的单进行提醒
			String sql = "SELECT order_id orderId FROM xb_order WHERE stat=0 AND expire_time>=NOW() AND expire_time<=DATE_ADD(NOW(), INTERVAL 10 MINUTE) AND source in (0,2,3,4) AND ex_hold1=''";
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql);
			exceptionHandler.getLog().info("【订单超时警告进程】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int orderId = (int) map.get("orderId");
				try {
					timerTasksService.notifyUnpaidUser(orderId);// 独立事务
				} catch (Exception e) {
					//避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);
		}
	}

	/**
	 * 取消超时订单<br>
	 * 可能会存在的问题:<br>
	 * ① 使用的DB服务器的时间<br>
	 * ② 单线程效率问题<br>
	 */
	@Transactional
	public void scanCancelOrder() {
		try {
			// source 0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS // rice说不要扫描去呼呼过来的单进行提醒
			String sql = "SELECT order_id orderId FROM xb_order WHERE stat=0 AND expire_time<NOW() AND expire_time>DATE_ADD(NOW(), INTERVAL -10 MINUTE) AND source in (0,2,3,4) AND ex_hold2=''";
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql);
			exceptionHandler.getLog().info("【订单超时取消进程】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int orderId = (int) map.get("orderId");
				try {
					timerTasksService.cancelUnpaid(orderId);// 独立事务
				} catch (Exception e) {
					//避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);
		}
	}
	
	/**
	 * 每天早上十点钟扫描预退房的,提醒,抛事件
	 */
	@Transactional
	public void scanPreCheckoutHint() {
		try {
			// source 0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS // rice说不要扫描去呼呼过来的单进行提醒
			String sql = "SELECT b.checkin_id checkinId FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND a.stat=1 AND b.stat=1 AND b.chg_room_flag!=1 AND DATE_FORMAT(b.checkout_time,'%Y-%m-%d')=? AND b.precheckout_flag=0";
			List<Map<String, Object>> mapList = daoUtil.orderMgntDao.queryMapList(sql, new Object[] { DateUtil.getCurDateStr() });
			System.out.println("【扫描预退房】" + mapList.size());
			exceptionHandler.getLog().info("【扫描预退房】" + mapList.size());
			for (Map<String, Object> map : mapList) {
				int checkinId = (int) map.get("checkinId");
				try {
					timerTasksService.preCheckoutHint(checkinId);// 独立事务
				} catch (Exception e) {
					// 避免被调用方法抛出后结束循环
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);
		}
	}
}
