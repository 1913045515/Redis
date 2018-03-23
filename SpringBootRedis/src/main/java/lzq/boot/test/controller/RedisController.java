package lzq.boot.test.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Controller
@RequestMapping("/test")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("redisTest")
    @ResponseBody
    public String redisTest() {
        // 数据库链接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMinIdle(20);
        config.setMaxWaitMillis(6 * 1000);
        config.setTestOnBorrow(true);
        // Redis集群的节点集合
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("211.159.165.227", 6380));
        jedisClusterNodes.add(new HostAndPort("211.159.165.227", 6381));
        jedisClusterNodes.add(new HostAndPort("211.159.165.227", 6379));
        // 根据节点集创集群链接对象
        //JedisCluster jedisCluster = newJedisCluster(jedisClusterNodes);
        // 节点，超时时间，最多重定向次数，链接池
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, 2000, 100, config);
        jedisCluster.set("key1", "test shuju1");
        jedisCluster.set("key2", "test shuju2");
        jedisCluster.set("key3", "test shuju3");
        System.out.println("key1::" + jedisCluster.get("key1"));
        System.out.println("key2:" + jedisCluster.get("key2"));
        System.out.println("key3:" + jedisCluster.get("key3"));
        return "success";
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() {
//		System.out.println(name);
//		RedisUtil.getJedis().set("newname", "中文测试");
//		System.out.println(RedisUtil.getJedis().get("name"));
//		System.out.println(RedisUtil.getJedis().get("key"));

        // 生成多机连接信息列表
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo("211.159.165.227", 6380));
        shards.add(new JedisShardInfo("211.159.165.227", 6381));
        shards.add(new JedisShardInfo("211.159.165.227", 6379));

        // 生成连接池配置信息
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(10);
        config.setMaxTotal(30);
        config.setMaxWaitMillis(3 * 1000);

        // 在应用初始化的时候生成连接池
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);

        // 在业务操作时，从连接池获取连接
        ShardedJedis client = pool.getResource();
        String keyValue = "", nameValue = "";
        try {
            // 执行指令
            String key = client.set("key", "Hello, Redis!111111111111");
            String name = client.set("name", "Hello, Redis!22222222222");
            System.out.println(String.format("set指令执行结果:%s", key));
            System.out.println(String.format("set指令执行结果:%s", name));
            keyValue = client.get("key");
            nameValue = client.get("name");
            System.out.println(String.format("get指令执行结果:%s", keyValue));
            System.out.println(String.format("get指令执行结果:%s", nameValue));
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            // 业务操作完成，将连接返回给连接池
            if (null != client) {
                pool.returnResource(client);
            }
        } // end of try block
        // 应用关闭时，释放连接池资源
        pool.destroy();
        return "success";
    }

    @RequestMapping("get")
    @ResponseBody
    public String getRedisInfo(String key) {
        String result = "";
        // 保存对象
        result = redisTemplate.opsForValue().get(key) + "";
        return result;
    }

    @RequestMapping("set")
    @ResponseBody
    public String saveRedisInfo(String key, String value) {
        // 保存对象
        redisTemplate.opsForValue().set(key, value);
        return "save is success and save key is :" + key + " save value is:" + value;
    }
}
