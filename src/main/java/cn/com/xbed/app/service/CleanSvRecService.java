package cn.com.xbed.app.service;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.ljh.CleanSystemService;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class CleanSvRecService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private EventService eventService;


	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanSvRecService.class));
	
	
	// 临时解决方案
	public void applyCleanServiceV2(int checkinId, String mobile, int period) {
		try {
			exceptionHandler.getLog().info("申请在住申请打扫,checkinId:" + checkinId + ",mobile:" + mobile);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			
			// 判断不能申请的情况
			if (checkinInfo.getStat() != AppConstants.Checkin_stat.CHECKIN_1) {
				throw exceptionHandler.newErrorCodeException("-3", "该单checkinId["+checkinId+"]非入住状态不能申请打扫");
			}
			
			// mobile和密码乱传的,不属于这个入住单
//			int checkinLodgerId = checkinInfo.getLodgerId();
//			int lodgerId = lodgerInfo.getLodgerId();
//			if (checkinLodgerId != lodgerId) {
//				throw exceptionHandler.newErrorCodeException("-4", "该号码lodgerId["+lodgerInfo.getLodgerId()+"]不属于这个入住单,不能申请");
//			}
			
			eventService.throwApplyCleanEvent(checkinId, period);
			cleanSystemService.sendApplyCleanCleanSheet(checkinId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("申请在住打扫失败,前台入参",e);
		}
	}
	
	private String getCleanApplyPeriod(int period) {
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
