package com.sy.tool;




import redis.clients.jedis.*;

import java.util.LinkedList;
import java.util.List;


public class RedisUtil {

    private static ShardedJedis jedis;

    private static ShardedJedisPool pool;



    /**
     * 初始化池
     */

    private static void initialPool() {
//池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        // 集群
//        JedisShardInfo jedisShardInfo1 = new JedisShardInfo("192.168.174.101", 6379);
//                jedisShardInfo1.setPassword("c866971331");
        JedisShardInfo jedisShardInfo1 = new JedisShardInfo("www.yimem.com", 6379);
        jedisShardInfo1.setPassword("123456");
        List<JedisShardInfo> list = new LinkedList<>();
        list.add(jedisShardInfo1);
         pool = new ShardedJedisPool(config, list);

    }



    public static ShardedJedis getJedisInstance() {

        if (jedis == null) {

            initialPool();

            return pool.getResource();

        } else {

            return jedis;

        }

    }

//    关闭存
    public static void closeJedisInstance(){
        if (jedis != null){
            pool.getResource().close();
        }
    }


}
