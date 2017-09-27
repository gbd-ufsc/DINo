package br.ufsc.lisa.DINo;

import com.mongodb.Cursor;

public interface IRelationalDB {
	
	public void connect(String uri);
	
	public Cursor read(String table, String keyColumns);
	
}
