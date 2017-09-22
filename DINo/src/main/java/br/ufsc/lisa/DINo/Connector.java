package br.ufsc.lisa.DINo;

public interface Connector {
	public void connect (String uri);
	public boolean put(String key, String json);
}
