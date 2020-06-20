package ru.ksu.room_sharer.server.rooms;

import ru.ksu.room_sharer.server.clients.Client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable, Cloneable
{
	private static final long serialVersionUID = -2648149105113125215L;
	
	private String name;
	private List<Client> clients;
	
	public Room()
	{
		clients = new ArrayList<>();
	}
	
	public Room(String name, List<Client> clients)
	{
		this.name = name;
		this.clients = clients;
	}
	
	public Room(Room roomToClone)
	{
		name = roomToClone.getName();
		clients = new ArrayList<>(roomToClone.getClients());
	}
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setClients(List<Client> clients)
	{
		this.clients = clients;
	}
	
	public List<Client> getClients()
	{
		return clients;
	}
}
