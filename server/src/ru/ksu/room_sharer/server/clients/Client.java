package ru.ksu.room_sharer.server.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Objects;

public class Client implements Serializable
{
	private static final long serialVersionUID = 3134915980113599282L;
	
	private String hostName, pseudoName, address;
	private int streamingPort;
	
	@JsonIgnore
	private boolean online = false;
	
	public Client() { }
	
	public Client(String hostName, String pseudoName, String address, int streamingPort)
	{
		this.hostName = hostName;
		this.pseudoName = pseudoName;
		this.address = address;
		this.streamingPort = streamingPort;
	}
	
	public Client(String clientDesc) throws IllegalArgumentException
	{
		String[] splitted = clientDesc.split(";");
		if (splitted.length < 3)
			throw new IllegalArgumentException("Client description '" + clientDesc + "' couldn't be resolved");
		
		hostName = splitted[0];
		address = splitted[1];
		streamingPort = Integer.parseInt(splitted[2]);
	}
	
	public Client(DatagramPacket packet) throws IllegalArgumentException
	{
		this(new String(packet.getData(), 0, packet.getLength()));
	}
	
	
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setPseudoName(String pseudoName)
	{
		this.pseudoName = pseudoName != null ? pseudoName.trim() : "";
	}
	
	public String getPseudoName()
	{
		return pseudoName;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setStreamingPort(int streamingPort)
	{
		this.streamingPort = streamingPort;
	}
	
	public int getStreamingPort()
	{
		return streamingPort;
	}
	
	public void setOnline(boolean online)
	{
		this.online = online;
	}
	
	public boolean isOnline()
	{
		return online;
	}
	
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Client client = (Client)o;
		return streamingPort == client.streamingPort && hostName.equals(client.hostName) && address.equals(client.address);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(hostName, address, streamingPort);
	}
	
	@Override
	public String toString()
	{
		return String.join(";", hostName, address, String.valueOf(streamingPort));
	}
}
