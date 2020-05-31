package ru.ksu.room_sharer.server.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientsListener
{
	private SearchingThread thread = null;
	private boolean searching = false;
	private Vector<String> clientsDescs = new Vector<>();
	
	public void startSearching()
	{
		clientsDescs.clear();
		searching = true;
		thread = new SearchingThread(clientsDescs);
		thread.start();
	}
	
	public void stopSearching()
	{
		searching = false;
		if (thread != null)
			thread.terminate();
	}
	
	public boolean isSearching()
	{
		return searching;
	}
	
	public List<Client> getClients()
	{
		List<Client> result = new ArrayList<>();
		clientsDescs.forEach(desc -> result.add(new Client(desc)));
		return result;
	}
}
