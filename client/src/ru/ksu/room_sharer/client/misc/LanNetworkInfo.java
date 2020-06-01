package ru.ksu.room_sharer.client.misc;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LanNetworkInfo
{
	private final String lanIpAddress, hostName;
	
	public LanNetworkInfo() throws SocketException, UnknownHostException
	{
		// Obtain lan address for the client
		try (DatagramSocket socket = new DatagramSocket())
		{
			socket.connect(InetAddress.getByName("8.8.8.8"), 1002);
			lanIpAddress = socket.getLocalAddress().getHostAddress();
		}
		// Obtain host name
		hostName = InetAddress.getLocalHost().getHostName();
	}
	
	public String getLanIpAddress()
	{
		return lanIpAddress;
	}
	
	public String getHostName()
	{
		return hostName;
	}
}
