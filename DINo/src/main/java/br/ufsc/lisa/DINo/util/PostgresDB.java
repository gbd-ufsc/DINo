package br.ufsc.lisa.DINo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.Cursor;

public class PostgresDB implements RelationalDB {

	private String driver = "org.postgresql.Driver";
	private Connection con = null;

	public Cursor read(String table, String keyColumns) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean connect(String uri, String porta, String user, String password) {

		// String user = "postgres";
		// String senha = "minhasenha";
		// String url = "jdbc:postgresql://localhost:5432/Databases";
		try {
			Class.forName(driver);
			con = (Connection) DriverManager.getConnection(uri, user, password);

			return true;
		} catch (ClassNotFoundException ex) {
			System.err.print(ex.getMessage());
			return false;
		} catch (SQLException e) {
			System.err.print(e.getMessage());
			return false;
		}		
	}
	
	public List<?> listarDatabases() throws ClassNotFoundException, SQLException  {

        List listaDatabase = new ArrayList<String>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("datname as nm_database ");
        sql.append("FROM ");
        sql.append("pg_database ");
        sql.append("WHERE ");
        sql.append("datistemplate = false ");
        sql.append("AND ");
        sql.append("datname != 'postgres' ");
        sql.append("ORDER BY nm_database");

        PreparedStatement pstmt = con.prepareStatement(sql.toString());
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            listaDatabase.add(rs.getString("nm_database"));
        }
        return listaDatabase;
        
    }
}
