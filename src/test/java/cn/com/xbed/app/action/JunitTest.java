package cn.com.xbed.app.action;


import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml", "classpath:spring-db.xml", "classpath:spring-mvc.xml", "classpath:spring-mq.xml" })
public class JunitTest {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(JunitTest.class));

	@Resource
	private DaoUtil daoUtil;

	@Test
	public void test() {
		System.out.println("****************************************");
//		XbQhhSyncLog entity = new XbQhhSyncLog();
//		entity.setCreateDtm(DateUtil.getCurDateTime());
//		entity.setDatagram("会运行吗");
//		entity.setSucc(AppConstants.QhhSyncLog_succ.SUCC_1);
//		entity.setOtaOrderNo("");
//		entity.setOrderStatusCode("");
//		entity.setOrderMan("");
//		entity.setOrderMobile("");
//		Long id = (Long) daoUtil.qhhSyncLogDao.addAndGetPk(entity);
	}

	
}
