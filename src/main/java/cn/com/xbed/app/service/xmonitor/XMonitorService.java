package cn.com.xbed.app.service.xmonitor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class XMonitorService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(XMonitorService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	@Resource
	private WarningSystemTool warningSystemTool;

	public Map<String, Object> qryNextLodger(int roomId, Date today) {
		exceptionHandler.getLog().info("qryNextLodger入参,roomId:"+roomId+",today:" + DateUtil.getYearMonDayHrMinSecStr_(today));
		try {
			List<Map<String, Object>> list = commonService.queryTodayCheckinOrderByRoomId(roomId, today);
			if (!list.isEmpty()) {
				int lodgerId = (int) list.get(0).get("lodgerId");
				int checkinId = (int) list.get(0).get("checkinId");
				String orderNo = (String) list.get(0).get("orderNo");
				Map<String, Object> param = new HashMap<>();
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
				XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
				XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
				param.put("lodgerInfo", lodgerInfo);
				param.put("roomInfo", roomInfo);
				param.put("checkinInfo", checkinInfo);
				param.put("money", checkinInfo.getChkinPrice()/100);
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
				param.put("checkinDate", DateUtil.getYearMonDayStr_(checkinInfo.getCheckinTime()));
				param.put("checkoutDate", DateUtil.getYearMonDayStr_(checkinInfo.getCheckoutTime()));
				String openId = lodgerInfo.getOpenId() == null ? "" : lodgerInfo.getOpenId();
				param.put("openId", openId);
				exceptionHandler.getLog().info("清洁完成通知XMonitor:" + param);
				return param;
			} else {
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
				String roomName = roomInfo == null ? "" : roomInfo.getRoomName();
				exceptionHandler.getLog().info("房间清洁完了,但没有今天入住的,无需提醒可以办理入住,roomId:" + roomId + "roomName:" + roomName + ",日期:"
						+ DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	
	public Map<String, Object> qryNowlodger(int roomId) {
		exceptionHandler.getLog().info("qryNowlodger入参,roomId:"+roomId);
		try {
			String sql = "SELECT b.order_id,b.checkin_id,b.room_id,b.lodger_id FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND b.chg_room_flag!=1 AND a.stat=1 AND b.stat=1 AND b.room_id=? AND b.checkin_time<NOW() AND b.checkout_time>NOW()";
			List<Map<String, Object>> list = daoUtil.checkinDao.queryMapList(sql, new Object[]{roomId});
			if (!list.isEmpty()) {
				int lodgerId = (int) list.get(0).get("lodger_id");
				int checkinId = (int) list.get(0).get("checkin_id");
				Map<String, Object> param = new HashMap<>();
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
				XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
				XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getCheckinId());
				XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
				param.put("lodgerInfo", lodgerInfo);
				param.put("roomInfo", roomInfo);
				param.put("checkinInfo", checkinInfo);
				param.put("orderInfo", orderInfo);
				exceptionHandler.getLog().info("查询现任XMonitor:" + param);
				return param;
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public Map<String, Object> qryRoomInfo(Integer roomId) {
		exceptionHandler.getLog().info("qryRoomInfo入参,roomId:"+roomId);
		Map<String, Object> result = new HashMap<>();
		try {
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			
			result.put("roomInfo", roomInfo);
			result.put("chainInfo", chainInfo);
			
			return result;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
