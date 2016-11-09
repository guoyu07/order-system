package cn.com.xbed.app.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbBfSendsmsRec;
import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class BreakfastService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(BreakfastService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonSms commonSms;

	// 早餐系统发送短信并记录下发送记录
	public void sendSmsAndLogRecord(String mobile, String content) {
		try {
			int recordId = commonSms.sendSms(mobile, content);

			Date now = DateUtil.getCurDateTime();
			XbBfSendsmsRec bfSendsmsRec = new XbBfSendsmsRec();
			bfSendsmsRec.setCreateDtm(now);
			bfSendsmsRec.setMobile(mobile);
			bfSendsmsRec.setSmsContent(content);
			bfSendsmsRec.setRecordId(recordId);
			daoUtil.bfSendsmsRecDao.addAndGetPk(bfSendsmsRec);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public Map<String, String> queryCheckinInfo(int checkinId, int lodgerId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			if (checkinInfo.getLodgerId() != lodgerId) {
				throw new RuntimeException("该用户无该入住单:lodgerId[" + lodgerId + "],checkinId[" + checkinId + "]");
			}
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			AssertHelper.notNull("数据问题,无lodgerInfo、orderInfo或roomInfo", lodgerInfo, orderInfo, roomInfo);
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			AssertHelper.notNull("数据问题,无chainInfo", chainInfo);

			Map<String, String> map = new LinkedHashMap<>();
			map.put("lodgerId", lodgerInfo.getLodgerId() + "");
			map.put("lodgerName", lodgerInfo.getLodgerName());
			map.put("mobile", lodgerInfo.getMobile());
			map.put("orderId", orderInfo.getOrderId() + "");
			map.put("orderNo", orderInfo.getOrderNo());
			map.put("orderStat", orderInfo.getStat() + "");
			map.put("checkinId", checkinInfo.getCheckinId() + "");
			map.put("checkinTime", DateUtil.dateToString(checkinInfo.getCheckinTime(), DateUtil.yrMonDayHrMinSec_));
			map.put("checkoutTime", DateUtil.dateToString(checkinInfo.getCheckoutTime(), DateUtil.yrMonDayHrMinSec_));
			map.put("checkinStat", checkinInfo.getStat() + "");
			map.put("roomId", roomInfo.getRoomId() + "");
			map.put("chainId", chainInfo.getChainId() + "");
			map.put("chainName", chainInfo.getName());
			map.put("province", roomInfo.getProvince());
			map.put("city", roomInfo.getCity());
			map.put("district", roomInfo.getDistrict());
			map.put("roomAddr", roomInfo.getAddr());
			map.put("roomNo", roomInfo.getRoomName());

			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
