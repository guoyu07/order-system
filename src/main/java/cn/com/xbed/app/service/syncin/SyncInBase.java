package cn.com.xbed.app.service.syncin;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.IFromQhh;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.service.syncin.vo.SyncOrdEntity;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;


/**
 * 请注意不要改变该方法及其调用方法的事务传播设置<br>
 * @author Administrator
 *
 */
@Service
public class SyncInBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private SyncInTool syncInTool;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SyncInBase.class));
	@Resource
	private WarningSystemTool warningSystemTool;

	/**
	 * 处理单条记录，单独的事务
	 * 
	 * @param syncOrdEntity
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean handleOne(SyncOrdEntity syncOrdEntity) {
		try {
			String orderStatusCode = syncOrdEntity.getOrderStatusCode();
			int iOrderStatusCode = Integer.parseInt(orderStatusCode);
			switch (iOrderStatusCode) {
			case AppConstants.Qunar_orderStat.NEW_1:
				syncInTool.syncUnpaid(syncOrdEntity);
				break;
			case AppConstants.Qunar_orderStat.DISPATCH_2:
				syncInTool.syncPaid(syncOrdEntity);
				break;
			case AppConstants.Qunar_orderStat.IN_HOUSE_3: // 去呼呼渠道来的单,办理了入住都会进入该条件,有一种情况是在去呼呼后台帮忙办理入住,对于这种情况需要我们同步下
				syncInTool.syncCheckin(syncOrdEntity);
				break;
			case AppConstants.Qunar_orderStat.OUT_HOUSE_4:
				syncInTool.syncCheckout(syncOrdEntity);
				break;
			case AppConstants.Qunar_orderStat.CANCEL_5:
				syncInTool.syncCancel(syncOrdEntity);
				break;
			}
			syncInTool.logSyncSuccess(syncOrdEntity);
			return true;
		} catch (Exception e) {
			String errorMsg = "otaOrderNo: " + syncOrdEntity.getOrderNo() + ",去呼呼同步失败 " + "|" + e.getMessage();
			syncInTool.logSyncError(errorMsg, e, syncOrdEntity);
			return false;
		}
	}

	/**
	 * 标记为已完成，单独的事务
	 * 
	 * @param sameOtaOrderNoGroup
	 *            传入相同otaOrderNo的一组集合
	 * @param syncResult
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markAsDone(List<SyncOrdEntity> sameOtaOrderNoGroup, SyncStat syncResult) {
		try {
			for (SyncOrdEntity syncOrdEntity : sameOtaOrderNoGroup) {
				IFromQhh entity = new IFromQhh();
				entity.setJobId(syncOrdEntity.getJobId());
				int result = syncResult == SyncStat.SUCC ? AppConstants.IFromQhh_syncStat.DONE_SUCC_2
						: AppConstants.IFromQhh_syncStat.DONE_FAIL_3;
				entity.setSyncStat(result);
				entity.setSyncDtm(DateUtil.getCurDateTime());
				daoUtil.fromQhhDao.updateEntityByPk(entity);
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("从OTA同步订单到本地失败: ", e);
		}
	}

	public enum SyncStat {
		FAIL, SUCC
	}
}
