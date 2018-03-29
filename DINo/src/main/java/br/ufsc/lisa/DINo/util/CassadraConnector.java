package br.ufsc.lisa.DINo.util;

import br.ufsc.lisa.DINo.views.MaindApp;

public class CassadraConnector implements Connector {

	@Override
	public boolean connect(String uri, String port, String user, String password, String DB) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return "Cassandra";
	}

	@Override
	public boolean importData(RelationalDB source, String query, MaindApp app, long block) {
		app.writeLog("Feito fera!");
		return false;
	}
	
	

}
