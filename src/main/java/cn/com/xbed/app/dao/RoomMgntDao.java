package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class RoomMgntDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomMgntDao.class));

	public boolean add(XbRoom entity) {
		try {
			return this.insertEntity(entity, "roomId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbRoom entity) {
		try {
			return this.insertEntityAndGetPk(entity, "roomId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbRoom findByPk(int pk) {
		try {
			XbRoom roomInfo = this.queryForSingleRow(XbRoom.class, "select * from xb_room where room_id=?", new Object[] { pk });
			AssertHelper.notNull("查无房间,roomId:", pk);
			return roomInfo;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbRoom findValidByPk(int pk) {
		try {
			return this.queryForSingleRow(XbRoom.class, "select * from xb_room where flag=? AND room_id=?",
					new Object[] { AppConstants.Room_flag.NORMAL_1, pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int findRoomDefaultPriceByPk(int pk) {
		try {
			XbRoom roomInfo = this.queryForSingleRow(XbRoom.class, "select * from xb_room where room_id=?",
					new Object[] { pk });
			AssertHelper.notNull(roomInfo, "查不到房间的默认价格,roomId[" + pk + "]");
			return roomInfo.getPrice();
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateRoomByPk(XbRoom entity) {
		try {
			if (entity.getRoomId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "roomId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public List<Map<String, Object>> findAllRoomsByFlag(int flag) {
		try {
			String sql = "SELECT r.locate as locate,r.room_id as roomId,r.room_name as roomName,r.addr as addr,r.province as province,r.city as city,r.district as district,c.name as chainName FROM xb_room r,xb_chain c WHERE r.chain_id=c.chain_id and flag=?";
			return this.queryMapList(sql, new Object[] { flag });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<Map<String, Object>> findByOrderId(Integer orderId){
		try {
			String sql = "select xb_checkin.overstay_flag overstayFlag,xb_checkin.chg_room_flag chgRoomFlag,xb_checkin.checkin_id checkinId,xb_room.lodger_count lodgerCount,xb_room.addr,xb_room.chain_id chainID,xb_room.province,xb_room.city,xb_room.district,xb_room.locate,xb_room.room_id roomId,xb_checkin.stat stat, xb_room.room_name roomName, xb_room.title, xb_room.room_type_name roomTypeName,"
					+ "xb_checkin.checkin_time checkinTime,xb_checkin.checkout_time checkoutTime,xb_checkin.night_count nightCount,xb_checkin.chkin_price chkinPrice,xb_checkin.stat stat"
					+ " from xb_checkin left join xb_room on xb_checkin.room_id=xb_room.room_id where xb_checkin.order_id=?";
			return this.queryMapList(sql, new Object[] { orderId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public Integer findRoomIdByRoomNoAndChainId(int chainId, String roomNo) {
		try {
			return this.queryForSingleColumnAndRow(Integer.class,
					"select room_id from xb_room where chain_id=? AND room_name=?", new Object[] { chainId, roomNo });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException("查询不到chainId["+chainId+"],roomName["+roomNo+"]",e);
		}
	}
}
