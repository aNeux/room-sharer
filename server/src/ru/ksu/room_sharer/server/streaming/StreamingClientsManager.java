package ru.ksu.room_sharer.server.streaming;

import javafx.util.Pair;
import ru.ksu.room_sharer.server.clients.Client;

import java.util.HashMap;
import java.util.Map;

public class StreamingClientsManager
{
	private Map<Client, Pair<StreamingClient, Integer>> streamingClients = new HashMap<>();
	
	public synchronized void connect(Client client) throws InterruptedException
	{
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		StreamingClient streamingClient;
		int listenersCount;
		
		if (streamingClientPair == null || !streamingClientPair.getKey().isRunning())
		{
			streamingClient = new StreamingClient(client.getAddress(), client.getStreamingPort());
			streamingClient.start();
			listenersCount = 1;
		}
		else
		{
			streamingClient = streamingClientPair.getKey();
			listenersCount = streamingClientPair.getValue() + 1;
		}
		streamingClients.put(client, new Pair<>(streamingClient, listenersCount));
	}
	
	public byte[] getCurrentScreenshot(Client client)
	{
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		return streamingClientPair == null ? null
				: streamingClientPair.getKey().getScreenshotBytesStorage().getCurrentScreenshot();
	}
	
	public synchronized void disconnect(Client client)
	{
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		if (streamingClientPair != null)
		{
			StreamingClient streamingClient = streamingClientPair.getKey();
			int listenersCount = streamingClientPair.getValue() - 1;
			if (listenersCount <= 0)
			{
				// No one active listener. Stop channel and remove from it from the map
				streamingClient.stop();
				streamingClients.remove(client);
			}
			else
				streamingClients.put(client, new Pair<>(streamingClient, listenersCount));
		}
	}
	
	public void disconnectAll()
	{
		for (Pair<StreamingClient, Integer> streamingClient : streamingClients.values())
			streamingClient.getKey().stop();
		
		streamingClients.clear();
		streamingClients = null;
	}
}
