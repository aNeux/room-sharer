package ru.ksu.room_sharer.server.clients;

public class Client
{
	private final String name, address;
	private final int port;
	
	public Client(String name, String address, int port)
	{
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	public Client(String clientDesc) throws IllegalArgumentException
	{
		String[] splitted = clientDesc.split(";");
		if (splitted.length < 3)
			throw new IllegalArgumentException("Client description '" + clientDesc + "' couldn't be resolved");
		
		name = splitted[0];
		address = splitted[1];
		port = Integer.parseInt(splitted[2]);
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port;
	}
}
