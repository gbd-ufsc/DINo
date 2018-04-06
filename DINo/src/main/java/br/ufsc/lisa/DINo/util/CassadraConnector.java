package br.ufsc.lisa.DINo.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
				cluster = Cluster.builder() // (1)
						.addContactPoint(uri).withPort(Integer.valueOf(port)).withCredentials(user, password).build();
			else
				cluster = Cluster.builder() // (1)
						.addContactPoint(uri).withPort(Integer.valueOf(port)).build();

			Session session = cluster.connect();

			session.execute("CREATE KEYSPACE IF NOT EXISTS " + DB + " WITH replication = {"
					+ " 'class': 'SimpleStrategy', " + " 'replication_factor': '3' " + "};");
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
		List<String> columns = new LinkedList<String>();
		String plainColumns = "";
		String plainValues = "";

		for (String pk : app.exportedPks()) {
			plainColumns += (pk + ", ");
			plainValues += (":" + pk + ", ");
			columns.add(pk);
		}

		for (int i = 0; i < app.exportedValues().size() - 1; i++) {
			String col = app.exportedValues().get(i);
			plainColumns += (col + ", ");
			plainValues += (":" + col + ", ");
			columns.add(col);
		}

		String last = app.exportedValues().get(app.exportedValues().size() - 1);
		plainColumns += (last);
		plainValues += (":" + last);
		columns.add(last);

		String insert = "INSERT INTO " + app.exportedTableName() + "(" + plainColumns + ") VALUES (" + plainValues
				+ " )";

		double progress = (double) 64 / block;
		try {
			pstmt = ((PostgresDB) source).getConnection().createStatement();
			ResultSet result = pstmt.executeQuery(query);
			int i = 0, l = 0;

			PreparedStatement statment;
			try {
				statment = session.get().prepare(insert);
				BatchStatement bath = new BatchStatement();

				while (result.next()) {
					BoundStatement bound = new BoundStatement(statment).bind();
					for (int k = 0; k < columns.size(); k++) {
						bound.setString(k, result.getString(columns.get(k)));
					}

					System.out.println(bound.toString());
					bath.add(bound);

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
		session.execute("DROP TABLE IF EXISTS " + name);
		session.close();

	}

	@Override
	public void createStructure(String name, MaindApp app) {
		Session session = cluster.connect(this.dbName);

		String ddl = "CREATE TABLE IF NOT EXISTS " + app.exportedTableName() + " ( ";

		for (String pk : app.exportedPks()) {
			ddl = ddl.concat(pk + " TEXT PRIMARY KEY, ");
		}

		for (int i = 0; i < app.exportedValues().size() - 1; i++) {
			String col = app.exportedValues().get(i);
			ddl = ddl.concat(col + " TEXT , ");
		}

		String last = app.exportedValues().get(app.exportedValues().size() - 1);
		ddl += (last + " TEXT ); ");

		session.execute(ddl);
		session.close();
	}

}
