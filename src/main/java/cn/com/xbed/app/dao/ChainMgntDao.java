package cn.com.xbed.app.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class ChainMgntDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ChainMgntDao.class));

	public boolean add(XbChain entity) {
		try {
			return this.insertEntity(entity, "chainId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbChain findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbChain.class, "select * from xb_chain where chain_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbChain entity) {
		try {
			if (entity.getChainId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "chainId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbChain> findAll() {
		try {
			return this.queryForMultiRow(XbChain.class, "select * from xb_chain");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
