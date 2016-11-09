package cn.com.xbed.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.CmLocation;
import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class LocationService {
	@Resource
	private DaoUtil daoUtil;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LocationService.class));

	public List<CmLocation> qryLocations(String provinceCode, String cityCode, String districtCode) {
		try {
			
			
			
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
