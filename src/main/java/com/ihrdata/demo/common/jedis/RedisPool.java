package com.ihrdata.demo.common.jedis;

import com.ihrdata.wtool.common.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.valueOf(PropertiesUtil.getProperty("jedis.maxTotal")));
        jedisPoolConfig.setMaxIdle(Integer.valueOf(PropertiesUtil.getProperty("jedis.maxIdle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(PropertiesUtil.getProperty("jedis.minIdle")));
        jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(PropertiesUtil.getProperty("jedis.testOnBorrow")));
        jedisPoolConfig.setTestOnReturn(Boolean.valueOf(PropertiesUtil.getProperty("jedis.testOnReturn")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(PropertiesUtil.getProperty("jedis.maxWaitMillis")));

        String property = PropertiesUtil.getProperty("spring.redis.password");
        jedisPool = new JedisPool(jedisPoolConfig, PropertiesUtil.getProperty("spring.redis.host"),
            Integer.valueOf(PropertiesUtil.getProperty("spring.redis.port")),
            Integer.valueOf(PropertiesUtil.getProperty("spring.redis.timeout")),
            "".equals(property) ? null : property,
            Integer.valueOf(PropertiesUtil.getProperty("spring.redis.database")));
    }

    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    public static void close(Jedis jedis) {
        jedis.close();
    }
}
