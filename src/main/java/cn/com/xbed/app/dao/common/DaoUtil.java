package cn.com.xbed.app.dao.common;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import cn.com.xbed.app.dao.AdditionDao;
import cn.com.xbed.app.dao.BfSendsmsRecDao;
import cn.com.xbed.app.dao.BusiSmsRecDao;
import cn.com.xbed.app.dao.CalendarDailyDao;
import cn.com.xbed.app.dao.ChainMgntDao;
import cn.com.xbed.app.dao.CheckinDao;
import cn.com.xbed.app.dao.CheckinExtDao;
import cn.com.xbed.app.dao.CheckinerDao;
import cn.com.xbed.app.dao.CleanRecDao;
import cn.com.xbed.app.dao.CleanSheetDao;
import cn.com.xbed.app.dao.CustAdviceDao;
import cn.com.xbed.app.dao.FacilityDao;
import cn.com.xbed.app.dao.InvoiceRecDao;
import cn.com.xbed.app.dao.LocationDao;
import cn.com.xbed.app.dao.LodgerDao;
import cn.com.xbed.app.dao.LogCouponCardDao;
import cn.com.xbed.app.dao.LogEventThrowDao;
import cn.com.xbed.app.dao.LogOverstayAutoCheckinDao;
import cn.com.xbed.app.dao.LogOwnerCreateDao;
import cn.com.xbed.app.dao.LogPaySuccDao;
import cn.com.xbed.app.dao.LogStopSheetDao;
import cn.com.xbed.app.dao.ManSendsmsDao;
import cn.com.xbed.app.dao.ModelDao;
import cn.com.xbed.app.dao.OmsUserDao;
import cn.com.xbed.app.dao.OperLogChgroomDao;
import cn.com.xbed.app.dao.OperOverstayDao;
import cn.com.xbed.app.dao.OrderCtripDao;
import cn.com.xbed.app.dao.OrderExtDao;
import cn.com.xbed.app.dao.OrderMgntDao;
import cn.com.xbed.app.dao.OrderOperDao;
import cn.com.xbed.app.dao.OrderPromoDao;
import cn.com.xbed.app.dao.OrderStopDao;
import cn.com.xbed.app.dao.PaymentDao;
import cn.com.xbed.app.dao.PictureDao;
import cn.com.xbed.app.dao.PromoConfDao;
import cn.com.xbed.app.dao.PromoRoomGroupDao;
import cn.com.xbed.app.dao.QhhSyncLogDao;
import cn.com.xbed.app.dao.QunarOrderOperDao;
import cn.com.xbed.app.dao.RoomCleanServDao;
import cn.com.xbed.app.dao.RoomCtrlSheetDao;
import cn.com.xbed.app.dao.RoomFacilityRelDao;
import cn.com.xbed.app.dao.RoomMgntDao;
import cn.com.xbed.app.dao.RoomStateDao;
import cn.com.xbed.app.dao.RpBaseDataDao;
import cn.com.xbed.app.dao.ScheduleDao;
import cn.com.xbed.app.dao.SmsRecordDao;
import cn.com.xbed.app.dao.SmsTemplateDao;
import cn.com.xbed.app.dao.SmsValidDao;
import cn.com.xbed.app.dao.SysParamDao;
import cn.com.xbed.app.dao.TempRoomDao;
import cn.com.xbed.app.dao.TestDao;
import cn.com.xbed.app.dao.i.FromQhhDao;
import cn.com.xbed.app.dao.i.QhhCancelOrderDao;
import cn.com.xbed.app.dao.i.QhhCheckoutDao;
import cn.com.xbed.app.dao.i.QhhNewOrderDao;

/**
 * 提供统一的dao管理,每个新表一个新dao,service层只要引入DaoUtil即可,避免代码不整洁<br>
 * 
 * dao的命名应该是去掉前缀,然后加到Dao,例如xb_lodger,去掉xbed的前缀xb,然后加上Dao,为LodgerDao<br>
 * 
 * 部分因为文件容易重名或混淆的进行特殊的命名<br>
 * 
 * 命名的原则是看得出在操作什么表<br>
 * 
 * @author Administrator
 *
 */
