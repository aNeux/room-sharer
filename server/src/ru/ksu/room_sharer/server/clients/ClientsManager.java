package ru.ksu.room_sharer.server.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.AppConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientsManager
{
	private static final Logger logger = LoggerFactory.getLogger(ClientsManager.class);
	
	private final InetAddress multicastGroup;
	private final int multicastPort;
	
	private HeartbeatsListenerThread listenerThread = null;
	private boolean searching = false;
	private Vector<Client> clients = new Vector<>();
	
	public ClientsManager(AppConfig appConfig) throws UnknownHostException
	{
		this.multicastGroup = InetAddress.getByName(appConfig.getMulticastGroupIpAddress());
		this.multicastPort = appConfig.getMulticastPort();
	}
	
	public void startListening()
	{
		searching = true;
		listenerThread = new HeartbeatsListenerThread(clients, multicastGroup, multicastPort);
		listenerThread.start();
		logger.info("Heartbeating listener started");
	}
	
	public void stopListening()
	{
		if (listenerThread != null)
			listenerThread.terminate();
		listenerThread = null;
		searching = false;
		logger.info("Heartbeating listener stopped");
	}
	
	public boolean isSearching()
	{
		return searching;
	}
	
	
	public boolean isClientOnline(Client client)
	{
		return clients.contains(client); // All found clients should be online at the current moment
	}
	
	public void refreshClientsStatuses(List<Client> clients)
	{
		for (Client client : clients)
			client.setOnline(isClientOnline(client));
	}
	
	public List<Client> getClients()
	{
		return new ArrayList<>(clients);
	}
}
