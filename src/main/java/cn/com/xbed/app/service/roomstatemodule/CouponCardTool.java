package cn.com.xbed.app.service.roomstatemodule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.commons.enu.HTTPMethod;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.Base64ImageUtil;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.HttpHelper.HeaderPair;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;

@Component
public class CouponCardTool {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CouponCardTool.class));
	public static final String COUPON_CARD_SECRET_KEY = "02a1a682e7f4482bbcd2cdc5682baaaa";// 卡券接口的密钥
	public static final int SUCCESS_CODE = 21020000;

	private String queryCalculateOrderPriceUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.couponcard.calculateMoney");
	}

	private String queryLockCardUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.couponcard.lock");
	}

	private String queryUnLockCardUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.couponcard.unlock");
	}

	private String queryDestroyCardUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.couponcard.destroy");
	}
	private String queryCardIdByOrderNoUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.couponcard.queryCardIdByOrderNo");
	}
	/**
	 * 查询订单的价格
	 * 
	 * @param checkinTime
	 *            必传
	 * @param checkoutTime
	 *            必传
	 * @param roomId
	 *            必传
	 * @param cardIds
	 *            选填
	 * @return 返回-1表示房间被占
	 */
	public Map<String, Object> calculateOrderPrice(Date checkinTime, Date checkoutTime, int roomId, String cardCode, int lodgerId) {
		try {
			Map<String, Object> resultMap = new HashMap<>();
			String calculateOrderPriceUrl = this.queryCalculateOrderPriceUrl();

			Map<String, Object> params = new HashMap<>();
			params.put("beginDateTime", DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
			params.put("endDateTime", DateUtil.getYearMonDayHrMinSecStr_(checkoutTime));
			params.put("roomId", roomId);
			params.put("lodgerId", lodgerId);
			if (StringUtils.isNotBlank(cardCode)) {
				params.put("code", cardCode);
			}
			exceptionHandler.getLog().info("【H】计算订单价格,入参: " + calculateOrderPriceUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", COUPON_CARD_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(calculateOrderPriceUrl, params, HTTPMethod.POST, headerPair);
			exceptionHandler.getLog().info("【H】计算订单价格,出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			Object data = obj.get("data");
			if (retCode == SUCCESS_CODE) {// 成功
				Integer actualPrice = obj.getJSONObject("data").getInteger("totalPrice");
				Integer discountPrice = obj.getJSONObject("data").getInteger("discountPrice");
				Map<String,Object> retval = new HashMap<>(2);
				retval.put("actualPrice", actualPrice);
				retval.put("discountPrice", discountPrice);
				resultMap.put("retcode", 1);
				resultMap.put("retval", retval);
				resultMap.put("retmsg", msg);
				return resultMap;
			} else {
				throw exceptionHandler.newErrorCodeException(retCode+"", msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("【卡券系统】报错", e);
		}
	}

	/**
	 * 锁定卡券
	 * 
	 * @param orderNo
	 * @param lodgerId
	 * @param cardIdList
	 * @return
	 */
	public Map<String,Object> lockCard(String orderNo, int lodgerId, String cardCode, Date checkinTime, Date checkoutTime, int roomId, int discountPrice, int actualPrice) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			String lockCardUrl = this.queryLockCardUrl();

			Map<String, Object> params = new HashMap<>();
			params.put("beginDateTime", DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
			params.put("endDateTime", DateUtil.getYearMonDayHrMinSecStr_(checkoutTime));
			params.put("roomId", roomId);
			if (StringUtils.isNotBlank(cardCode)) {
				params.put("code", cardCode);
			}
			params.put("lodgerId", lodgerId);
			params.put("orderNo", orderNo);
			params.put("discountPrice", discountPrice);
			params.put("totalPrice", actualPrice);

			exceptionHandler.getLog().info("【I】锁定卡券,入参: " + lockCardUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", COUPON_CARD_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(lockCardUrl, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【I】锁定卡券,出参: " + retStr);

			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			Object data = obj.get("data");
			if (retCode == SUCCESS_CODE) {// 成功
				resultMap.put("retcode", 1);
				resultMap.put("retval", data);
				resultMap.put("retmsg", msg);
				return resultMap;
			} else {
				throw exceptionHandler.newErrorCodeException(retCode+"", msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("【卡券系统】报错",e);
		}
	}

	/**
	 * 解锁卡券
	 * 
	 * @param orderNo
	 * @param lodgerId
	 * @return
	 */
	public Map<String,Object> unLockCard(String orderNo, int lodgerId) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			String unLockCardUrl = this.queryUnLockCardUrl();

			Map<String, Object> params = new HashMap<>();
			params.put("lodgerId", lodgerId);
			params.put("orderNo", orderNo);

			exceptionHandler.getLog().info("【J】解锁卡券,入参: " + unLockCardUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", COUPON_CARD_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(unLockCardUrl, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【J】解锁卡券,出参: " + retStr);

			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			Object data = obj.get("data");
			if (retCode == SUCCESS_CODE) {// 成功
				//
				resultMap.put("retcode", 1);
				resultMap.put("retval", data);
				resultMap.put("retmsg", msg);
				return resultMap;
			} else {
				throw exceptionHandler.newErrorCodeException(retCode+"", msg);
			}
		} catch (Exception e) {
			//throw exceptionHandler.logToolException("【卡券系统】报错",e);
			throw new RuntimeException("【卡券系统】报错,可能是因为卡券已被核销,不能再进行解锁操作(可能不是要紧的异常)");
		}
	}

	/**
	 * 核销卡券
	 * 
	 * @param orderNo
	 * @param lodgerId
	 * @return
	 */
	public void destroyCard(String orderNo, int lodgerId) {
		try {
			String destroyCardUrl = this.queryDestroyCardUrl();

			Map<String, Object> params = new HashMap<>();
			params.put("lodgerId", lodgerId);
			params.put("orderNo", orderNo);

			exceptionHandler.getLog().info("【K】核销卡券,入参: " + destroyCardUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", COUPON_CARD_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(destroyCardUrl, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【K】核销卡券,出参: " + retStr);

			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw exceptionHandler.newErrorCodeException(retCode+"", msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("【卡券系统】报错",e);
		}
	}
	
	/**
	 * 订单所消费的卡券查询
	 * @param orderNo
	 */
	public List<Map<String, String>> queryCardIdByOrderNo(String orderNo) {
		try {
			String queryCardIdByOrderNoUrl = this.queryCardIdByOrderNoUrl();
			queryCardIdByOrderNoUrl = queryCardIdByOrderNoUrl.replaceAll("\\{orderNo\\}", orderNo);

			Map<String, Object> params = new HashMap<>();
			params.put("orderNo", orderNo);

			exceptionHandler.getLog().info("【L】订单所消费的卡券查询,入参: " + queryCardIdByOrderNoUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", COUPON_CARD_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(queryCardIdByOrderNoUrl, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【L】订单所消费的卡券查询,出参: " + retStr);

			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				JSONArray arra = obj.getJSONArray("data");
				List<Map<String, String>> resultList = new ArrayList<>();
				for (Object object : arra) {
					JSONObject obje = (JSONObject) object;
					Map<String, String> pair = new HashMap<>(2);
					pair.put("cardId", obje.getString("cardId"));
					pair.put("code", obje.getString("code"));
					resultList.add(pair);
				}
				return resultList;
			} else {
				throw exceptionHandler.newErrorCodeException(retCode+"", msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("【卡券系统】报错",e);
		}
	}
}
