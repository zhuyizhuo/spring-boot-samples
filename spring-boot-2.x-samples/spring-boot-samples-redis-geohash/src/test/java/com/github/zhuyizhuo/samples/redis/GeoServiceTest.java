package com.github.zhuyizhuo.samples.redis;

import com.alibaba.fastjson.JSON;
import com.github.zhuyizhuo.samples.redis.service.GeoService;
import com.github.zhuyizhuo.samples.redis.vo.CityInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h1>GeoService 测试用例</h1>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisGeoApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GeoServiceTest {

    /** fake some cityInfos */
    private List<CityInfo> cityInfos;

    @Autowired
    private GeoService geoService;

    @Before
    public void init() {
        cityInfos = new ArrayList<>();
        cityInfos.add(new CityInfo("beijing", 116.408, 39.904));
        cityInfos.add(new CityInfo("shanghai", 121.445, 31.213));
        cityInfos.add(new CityInfo("tianjin", 117.246, 39.117));
        cityInfos.add(new CityInfo("suzhou", 116.58, 33.38));
        cityInfos.add(new CityInfo("zhengzhou", 113.641, 34.758));
        cityInfos.add(new CityInfo("shuangcheng", 126.193, 45.421));
        cityInfos.add(new CityInfo("baoding", 115.484, 38.868));
    }

    /**
     * <h2>测试 saveCityInfoToRedis 方法</h2>
     * */
    @Test
    public void testSaveCityInfoToRedis() {

        System.out.println(geoService.saveCityInfoToRedis(cityInfos));
    }

    /**
     * <h2>测试 getCityPos 方法</h2>
     * 如果传递的 city 在 Redis 中没有记录, 会返回什么呢 ? 例如, 这里传递的 xxx
     * */
    @Test
    public void testGetCityPos() {

        List<Point> cityPos = geoService.getCityPos(
                Arrays.asList("beijing", "shanghai", "nothing").toArray(new String[3])
        );
        System.out.println(JSON.toJSONString(cityPos));
    }

    /**
     * <h2>测试 getTwoCityDistance 方法</h2>
     * */
    @Test
    public void testGetTwoCityDistance() {

        System.out.println(geoService.getTwoCityDistance("anqing", "suzhou", null).getValue());
        System.out.println(geoService.getTwoCityDistance("anqing", "suzhou", Metrics.KILOMETERS).getValue());
    }

    /**
     * <h2>测试 getPointRadius 方法</h2>
     * */
    @Test
    public void testGetPointRadius() {

        Point center = new Point(cityInfos.get(0).getLongitude(), cityInfos.get(0).getLatitude());
        Distance radius = new Distance(200, Metrics.KILOMETERS);
        Circle within = new Circle(center, radius);

        System.out.println(JSON.toJSONString(geoService.getPointRadius(within, null)));

        // order by 距离 limit 2, 同时返回距离中心点的距离
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().limit(2).sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> pointRadius = geoService.getPointRadius(within, args);
        System.out.println(JSON.toJSONString(pointRadius));
    }

    /**
     * <h2>测试 getMemberRadius 方法</h2>
     * */
    @Test
    public void testGetMemberRadius() {

        Distance radius = new Distance(200, Metrics.KILOMETERS);

        System.out.println(JSON.toJSONString(geoService.getMemberRadius("suzhou", radius, null)));

        // order by 距离 limit 2, 同时返回距离中心点的距离
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().limit(2).sortAscending();
        System.out.println(JSON.toJSONString(geoService.getMemberRadius("suzhou", radius, args)));
    }

    /**
     * <h2>测试 getCityGeoHash 方法</h2>
     * */
    @Test
    public void testGetCityGeoHash() {

        System.out.println(JSON.toJSONString(geoService.getCityGeoHash(
                Arrays.asList("anqing", "suzhou", "xxx").toArray(new String[3])
        )));
    }
}