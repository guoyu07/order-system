package cn.com.xbed.app.service.invoicemodule;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbInvoiceRec;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;

@Service
@Transactional
public class InvoiceModuleBase {
	@Resource
	private DaoUtil daoUtil;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(InvoiceModuleBase.class));

	/*
	 * {invoiceTitle:发票抬头, mailName:收件人,contactNo:手机号,mailAddr:详细地址,postCode:邮编}
	 */
	public int addInvoice(int orderId, Invoice invoiceInfo) {
		try {
			if (invoiceInfo != null) {
				XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
				AssertHelper.notNull(orderInfo, "查无此订单,orderId:" + orderId);
				
				int invoiceRecId = -1;
				if (invoiceInfo != null) {
					XbInvoiceRec invoiceRec = new XbInvoiceRec();
					invoiceRec.setLodgerId(orderInfo.getLodgerId());
					invoiceRec.setOrderNo(orderInfo.getOrderNo());
					invoiceRec.setCreateDtm(DateUtil.getCurDateTime());
					invoiceRec.setInvoiceMoney(invoiceInfo.getInvoiceMoney());
					invoiceRec.setMailName(invoiceInfo.getMailName());
					invoiceRec.setContactNo(invoiceInfo.getContactNo());
					invoiceRec.setMailAddr(invoiceInfo.getMailAddr());
					invoiceRec.setPostCode(invoiceInfo.getPostCode());
					invoiceRec.setInvoiceTitle(invoiceInfo.getInvoiceTitle());
					invoiceRecId = ((Long) daoUtil.invoiceRecDao.addAndGetPk(invoiceRec)).intValue();
					
					// 双向
					XbOrder newOrder = new XbOrder();
					newOrder.setOrderId(orderId);
					newOrder.setInvoiceRecId(invoiceRecId);
					daoUtil.orderMgntDao.updateEntityByPk(newOrder);
					return invoiceRecId;
				}
			}
			return 0;
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException(e);
		}
	}
}
