package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbPicture;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class PictureDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(PictureDao.class));

	public boolean add(XbPicture entity) {
		try {
			return this.insertEntity(entity, "picId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbPicture entity) {
		try {
			return this.insertEntityAndGetPk(entity, "picId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int deleteByPk(int pk) {
		try {
			return this.updateOrDelete("delete from xb_picture where pic_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbPicture findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbPicture.class, "select * from xb_picture where pic_id=?",
					new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateRoomByPk(XbPicture entity) {
		try {
			if (entity.getPicId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "picId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public List<XbPicture> findRoomPicByRoomId(int roomId) {
		try {
			return this.queryForMultiRow(XbPicture.class, "SELECT * FROM xb_picture WHERE room_id=? AND pic_type=?",
					new Object[] { roomId, AppConstants.Picture_picType.ROOM_PIC_1 });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public String findRoomPicByRoomIdTopPic(int roomId) {
		try {
			List<Map<String, Object>> results = this.queryMapList("SELECT file_path FROM xb_picture WHERE room_id=? AND pic_type=? order by create_dtm desc limit 1 ", new Object[] { roomId, AppConstants.Picture_picType.ROOM_PIC_1 }, true);
			if(results != null && results.size() > 0)
				return results.get(0).get("file_path").toString();
			else
				return "";
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbPicture> findRoomPicByRoomIdCoverPicFirst(int roomId) {
		try {
			return this.queryForMultiRow(XbPicture.class, "SELECT * FROM xb_picture WHERE room_id=? AND pic_type=? ORDER BY cover DESC",
					new Object[] { roomId, AppConstants.Picture_picType.ROOM_PIC_1 });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbPicture> findNaviPicByRoomId(int roomId) {
		try {
			return this.queryForMultiRow(XbPicture.class, "SELECT * FROM xb_picture WHERE room_id=? AND pic_type=?",
					new Object[] { roomId, AppConstants.Picture_picType.NAVI_MAP_PIC_2 });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbPicture findCoverRoomPicByRoomId(int roomId) {
		try {
			List<XbPicture> list = this.queryForMultiRow(XbPicture.class,
					"SELECT * FROM xb_picture WHERE room_id=? AND cover=1 AND pic_type=?",
					new Object[] { roomId, AppConstants.Picture_picType.ROOM_PIC_1 });
			return list == null || list.size() == 0 ? null : list.get(0);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
