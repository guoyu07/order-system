package cn.com.xbed.app.service.maintainmodule;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class MaintainTool {
	@Resource
	private DaoUtil daoUtil;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(MaintainTool.class));

	// 维护去呼呼otaOrderNo(在去呼呼新建订单后我们这里需要更新进来)
	public int maintainOtaOrderNo(int orderId, String otaOrderNo) {
		try {
			if (StringUtils.isBlank(otaOrderNo)) {
				throw new RuntimeException("维护otaOrderNo.入参错误");
			}
			XbOrder orderInfo = new XbOrder();
			orderInfo.setOrderId(orderId);
			if (otaOrderNo != null) {
				orderInfo.setOtaOrderNo(otaOrderNo);
			}
			return daoUtil.orderMgntDao.updateEntityByPk(orderInfo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 维护入住单otaStayId
	public int maintainOtaStayId(int checkinId, String otaStayId) {
		try {
			if (StringUtils.isBlank(otaStayId)) {
				throw new RuntimeException("维护otaStayId.入参错误");
			}
			XbCheckin checkinInfo = new XbCheckin();
			checkinInfo.setCheckinId(checkinId);
			if (otaStayId != null) {
				checkinInfo.setOtaStayId(otaStayId);
			}
			return daoUtil.checkinDao.updateEntityByPk(checkinInfo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 维护入住单otaStayId
	public int maintainOpenPwd(int checkinId, String openPwd) {
		try {
			if (StringUtils.isBlank(openPwd)) {
				throw new RuntimeException("维护openPwd.入参错误");
			}
			XbCheckin checkinInfo = new XbCheckin();
			checkinInfo.setCheckinId(checkinId);
			checkinInfo.setOpenPwd(openPwd);
			return daoUtil.checkinDao.updateEntityByPk(checkinInfo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
