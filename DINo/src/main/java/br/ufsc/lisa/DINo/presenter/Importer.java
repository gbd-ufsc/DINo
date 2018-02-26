package br.ufsc.lisa.DINo.presenter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.ufsc.lisa.DINo.util.Connector;
import br.ufsc.lisa.DINo.util.PostgresDB;
import br.ufsc.lisa.DINo.util.RedisConnector;
import br.ufsc.lisa.DINo.util.RelationalDB;
import br.ufsc.lisa.DINo.util.TimeFormatter;
import br.ufsc.lisa.DINo.views.MaindApp;

public class Importer{
	
	private RelationalDB rDB;
	private Connector nRDB; 
	private MaindApp app;
	public int progress;
	private long timeB, timeE;
	private ArrayList<Thread> threads = new ArrayList<>();
	
	public void importData(String sql,String table, MaindApp app) throws SQLException {
		Statement pstmt = ((PostgresDB) nRDB).getConnection().createStatement();
		
		ResultSet result = pstmt.executeQuery("SELECT COUNT(*) as total FROM "+ table);
		int total=0;
		if (result.next())
			total = result.getInt("total");
		else {
			System.err.println("Não possui resultado para o COUNT!");
			return;
		}
		result.close();
		
//		int cores = Runtime.getRuntime().availableProcessors()>1? Runtime.getRuntime().availableProcessors()-1: 1;
		int cores = 2;
		app.writeLog("[info] Criadas "+cores+" Threads de importação");
		
		long registrosCore = total/cores+1;
		
		ArrayList<Thread> threads = new ArrayList<>();
		
		for (int i=0; i< cores; i++) {
			final String query = sql+" limit "+(registrosCore) + " offset "+registrosCore*i;
			threads.add(new Thread(()->{
				try {
					this.exportRecords(query,nRDB);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}));
		}
		
		timeB =  System.currentTimeMillis();
		
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
//		Statement pstmt = con.createStatement();
//		ResultSet result = pstmt.executeQuery(query);
		if (host instanceof RedisConnector) {
			RedisConnector rc = (RedisConnector) host;
			((RedisConnector) host).importData(rDB, query);
		}
		
	}
	
	private void watcher() {
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		app.writeLog("[info] Importação concluida");
		app.writeLog("[info] Tempo de importação: "+ TimeFormatter.formatTime(System.currentTimeMillis()-timeB));
	}
}
