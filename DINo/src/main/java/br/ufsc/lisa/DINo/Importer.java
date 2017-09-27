package br.ufsc.lisa.DINo;

import com.mongodb.MongoClient;

public class Importer implements IImporter{
	
	private RelationalDB rDB;
	private MongoClient nRDB; // mongoClient ou mongoConnector??

	public void importData(String table) {
		
	}

}
