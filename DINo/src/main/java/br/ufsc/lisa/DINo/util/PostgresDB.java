package br.ufsc.lisa.DINo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.mongodb.Cursor;

public class PostgresDB implements RelationalDB {

	private String driver = "org.postgresql.Driver";
	private Connection con = null;
	private String url;
	private String port;
	private String user;
	private String password; 

	public Cursor read(String table, String keyColumns) {
		
		return null;
	}

	public boolean connect(String host, String porta, String user, String password) {
		this.url = "jdbc:postgresql://"+host+":"+porta+"/poi_uruguay";
		 
//		 uri = "jdbc:postgresql://localhost:5432/poi_uruguay";
		try {
			Class.forName(driver);
			con = (Connection) DriverManager.getConnection(url, user, password);
			System.out.println("Conexão realizada com sucesso.");

			return true;
		} catch (ClassNotFoundException ex) {
			System.err.print(ex.getMessage());
			return false;
		} catch (SQLException e) {
			System.err.print(e.getMessage());
			return false;
		}		
	}
	
	public DefaultListModel<String> listarDatabases() throws ClassNotFoundException, SQLException  {

        DefaultListModel<String> listaDatabase = new DefaultListModel();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM pg_catalog.pg_tables WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'pg_toast') ORDER BY schemaname, tablename;");
        		
        PreparedStatement pstmt = con.prepareStatement(sql.toString());
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            listaDatabase.addElement(rs.getString("Banco poi_uruguay "));
        }
        return listaDatabase;
        
    }
}