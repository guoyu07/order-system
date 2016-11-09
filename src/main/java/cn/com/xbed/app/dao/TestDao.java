package cn.com.xbed.app.dao;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class TestDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TestDao.class));

	public void test() {
		try {
			throw exceptionHandler.newErrorCodeException("-3", "来自dao层");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
