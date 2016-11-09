package cn.com.xbed.app.dao;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCustAdvice;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class CustAdviceDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CustAdviceDao.class));

	public boolean addCustAdvice(XbCustAdvice entity) {
		try {
			return this.insertEntity(entity, "custAdviceId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
