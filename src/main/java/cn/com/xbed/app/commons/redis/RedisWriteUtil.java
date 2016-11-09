//package cn.com.xbed.app.commons.redis;
//
//import cn.com.xbed.clean.common.util.CommonUtil;
//import cn.com.xbed.clean.common.util.DateUtil;
//import cn.com.xbed.clean.common.util.IOUtile;
//import cn.com.xbed.clean.common.util.ParamsUtil;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
//public final class RedisWriteUtil {
//    
//    //Redis服务器IP
//    private static String ADDR = ParamsUtil.getInstance().getRedisWriteUrl();
//    
//    //Redis的端口号
//    private static int PORT = Integer.parseInt( ParamsUtil.getInstance().getRedisWritePort() );
//    
//    //访问密码
//    private static String AUTH = CommonUtil.isEmpty(ParamsUtil.getInstance().getRedisWriteAuth()) ? null : ParamsUtil.getInstance().getRedisWriteAuth();
//    
//    //Redis副库服务器IP
//    private static String ADDR_TEMP = ParamsUtil.getInstance().getRedisWriteTempUrl();
//    
//    //Redis副库的端口号
//    private static int PORT_TEMP = Integer.parseInt( ParamsUtil.getInstance().getRedisWriteTempPort() );
//    
//    //访问副库密码
//    private static String AUTH_TEMP = CommonUtil.isEmpty(ParamsUtil.getInstance().getRedisWriteTempAuth()) ? null : ParamsUtil.getInstance().getRedisWriteTempAuth();
//    
//    //可用连接实例的最大数目，默认值为8；
//    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//    private static int MAX_ACTIVE = 1024;
//    
//    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
//    private static int MAX_IDLE = 200;
//    
//    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
//    private static int MAX_WAIT = 10000;
//    
//    private static int TIMEOUT = 10000;
//    
//    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//    private static boolean TEST_ON_BORROW = true;
//    
//    //报警标志
//    private static boolean alarmFlag = true;
//    
//    //主库
//    private static JedisPool jedisPool = null;
//    
//    //副库
//    private static JedisPool jedisPoolTemp = null;
//    
//    /**
//     * 初始化Redis连接池
//     */
//    static {
//        try {
//            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxActive(MAX_ACTIVE);
//            config.setMaxIdle(MAX_IDLE);
//            config.setMaxWait(MAX_WAIT);
//            config.setTestOnBorrow(TEST_ON_BORROW);
//            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
//    /**
//     * 获取Jedis实例
//     * @return
//     */
//    public synchronized static Jedis getJedis() {
//        try {
//            if (jedisPool != null) {
//                Jedis resource = jedisPool.getResource();
//                
//                if (!alarmFlag)
//				{
//                	alarmFlag = true;
//				}
//                return resource;
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (alarmFlag)
//			{
//            	IOUtile.sendMessageCatchException("13422196609", DateUtil.formateDateStr() + "丽家会Redis主读写库已经挂掉，请马上处理。");
//            	IOUtile.sendMessageCatchException("18680221619", DateUtil.formateDateStr() + "丽家会Redis主读写库已经挂掉，请马上处理。");
//            	IOUtile.sendMessageCatchException("15876533270", DateUtil.formateDateStr() + "丽家会Redis主读写库已经挂掉，请马上处理。");
//            	alarmFlag = false;
//			}
//            return getJedisTemp();
//        }
//    }
//    
//    public synchronized static Jedis getJedisTemp() {
//        try {
//            if (jedisPoolTemp != null) {
//                Jedis resource = jedisPoolTemp.getResource();
//                
//                return resource;
//            } else {
//            	JedisPoolConfig config = new JedisPoolConfig();
//                config.setMaxActive(MAX_ACTIVE);
//                config.setMaxIdle(MAX_IDLE);
//                config.setMaxWait(MAX_WAIT);
//                config.setTestOnBorrow(TEST_ON_BORROW);
//                jedisPoolTemp = new JedisPool(config, ADDR_TEMP, PORT_TEMP, TIMEOUT, AUTH_TEMP);
//                
//                return jedisPoolTemp.getResource();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            IOUtile.sendMessageCatchException("13422196609", DateUtil.formateDateStr() + "丽家会Redis副读写库已经挂掉，请马上紧急处理。");
//        	IOUtile.sendMessageCatchException("18680221619", DateUtil.formateDateStr() + "丽家会Redis副读写库已经挂掉，请马上紧急处理。");
//        	IOUtile.sendMessageCatchException("15876533270", DateUtil.formateDateStr() + "丽家会Redis副读写库已经挂掉，请马上紧急处理。");
//            return null;
//        }
//    }
//    
//    /**
//     * 释放jedis资源
//     * @param jedis
//     */
//    public static void returnResource(final Jedis jedis) {
//        if (jedis != null) {
//        	if (alarmFlag)
//			{
//        		jedisPool.returnResource(jedis);
//			} else
//			{
//				jedisPoolTemp.returnResource(jedis);
//			}
//        }
//    }
//}
