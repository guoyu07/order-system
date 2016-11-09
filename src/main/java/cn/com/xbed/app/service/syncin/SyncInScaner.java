package cn.com.xbed.app.service.syncin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.IFromQhh;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.dao.i.FromQhhDao;
import cn.com.xbed.app.service.syncin.SyncInBase.SyncStat;
import cn.com.xbed.app.service.syncin.vo.SyncOrdEntity;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;

@Service
public class SyncInScaner {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SyncInScaner.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private SyncInBase syncInBase;

	/**
	 * 扫描i_from_qhh并处理
	 */
	@Transactional
	public void scanSyncQhh() {
		try {
			// 扫描表
			exceptionHandler.getLog().info("scanSyncQhh..");
			String sql = "SELECT * FROM i_from_qhh WHERE sync_stat=0 ORDER BY create_dtm ASC";
			List<IFromQhh> iFromQhhList = daoUtil.fromQhhDao.queryForMultiRow(IFromQhh.class, sql, true);
			exceptionHandler.getLog().info(iFromQhhList.size());
			if (iFromQhhList.isEmpty()) {
				return;
			}
			// 将报文封装成实体
			List<SyncOrdEntity> entityList = new ArrayList<>();
			for (IFromQhh iFromQhh : iFromQhhList) {
				String datagram = iFromQhh.getOrderInfo();// 单条订单的报文(一单一房或多房)
				exceptionHandler.getLog().info("$同步报文" + datagram);
				SyncOrdEntity entity = this.getSyncOrdEntity(datagram, iFromQhh.getJobId());
				String chanelId = entity.getChannelID();
				if (StringUtils.isNotBlank(chanelId) && chanelId.equals("qhh")) {
					entityList.add(entity);
				}
			}

			// 要考虑: 查出相同otaOrderNo，但是不同的orderStatusCode
			Map<String, List<SyncOrdEntity>> resu = this.meargeResult(entityList);

			// 处理
			for (String otaOrderNo : resu.keySet()) {
				List<SyncOrdEntity> sameOtaOrderNoGroup = resu.get(otaOrderNo);
				SyncOrdEntity sync = getNeedHandle(sameOtaOrderNoGroup);// 获得需要处理的那一条
				exceptionHandler.getLog().info("$处理单个" + sync);
				boolean result = syncInBase.handleOne(sync);
				syncInBase.markAsDone(sameOtaOrderNoGroup, result ? SyncStat.SUCC : SyncStat.FAIL);
			}
		} catch (Exception e) {
			throw exceptionHandler.logActionException("扫描去呼呼同步本地出错", e);
		}
	}

	private SyncOrdEntity getSyncOrdEntity(String datagram, int jobId) {
		try {
			SyncOrdEntity entity = JsonHelper.parseJSONStr2T(datagram, SyncOrdEntity.class);
			entity.setJobId(jobId);
			return entity;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("获得SyncOrdEntity错误: " + datagram, e);
		}
	}

	/**
	 * 返回键是otaOrderNo，值是该otaOrderNo的所有操作
	 * 
	 * @param entityList
	 * @return
	 */
	private Map<String, List<SyncOrdEntity>> meargeResult(List<SyncOrdEntity> entityList) {
		Map<String, List<SyncOrdEntity>> mapList = new HashMap<>();

		for (SyncOrdEntity syncOrdEntity : entityList) {
			String otaOrderNo = syncOrdEntity.getOrderNo();
			if (!mapList.containsKey(otaOrderNo)) {
				List<SyncOrdEntity> l = new ArrayList<>();
				l.add(syncOrdEntity);
				mapList.put(otaOrderNo, l);
			} else {
				mapList.get(otaOrderNo).add(syncOrdEntity);
			}
		}
		return mapList;
	}

	/**
	 * 获得相同otaOrderNo的记录中需要处理的，同时存在未支付和已支付的则只要处理已支付的即可
	 * 
	 * @param list
	 */
	private SyncOrdEntity getNeedHandle(List<SyncOrdEntity> list) {
		if (list.size() > 1) {
			exceptionHandler.getLog().info("出现了单次查询有相同otaOrderNo的情况:" + list);
			for (SyncOrdEntity syncOrdEntity : list) {
				int orderStatusCode = Integer.parseInt(syncOrdEntity.getOrderStatusCode());
				if (orderStatusCode == AppConstants.Qunar_orderStat.IN_HOUSE_3) {
					return syncOrdEntity;
				} else if (orderStatusCode == AppConstants.Qunar_orderStat.DISPATCH_2) {
					return syncOrdEntity;
				}
			}
		}
		return list.get(0);
	}
}
