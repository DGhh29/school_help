package com.dg.schoolhelp.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public String getkey(String momo){
        return stringRedisTemplate.opsForValue().get(momo);
    }

    public Boolean delete(String momo){
        return stringRedisTemplate.delete(momo);
    }

    public void set(String momo, String momoVlaue, long timeout){ stringRedisTemplate.opsForValue().set(momo, momoVlaue, timeout, TimeUnit.SECONDS); }

    public void set(String momo, String momoVlaue){
        stringRedisTemplate.opsForValue().set(momo, momoVlaue);
    }

    public boolean hasKey(String momo){return Boolean.TRUE.equals(stringRedisTemplate.hasKey(momo));}
}
