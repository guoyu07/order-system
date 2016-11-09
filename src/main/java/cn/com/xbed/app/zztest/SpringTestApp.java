package cn.com.xbed.app.zztest;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.xbed.app.bean.LogEventThrow;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.EnumHelper;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.OperOverstayDao;
import cn.com.xbed.app.dao.PaymentDao;
import cn.com.xbed.app.dao.basedao.impl.BaseDaoImpl;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.CleanSvRecService;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.OmsUserService;
import cn.com.xbed.app.service.OperLogCommon;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.RoomMgntService;
import cn.com.xbed.app.service.ValidService;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.service.eventsystem.EventCode;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.ljh.CleanSystemService;
import cn.com.xbed.app.service.localordermodule.LocalOrderModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.owner.OwnerService;
import cn.com.xbed.app.service.roomstatemodule.CouponCardTool;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;
import cn.com.xbed.app.service.roomstatemodule.SendDatagram2RoomStateSystemTool;
import cn.com.xbed.app.service.syncin.SyncInScaner;
import cn.com.xbed.app.service.tag.TagService;
import cn.com.xbed.app.service.timer.TimerTasks;
import cn.com.xbed.commond.LogTool;
import cn.com.xbed.commond.XbedMQClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml", "classpath:spring-db.xml", "classpath:spring-mvc.xml", "classpath:spring-mq.xml" })
public class SpringTestApp {
	private Log log = LogFactory.getLog(this.getClass());
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SpringTestApp.class));
	@Resource
	private LodgerService lodgerService;
	@Resource
	private PaymentDao paymentDao;
	@Resource
	private CouponCardTool couponCardTool;
	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private OperOverstayDao operOverstayDao;
	
	
	
	@Test
	public void test3333() {
		int orderId = 3683;
		System.out.println(sendDatagram2RoomStateSystemTool.getNewOrderDatagramV3(orderId ));
	}
	
	
	
	@Test
	public void testQryNowlodger() {
		LogEventThrow eventThrow = new LogEventThrow();
		eventThrow.setLogTitle("logTitle23");
		eventThrow.setLogType(1);
		eventThrow.setCreateDtm(DateUtil.getCurDateTime());
		eventThrow.setDetailDesc("detailDes22c");
		eventThrow.setSuccFlag(EnumHelper.getEnumString(Log_succFlag.SUCC));
		eventThrow.setOrderId(1);
		eventThrow.setCheckinId(1);
		eventThrow.setStopId(2);
		eventThrow.setEventCode("xxxxx");
		
		System.out.println(daoUtil.logEventThrowDao.addAndGetPk(eventThrow));
	}
	
	
	@Test
	public void testThrowWarningUnpaidEv2222ent() {
		commonDbLogService.logEvent("logTitle", 1, "detailDesc", Log_succFlag.FAIL, 1, null,null, EventCode.WARNING_UNPAID_20_MIN);
	}
	
	/**
	 * 抛出事件: 未支付订单警告尽快支付
	 * 
	 */
	@Test
	public void testThrowWarningUnpaidEvent() {
		int orderId = 3088;
		eventService.throwCancelUnpaidEvent(orderId);
	}

	/**
	 * 抛出事件: 超时未支付取消通知
	 * 
	 */
	@Test
	public void testThrowCancelUnpaidEvent() {
		int orderId = 3088;
		eventService.throwCancelUnpaidEvent(orderId);
	}

	/**
	 * 抛出事件: 预退房
	 */
	@Test
	public void testThrowPreCheckoutEvent() {
		int orderId = 3088;
		eventService.throwPreCheckoutEvent(orderId);
	}

	@Test
	public void testGet() {
		System.out.println(JsonHelper.toJSONString(eventService.getOrderInfo(3088, false)));
	}

	@Test
	public void testRegisterSubRoot() {
		System.out.println(tagService.registerSubRoot("地铁"));
	}

	@Test
	public void testAssignTagToRoom() {
		String tagCode = "0201";
		int roomId = 1;
		int roomId2 = 2;
		tagService.assignTagToRoom(tagCode, roomId, roomId2);
	}

	@Test
	public void testGetAllRoomsByTag() {
		String expression = "";
		System.out.println(tagService.getAllRoomsByTag(expression));
	}

	@Test
	public void testCancelRoomAssignTag() {
		tagService.cancelRoomAssignTag(1, "0201");
	}

	@Test
	public void testCleanRoomAllTag() {
		tagService.cleanRoomAllTag(1);
	}

	@Test
	public void testCreateSubTag() {
		String tagCode = "01";
		String subTagCname = "去哪儿";
		System.out.println(tagService.createSubTag(tagCode, subTagCname));
	}

	@Test
	public void testGetSubTag() {
		String tagCode = "01";
		System.out.println(tagService.getSubTag(tagCode));
	}

	@Test
	public void testGetTagByRoomId() {
		System.out.println(tagService.getTagByRoom(1));
	}

	@Test
	public void testEditTag() {
		String tagCode = "0201";
		String tagCname = "临时";
		tagService.editTag(tagCode, tagCname);
	}

	@Test
	public void testRemoveTag() {
		String tagCode = "0201";
		tagService.removeTag(tagCode);
	}
	public static void main(String[] args) {
		String s = String.format("替换字符串,param1:%s,param2:%s,param3:%s,param4:%s", 1,"string",true,new XbRoom());
		System.out.println(s);
	}
	@Resource
	private CommonService commonService;

	@Resource
	private RoomMgntService roomMgntService;
	@Resource
	private OrderMgntService orderMgntService;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private BaseDaoImpl baseDaoImpl;
	@Resource
	private TimerTasks timerTasks;
	@Resource
	private OmsUserService omsUserService;
	@Resource
	private OperLogCommon operLogCommon;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private CleanSvRecService cleanSvRecService;
	@Resource
	private RoomStateTool roomStateTool;
	@Resource
	private LogTool logTool;
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private XbedMQClient xbedMQClient;
	@Resource
	private CommonDbLogService commonDbLogService;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private ValidService validService;
	@Resource
	private LocalOrderModuleBase orderModuleBase;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private SendDatagram2RoomStateSystemTool sendDatagram2RoomStateSystemTool;
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private SyncInScaner syncInScaner;
	@Resource
	private OwnerService ownerService;
	@Resource
	private TagService tagService;
	@Resource
	private EventService eventService;
}
