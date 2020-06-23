package ru.ksu.room_sharer.server.streaming;

import ru.ksu.room_sharer.server.clients.Client;
import ru.ksu.room_sharer.server.misc.Pair;

import java.util.HashMap;
import java.util.Map;

public class StreamingClientsManager
{
	private volatile Map<Client, Pair<StreamingClient, Integer>> streamingClients = new HashMap<>();
	
	public synchronized void connect(Client client) throws InterruptedException
	{
		disconnectIfIdle(client);
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		
		if (streamingClientPair == null)
		{
			StreamingClient streamingClient = new StreamingClient(client.getAddress(), client.getStreamingPort());
			streamingClient.start();
			streamingClients.put(client, new Pair<>(streamingClient, 1));
		}
		else
			streamingClientPair.setSecond(streamingClientPair.getSecond() + 1);
	}
	
	public byte[] getCurrentScreenshot(Client client)
	{
		disconnectIfIdle(client);
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		return streamingClientPair == null ? null
				: streamingClientPair.getFirst().getScreenshotBytesStorage().getCurrentScreenshot();
	}
	
	public synchronized void disconnect(Client client)
	{
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		if (streamingClientPair != null)
		{
			int modifiedListenersCount = streamingClientPair.getSecond() - 1;
			if (modifiedListenersCount <= 0)
			{
				// No one active listener. Stop channel and remove from it from the map
				streamingClientPair.getFirst().stop();
				streamingClients.remove(client);
			}
			else
				streamingClientPair.setSecond(modifiedListenersCount);
		}
	}
	
	public void disconnectAll()
	{
		for (Pair<StreamingClient, Integer> streamingClient : streamingClients.values())
			streamingClient.getFirst().stop();
		
		streamingClients.clear();
		streamingClients = null;
	}
	
	
	private void disconnectIfIdle(Client client)
	{
		Pair<StreamingClient, Integer> streamingClientPair = streamingClients.get(client);
		// Check if client has been stopped but not removed from streaming clients map due to some reason
		if (streamingClientPair != null && !streamingClientPair.getFirst().isRunning())
			disconnect(client);
	}
}
