# Redis Geo Hash 实现根据经纬度计算距离
环境
1. 本地 Redis ,无密码 默认 6379 端口
2. jdk8+

首先运行单元测试 GeoServiceTest 的 testSaveCityInfoToRedis 方法,将测试数据加载至 redis。
依次运行余下单元测试，查看输出结果。