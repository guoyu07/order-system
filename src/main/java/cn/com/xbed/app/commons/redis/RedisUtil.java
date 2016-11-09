//package cn.com.xbed.app.commons.redis;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import redis.clients.jedis.Jedis;
//import cn.com.xbed.clean.common.util.CommonUtil;
//import cn.com.xbed.clean.common.util.JsonUtil;
//import cn.com.xbed.clean.common.util.ListUtil;
//
//public class RedisUtil {
//	
//	/**
//	 * 获取整个Redis key中所有的map数据（map的value是数组List）
//	 * @param redisKey
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> List<T> getAllListFromRedisMapList(String redisKey, Class<T> clazz)
//	{
//		List<T> list = new ArrayList<T>();
//		Jedis jedisRead = RedisReadUtil.getJedis();
//		
//		Iterator<String> iter=jedisRead.hkeys(redisKey).iterator();  
//        while (iter.hasNext()){  
//            String mapKey = iter.next();  
//            list.addAll(getListFromRedisMapList(redisKey, mapKey, clazz));
//        }  
//        
//        RedisReadUtil.returnResource(jedisRead);
//		return list;
//	}
//	
//	/**
//	 * 获取redis中map里面的一个value(map的value是数组List)
//	 * @param redisKey
//	 * @param mapKey
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> List<T> getListFromRedisMapList(String redisKey, String mapKey, Class<T> clazz)
//	{
//		Jedis jedisRead = RedisReadUtil.getJedis();
//		
//		List<T> targetList = ListUtil.createList();
//		List<Object> objectList = JsonUtil.toBean( ListUtil.getFirstOne(jedisRead.hmget(redisKey, mapKey)) , List.class);
//		
//		if ( !CommonUtil.isEmpty(objectList)) {
//			
//			for (Object object : objectList)
//			{
//				targetList.add(JsonUtil.toBean(JsonUtil.toJson(object), clazz));
//			}
//			
//		}
//		
//		RedisReadUtil.returnResource(jedisRead);
//		
//		return targetList;
//	}
//	
//	/**
//	 * 从redis中获取一整个map，并把map中所有对象存放到list中(map的value是对象)
//	 * @param redisKey
//	 * @param mapKey
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> List<T> getListFromRedisMap(String redisKey, Class<T> clazz)
//	{
//		List<T> list = new ArrayList<T>();
//		Jedis jedisRead = RedisReadUtil.getJedis();
//		
//		Iterator<String> iter=jedisRead.hkeys(redisKey).iterator();  
//        while (iter.hasNext()){  
//            String mapKey = iter.next();  
//            list.add( getBeanFromRedisMap(redisKey, mapKey, clazz) );
//        }  
//        
//        RedisReadUtil.returnResource(jedisRead);
//		return list;
//	}
//	
//	/**
//	 * 从redis获取map结构的某个mapKey的value(map的value是对象)
//	 * @param redisKey
//	 * @param mapKey
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> T getBeanFromRedisMap(String redisKey, String mapKey, Class<T> clazz)
//	{
//		Jedis jedisRead = RedisReadUtil.getJedis();
//		T bean = JsonUtil.toBean( ListUtil.getFirstOne(jedisRead.hmget(redisKey, mapKey)) , clazz);
//		RedisReadUtil.returnResource(jedisRead);
//		
//		return bean;
//	}
//	
//	/**
//	 * 把某个mapKey的value（json格式）存放进redis中map结构
//	 * @param redisKey
//	 * @param mapKey
//	 * @param jsonBean
//	 */
//	public static void writeBeanToRedisMap(String redisKey, String mapKey, String jsonBean)
//	{
//		Jedis jedisWrite = RedisWriteUtil.getJedis();
//		jedisWrite.hset(redisKey, mapKey, jsonBean);
//		RedisWriteUtil.returnResource(jedisWrite);
//	}
//	
//	/**
//	 * 删除redis中mapKey对应的map
//	 * @param redisKey
//	 * @param mapKey
//	 */
//	public static void removeBeanFromRedisMap(String redisKey, String mapKey)
//	{
//		Jedis jedisWrite = RedisWriteUtil.getJedis();
//		jedisWrite.hdel(redisKey,mapKey);
//		RedisWriteUtil.returnResource(jedisWrite);
//	}
//	/**
//	 * 删除整个redisKey
//	 * @param redisKey
//	 */
//	public static void removeRedisKey(String redisKey)
//	{
//		Jedis jedisWrite = RedisWriteUtil.getJedis();
//		jedisWrite.del(redisKey);
//		RedisWriteUtil.returnResource(jedisWrite);
//	}
//}
