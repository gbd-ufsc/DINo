package br.ufsc.lisa.DINo.util;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

import br.ufsc.lisa.DINo.views.MaindApp;

public class MongoConnector implements Connector{
	
	private MongoClient client;
	private MongoCredential credential;
	private MongoDatabase db;
	
	public boolean put(String key, String json) {
		Document doc = Document.parse(json);
		doc.replace("_id", key);
		this.db.getCollection("teste").insertOne(doc);
		return true;
	}
	@Override
	public boolean  connect(String uri, String port, String user, String password, String DB) {
		if (user != null && !user.isEmpty()) {
			try {
				this.credential = MongoCredential.createCredential(user, DB, password.toCharArray());
				this.client = new MongoClient(new ServerAddress(uri, Integer.valueOf(port)), Arrays.asList(credential));
				db = this.client.getDatabase(DB);	
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				this.client = new MongoClient(new ServerAddress(uri, Integer.valueOf(port)));
				db = this.client.getDatabase(DB);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	@Override
	public void close() {
		this.client.close();
	}
	@Override
	public String toString() {
		return "MongoDB";
	}
	@Override
	public boolean importData(RelationalDB source, String query, MaindApp app, long block) {
		Statement pstmt;
		String collection = app.exportedTableName();
		double progress =  (double) 256/block;
		try {
			pstmt = ((PostgresDB) source).getConnection().createStatement();
			ResultSet result = pstmt.executeQuery(query);
			int i = 0, l = 0;
			//Transaction t = jedis.multi();
			List<Document> bath = new ArrayList<Document>();
			InsertManyOptions options = new InsertManyOptions().ordered(false);
			while (result.next()) {
				String key = result.getString("?column?");
				Document value = Document.parse(result.getString("value"));
				
				value.put("_id", key+"_"+i+"."+l);
				
				bath.add(value);
				
				if (++i % 256 == 0) {
					System.out.println("Lote: " + ++l);
					app.updateProgressBar(progress);
					this.db.getCollection(collection).insertMany(bath, options);
					bath = new ArrayList<>();
				}
			}
			if (bath.size()>0) {
				app.updateProgressBar(progress);
				this.db.getCollection(collection).insertMany(bath, options);
			}
				
			System.out.println("Lote: " + ++l);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	@Override
	public void dropObject(String name) {
		this.db.getCollection(name).drop();
		
	}
	@Override
	public void createStructure(String name, MaindApp app) {
		// unnecessary
		
	}

}
