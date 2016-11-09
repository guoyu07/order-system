package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbInvoiceRec;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class InvoiceRecDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(InvoiceRecDao.class));

	public boolean add(XbInvoiceRec entity) {
		try {
			return this.insertEntity(entity, "invoiceRecId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbInvoiceRec entity) {
		try {
			return this.insertEntityAndGetPk(entity, "invoiceRecId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbInvoiceRec findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbInvoiceRec.class, "select * from xb_invoice_rec where invoice_rec_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbInvoiceRec findByCheckinId(int checkinId) {
		try {
			List<Map<String, Object>> tmpList = this.queryMapList("select checkin_id,order_id from xb_checkin where checkin_id=?",
					new Object[] { checkinId });
			if (tmpList.isEmpty()) {
				return null;
			}
			tmpList = this.queryMapList("select order_id,invoice_rec_id from xb_order where order_id=?",
					new Object[] { tmpList.get(0).get("order_id") });
			if (tmpList.isEmpty()) {
				return null;
			}
			return this.findByPk((int) tmpList.get(0).get("invoice_rec_id"));
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbInvoiceRec findByOrderId(int orderId) {
		try {
			List<Map<String, Object>> tmpList = this.queryMapList("select order_id,invoice_rec_id from xb_order where order_id=?",
					new Object[] { orderId });
			if (tmpList.isEmpty()) {
				return null;
			}
			return this.findByPk((int) tmpList.get(0).get("invoice_rec_id"));
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateRoomByPk(XbInvoiceRec entity) {
		try {
			if (entity.getInvoiceRecId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "invoiceRecId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
