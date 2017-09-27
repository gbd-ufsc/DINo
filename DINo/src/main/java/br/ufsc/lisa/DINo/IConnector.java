package br.ufsc.lisa.DINo;

import com.mongodb.util.JSON;

public interface IConnector {
	
	public void connect(String uri);
	
	public boolean put(String key, JSON value);
	
}
