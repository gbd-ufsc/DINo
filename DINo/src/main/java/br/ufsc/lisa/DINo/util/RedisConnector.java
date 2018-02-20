package br.ufsc.lisa.DINo.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnector implements Connector {

	// private MongoClient client;
	//private Jedis jedis;
	public JedisPool pool;
	private String db;
	private Integer port;
	public String password;

	public boolean connect(String uri, String port, String password) {
		
		try {
			pool = new JedisPool(new JedisPoolConfig(), uri, Integer.valueOf(port), 2000);

			Jedis jedis = pool.getResource();
			if(password != null & !password.isEmpty()) {
				this.password = password;
				jedis.auth(password);
			}
			jedis.connect();
			jedis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		db = uri;
		this.port = Integer.valueOf(port);
		this.password =  password;
//		jedis = pool.getResource();
		return true;
		
	}

	@Override
	public boolean put(String key, String value) {
		Jedis jedis = pool.getResource();
		if (jedis != null) {
			String rkey = key;
			String rvalue = value.toString();
			jedis.set(rkey, rvalue);
			// jedis.close();
			return true;
		}
		jedis.close();
		return false;
	}
	
	@Override
	public void close() {
		this.pool.close();;
	}

	@Override
	public String toString() {
		return "Redis DB";
	}

	@Override
	public boolean connect(String uri, String port, String user, String password, String DB) {
		return this.connect(uri, port, password);
	}



}
