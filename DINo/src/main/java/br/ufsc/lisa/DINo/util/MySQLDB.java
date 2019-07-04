package br.ufsc.lisa.DINo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MySQLDB implements RelationalDB {

	private String driver = "org.postgresql.Driver";
	private Connection con = null;
	private String url;
	
	public Connection getConnection() {
		return this.con;
	}
	
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

		List<String> listDatabase = new LinkedList<>();

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
		
}
	
	
