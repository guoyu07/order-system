package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class OrderMgntDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderMgntDao.class));

	/**
	 * 根据用户ID查询当前订单
	 * @param lodgerId
	 * @return
	 */
	public List<Map<String, Object>> findByLodgerIdSelfOrder(Integer lodgerId){
		try {
			String sql = "select * from ("
					+ "select  "
						+ "xb_order.order_id orderId, xb_order.source source, xb_order.invoice_rec_id invoiceRecId,"
						+ " xb_order.order_no orderNo, xb_order.stat orderStat, xb_order.order_type orderType, xb_order.total_price totalPrice,"
						+ " xb_order.lodger_id lodgerId, xb_order.lodger_name lodgerName, xb_order.create_time createTime, "
						+ "(select count(checkin_id) from xb_checkin where xb_checkin.order_id=xb_order.order_id and xb_checkin.stat!=2 and (checkout_time >=? and actual_checkout_time is null)) checkinCount "
					+ "from xb_order,(select distinct checkin.order_id from xb_checkin checkin,xb_checkiner checkiner  "
					+ "where checkin.checkin_id=checkiner.checkin_id and checkiner.is_del=0 and checkiner.lodger_id=?) temp "
					+ "where xb_order.order_id=temp.order_id and xb_order.stat<=1) obj "
					+ "where obj.checkinCount != 0 order by createTime desc"; 
			return this.queryMapList(sql, new Object[]{ DateUtil.dateToYrMonDay_(new Date()), lodgerId }, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	/**
	 * 查询过往订单
	 * @param lodgerId
	 * @return
	 */
	public List<Map<String, Object>> findByLodgerIdOverdueOrder(Integer lodgerId){
		try {
			String sql = "select * from ("
					+ "select  "
					+ "xb_order.order_id orderId, xb_order.source source, xb_order.invoice_rec_id invoiceRecId, xb_order.order_no orderNo, "
					+ "xb_order.stat orderStat, xb_order.order_type orderType, xb_order.total_price totalPrice, xb_order.lodger_id lodgerId, "
					+ "xb_order.lodger_name lodgerName, xb_order.create_time createTime, "
					+ "(select count(checkin_id) from xb_checkin where xb_checkin.order_id=xb_order.order_id and xb_checkin.stat!=2 and (checkout_time >=? and actual_checkout_time is null)) checkinCount "
					+ "from xb_order,"
					+ "(select distinct checkin.order_id from xb_checkin checkin,xb_checkiner checkiner  where checkin.checkin_id=checkiner.checkin_id and checkiner.is_del=0 and checkiner.lodger_id=?) temp "
					+ "where xb_order.order_id=temp.order_id) obj where "
					+ "obj.orderStat>=2 or obj.checkinCount = 0 order by createTime desc"; 
			return this.queryMapList(sql, new Object[]{ DateUtil.dateToYrMonDay_(new Date()), lodgerId}, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	// 从主库获取
	public XbOrder findByOtaOrderNo(String otaOrderNo) {
		try {
			return this.queryForSingleRow(XbOrder.class, "select * from xb_order where ota_order_no=?",
					new Object[] { otaOrderNo }, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public boolean add(XbOrder entity) {
		try {
			return this.insertEntity(entity, "orderId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrder entity) {
		try {
			return this.insertEntityAndGetPk(entity, "orderId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	// 从主库获取
	public XbOrder findByPk(int pk) {
		try {
			XbOrder orderInfo = this.queryForSingleRow(XbOrder.class, "select * from xb_order where order_id=?",
					new Object[] { pk }, true);
			AssertHelper.notNull(orderInfo, "查无订单,orderId:" + pk);
			return orderInfo;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	// 从主库获取
	public XbOrder findByOrderNo(String orderNo) {
		try {
			XbOrder orderInfo = this.queryForSingleRow(XbOrder.class, "select * from xb_order where order_no=?",
					new Object[] { orderNo },true);
			AssertHelper.notNull(orderInfo, "查无订单,orderNo:" + orderNo);
			return orderInfo;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbOrder entity) {
		try {
			if (entity.getOrderId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "orderId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	// 从主库获取
	public XbOrder findOrderByCheckinId(int checkinId) {
		try {
			List<XbOrder> mapList = this.queryForMultiRow(XbOrder.class,
					"select a.* from xb_order a,xb_checkin b where a.order_id=b.order_id and b.checkin_id=?", new Object[] { checkinId }, true);
			if (!mapList.isEmpty()) {
				return mapList.get(0);
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	public int transOrderNo2orderId(String orderNo) {
		try {
			String sql = "select order_id from xb_order where order_no=?";
			List<Map<String, Object>> mapList = this.queryMapList(sql, new Object[] { orderNo });
			if (!mapList.isEmpty()) {
				return (int) mapList.get(0).get("order_id");
			}
			return 0;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public String transOrderId2orderNo(int orderId) {
		try {
			String sql = "select order_no from xb_order where order_id=?";
			List<Map<String, Object>> mapList = this.queryMapList(sql, new Object[] { orderId });
			if (!mapList.isEmpty()) {
				return (String) mapList.get(0).get("order_no");
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
