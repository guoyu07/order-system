package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbPayment;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class PaymentDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(PaymentDao.class));

	public boolean add(XbPayment entity) {
		try {
			return this.insertEntity(entity, "paymentId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbPayment entity) {
		try {
			return this.insertEntityAndGetPk(entity, "paymentId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbPayment findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbPayment.class, "select * from xb_payment where payment_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbPayment entity) {
		try {
			if (entity.getPaymentId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "paymentId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateByPlatformSnAndGetPk(String platformSn) {
		try {
			String sql = "SELECT payment_id FROM xb_payment WHERE platform_sn=?";
			List<Map<String, Object>> mapList = this.queryMapList(sql, new Object[] { platformSn });
			if (!mapList.isEmpty()) {
				sql = "UPDATE xb_payment SET recv_status=2,recv_code='SUCCESS', recv_desc='SUCCESS',recv_time=? WHERE platform_sn=?";
				this.updateOrDelete(sql, new Object[] { DateUtil.getCurDateTime(), platformSn });
				return (int) mapList.get(0).get("payment_id");
			}
			return 0;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
