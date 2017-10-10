package br.ufsc.lisa.DINo.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoConnector implements Connector{
	
	private MongoClient client;
	private DB db;
	
	public void connect(String uri) {
		// TODO Auto-generated method stub
		
	}
	public boolean put(String key, JSON value) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean put(String key, String json) {
		// TODO Auto-generated method stub
		return false;
	}
		

}
