package cn.com.xbed.app.service;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCustAdvice;
import cn.com.xbed.app.dao.CustAdviceDao;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class CustAdviceService {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CustAdviceService.class));

	public boolean addCustAdvice(XbCustAdvice entity) {
		try {
			entity.setCreateDtm(DateUtil.getCurDateTime());
			return daoUtil.custAdviceDao.addCustAdvice(entity);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	
}
