import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class TestRedis {

    static ExecutorService executor = Executors.newSingleThreadExecutor();

    final static ShardedJedisPool redisStatsPool;
    static {
        String host = "127.0.0.1";
        int port = 6379;
        List<JedisShardInfo> redisClickShard = new ArrayList<JedisShardInfo>();
        redisClickShard.add(new JedisShardInfo(host, port));

        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxIdle(10);
//        config.maxActive = 1000;
//        config.maxIdle = 10;
//        config.minIdle = 1;
//        config.maxWait = 30000;
//        config.numTestsPerEvictionRun = 3;
//        config.testOnBorrow = true;
//        config.testOnReturn =  true;
//        config.testWhileIdle =  true;
//        config.timeBetweenEvictionRunsMillis = 30000;
        redisStatsPool = new ShardedJedisPool( config, redisClickShard);
    }

    public TestRedis() {

    }

    String[] getRandomNumber(int min, int max){
        String[] test = new String[8];
        for (int i = 0; i < test.length; i++) {
            int partition = min + (int)(Math.random() * ((max - min) + 1));
            test[i] = "key"+partition;
        }
        return test;
    }

    static volatile long  sum = 0;

    public Runnable hincrBy(final String keyname, final String[] keyfields , final long val){
        Runnable job = new Runnable() {
            @Override
            public void run() {
                c++;
                System.out.println(c);
                try {
                    ShardedJedis shardedJedis = redisStatsPool.getResource();
                    final Jedis jedis = shardedJedis.getShard("") ;
                    Pipeline p = jedis.pipelined();
                    for (String keyfield : keyfields) {
                        p.hincrBy(keyname, keyfield, val);
                        sum += val;
                    }
                    p.sync();
                    redisStatsPool.returnResource(shardedJedis);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };
        return job;
    }

    static volatile int c = 0;
    static final int MAX = (int) Math.pow(10, 6);
    void masterThread() {
        for (int i = 0; i < MAX; i++) {
            String[] keynames = getRandomNumber(100, 1000);
            executor.submit(hincrBy("test10^6", keynames, 1L));
        }
        executor.shutdown();
        while(!executor.isTerminated()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public int sumTest() {
        int total = 0;
        try {
            ShardedJedis shardedJedis = redisStatsPool.getResource();
            final Jedis jedis = shardedJedis.getShard("") ;
            Map<String,String> map  = jedis.hgetAll("test10^6");
            Set<String> keys = map.keySet();

            for (String keyfield : keys) {
                int v = Integer.parseInt(map.get(keyfield));
                total += v;
            }

            redisStatsPool.returnResource(shardedJedis);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return total;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TestRedis test = new TestRedis();
        test.masterThread();
        System.out.println(sum);
        System.out.println(test.sumTest());
        System.out.println(test.sumTest() == sum);
    }
}