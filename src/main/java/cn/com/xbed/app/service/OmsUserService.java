package cn.com.xbed.app.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.OmsUser;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class OmsUserService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OmsUserService.class));
	@Resource
	private DaoUtil daoUtil;

	public OmsUser queryOmsUser(String account, String password) {
		try {
			return daoUtil.omsUserDao.queryForSingleRow(OmsUser.class, "select * from oms_user where account=? and password=?",
					new Object[] { account, password });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public int queryOmsUserId(String account) {
		try {
			List<Map<String, Object>> result = daoUtil.omsUserDao.queryMapList("select user_id userId from oms_user where account=?",
					new Object[] { account });
			if (!result.isEmpty()) {
				return (int) result.get(0).get("userId");
			}
			return -1;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
