package cn.com.xbed.app.service.maintainmodule;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckinExt;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderExt;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.maintainmodule.vo.OpenPwdEntity;
import cn.com.xbed.app.service.maintainmodule.vo.OtaStayIdEntity;
import cn.com.xbed.app.service.otamodule.vo.OtaNewOrderOut;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;

@Service
@Transactional
public class MaintainModuleBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private MaintainTool maintainTool;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(MaintainModuleBase.class));

	
	
	public void chgOverstayFlag(int oriCheckinId, int newCheckinId) {
		try {
			XbCheckin oriCheckin = new XbCheckin();
			oriCheckin.setCheckinId(oriCheckinId);
			oriCheckin.setOverstayFlag(AppConstants.Checkin_overstayFlag.ORI_ROOM_1);
			daoUtil.checkinDao.updateEntityByPk(oriCheckin);
			
			XbCheckin newCheckin = new XbCheckin();
			newCheckin.setCheckinId(newCheckinId);
			newCheckin.setOverstayFlag(AppConstants.Checkin_overstayFlag.NEW_ROOM_2);
			daoUtil.checkinDao.updateEntityByPk(newCheckin);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 标记发送了"预订成功短信" OMS展示要用
	 * 
	 * @param orderId
	 */
	public void markHasSendBookSms(int orderId) {
		try {
			this.maintainOrderExt(orderId, null, "事件系统维护", null, null, null);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 标记发送了"办理入住短信" OMS展示要用
	 * 
	 * @param orderId
	 */
	public void markHasSendCheckinSms(int checkinId) {
		try {
			this.maintainCheckinExt(checkinId, null, null, "事件系统维护", null, null);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 维护一些发送短信的
	 * 
	 * @param orderId
	 * @param ext1
	 *            扩展字段1:预定发送给下单人的短信ID
	 * @param ext2
	 *            扩展字段2:预定发送给首个入住人的短信ID
	 * @param ext3
	 *            帮客户注册后发送短信NEW_CREATE_ACCT_SMS
	 * @param ext4
	 *            【废弃】扩展字段4:办理入住,发送给checkin阶段添加的入住人的短信ID,lodgerId:短信ID
	 * @param ext5
	 *            未用
	 */
	public void maintainOrderExt(int orderId, String ext1, String ext2, String ext3, String ext4, String ext5) {
		try {
			if (StringUtils.isBlank(ext1) && StringUtils.isBlank(ext2) && StringUtils.isBlank(ext3) && StringUtils.isBlank(ext4)
					&& StringUtils.isBlank(ext5)) {
				return;
			}
			StringBuffer buf = new StringBuffer("update xb_order_ext set ");
			List<Object> params = new ArrayList<>();
			if (StringUtils.isNotBlank(ext1)) {
				buf.append(" ext1=?,");
				params.add(ext1);
			}
			if (StringUtils.isNotBlank(ext2)) {
				buf.append(" ext2=?,");
				params.add(ext2);
			}

			if (StringUtils.isNotBlank(ext3)) {
				buf.append(" ext3=?,");
				params.add(ext3);
			}
			if (StringUtils.isNotBlank(ext4)) {
				buf.append(" ext4=?,");
				params.add(ext4);
			}
			if (StringUtils.isNotBlank(ext5)) {
				buf.append(" ext5=?,");
				params.add(ext5);
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			buf.append(" where order_id=?");
			params.add(orderId);
			daoUtil.orderExtDao.updateOrDelete(buf.toString(), params.toArray());
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	
	/**
	 * 维护一些发送短信的
	 * 
	 * @param checkinId
	 * @param ext1
	 *            扩展字段1:重新发送门锁密码的短信ID,格式【lodgerId:open_pwd】
	 * @param ext2
	 *            扩展字段2:房态控制单发送开门密码短信ID
	 * @param ext3
	 *            扩展字段3:办理入住,发送给首个入住人的入住短信ID
	 * @param ext4
	 *            扩展字段4:办理入住,发送给checkin阶段添加的入住人的短信ID,lodgerId:短信ID
	 * @param ext5
	 */
	public void maintainCheckinExt(int checkinId, String ext1, String ext2, String ext3, String ext4, String ext5) {
		try {
			if (StringUtils.isBlank(ext1) && StringUtils.isBlank(ext2) && StringUtils.isBlank(ext3) && StringUtils.isBlank(ext4)
					&& StringUtils.isBlank(ext5)) {
				return;
			}
			StringBuffer buf = new StringBuffer("update xb_checkin_ext set ");
			List<Object> params = new ArrayList<>();
			if (StringUtils.isNotBlank(ext1)) {
				buf.append(" ext1=?,");
				params.add(ext1);
			}
			if (StringUtils.isNotBlank(ext2)) {
				buf.append(" ext2=?,");
				params.add(ext2);
			}

			if (StringUtils.isNotBlank(ext3)) {
				buf.append(" ext3=?,");
				params.add(ext3);
			}
			if (StringUtils.isNotBlank(ext4)) {
				buf.append(" ext4=?,");
				params.add(ext4);
			}
			if (StringUtils.isNotBlank(ext5)) {
				buf.append(" ext5=?,");
				params.add(ext5);
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			buf.append(" where checkin_id=?");
			params.add(checkinId);
			daoUtil.orderExtDao.updateOrDelete(buf.toString(), params.toArray());
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}



	public void maintain(int orderId, OtaNewOrderOut otaNewOrderOut) {
		try {
			if (otaNewOrderOut != null) {
				String otaOrderNo = otaNewOrderOut.getOtaOrderNo();
				this.maintainOtaOrderNo(orderId, otaOrderNo);
				int checkinId = otaNewOrderOut.getCheckinId();
				String otaStayId = otaNewOrderOut.getOtaStayId();
				List<OtaStayIdEntity> otaStayIdList = new ArrayList<>();
				otaStayIdList.add(new OtaStayIdEntity(checkinId, otaStayId));
				this.maintainOtaStayId(orderId, otaStayIdList);
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 不维护的时候传maintainInput null即可,部分不维护,部分传null即可
	 * 
	 * @param maintainInput
	 */
	public void maintainOtaOrderNo(int orderId, String otaOrderNo) {
		try {
			if (StringUtils.isNotBlank(otaOrderNo)) {
				maintainTool.maintainOtaOrderNo(orderId, otaOrderNo);
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void maintainOtaStayId(int orderId, List<OtaStayIdEntity> otaStayIdList) {
		try {
			if (otaStayIdList != null) {
				for (OtaStayIdEntity maintainEntity : otaStayIdList) {
					int checkinId = maintainEntity.getCheckinId();
					String otaStayId = maintainEntity.getOtaStayId();
					maintainTool.maintainOtaStayId(checkinId, otaStayId);
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void maintainOpenPwd(int checkinId, String openPwd) {
		if (StringUtils.isNotBlank(openPwd)) {
			maintainTool.maintainOpenPwd(checkinId, openPwd);
		}
	}
}
