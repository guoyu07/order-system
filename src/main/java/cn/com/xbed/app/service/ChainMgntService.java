package cn.com.xbed.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class ChainMgntService {
	@Resource
	private DaoUtil daoUtil;
	
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ChainMgntService.class));

	public XbChain findByChainId(Integer chainId) {
		try {
			return daoUtil.chainMgntDao.queryForSingleRow(XbChain.class,
					"select * from xb_chain where chain_id=?", new Object[] { chainId });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	public XbChain queryChainInfo(int chainId) {
		try {
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(chainId);
			if (chainInfo != null) {
				Date now = DateUtil.getCurDateTime();
				Date d = chainInfo.getLastModifyDtm();
				if (chainInfo.getLastModifyDtm() == null || DateUtil.compareDate(d, now) < 0){
					String sql = "SELECT count(order_id) as predictCheckout FROM xb_order WHERE stat=? AND DATE_FORMAT(depart_time,'%Y-%m-%d')=?";
					List<Map<String,Object>> list = daoUtil.chainMgntDao.queryMapList(sql, new Object[]{AppConstants.Order_stat.PAYED_1, DateUtil.dateToYrMonDay_(now)});
					if (list != null && list.size() > 0) {
						Long count = (Long) list.get(0).get("predictCheckout");
						int predictCheckout = count == null ? 0 : count.intValue(); 
						XbChain newChainInfo = new XbChain();
						newChainInfo.setChainId(chainId);
						newChainInfo.setLastModifyDtm(now);
						newChainInfo.setPredictCheckout(predictCheckout);
						daoUtil.chainMgntDao.updateEntityByPk(newChainInfo);
						
						chainInfo.setLastModifyDtm(now);
						chainInfo.setPredictCheckout(predictCheckout);
					}
				}
			}
			return chainInfo;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 只查xb_chain,不去尝试更新预计离店数
	 * @param chainId
	 * @return
	 */
	public List<XbChain> queryAllChainInfo() {
		try {
			return daoUtil.chainMgntDao.findAll();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
