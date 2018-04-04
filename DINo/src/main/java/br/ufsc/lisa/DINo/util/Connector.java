package br.ufsc.lisa.DINo.util;

import br.ufsc.lisa.DINo.views.MaindApp;

public interface Connector {
	public boolean connect(String uri, String port, String user, String password, String DB);
	public boolean importData(RelationalDB source, String query, MaindApp app, long block);
	public boolean put(String key, String json);
	public void close();
	public void dropObject(String name);
}
