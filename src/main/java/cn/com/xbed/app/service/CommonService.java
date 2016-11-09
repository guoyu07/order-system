package cn.com.xbed.app.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.MD5Util;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;

@Service
@Transactional
public class CommonService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CommonService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private EventService eventService;



	

	/**
	 * 注册接口,已注册则返回原来的lodger_id
	 * @param name
	 * @param mobile
	 * @param password 明文密码
	 * @param registerDate
	 * @param remark
	 * @param source
	 * @param hasChgPwd
	 * @return
	 */
	public int register(String name, String mobile, String password, Date registerDate, String remark, int source, int hasChgPwd) {
		try {
			XbLodger lodger = daoUtil.lodgerDao.findByMobile(mobile);
			if (lodger != null) {
				return lodger.getLodgerId();
			}
			Date now = registerDate == null ? DateUtil.getCurDateTime() : registerDate;
			XbLodger newLodger = new XbLodger();
			newLodger.setLodgerName(name);
			newLodger.setMobile(mobile);
			newLodger.setCreateTime(now);
			String salt = MD5Util.getRandomStr();
			String miwen = MD5Util.getMd5Passwd(password, salt);
			newLodger.setPassword(miwen);
			newLodger.setPassword2(password);
			newLodger.setSalt(salt);
			newLodger.setRemark(remark);
			newLodger.setSource(source);
			newLodger.setHasChgPwd(hasChgPwd);
			Long newGenLodgerId = (Long) daoUtil.lodgerDao.addAndGetPk(newLodger);
			return newGenLodgerId.intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 判断要不要创建用户,password为密码,如果不传则默认是手机后六位
	/**
	 * 判断xb_lodger表里有没有该号码的用户,有就返回已有的ID,无的话新增并且返回新的ID
	 * @param contactMobile 号码
	 * @param contactName 用户名
	 * @param remark 注释
	 * @param source 来源
	 * @param password 密码,不传则给随机
	 * @param hasChgPwd 是否更改了密码
	 * @param throwNewCreateEvent 是否在新建用户的时候要抛事件
	 * @return
	 */
	public Map<String, Object> judgeIfNeedCreateLodger(String contactMobile, String contactName, String remark, int source, String password,
			int hasChgPwd, boolean throwNewCreateEvent) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			XbLodger lodger = daoUtil.lodgerDao.findByMobile(contactMobile);
			if (lodger == null) {
				if (password == null || password.length() == 0) {
					password = contactMobile.substring(5);// 后六位
				}
				int lodgerId = register(contactName, contactMobile, password, null, remark, source, hasChgPwd);
				resultMap.put("isNewCreate", true);
				resultMap.put("lodgerId", lodgerId);
				
				if (throwNewCreateEvent) {
					eventService.throwRegisterUserEvent(lodgerId);
				}
				return resultMap;
			} else {
				resultMap.put("isNewCreate", false);
				resultMap.put("lodgerId", lodger.getLodgerId());
				return resultMap;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}


	/**
	 * 查询该房间当天有需要办理入住的
	 * 
	 * @param roomId
	 */
	public List<Map<String, Object>> queryTodayCheckinOrderByRoomId(int roomId, Date today) {
		try {
			String sql = "SELECT b.lodger_id lodgerId,a.order_id orderId,a.order_no orderNo,a.stat orderStat,b.checkin_id checkinId,b.stat checkinStat,b.checkin_time checkinTime,b.checkout_time checkoutTime,b.room_id roomId "
					+ "FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND a.stat=1 AND b.stat=0 AND a.order_type!=2"
					+ " AND b.room_id=? AND DATE_FORMAT(checkin_time,'%Y-%m-%d')=?";
			return daoUtil.chainMgntDao.queryMapList(sql, new Object[] { roomId, DateUtil.getYearMonDayStr_(today) });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
