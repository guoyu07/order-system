package cn.com.xbed.app.service.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.commons.enu.HTTPMethod;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Service
@Transactional
public class TagService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TagService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonSms commonSms;
	public static final int SUCCESS_CODE = 200;

	/**
	 * 查房间所有标签
	 * 
	 * @param roomId
	 * @return
	 */
	public List<String> getTagByRoom(int roomId) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("roomId", roomId);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【A】查房间所有标签,入参: " + roomId);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.getTagByRoom");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			exceptionHandler.getLog().info("【A】查房间所有标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				JSONArray array = obj.getJSONArray("data");
				List<String> tags = new ArrayList<>();
				for (Object object : array) {
					tags.add(object.toString());
				}
				return tags;
			} else {
				throw new RuntimeException("查房间所有标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("查房间所有标签签错误", e);
		}
	}

	/**
	 * 修改标签中文名字
	 * 
	 * @param tagCode
	 * @param tagCname
	 * @return
	 */
	public void editTag(String tagCode, String tagCname) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCode", tagCode);
			jsonData.put("tagCname", tagCname);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【B】修改标签中文名字,入参,tagCode: " + tagCode + ",tagCname:" + tagCname);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.editTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			exceptionHandler.getLog().info("【B】修改标签中文名字,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw new RuntimeException("修改标签中文名字错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("修改标签中文名字错误", e);
		}
	}

	/**
	 * 删除标签
	 * 
	 * @param tagCode
	 */
	public void removeTag(String tagCode) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCode", tagCode);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【C】删除标签,入参,tagCode: " + tagCode);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.removeTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			exceptionHandler.getLog().info("【C】删除标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw new RuntimeException("删除标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("删除标签错误", e);
		}
	}

	/**
	 * 取某标签的子标签
	 * 
	 * @param tagCode
	 * @return
	 */
	public List<Map<String, String>> getSubTag(String tagCode) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCode", tagCode);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【D】取某标签的子标签,入参: " + tagCode);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.getSubTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【D】取某标签的子标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				JSONArray array = obj.getJSONArray("data");
				List<Map<String, String>> tags = new ArrayList<>();
				for (Object obje : array) {
					JSONObject object = (JSONObject) obje;
					Map<String, String> map = new HashMap<>(2);
					map.put("code", object.getString("code"));
					map.put("cname", object.getString("cname"));
					tags.add(map);
				}
				return tags;
			} else {
				throw new RuntimeException("取某标签的子标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("取某标签的子标签错误", e);
		}
	}

	/**
	 * 在某标签下创建子标签
	 * 
	 * @param tagCode
	 * @param subTagCname
	 * @return
	 */
	public Map<String, String> createSubTag(String tagCode, String subTagCname) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCode", tagCode);
			jsonData.put("subTagCname", subTagCname);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【E】在某标签下创建子标签,入参: " + tagCode + ",subTagCname:" + subTagCname);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.createSubTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【E】在某标签下创建子标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				JSONObject object = obj.getJSONObject("data");
				Map<String, String> map = new HashMap<>(2);
				map.put("tagCode", object.getString("tagCode"));
				map.put("tagCname", object.getString("tagCname"));
				return map;
			} else {
				throw new RuntimeException("在某标签下创建子标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("在某标签下创建子标签错误", e);
		}
	}

	/**
	 * 删除某房间所有标签
	 * 
	 * @param roomId
	 * @return
	 */
	public void cleanRoomAllTag(int roomId) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("roomId", roomId);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【F】删除某房间所有标签,入参: " + roomId);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.cleanRoomAllTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【F】删除某房间所有标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw new RuntimeException("删除某房间所有标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("删除某房间所有标签错误", e);
		}
	}

	/**
	 * 取消房间某个标签
	 * 
	 * @param roomId
	 * @param tagCode
	 */
	public void cancelRoomAssignTag(int roomId, String tagCode) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("roomId", roomId);
			jsonData.put("tagCode", tagCode);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【G】取消房间某个标签,入参: " + roomId);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.cancelRoomAssignTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【G】取消房间某个标签,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw new RuntimeException("取消房间某个标签错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("取消房间某个标签错误", e);
		}
	}

	/**
	 * 按标签查房间
	 * 
	 * @param expression
	 */
	public List<Integer> getAllRoomsByTag(String expression) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("query", expression);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【F】按标签查房间,入参: " + expression);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.getAllRoomsByTag");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【F】按标签查房间,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			List<Integer> roomIdList = new ArrayList<>();
			if (retCode == SUCCESS_CODE) {// 成功
				JSONArray array = obj.getJSONArray("data");
				for (Object object : array) {
					roomIdList.add((Integer) object);
				}
				return roomIdList;
			} else {
				throw new RuntimeException("按标签查房间错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("按标签查房间错误", e);
		}
	}

	/**
	 * 将某个标签赋值给指定房间
	 * 
	 * @param tagCode
	 * @param roomIdList
	 * @return
	 */
	public void assignTagToRoom(String tagCode, int... roomIds) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCode", tagCode);
			jsonData.put("roomList", roomIds);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【G】将某个标签赋值给指定房间,入参,tagCode: " + tagCode + ",roomIdList:" + Arrays.toString(roomIds));
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.assignTagToRoom");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【G】将某个标签赋值给指定房间,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				//
			} else {
				throw new RuntimeException("将某个标签赋值给指定房间错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("将某个标签赋值给指定房间错误", e);
		}
	}

	/**
	 * 注册根标签
	 * 
	 * @param tagCname
	 */
	public Map<String, String> registerSubRoot(String tagCname) {
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> jsonData = new HashMap<>();
			jsonData.put("tagCname", tagCname);
			params.put("jsonData", jsonData);

			// 调用接口
			exceptionHandler.getLog().info("【H】将某个标签赋值给指定房间,入参,tagCname: " + tagCname);
			String url = daoUtil.sysParamDao.getValueByParamKey("url.tag.registerSubRoot");
			String retStr = HttpHelper.callRemoteMethod(url, params, HTTPMethod.POST);
			// String retStr = Base64ImageUtil.readFileContent("d:/1.txt");
			exceptionHandler.getLog().info("【H】将某个标签赋值给指定房间,出参: " + retStr);

			// 处理返回
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				JSONObject object = obj.getJSONObject("data");
				Map<String, String> map = new HashMap<>(2);
				map.put("tagCode", object.getString("tagCode"));
				map.put("tagCname", object.getString("tagCname"));
				return map;
			} else {
				throw new RuntimeException("将某个标签赋值给指定房间错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("将某个标签赋值给指定房间错误", e);
		}
	}
}
