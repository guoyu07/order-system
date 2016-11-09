package cn.com.xbed.app.zztest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.ordermodule.impl.FromType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.commons.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml", "classpath:spring-db.xml", "classpath:spring-mvc.xml", "classpath:spring-mq.xml" })
public class SpringTestOrder {
	@Resource
	private OrderUtil orderUtil;

	@Test
	public void testNewOrderFromLocal() {
		int stat = AppConstants.Order_stat.NEW_0;
		int source = AppConstants.Order_source.IOS_2;
		int payType = AppConstants.Order_payType.WECHAT_1;
		int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
		int totalPriceCentUnit = 26800;
		int bookLodgerId = 2;
		String bookLodgerName = "王永锋";
		String bookLodgerMobile = "13042014179";
		Date beginDateTime = DateUtil.parseDateTime("2015-12-17 14:00:00");
		Date endDateTime = DateUtil.parseDateTime("2015-12-18 12:00:00");
		OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId, bookLodgerName, bookLodgerMobile, beginDateTime, endDateTime, null,0);
		
		
		List<CheckinInput> checkinInputList = new ArrayList<>();
		int roomId = 69;
		int checkinLodgerId = 24;
		String checkinName = "庞军2";
		String checkinMobile = "15917403623";
		Date checkinBeginTime = beginDateTime;
		Date checkinEndTime = endDateTime;
		int chkinPriceCentUnit = 26800;
		CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinName, checkinMobile, checkinBeginTime, checkinEndTime, chkinPriceCentUnit, null);
		checkinInputList.add(checkinInput);
		
		
		String invoiceTitle = "广州搜床网络科技有限公司";
		String mailName = "王永锋";
		String contactNo = "13042014179";
		String mailAddr = "广州市荔湾区芳村信义路24号七喜创意园3栋Xbed中心";
		String postCode = "510642";
		int invoiceMoney = 26800;
		Invoice invoiceInfo = new Invoice(invoiceTitle, mailName, contactNo, mailAddr, postCode, invoiceMoney);
		FromType fromType = FromType.FROM_LOCAL;
		orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
	}

	
	@Test
	public void testNewOrderFromLocal2() {
		int stat = AppConstants.Order_stat.NEW_0;
		int source = AppConstants.Order_source.IOS_2;
		int payType = AppConstants.Order_payType.WECHAT_1;
		int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
		int totalPriceCentUnit = 26800;
		int bookLodgerId = 2;
		String bookLodgerName = "王永锋第二次测试";
		String bookLodgerMobile = "13042014179";
		Date beginDateTime = DateUtil.parseDateTime("2015-12-19 14:00:00");
		Date endDateTime = DateUtil.parseDateTime("2015-12-20 12:00:00");
		OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId, bookLodgerName, bookLodgerMobile, beginDateTime, endDateTime, null,0);
		
		
		List<CheckinInput> checkinInputList = new ArrayList<>();
		int roomId = 69;
		int checkinLodgerId = 24;
		String checkinName = "庞军第二次测试";
		String checkinMobile = "15917403623";
		Date checkinBeginTime = beginDateTime;
		Date checkinEndTime = endDateTime;
		int chkinPriceCentUnit = 26800;
		CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinName, checkinMobile, checkinBeginTime, checkinEndTime, chkinPriceCentUnit, null);
		checkinInputList.add(checkinInput);
		
		
		
		FromType fromType = FromType.FROM_LOCAL;
		Invoice invoiceInfo = null;
		orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
	}

	
	@Test
	public void testNewOrderFromOta() {
		int stat = AppConstants.Order_stat.NEW_0;
		int source = AppConstants.Order_source.QUNAR_1;
		int payType = AppConstants.Order_payType.WECHAT_1;
		int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
		int totalPriceCentUnit = 26800;
		int bookLodgerId = 2;
		String bookLodgerName = "王永锋(测试from_OTA)";
		String bookLodgerMobile = "13042014179";
		Date beginDateTime = DateUtil.parseDateTime("2015-12-19 14:00:00");
		Date endDateTime = DateUtil.parseDateTime("2015-12-20 12:00:00");
		OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId, bookLodgerName, bookLodgerMobile, beginDateTime, endDateTime, "1705599887766",0);
		
		
		List<CheckinInput> checkinInputList = new ArrayList<>();
		int roomId = 69;
		int checkinLodgerId = 2;
		String checkinName = "王永锋(测试from_OTA)";
		String checkinMobile = "13042014179";
		Date checkinBeginTime = beginDateTime;
		Date checkinEndTime = endDateTime;
		int chkinPriceCentUnit = 26800;
		CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinName, checkinMobile, checkinBeginTime, checkinEndTime, chkinPriceCentUnit, "4545454545");
		checkinInputList.add(checkinInput);
		
		// ota不会有发票,传了也没用
		String invoiceTitle = "广州搜床网络科技有限公司";
		String mailName = "王永锋";
		String contactNo = "13042014179";
		String mailAddr = "广州市荔湾区芳村信义路24号七喜创意园3栋Xbed中心";
		String postCode = "510642";
		int invoiceMoney = 26800;
		Invoice invoiceInfo = new Invoice(invoiceTitle, mailName, contactNo, mailAddr, postCode, invoiceMoney);
		FromType fromType = FromType.FROM_OTA;
		orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
	}

	
	@Test
	public void testNewOrderFromOta2() {
		int stat = AppConstants.Order_stat.PAYED_1;
		int source = AppConstants.Order_source.QUNAR_1;
		int payType = AppConstants.Order_payType.UNKNOW_2;
		int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
		int totalPriceCentUnit = 53600;
		int bookLodgerId = 2;
		String bookLodgerName = "王永锋(测试from_OTA,一单多房)";
		String bookLodgerMobile = "13042014179";
		Date beginDateTime = DateUtil.parseDateTime("2015-12-20 14:00:00");
		Date endDateTime = DateUtil.parseDateTime("2015-12-21 12:00:00");
		OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId, bookLodgerName, bookLodgerMobile, beginDateTime, endDateTime, "170888888886",0);
		
		
		List<CheckinInput> checkinInputList = new ArrayList<>();
		int roomId = 69;
		int checkinLodgerId = 2;
		String checkinName = "入住人一";
		String checkinMobile = "13042014179";
		Date checkinBeginTime = beginDateTime;
		Date checkinEndTime = endDateTime;
		int chkinPriceCentUnit = 26800;
		CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinName, checkinMobile, checkinBeginTime, checkinEndTime, chkinPriceCentUnit, "4545454546");
		checkinInputList.add(checkinInput);
		
		int roomId2 = 70;
		int checkinLodgerId2 = 24;
		String checkinName2 = "入住人二";
		String checkinMobile2 = "15917403623";
		Date checkinBeginTime2 = beginDateTime;
		Date checkinEndTime2 = endDateTime;
		int chkinPriceCentUnit2 = 26800;
		CheckinInput checkinInput2 = new CheckinInput(roomId2, checkinLodgerId2, checkinName2, checkinMobile2, checkinBeginTime2, checkinEndTime2, chkinPriceCentUnit2, "88999986");
		checkinInputList.add(checkinInput2);
		
		
		// ota不会有发票,传了也没用
		FromType fromType = FromType.FROM_OTA;
		Invoice invoiceInfo = null;
		orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
	}
	
	
	
	
	@Test
	public void testCheckinLocal() {
		FromType fromType = FromType.FROM_LOCAL;
	//	orderUtil.checkin(1923, fromType);
	}
	
	
	@Test
	public void testCheckinOta() {
		FromType fromType = FromType.FROM_OTA;
	//	orderUtil.checkin(1924, fromType);
	}
	
	
	
}
