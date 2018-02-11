package cn.xma.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @author xma
 */
public class RedisUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final int REDIS_DEFAULT_DB = 0;
    public static final int REDIS_WMS_INV_DB = 1;
    public static final int REDIS_OMS_INV_DB = 5;

    public RedisUtil(String redisUrl, String password) {
        this.redisUrl = redisUrl;
        this.password = password;
    }

    public RedisUtil(String redisUrl) {
        this.redisUrl = redisUrl;
        this.password = null;
    }

    private String redisUrl;
    private String password;
    private JedisPool pool;
    private Object lock = new Object();
    private String host;
    private int port = 6379;

    private void prepareRedisConnectionUrl() {
        if (redisUrl != null) {
            String[] g = redisUrl.split(":");
            host = g[0];
            if (g.length > 1) {
                port = Integer.valueOf(g[1]);
            }
        }
    }

    public boolean isEnabled() {
        return host != null;
    }

    public void connect() {
        prepareRedisConnectionUrl();
        // Create and set a JedisPoolConfig
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // retrieval method is called
        poolConfig.setTestOnBorrow(false);

        // Tests whether connection is dead when returning a
        // connection to the pool
        poolConfig.setTestOnReturn(false);
        // Number of connections to Redis that just sit there
        // and do nothing
        poolConfig.setMaxIdle(20);
        // Minimum number of idle connections to Redis
        // These can be seen as always open and ready to serve
        poolConfig.setMinIdle(10);
        poolConfig.setMaxTotal(50);
        // Tests whether connections are dead during idle periods
        poolConfig.setTestWhileIdle(false);
        // Maximum number of connections to test in each idle check
        poolConfig.setNumTestsPerEvictionRun(10);
        // Idle connection checking period
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);

        // Create the jedisPool
        //timeout值设置为10秒
        pool = new JedisPool(poolConfig, host, port, 2000 * 5, password);
    }

    public void release() {
        pool.destroy();
    }

    public Jedis getJedis() {
        return getJedis(REDIS_DEFAULT_DB);
    }

    public Jedis getJedis(Integer db) {
        if (pool == null) {
            synchronized (lock) {
                if (pool == null) {
                    connect();
                    testRedisConnection();
                }
            }
        }
        Jedis jedis = pool.getResource();
        jedis.select(db);
        return jedis;
    }

    private void testRedisConnection() {
        try (Jedis jedis = pool.getResource()) {
            jedis.ping();
        } catch (Exception e) {
            logger.error("Redis [" + host + ":" + port + "] 连接失败!", e);
        }
    }

    public void returnJedis(Jedis jedis) {
        jedis.close();
    }

    public long getSequence(String seqName) {
        return getSequence(seqName, REDIS_DEFAULT_DB);
    }

    public long getSequence(String seqName, Integer db) {
        String key = "SEQUENCE_" + seqName;
        Jedis jedis = getJedis(db);
        long seq = jedis.incr(key);
        jedis.close();
        return seq;
    }

    public void set(String key, String value) {
        set(key, value, REDIS_DEFAULT_DB);
    }

    public void set(String key, byte[] bytes) {
        Jedis jedis = getJedis(REDIS_DEFAULT_DB);
        jedis.set(key.getBytes(), bytes);
        jedis.close();
    }

    public void set(String key, String value, Integer db) {
        Jedis jedis = getJedis(db);
        jedis.set(key, value);
        jedis.close();
    }

    public void setex(String key, int seconds, String value) {
        setex(key, seconds, value, REDIS_DEFAULT_DB);
    }

    public void setex(String key, int seconds, String value, Integer db) {
        Jedis jedis = getJedis(db);
        jedis.setex(key, seconds, value);
        jedis.close();
    }

    public void setex(String key, int seconds, byte[] value, Integer db) {
        Jedis jedis = getJedis(db);
        jedis.setex(key.getBytes(), seconds, value);
        jedis.close();
    }

    public long setnx(String key, String value) {
        return setnx(key, value, REDIS_DEFAULT_DB);
    }

    public long setnx(String key, String value, Integer db) {
        Jedis jedis = getJedis(db);
        long returnValue = jedis.setnx(key, value);
        jedis.close();
        return returnValue;
    }

    public boolean setnx(String key, String value, int seconds, Integer db) {
        try (Jedis jedis = getJedis(db)) {
            long ret = jedis.setnx(key, value);
            //锁拿到返回1，否则0
            if (ret > 0) {
                jedis.expire(key, seconds);
                return true;
            }
        }
        return false;
    }

    public boolean setnx(String key, int seconds, Integer db) {
        return setnx(key, "1", seconds, db);
    }

    public String getSet(String key, String value) {
        return getSet(key, value, REDIS_DEFAULT_DB);
    }

    public String getSet(String key, String value, Integer db) {
        Jedis jedis = getJedis(db);
        String oldvalue = jedis.getSet(key, value);
        jedis.close();
        return oldvalue;
    }

    public String get(String key) {
        return get(key, REDIS_DEFAULT_DB);
    }

    public byte[] getBytes(String key) {
        Jedis jedis = getJedis(REDIS_DEFAULT_DB);
        byte[] value = jedis.get(key.getBytes());
        jedis.close();
        return value;
    }

    public String get(String key, Integer db) {
        Jedis jedis = getJedis(db);
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    public boolean exists(String key) {
        return exists(key, REDIS_DEFAULT_DB);
    }

    public boolean exists(String key, Integer db) {
        Jedis jedis = getJedis(db);
        boolean b = jedis.exists(key);
        jedis.close();
        return b;
    }

    public void del(String key) {
        del(key, REDIS_DEFAULT_DB);
    }

    public void del(String key, Integer db) {
        Jedis jedis = getJedis(db);
        jedis.del(key);
        jedis.close();
    }

    public Set<String> getKeys(String key) {
        return getKeys(key, REDIS_DEFAULT_DB);
    }

    public Set<String> getKeys(String key, Integer db) {
        Jedis jedis = getJedis(db);
        Set<String> keys = jedis.keys(key);
        jedis.close();
        return keys;
    }

    public void hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        jedis.hset(key, field, value);
        jedis.close();
    }
}
