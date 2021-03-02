package com.weking.redis;

import com.wekingframework.core.LibSysUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.util.*;


public class LibRedis {
    //jedis池
    private static volatile JedisPool pool;

    private static Logger log = Logger.getLogger(LibRedis.class);

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    //静态代码初始化池配置
    static {
        //加载redis配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found!");
        }
        //创建jedis池配置实例
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池配置项值
        //config.setMaxActive(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
        config.setMaxTotal(1000);
        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        //config.setMaxWait(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));
        config.setTestWhileIdle(true);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRunsMillis(30000);
        //表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(10);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTimeMillis(60000);
        String pwd = bundle.getString("redis.password");
        //根据配置实例化jedis池
        if (LibSysUtils.isNullOrEmpty(pwd)) {
            pool = new JedisPool(config, bundle.getString("redis.ip"),
                    Integer.valueOf(bundle.getString("redis.port")));
        }else {
            pool = new JedisPool(config, bundle.getString("redis.ip"),
                    Integer.valueOf(bundle.getString("redis.port")), 3000, pwd);
        }
    }


    /**
     * 描述：保存对象
     *
     * @param key
     * @param value
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static void putObject(String key, Object value) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || value == null) {
            return;
        }
        byte[] val = SerializeUtil.serialize(value);
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            jedis.set(key.getBytes(), val);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
    }

    /**
     * 描述：根据Key获取对象
     *
     * @param key
     * @return
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:48:27
     */
    public static Object getObject(String key) {
        Jedis jedis = null;
        Object result = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            // 获取jedis实例后可以对redis服务进行一系列的操作
            byte[] value = jedis.get(key.getBytes());
            if (value != null){
                result = SerializeUtil.unserialize(value);}
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return result;
    }


    public static long delObject(String key) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.del(key.getBytes());
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
            return 0;
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
    }


    /**
     * 描述：设置String值
     *
     * @param key
     * @param value
     * @author xiaojun.zhou
     * @date 2015年6月18日下午12:04:53
     */
    public static String put(String key, String value) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || value == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 描述：获取String值
     *
     * @param key
     * @return
     * @author xiaojun.zhou
     * @date 2015年6月18日下午12:05:58
     */
    public static String get(String key) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 描述：增加String值
     * @param key
     * @return
     */
    public static Long incrBy(String key,long value) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.incrBy(key,value);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }


    /**
     * 描述：判断key是否存在
     *
     * @param key
     * @return
     * @author xiaojun.zhou
     * @date 2015年7月7日下午6:44:08
     */
    public static boolean exists(String key) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.exists(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return false;
    }

    /**
     * 描述：移除存在的KEY
     *
     * @param key
     * @author xiaojun.zhou
     * @date 2015年7月14日下午4:45:32
     */
    public static long del(String key) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.del(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e);
            broken = handleJedisException(e);
            return 0;
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
    }


    /**
     * 描述：设置Hash对象
     *
     * @param key
     * @param field 字段名称
     * @param value 字段值
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static Long hset(String key, String field, String value) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || value == null) {
            return 0L;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hset(key, field, value);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return 0L;
    }

    /**
     * 描述：将名称为key的hash中field的value增加integer
     *
     * @param key
     * @param field 字段名称
     * @param value 字段值
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static void hincrBy(String key, String field, long value) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            jedis.hincrBy(key, field, value);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
    }

    /**
     * 获取hash中的值
     *
     * @param key
     * @param field 字段名称
     */
    public static String hget(String key, String field) {
        String result = null;
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || field == null) {
            return result;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            result = jedis.hget(key, field);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return result;
    }

    /**
     * 描述：判断hash中是否有值
     *
     * @param key
     * @param field
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static boolean hexists(String key, String field) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || field == null) {
            return false;
        }
        boolean result = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            result = jedis.hexists(key, field);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return result;
    }

    /**
     * 描述：删除hash中的值
     *
     * @param key
     * @param field
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static long hdel(String key, String field) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || field == null) {
            return 0L;
        }
        long result = 0L;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            result = jedis.hdel(key, field);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return result;
    }

    public static String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || hash == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hmset(key, hash);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || fields == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hmget(key, fields);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    public static Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hgetAll(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 描述：取长度
     *
     * @param key
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static Long hlen(String key) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return (long) 0;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hlen(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return (long) 0;
    }

    /**
     * 描述：取Hash Map所有key
     *
     * @param key
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static Set<String> hkeys(String key) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.hkeys(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 描述：保长度
     */
    public static Map<String, Map<String, String>> getAllMap(String filter) {
        Jedis jedis = null;
        boolean broken = false;
        Map<String, Map<String, String>> hash = new HashMap<>();
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            Set s = jedis.keys(filter+"*");
            Iterator it = s.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Map<String, String> map = jedis.hgetAll(key);
                if (null != map) {
                    hash.put(key, map);
                }
            }
            return hash;
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return hash;
    }


    /**
     * 描述：保长度
     *
     * @param filter
     * @author xiaojun.zhou
     * @date 2015年6月18日上午11:49:05
     */
    public static List<Map<String, String>> hgetAllObject(String filter) {
        Jedis jedis = null;
        boolean broken = false;
        ArrayList hash = new ArrayList<>();
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            Set s = jedis.keys(filter + "*");
            Iterator it = s.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Map<String, String> value = jedis.hgetAll(key);
                if (null != value) {
                    hash.add(value);
                }
            }
            //return hash;
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                //pool.returnResource(jedis);
                closeResource(jedis, broken);}
        }
        return hash;
    }

    /**
     * Handle jedisException, write log and return whether the connection is broken.
     */
    protected static boolean handleJedisException(JedisException jedisException) {
        if (jedisException instanceof JedisConnectionException) {
            log.error("Redis connection lost.", jedisException);
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
                log.error("Redis connection are read-only slave.", jedisException);
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
            log.error("Jedis exception happen.", jedisException);
        }
        return true;
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected static void closeResource(Jedis jedis, boolean conectionBroken) {
        try {
            if (conectionBroken) {
                pool.returnBrokenResource(jedis);
            } else {
                pool.returnResource(jedis);
            }
        } catch (Exception e) {
            log.error("return back jedis failed, will fore close the jedis.", e);
            pool.destroy();
        }
    }

    //向名称为key的zset中添加元素member，score用于排序。如果该元素已经存在，则根据score更新该元素的顺序。
    public static Long zadd(String key, double score, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zadd(key, score, member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //删除名称为key的zset中的元素member
    public static Long zrem(String key, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zrem(key, member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //批量增加有序集
    public Long zadd(String key, Map<String,Double> scoreMembers) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || scoreMembers == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zadd(key, scoreMembers);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //如果在名称为key的zset中已经存在元素member，则该元素的score增加increment；否则向集合中添加该元素，其score的值为increment
    public Double zincrby(String key, double score, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zincrby(key, score, member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //返回名称为key的zset（元素已按score从小到大排序）中member元素的rank（即index，从0开始），若没有member元素，返回“nil”
    public Long zrank(String key, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zrank(key, member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //返回名称为key的zset（元素已按score从大到小排序）中member元素的rank（即index，从0开始），若没有member元素，返回“nil”
    public Long zrevrank(String key, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zrevrank(key, member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //返回名称为key的zset（元素已按score从小到大排序）中的index从start到end的所有元素
    public static Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //返回名称为key的zset（元素已按score从大到小排序）中的index从start到end的所有元素
    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //返回名称为key的zset中元素element的score
    public Double zscore(String key, String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null || member == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zscore(key, member);

        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //删除名称为key的zset中score >= min且score <= max的所有元素
    public Long zremrangebyscore(String key, double start, double end) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.zremrangeByScore(key, start, end);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**************************** 集合(Set)    ***************************/
    /**
     * 注意：在Redis2.4版本以前， SADD 只接受单个成员值。
     * 将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。
     *
     * @param key   当集合 key 不是集合类型时，返回一个错误。假如集合 key 不存在，则创建一个只包含添加的元素作成员的集合。
     * @param value value
     * @return Long
     */
    public static Long sadd(String key, String... value) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /***
     * 获取集合(Set)
     * @param key
     * @return
     */
    public static Set<String> smembers(String key) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.smembers(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 移除Set元素
     */
    public static Long srem(String key,String... member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return 0L;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.srem(key,member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return 0L;
    }

    /**
     * set中是否存在这个value
     */
    public static Boolean sismember(String key,String mem) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return false;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.sismember(key,mem);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return false;
    }

    /**
     * set中元素数量
     */
    public static Long scard(String key) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    //设置KEY的过期时间
    public Long expire(String key, int time) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.expire(key,time);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);}
        }
        return null;
    }

    /**
     * 将一个或多个值插入到列表头部。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key   key
     * @param value String
     * @return 返回List的长度
     */
    public static Long lpush(String key, String... value) {
        Long length = 0L;

        Jedis jedis = null;
        boolean broken = false;
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            length = jedis.lpush(key, value);

        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                //pool.returnResource(jedis);
                closeResource(jedis, broken);
            }
        }

        return length;
    }

    /************************************ 位置GEO ***************************************/
    /**
     * 添加经纬度
     */
    public static Long geoAdd(String key, double lng,double lat,String members) {
        boolean broken = false;
        Jedis jedis = null;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.geoadd(key,lng,lat,members);
        }catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                //pool.returnResource(jedis);
                closeResource(jedis, broken);
            }
        }
        return null;
    }

    /**
     * 获取用户附近范围内的用户
     */
    public static List<GeoRadiusResponse> geoRadiusByMember(String key,String member,double radius) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.georadiusByMember( key, member,radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().sortAscending());
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                //pool.returnResource(jedis);
                closeResource(jedis, broken);
            }
        }
        return null;
    }

    /**
     * 获取用户附近范围内的用户
     */
    public static Double geoMemberDist(String key,String member1,String member2) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.geodist(key,member1,member2,GeoUnit.KM);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                //pool.returnResource(jedis);
                closeResource(jedis, broken);
            }
        }
        return null;
    }

    /**
     * 获取用户附近范围内的用户
     */
    public static List<GeoCoordinate> geoMemberPos(String key,String member) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.geopos(key,member);
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);
            }
        }
        return null;
    }

    /**
     * 获取用户附近范围内的用户
     */
    public static List<GeoRadiusResponse> georadius(String key,double lng,double lat,double radius) {
        Jedis jedis = null;
        boolean broken = false;
        if (key == null) {
            return null;
        }
        try {
            // 从jedis池中获取一个jedis实例
            jedis = pool.getResource();
            return jedis.georadius(key,lng,lat,radius,GeoUnit.KM,GeoRadiusParam.geoRadiusParam().withDist().sortAscending());
        } catch (JedisException e) {
            log.error("【异常提示信息】" + e, e);
            broken = handleJedisException(e);
        } finally {
            // 释放对象池，即获取jedis实例使用后要将对象还回去
            if (jedis != null){
                closeResource(jedis, broken);
            }
        }
        return null;
    }

    public static void main(String[] args){
        sadd("aaa","bbb");
        sadd("aaa","ccc");
        sadd("aaa","xxx");
        System.out.println(exists("aaa"));
        System.out.println(exists("aa"));
//        System.out.println(scard("aaa"));
//        System.out.println(scar("aaa"));
//        System.out.println(sismember("aa","bbb"));
//        System.out.println(sismember("aaa","sss"));
    }

    /**

     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */

    public static boolean tryGetDistributedLock( String lockKey, String requestId, int expireTime) {
        // 从jedis池中获取一个jedis实例
        Jedis jedis = pool.getResource();
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }


    /**

     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功

     */

    public static boolean releaseDistributedLock( String lockKey, String requestId) {
        Jedis jedis = pool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
}
