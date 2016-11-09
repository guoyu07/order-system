package cn.com.xbed.app.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbPicture;
import cn.com.xbed.app.commons.enu.IdCardType;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.RandomUtil;
import net.coobird.thumbnailator.Thumbnails;

@Service
@Transactional
public class ImageCommon {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ImageCommon.class));
	@Resource
	private DaoUtil daoUtil;
	
	public static final String ROOM_PIC_ROOT_PATH = "/home/wwwroot/images/upload/images";// 房间图片根目录
	public static final String IDCARD_ROOT_PATH = "/home/wwwroot/images/upload/idcard";// 身份证照片根目录
	public static final String ORI_DIR = "ori";// 原图保存的子目录
	public static final String THUMBNAIL_400 = "400";// 原图保存的子目录
	public static final String THUMBNAIL_700 = "700";// 原图保存的子目录

	//public String get

	// 生成身份证图片名
	public String getIdCardPicName(int lodgerId,Date now, IdCardType idCardType) {
		try {
			String idCardPicName = lodgerId + "_" + DateUtil.dateToString(now, DateUtil.yrMonDay) + "_"
					+ DateUtil.dateToString(now, DateUtil.hrMinSec) + "_" + RandomUtil.getRandomStr(3);
			if (idCardType == IdCardType.FRONT) {
				idCardPicName += "_front";
			} else {
				idCardPicName += "_back";
			}
			idCardPicName += ".jpg";
			return idCardPicName;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 获得房间图片上传后的文件名
	public String getRoomPicName(int roomId, Date now, String extension) {
		try {
			// 1_999_20150826_171943.jpg
			String roomPicName = roomId + "_" + RandomUtil.getRandomStr(3) + "_"
					+ DateUtil.dateToString(now, DateUtil.yrMonDay) + "_"
					+ DateUtil.dateToString(now, DateUtil.hrMinSec) + extension;
			return roomPicName;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得身份证图片上传根目录
	public String getIdCardPicRootPath() {
		try {
			ensureDirExists(IDCARD_ROOT_PATH);
			return IDCARD_ROOT_PATH;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得身份证原图的上传目录
	public String getIdCardPicOriPath() {
		try {
			String oriPath = IDCARD_ROOT_PATH + "/" + ORI_DIR;
			ensureDirExists(oriPath);
			return oriPath;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得房间图片上传根目录
	public String getRoomPicRootPath() {
		try {
			ensureDirExists(ROOM_PIC_ROOT_PATH);
			return ROOM_PIC_ROOT_PATH;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得房间原图的上传目录
	public String getRoomPicOriPath() {
		try {
			String oriPath = ROOM_PIC_ROOT_PATH + "/" + ORI_DIR;
			ensureDirExists(oriPath);
			return oriPath;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}


	/**
	 * 生成各种尺寸的缩略图
	 * @param rootPath 房间图片、或身份证图片存放的根目录
	 * @param oriNameNotContainPath 文件名,包括扩展名
	 * @return
	 */
	public int generateThumb(String rootPath, String oriNameNotContainPath) {
		try {
			// 原图位置
			String oriPic = rootPath + "/" + ORI_DIR + "/" + oriNameNotContainPath;
			
			// 适合PC端的图
			String path700 = rootPath + "/" + THUMBNAIL_700;
			ensureDirExists(path700);
			String thumb700 = path700 + "/" + oriNameNotContainPath;
			Thumbnails.of(oriPic).size(700, 700).toFile(thumb700);

			// 适合手机端的图
			String path400 = rootPath + "/" + THUMBNAIL_400;
			ensureDirExists(path400);

			String thumb400 = path400 + "/" + oriNameNotContainPath;
			Thumbnails.of(oriPic).size(400, 400).toFile(thumb400);

			return 1;
		} catch (Exception e) {
			return -1;
		}
	}
	
	// 保证目录存在
	private void ensureDirExists(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得房间封面图片
	public String getCoverPicName(int roomId) {
		try {	
			List<XbPicture> list = daoUtil.pictureDao.findRoomPicByRoomId(roomId);
			if (list == null || list.isEmpty()) {
				return "";
			}
			int sz = list.size();
			String picName = list.get(0).getFilePath();
			for (int i = 0; i < sz; i++) {
				XbPicture unit = list.get(i);
				if (unit.getCover() == 1) {
					picName = unit.getFilePath();
					break;
				}
			}
			return picName;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 获得房间导航图
	 * @param roomId
	 * @return
	 */
	public String getNaviPicName(int roomId) {
		try {	
			List<XbPicture> list = daoUtil.pictureDao.findNaviPicByRoomId(roomId);
			if (list == null || list.isEmpty()) {
				return "";
			}
			String picName = list.get(0).getFilePath();
			return picName;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
