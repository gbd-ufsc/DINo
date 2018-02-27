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
	
	
	
	public Importer(RelationalDB rDB, Connector nRDB) {
		super();
		this.rDB = rDB;
		this.nRDB = nRDB;
	}

	public void importData(String sql,String table, MaindApp app) throws SQLException {
		Statement pstmt = ((PostgresDB) rDB).getConnection().createStatement();
		this.app = app;
		
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
					this.exportRecords(query,nRDB,registrosCore);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}));
		}
		
		Thread w = new Thread(()->{
				this.watcher(threads);
		});
		
		timeB =  System.currentTimeMillis();
		
		for(Thread t : threads)
			t.start();
		w.start();
		
	}

	private void exportRecords(String query,Connector host, long block) throws SQLException {
		if (host instanceof RedisConnector) {
			RedisConnector rc = (RedisConnector) host;
			((RedisConnector) host).importData(rDB, query, this.app, block);
		}
		
	}
	
	private void watcher(ArrayList<Thread> threads) {
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				app.writeLog("[erro] Problema com as Threads de importação, os dados devem estar inconsistentes");
			}
		app.writeLog("[info] Importação concluida");
 		app.writeLog("[info] Tempo de importação: "+ TimeFormatter.formatTime(System.currentTimeMillis()-timeB));
		app.updateProgressBar(-1.);
		app.importBloqued(false);
	}
}
