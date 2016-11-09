package cn.com.xbed.app.service.localordermodule.vo;

import java.util.List;

import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderExt;

public class NewOrderOut {
	private XbOrder orderInfo;
	private XbOrderExt orderExtInfo;
	private List<NewCheckinOut> newCheckinList;

	public XbOrder getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(XbOrder orderInfo) {
		this.orderInfo = orderInfo;
	}

	public XbOrderExt getOrderExtInfo() {
		return orderExtInfo;
	}

	public void setOrderExtInfo(XbOrderExt orderExtInfo) {
		this.orderExtInfo = orderExtInfo;
	}

	public List<NewCheckinOut> getNewCheckinList() {
		return newCheckinList;
	}

	public void setNewCheckinList(List<NewCheckinOut> newCheckinList) {
		this.newCheckinList = newCheckinList;
	}

	public NewOrderOut(XbOrder orderInfo, XbOrderExt orderExtInfo, List<NewCheckinOut> newCheckinList) {
		super();
		this.orderInfo = orderInfo;
		this.orderExtInfo = orderExtInfo;
		this.newCheckinList = newCheckinList;
	}

}