package cn.com.xbed.app.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class SysParamDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SysParamDao.class));

	public SvSysParam getByParamKey(String paramKey) {
		try {
			return this.queryForSingleRow(SvSysParam.class, "select * from sv_sys_param where param_key=?", new Object[] { paramKey });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	// 查询不到返回null,不抛错
	public String getValueByParamKeyNoThrow(String paramKey) {
		try {
			List<Map<String,Object>> list = this.queryMapList("select param_value from sv_sys_param where param_key=?", new Object[] { paramKey });
			if (!list.isEmpty()) {
				return (String) list.get(0).get("param_value");
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	/**
	 * 通过key值查找系统参数值，未配置或配置无效(空串)都将抛出运行时异常
	 * @param paramKey
	 * @return
	 */
	public String getValueByParamKey(String paramKey) {
		try {
			SvSysParam sysParam = this.queryForSingleRow(SvSysParam.class, "select * from sv_sys_param where param_key=?",
					new Object[] { paramKey });
			if (sysParam == null || sysParam.getParamValue() == null || sysParam.getParamValue().trim().equals("")) {
				throw new RuntimeException("未配置系统参数: [" + paramKey + "]");
			}
			return sysParam.getParamValue();
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
