package com.github.zhuyizhuo.samples.redis.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.zhuyizhuo.samples.redis.service.GeoService;
import com.github.zhuyizhuo.samples.redis.vo.CityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GeoServiceImpl implements GeoService {

    private final String GEO_KEY = "cities";

    /** redis 客户端 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public GeoServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Long saveCityInfoToRedis(Collection<CityInfo> cityInfos) {

        log.info("start to save city info: {}.", JSON.toJSONString(cityInfos));
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        Set<RedisGeoCommands.GeoLocation<String>> locations = new HashSet<>();
        cityInfos.forEach(ci -> locations.add(new RedisGeoCommands.GeoLocation<String>(
                ci.getCity(), new Point(ci.getLongitude(), ci.getLatitude())
        )));

        Long add = ops.add(GEO_KEY, locations);
        log.info("done to save city info.");
        return add;

    }

    @Override
    public List<Point> getCityPos(String[] cities) {

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        return ops.position(GEO_KEY, cities);
    }

    @Override
    public Distance getTwoCityDistance(String city1, String city2, Metric metric) {

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        return metric == null ?
                ops.distance(GEO_KEY, city1, city2) : ops.distance(GEO_KEY, city1, city2, metric);
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> getPointRadius(
            Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        return args == null ?
                ops.radius(GEO_KEY, within) : ops.radius(GEO_KEY, within, args);
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> getMemberRadius(
            String member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args
    ) {

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        return args == null ?
                ops.radius(GEO_KEY, member, distance) : ops.radius(GEO_KEY, member, distance, args);
    }

    @Override
    public List<String> getCityGeoHash(String[] cities) {

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        return ops.hash(GEO_KEY, cities);
    }
}