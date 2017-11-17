package br.ufsc.lisa.DINo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.mongodb.Cursor;
import com.mongodb.util.JSON;

public class PostgresDB implements RelationalDB {

	private String driver = "org.postgresql.Driver";
	private Connection con = null;
	private String url;
	private String port;
	private String user;
	private String password; 
	private RedisConnector redisDb;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Cursor read(String table, String keyColumns) {

		return null;
	}

	public boolean connect(String host, String porta, String user, String password, String dbName) {
		this.url = "jdbc:postgresql://"+host+":"+porta+"/"+ dbName;
		
		//		 uri = "jdbc:postgresql://localhost:5432/poi_uruguay";
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

	public DefaultListModel<String> listDatabases() throws ClassNotFoundException, SQLException  {

		DefaultListModel<String> listDatabase = new DefaultListModel();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT datname from pg_database where datistemplate = false");

		Statement pstmt = con.createStatement();

		ResultSet rs = pstmt.executeQuery(sql.toString());

		while (rs.next()) {
			listDatabase.addElement(rs.getString("datname"));
		}
		con.close();
		con=null;
		return listDatabase;        
	}

	public DefaultListModel<String> listTables() throws ClassNotFoundException, SQLException  {

		DefaultListModel<String> listTables = new DefaultListModel();

		StringBuilder sql = new StringBuilder();
		sql.append("select tablename FROM pg_catalog.pg_tables WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'pg_toast') ORDER BY tablename;");

		Statement pstmt = con.createStatement();

		ResultSet rs = pstmt.executeQuery(sql.toString());

		while (rs.next()) {
			listTables.addElement(rs.getString("tablename"));
		}
		return listTables;        
	}

	public DefaultListModel<String> listColumns(String tableName) throws ClassNotFoundException, SQLException  {

		
		DefaultListModel<String> listColumns = new DefaultListModel();

		if(tableName != null) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT column_name FROM information_schema.columns WHERE table_name ='"+tableName+"';");

			Statement pstmt = con.createStatement();

			ResultSet rs = pstmt.executeQuery(sql.toString());

			while (rs.next()) {
				listColumns.addElement(rs.getString("column_name"));
			}
		}
		return listColumns;        
	}
	
	public void getDataFromPSQL(String cmdSql) throws SQLException {
		redisDb = new RedisConnector();
		redisDb.connect("localhost");

		StringBuilder sql = new StringBuilder();
		sql.append(""+cmdSql+"");
		Statement pstmt = con.createStatement();
		
		ResultSet result = pstmt.executeQuery(sql.toString());
		
		while (result.next()) {
			String key =  result.getString("?column?");
			String value =  result.getString("value");
			redisDb.set(key, value);
			System.out.println(key);
		}
	}
	
}
	
	
