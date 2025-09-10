package com.br.stock.control.service

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(RedisService::class.java)

    fun <T> get(key: String): T? {
        logger.debug("Trying to get '$key' from redis...")
        return redisTemplate.opsForValue().get(key) as T?
    }

    fun <T> set(key: String, value: T, ttl: Duration? = null) {
        logger.debug("Setting key '$key' in Redis...")
        val ops = redisTemplate.opsForValue()
        if (ttl != null) {
            ops.set(key, value as Any, ttl)
        } else {
            ops.set(key, value as Any)
        }
    }

    fun delete(key: String) {
        logger.debug("Deleting key '$key' from Redis...")
        redisTemplate.delete(key)
    }

    fun deleteAll() {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

}