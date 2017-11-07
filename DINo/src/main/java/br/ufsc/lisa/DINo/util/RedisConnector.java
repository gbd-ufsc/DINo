package br.ufsc.lisa.DINo.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnector implements Connector{
	
//	private MongoClient client;	
	Jedis jedis;
	JedisPool pool;
	String db;
	
	public void connect(String uri) {
		pool = new JedisPool(new JedisPoolConfig(), "localhost");
        db = uri;
        jedis = pool.getResource();		
	}
	public boolean put(String key, JSON value) {
		if (jedis != null){
            String rkey = key;
            String rvalue = value.toString();
            jedis.set(rkey, rvalue);
            //jedis.close();
            return true;
        }
        	return false;
	}
	
	public boolean put(String key, String json) {
		// TODO Auto-generated method stub
		return false;
	}
	
}


