package cn.com.xbed.app.service.sysparam;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.dao.common.DaoUtil;

@Service
@Transactional
public class SystemParamService {
	private ConcurrentHashMap<String, SvSysParam> sysParamCache = new ConcurrentHashMap<>();

	@Resource
	private DaoUtil daoUtil;

	public void initAllSysParam() {
		String sql = "select * from sv_sys_param";
		List<SvSysParam> list = daoUtil.sysParamDao.queryForMultiRow(SvSysParam.class, sql);
		this.clearCache();
		for (SvSysParam svSysParam : list) {
			sysParamCache.put(svSysParam.getParamKey(), svSysParam);
		}
	}

	/**
	 * 从缓存中获得配置,如果没有缓存,从库表里获取
	 * 
	 * @param key
	 * @return 配置字符串,库表无配置返回null
	 */
	public String getValue(String key) {
		SvSysParam sys = this.getSysParam(key);
		if (sys != null) {
			return sys.getParamValue();
		}
		return null;
	}

	/**
	 * 不从缓存中取数据
	 * 
	 * @param key
	 * @return 配置字符串,库表无配置返回null
	 */
	public String getValueFromDb(String key) {
		SvSysParam svSysParam = sysParamCache.get(key);
		if (svSysParam != null) {
			return svSysParam.getParamValue();
		}
		return null;
	}

	/**
	 * 从缓存中获得配置,如果没有缓存,从库表里获取
	 * 
	 * @param key
	 * @return SvSysParam
	 */
	public SvSysParam getSysParam(String key) {
		SvSysParam svSysParam = sysParamCache.get(key);
		if (svSysParam == null) {
			this.fetchFromDb(key);
		}
		return sysParamCache.get(key);
	}

	/**
	 * 删除缓存中的对应key的值
	 * 
	 * @param key
	 */
	public void remove(String key) {
		sysParamCache.remove(key);
	}

	/**
	 * 清除缓存中的所有值
	 */
	private void clearCache() {
		sysParamCache.clear();
	}

	/**
	 * 从库表获得该key对应的值
	 * 
	 * @param key
	 */
	private void fetchFromDb(String key) {
		try {
			SvSysParam svSysParam = daoUtil.sysParamDao.getByParamKey(key);
			if (svSysParam != null) {
				sysParamCache.put(svSysParam.getParamKey(), svSysParam);
			}
		} catch (Exception e) {
			// 不处理异常
		}
	}

}
