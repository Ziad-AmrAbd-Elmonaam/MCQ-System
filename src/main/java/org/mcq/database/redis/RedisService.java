package org.mcq.database.redis;

import redis.clients.jedis.Jedis;

public class RedisService {
    private final Jedis jedis;

    public RedisService(Jedis jedis) {
        this.jedis = jedis;
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void delete(String key) {
        jedis.del(key);
    }

    // Add other Redis-related methods here
}
