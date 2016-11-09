package cn.com.xbed.app.service.localordermodule.vo;

import java.util.Date;

public class OrderInput {
	private int stat;
	private int source;
	private int payType;
	private int orderType;
	private int totalPriceCentUnit;
	private Integer discountPriceCentUnit;
	private int bookLodgerId;
	private String bookLodgerName;
	private String bookLodgerMobile;
	private Date beginDateTime;
	private Date endDateTime;
	private String otaOrderNo;
	private String orderNo = null;

	public OrderInput(int stat, int source, int payType, int orderType, int totalPriceCentUnit, int bookLodgerId, String bookLodgerName,
			String bookLodgerMobile, Date beginDateTime, Date endDateTime, String otaOrderNo, Integer discountPriceCentUnit) {
		super();
		this.stat = stat;
		this.source = source;
		this.payType = payType;
		this.orderType = orderType;
		this.totalPriceCentUnit = totalPriceCentUnit;
		this.bookLodgerId = bookLodgerId;
		this.bookLodgerName = bookLodgerName;
		this.bookLodgerMobile = bookLodgerMobile;
		this.beginDateTime = beginDateTime;
		this.endDateTime = endDateTime;
		this.otaOrderNo = otaOrderNo;
		this.discountPriceCentUnit = discountPriceCentUnit;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getStat() {
		return stat;
	}

	public int getSource() {
		return source;
	}

	public int getPayType() {
		return payType;
	}

	public int getOrderType() {
		return orderType;
	}

	public int getTotalPriceCentUnit() {
		return totalPriceCentUnit;
	}

	public int getBookLodgerId() {
		return bookLodgerId;
	}

	public String getBookLodgerName() {
		return bookLodgerName;
	}

	public String getBookLodgerMobile() {
		return bookLodgerMobile;
	}

	public Date getBeginDateTime() {
		return beginDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public Integer getDiscountPriceCentUnit() {
		return discountPriceCentUnit;
	}

	@Override
	public String toString() {
		return "OrderInput [stat=" + stat + ", source=" + source + ", payType=" + payType + ", orderType=" + orderType + ", totalPriceCentUnit="
				+ totalPriceCentUnit + ", bookLodgerId=" + bookLodgerId + ", bookLodgerName=" + bookLodgerName + ", bookLodgerMobile="
				+ bookLodgerMobile + ", beginDateTime=" + beginDateTime + ", endDateTime=" + endDateTime + ", otaOrderNo=" + otaOrderNo + "]";
	}

}