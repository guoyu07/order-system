package cn.com.xbed.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbSmsValid;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.Base64ImageUtil;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.MD5Util;
import cn.com.xbed.app.commons.enu.IdCardType;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;

@Service
@Transactional
public class LodgerService {
	@Resource
	private ImageCommon imageCommon;
	@Resource
	private ValidService validService;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LodgerService.class));
	@Resource
	private DaoUtil daoUtil;
	
	public List<XbLodger> queryLodgerInfo(Integer lodgerId, String mobile, String lodgerName, String idCardNo) {
		try {
			StringBuffer sql = new StringBuffer("select * from xb_lodger where 1=1 ");
			List<Object> params = new ArrayList<>();
			if (lodgerId != null) {
				sql.append("and lodger_id=?");
				params.add(lodgerId);
			}
			if (mobile != null && mobile.length() > 0) {
				sql.append(" and mobile=?");
				params.add(mobile);
			}
			if (lodgerName != null && lodgerName.length() > 0) {
				sql.append(" and lodger_name=?");
				params.add(lodgerName);
			}
			if (idCardNo != null && idCardNo.length() > 0) {
				sql.append(" and id_card like ?");
				params.add("%" + idCardNo + "%");
			}
			return daoUtil.lodgerDao.queryForMultiRow(XbLodger.class, sql.toString(), params.toArray(), true);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	
	public String uploadIdCardPic(int lodgerId, IdCardType idCardType, String base64code, String idCardNo) {
		// 照片和证件号两者必修改其中之一
		try {
			if ((base64code == null || base64code.length() == 0) && (idCardNo == null || idCardNo.length() == 0)) {
				throw new RuntimeException("必须修改照片或身份证号之一");
			}
			boolean isModIdCardNo = idCardNo == null || idCardNo.length() == 0 ? false : true;// 是否修改身份证号码
			boolean isModIdCardPic = base64code == null || base64code.length() == 0 ? false : true;// 是否修改身份证照片
			Date now = DateUtil.getCurDateTime();
			
			// 记录到xb_lodger表
			String idCardPicName = imageCommon.getIdCardPicName(lodgerId, now, idCardType);
			XbLodger lodger = new XbLodger();
			lodger.setLodgerId(lodgerId);// 主键
			if (isModIdCardNo) {
				lodger.setIdCard(idCardNo);
			}
			if (isModIdCardPic) {
				if (idCardType == IdCardType.FRONT) {
					lodger.setCardFront(idCardPicName);
				} else {
					lodger.setCardBack(idCardPicName);
				}
			}
			int affected = daoUtil.lodgerDao.updateEntityByPk(lodger);
			AssertHelper.notZeroNotNegative(affected, "更新数错误,请检查");

			if (isModIdCardPic) {
				// 生成原始文件
				base64code = base64code.replaceAll(" ", "+");// HTTP协议把加号当成空格,要还原回去,避免前端忘记替换
				String oriPath = imageCommon.getIdCardPicOriPath();
				String oriFilePath = oriPath + "/" + idCardPicName;// 原始图片路径+文件名+扩展名
				Base64ImageUtil.generateImage(base64code, oriFilePath);
				
				// 生成缩略图
				String rootPath = imageCommon.getIdCardPicRootPath();
				imageCommon.generateThumb(rootPath, idCardPicName);
				return idCardPicName;
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 验证手机号
	 * 
	 * @param mobile
	 * @param password 明文密码
	 * @return
	 */
	public XbLodger queryLodger(String mobile, String password) {
		try {
			XbLodger lodger = daoUtil.lodgerDao.queryForSingleRow(XbLodger.class, "select * from xb_lodger where mobile=?",
					new Object[] { mobile },true);
			if (lodger == null || lodger.getStat() == AppConstants.Lodger_stat.DELETED_2) {
				throw exceptionHandler.newErrorCodeException("-1", "无此用户");
			}

			String salt = lodger.getSalt();
			String cipherPwdFromDb = lodger.getPassword();
			String newGenCipherPwd = MD5Util.getMd5Passwd(password, salt);
			if (!newGenCipherPwd.equals(cipherPwdFromDb)) {
				throw exceptionHandler.newErrorCodeException("-2", "密码错误");
			} else {
				return lodger;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public Map<String, Object> queryLodger(int lodgerId, String password) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			XbLodger lodger = daoUtil.lodgerDao.findByPk(lodgerId);
			if (lodger == null || lodger.getStat() == AppConstants.Lodger_stat.DELETED_2) {
				resultMap.put("retcode", "-1");// 无此用户
				resultMap.put("retval", null);
				return resultMap;
			}

			String salt = lodger.getSalt();
			String cipherPwdFromDb = lodger.getPassword();
			String newGenCipherPwd = MD5Util.getMd5Passwd(password, salt);
			if (!newGenCipherPwd.equals(cipherPwdFromDb)) {
				resultMap.put("retcode", "-2");// 密码错误
				resultMap.put("retval", null);
				return resultMap;
			} else {
				resultMap.put("retcode", "1");// 验证通过
				resultMap.put("retval", lodger);
				return resultMap;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	public void updateFollow(int lodgerId, int isFollow) {
		try {
			if (isFollow != AppConstants.Lodger_isFollow.FOLLOW_2 && isFollow != AppConstants.Lodger_isFollow.NOT_FOLLOW_1) {
				throw new RuntimeException("传参错误isFollow[" + isFollow + "]");
			}
			XbLodger lodger = new XbLodger();
			lodger.setLodgerId(lodgerId);
			lodger.setIsFollow(isFollow);
			daoUtil.lodgerDao.updateEntityByPk(lodger);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public void updateOpenId(int lodgerId, String oldOpenId, String newOpenId, XbLodger foundLodger) {
		try {
			exceptionHandler.getLog().info("lodgerId:" + lodgerId + ",newOpenId:" + newOpenId + ",oldOpenId:" + oldOpenId);
			if(newOpenId != null && newOpenId.length() > 0) {
				if (oldOpenId == null || !oldOpenId.equals(newOpenId)) {
					XbLodger lodger = new XbLodger();
					lodger.setLodgerId(lodgerId);
					lodger.setOpenId(newOpenId);
					daoUtil.lodgerDao.updateEntityByPk(lodger);
					foundLodger.setOpenId(newOpenId);/// 
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public int updatePwd(Integer lodgerId, String newPassword) {
		try {
			String newSalt = MD5Util.getRandomStr();
			String newPasswordCipher = MD5Util.getMd5Passwd(newPassword, newSalt);
			XbLodger newLodger = new XbLodger();
			newLodger.setLodgerId(lodgerId);
			newLodger.setPassword(newPasswordCipher);
			newLodger.setPassword2(newPassword);
			newLodger.setSalt(newSalt);
			newLodger.setHasChgPwd(AppConstants.Lodger_hasChgPwd.HAS_CHG_2);
			return daoUtil.lodgerDao.updateEntityByPk(newLodger);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 根据手机号码来进行重置密码操作
	 * @param mobile
	 * @param newPassword
	 * @return
	 */
	public int updatePwdByMobile(String mobile, String newPassword) {
		try {
			String newSalt = MD5Util.getRandomStr();
			String newPasswordCipher = MD5Util.getMd5Passwd(newPassword, newSalt);
			return daoUtil.lodgerDao.updateOrDelete("update xb_lodger set password=?,password2=?,salt=?,has_chg_pwd=? where mobile=?",
					new Object[] { newPasswordCipher, newPassword, newSalt, AppConstants.Lodger_hasChgPwd.HAS_CHG_2, mobile });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 存在该手机号返回true
	public boolean existSameLodger(String mobile) {
		try {
			Long count = daoUtil.lodgerDao.queryForSingleColumnAndRow(Long.class,
					"select count(*) from xb_lodger where mobile=?", new Object[] { mobile }, true);
			return count != null && count > 0 ? true : false; 
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 根据lodgerId或mobile查询openId,查无返回null
	 * @param lodgerId
	 * @param mobile
	 * @return
	 */
	public String findOpenIdByMobileOrLodgerId(Integer lodgerId, String mobile) {
		try {
			if (lodgerId == null && StringUtils.isBlank(mobile)) {
				throw new RuntimeException("入参错误");
			}
			String sql = "select open_id from xb_lodger where 1=1";
			List<Object> params = new ArrayList<>();
			if (lodgerId != null) {
				sql += " and lodger_id=?";
				params.add(lodgerId);
			}
			if (StringUtils.isNotBlank(mobile)) {
				sql += " and mobile=?";
				params.add(mobile);
			}
			List<Map<String, Object>> result = daoUtil.lodgerDao.queryMapList(sql, params.toArray());
			if (!result.isEmpty()) {
				return (String) result.get(0).get("open_id");
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 用户重置密码
	 * @param resultMap	参数集合
	 * @param mobile	手机号码
	 * @param validId	随机码表的ID
	 * @param validValue	用户输入的随机码
	 * @param passwordNew	新密码
	 */
	public void resetPassword(String mobile, String validId, String validValue, String passwordNew) {
		XbSmsValid smsValid = validService.findSmsValidByValidId(Integer.parseInt(validId));
		// 判断是否存在记录
		if (null != smsValid) {

			// 判断验证码输入是否正确
			if (validValue.equals(smsValid.getRandomNo())) {

				// 判断验证码是否超时
				if (new Date().getTime() < smsValid.getExpireDtm().getTime()) {

					// 开始重置密码
					if (0 == updatePwdByMobile(mobile, passwordNew)) {
						throw exceptionHandler.newErrorCodeException("-1", "不存在该用户");
					}
				} else {
					// 验证已经超时
					throw exceptionHandler.newErrorCodeException("-2", "验证码已经超时");
				}

			} else {
				// 验证码输入错误
				throw exceptionHandler.newErrorCodeException("-3", "验证码输入错误");
			}

		} else {
			// 不存记录的情况
			throw exceptionHandler.newErrorCodeException("-4", "不存在该记录");
		}
	}

	/**
	 * 通过手机号码查询用户id
	 * 
	 * @param mobile
	 * @return
	 */
	public Integer findByMobile(String mobile) {
		AssertHelper.notNullEmptyBlank("传参错误", mobile);
		XbLodger lodger = daoUtil.lodgerDao.findByMobile(mobile);
		if (lodger != null) {
			return lodger.getLodgerId();
		} else {
			return null;
		}
	}

	/**
	 * 根据手机号码和登录密码进行修改名字
	 * @param resultMap
	 * @param mobile
	 * @param password
	 * @param lodgerName
	 */
	public void updateLogerName(String mobile, String lodgerName) {
		int result = daoUtil.lodgerDao.updateOrDelete("update xb_lodger set lodger_name=? where mobile=?", new Object[] { lodgerName, mobile });
		if (0 == result) {
			throw exceptionHandler.newErrorCodeException("-1", "手机号码输入有误");
		}
	}
	
	
	public List<Map<String, Object>> queryIdCardInfo(String checkinId) {
		try {
			return daoUtil.lodgerDao.queryMapList(
					"SELECT b.lodger_name lodgerName,b.id_card idCard,b.card_front cardFront,b.card_back cardBack FROM xb_checkin a,xb_lodger b WHERE a.lodger_id=b.lodger_id AND a.checkin_id=?",
					new Object[] { checkinId });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
