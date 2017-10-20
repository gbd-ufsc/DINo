package br.ufsc.lisa.DINo.util;

import com.mongodb.Cursor;

public interface RelationalDB {
	
	public boolean connect(String uri, String porta, String user, String password, String dbName);
	
	public Cursor read(String table, String keyColumns);
	
}



