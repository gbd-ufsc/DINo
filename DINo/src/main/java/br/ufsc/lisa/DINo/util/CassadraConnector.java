package br.ufsc.lisa.DINo.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.plaf.metal.MetalIconFactory.PaletteCloseIcon;

import org.bson.Document;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.ListenableFuture;

import br.ufsc.lisa.DINo.views.MaindApp;

public class CassadraConnector implements Connector {

	private Cluster cluster;
	private String dbName;
	
	
	@Override
	public boolean connect(String uri, String port, String user, String password, String DB) {
	try {
		if (user != null && !user.isEmpty())
		 cluster = Cluster.builder()                                                    // (1)
	            .addContactPoint(uri)
	            .withPort(Integer.valueOf(port))
	            .withCredentials(user, password)
	            .build();
		else
			cluster = Cluster.builder()                                                    // (1)
            .addContactPoint(uri)
            .withPort(Integer.valueOf(port))
            .build();
		
		
		Session session = cluster.connect();
		
		session.execute("CREATE KEYSPACE IF NOT EXISTS "+DB+" WITH replication = {"
				  + " 'class': 'SimpleStrategy', "
				  + " 'replication_factor': '3' "
				  + "};" );		
		this.dbName = DB;
		session.close();
		
	} catch (Exception e) {
		return false;
	}
			
		return true;
	}
	

	@Override
	public boolean put(String key, String json) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Cassandra";
	}

	@Override
	public boolean importData(RelationalDB source, String query, MaindApp app, long block) {
		ListenableFuture<Session> session = cluster.connectAsync(this.dbName);    
		
		Statement pstmt;
		List <String> columns = new LinkedList<String>();
		String plainColumns = "";
		String plainValues = "";
		
		String ddl = "CREATE TABLE IF NOT EXISTS "+app.exportedTableName()+" ( ";
		
		for (String pk: app.exportedPks() ){
			ddl = ddl.concat(pk + " TEXT PRIMARY KEY, ");
			plainColumns+=(pk+", ");
			plainValues+=(":"+pk+", ");
			columns.add(pk);
		}
		
		for (int i=0; i< app.exportedValues().size()-1; i++) {
			String col = app.exportedValues().get(i);
			ddl = ddl.concat(col + " TEXT , ");
			plainColumns+=(col+", ");
			plainValues+=(":"+col+", ");
			columns.add(col);
		}
		
		String last = app.exportedValues().get(app.exportedValues().size()-1);
		ddl+=(last+ " TEXT ); ");
		plainColumns+=(last);
		plainValues+=(":"+last);
		columns.add(last);
		
		String insert = "INSERT INTO "+app.exportedTableName()+"("+plainColumns+") VALUES ("+plainValues+" )";
		
		//session.execute("DROP TABLE IF EXISTS "+app.exportedTableName());
		try {
			session.get().execute(ddl);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		double progress =  (double) 64/block;
		try {
			pstmt = ((PostgresDB) source).getConnection().createStatement();
			ResultSet result = pstmt.executeQuery(query);
			int i = 0, l = 0;
			
			PreparedStatement statment;
			try {
				statment = session.get().prepare(insert);
			BoundStatement boundStatement = new BoundStatement(statment);
			BatchStatement bath = new BatchStatement();
			
			
			while (result.next()) {
				BoundStatement bound = new BoundStatement(statment).bind();
				for (int k=0; k<columns.size(); k++) {
					bound.setString(k, result.getString(columns.get(k)));
				}
				
				System.out.println(bound.toString());
				bath.add(bound);
				
				
//				for (int k=0; k<columns.size()-1; k++) {
//					values.concat("\'"+result.getString(columns.get(k))+"\', ");
//				}
//				
//				boundStatement.setS
//				
//				values.concat("\'"+result.getString(columns.get(columns.size()-1))+"\') ");
				
			//	bath.add(value);
				
				if (++i % 64 == 0) {
					System.out.println("Lote: " + ++l);
					app.updateProgressBar(progress);
						session.get().executeAsync(bath);
					bath = new BatchStatement();
				}
			}
			System.out.println("Lote: " + ++l);
			
				session.get().executeAsync(bath);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
		
		return true;
	}

	@Override
	public void dropObject(String name) {
		Session session = cluster.connect(this.dbName);    
		session.execute("DROP TABLE IF EXISTS "+ name);
		session.close();
		
	}
	
	

}
