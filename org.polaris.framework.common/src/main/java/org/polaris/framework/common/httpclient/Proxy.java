package org.polaris.framework.common.httpclient;

public class Proxy
{
	private String ip;
	private int port;

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public String toString()
	{
		return "Proxy " + ip + ":" + port;
	}

}