@Repository
public class DaoUtil {
	@Resource
	public TempRoomDao tempRoomDao;
	@Resource
	public LogOverstayAutoCheckinDao logOverstayAutoCheckinDao;
	@Resource
	public OperOverstayDao operOverstayDao;
	@Resource
	public LogCouponCardDao logCouponCardDao;
	@Resource
	public LogPaySuccDao logPaySuccDao;
	@Resource
	public PaymentDao paymentDao;
	@Resource
	public RoomStateDao roomStateDao;
	@Resource
	public LogEventThrowDao logEventThrowDao;
	@Resource
	public OrderCtripDao orderCtripDao;
	@Resource
	public OrderPromoDao orderPromoDao;
	@Resource
	public LogOwnerCreateDao logOwnerCreateDao;
	@Resource
	public LogStopSheetDao logStopSheetDao;
	@Resource
	public OrderStopDao orderStopDao;
	@Resource
	public AdditionDao additionDao;
	@Resource
	public BfSendsmsRecDao bfSendsmsRecDao;
	@Resource
	public BusiSmsRecDao busiSmsRecDao;
	@Resource
	public CalendarDailyDao calendarDailyDao;
	@Resource
	public ChainMgntDao chainMgntDao;
	@Resource
	public CheckinDao checkinDao;
	@Resource
	public CheckinerDao checkinerDao;
	@Resource
	public CheckinExtDao checkinExtDao;
	@Resource
	public CleanRecDao cleanRecDao;
	@Resource
	public CleanSheetDao cleanSheetDao;
	@Resource
	public CustAdviceDao custAdviceDao;
	@Resource
	public FacilityDao facilityDao;
	@Resource
	public InvoiceRecDao invoiceRecDao;
	@Resource
	public LocationDao locationDao;
	@Resource
	public LodgerDao lodgerDao;
	@Resource
	public ManSendsmsDao manSendsmsDao;
	@Resource
	public ModelDao modelDao;
	@Resource
	public OmsUserDao omsUserDao;
	@Resource
	public OperLogChgroomDao operLogChgroomDao;
	@Resource
	public OrderExtDao orderExtDao;
	@Resource
	public OrderMgntDao orderMgntDao;
	@Resource
	public OrderOperDao orderOperDao;
	@Resource
	public PictureDao pictureDao;
	@Resource
	public PromoConfDao promoConfDao;
	@Resource
	public PromoRoomGroupDao promoRoomGroupDao;
	@Resource
	public QhhSyncLogDao qhhSyncLogDao;
	@Resource
	public QunarOrderOperDao qunarOrderOperDao;
	@Resource
	public RoomCtrlSheetDao roomCtrlSheetDao;
	@Resource
	public RoomFacilityRelDao roomFacilityRelDao;
	@Resource
	public RoomMgntDao roomMgntDao;
	@Resource
	public RpBaseDataDao rpBaseDataDao;
	@Resource
	public ScheduleDao scheduleDao;
	@Resource
	public TestDao testDao;
	@Resource
	public FromQhhDao fromQhhDao;
	@Resource
	public QhhCancelOrderDao qhhCancelOrderDao;
	@Resource
	public QhhCheckoutDao qhhCheckoutDao;
	@Resource
	public QhhNewOrderDao qhhNewOrderDao;
	@Resource
	public SmsRecordDao smsRecordDao;
	@Resource
	public SmsValidDao smsValidDao;
	@Resource
	public SmsTemplateDao smsTemplateDao;

	// cn.com.xbed.support.dao begin------------------
	@Resource
	public RoomCleanServDao roomCleanServDao;
	@Resource
	public SysParamDao sysParamDao;
	// cn.com.xbed.support.dao end------------------

}
