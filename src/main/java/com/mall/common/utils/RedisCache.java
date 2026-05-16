package com.mall.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {

    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ========== String操作 ==========

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.warn("Redis set failed, key: {}", key);
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis set failed, key: {}", key);
        }
    }

    public <T> T get(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis get failed, key: {}", key);
            return null;
        }
    }

    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis delete failed, key: {}", key);
            return false;
        }
    }

    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.warn("Redis delete failed, keys: {}", keys);
            return 0L;
        }
    }

    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.warn("Redis hasKey failed, key: {}", key);
            return false;
        }
    }

    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.warn("Redis getExpire failed, key: {}", key);
            return null;
        }
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis expire failed, key: {}", key);
            return false;
        }
    }

    // ========== Key操作 ==========

    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.warn("Redis keys failed, pattern: {}", pattern);
            return Collections.emptySet();
        }
    }

    // ========== Hash操作 ==========

    public void hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.warn("Redis hSet failed, key: {}", key);
        }
    }

    public <T> T hGet(String key, String field) {
        try {
            return (T) redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.warn("Redis hGet failed, key: {}", key);
            return null;
        }
    }

    public Map<Object, Object> hEntries(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.warn("Redis hEntries failed, key: {}", key);
            return null;
        }
    }

    public Boolean hDelete(String key, Object... fields) {
        try {
            return redisTemplate.opsForHash().delete(key, fields) > 0;
        } catch (Exception e) {
            log.warn("Redis hDelete failed, key: {}", key);
            return false;
        }
    }

    // ========== List操作 ==========

    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.warn("Redis lPush failed, key: {}", key);
            return null;
        }
    }

    public <T> T lPop(String key) {
        try {
            return (T) redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.warn("Redis lPop failed, key: {}", key);
            return null;
        }
    }

    public <T> List<T> lRange(String key, long start, long end) {
        try {
            return (List<T>) redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.warn("Redis lRange failed, key: {}", key);
            return null;
        }
    }

    // ========== Set操作 ==========

    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.warn("Redis sAdd failed, key: {}", key);
            return null;
        }
    }

    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.warn("Redis sMembers failed, key: {}", key);
            return null;
        }
    }

    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.warn("Redis sIsMember failed, key: {}", key);
            return false;
        }
    }

    // ========== ZSet操作 ==========

    public Boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.warn("Redis zAdd failed, key: {}", key);
            return false;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.warn("Redis zRange failed, key: {}", key);
            return null;
        }
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.warn("Redis zReverseRange failed, key: {}", key);
            return null;
        }
    }
}
