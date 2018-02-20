package br.ufsc.lisa.DINo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class PostgresDB implements RelationalDB {

	private String driver = "org.postgresql.Driver";
	private Connection con = null;
	private String url;
	private RedisConnector redisDb;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean connect(String host, String porta, String user, String password, String dbName) {
		this.url = "jdbc:postgresql://"+host+":"+porta+"/"+ dbName;
		
		try {
			Class.forName(driver);
			if(con != null) {
				con.close();
			}
			con = (Connection) DriverManager.getConnection(url, user, password);
			if(dbName == "?") {
				System.out.println("Conexão estável, favor selecionar um banco de dados.");
			return true;
			}
		} catch (ClassNotFoundException ex) {
			System.err.print(ex.getMessage());
			return false;
		} catch (SQLException e) {
			System.err.print(e.getMessage());
			return false;
		}		
		System.out.println("Conexão estável com o banco de dados " + dbName);
		return true;
	}

	public List<String> listDatabases() throws ClassNotFoundException, SQLException  {

		List<String> listDatabase = new LinkedList();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT datname from pg_database where datistemplate = false");

		Statement pstmt = con.createStatement();

		ResultSet rs = pstmt.executeQuery(sql.toString());

		while (rs.next()) {
			listDatabase.add(rs.getString("datname"));
		}
		con.close();
		con=null;
		return listDatabase;        
	}

	public List<String> listTables() throws ClassNotFoundException, SQLException  {

		List<String> listTables = new LinkedList<String>();

		StringBuilder sql = new StringBuilder();
		sql.append("select tablename FROM pg_catalog.pg_tables WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'pg_toast') ORDER BY tablename;");

		Statement pstmt = con.createStatement();

		ResultSet rs = pstmt.executeQuery(sql.toString());

		while (rs.next()) {
			listTables.add(rs.getString("tablename"));
		}
		return listTables;        
	}

	public List<String> listColumns(String tableName) throws ClassNotFoundException, SQLException  {

		
		List<String> listColumns = new LinkedList<String>();

		if(tableName != null) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT column_name FROM information_schema.columns WHERE table_name ='"+tableName+"';");

			Statement pstmt = con.createStatement();

			ResultSet rs = pstmt.executeQuery(sql.toString());

			while (rs.next()) {
				listColumns.add(rs.getString("column_name"));
			}
		}
		return listColumns;        
	}
	
	@Override
	public void exportRelationalDataToNoSQL(String cmdSql, Connector host, String table) throws SQLException {
		this.exportRelationalDataToNoSQLP(cmdSql, host, table);
//		StringBuilder sql = new StringBuilder();
//		sql.append(""+cmdSql+"");
//		Statement pstmt = con.createStatement();
//		
//		ResultSet result = pstmt.executeQuery(sql.toString());
//		while (result.next()) {
//			String key =  result.getString("?column?");
//			String value =  result.getString("value");
//			host.put(key, value);
//			System.out.println("Chave: " + key);
//		}
		
	}
	
	
public void exportRelationalDataToNoSQLP(String cmdSql, Connector host, String table) throws SQLException {
		
		StringBuilder sql = new StringBuilder();
		sql.append(""+cmdSql+"");
		Statement pstmt = con.createStatement();
		
		ResultSet result = pstmt.executeQuery("SELECT COUNT(*) as total FROM "+ table);
		int total=0;
		if (result.next())
			total = result.getInt("total");
		else {
			System.err.println("Não possui resultado para o COUNT!");
			return;
		}
		result.close();
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		long registrosCore = total/cores+1;
		
		ArrayList<Thread> threads = new ArrayList<>();
		
		for (int i=0; i< cores; i++) {
			final String query = sql+" limit "+(registrosCore) + " offset "+registrosCore*i;
			threads.add(new Thread(()->{
				try {
					this.exportRecords(query,host);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}));
		}
		
		for(Thread t : threads)
			t.start();
		
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

private void exportRecords(String query,Connector host) throws SQLException {
	Statement pstmt = con.createStatement();
	ResultSet result = pstmt.executeQuery(query);
	if (host instanceof RedisConnector) {
		RedisConnector rc = (RedisConnector) host;
		Jedis jedis = rc.pool.getResource();
		while (result.next()) {
			String key =  result.getString("?column?");
			String value =  result.getString("value");
			jedis.set(key, value);
			System.out.println("Chave: " + key);
		}
	} else {
		while (result.next()) {
			String key =  result.getString("?column?");
			String value =  result.getString("value");
			host.put(key, value);
			System.out.println("Chave: " + key);
		}
	}
	
}
	
}
	
	
