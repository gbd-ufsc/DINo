package br.ufsc.lisa.DINo.util;

public interface Connector {
	public boolean connect(String uri, String port, String user, String password, String DB);
	public boolean put(String key, String json);
	public void close();
}
