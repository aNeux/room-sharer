package ru.ksu.room_sharer.server.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.Utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatsListenerThread extends Thread
{
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatsListenerThread.class);
	
	private final Vector<Client> clients;
	private final InetAddress multicastGroup;
	private int multicastPort;
	
	private final Timer timer;
	private final AtomicBoolean stopped = new AtomicBoolean(false);
	
	public HeartbeatsListenerThread(Vector<Client> clients, InetAddress multicastGroup, int multicastPort)
	{
		super(HeartbeatsListenerThread.class.getSimpleName());
		this.clients = clients;
		this.multicastGroup = multicastGroup;
		this.multicastPort = multicastPort;
		
		// Create and start timer to remove firstly added client to the vector.
		// We are thinking that client beats all the time it's online so it will be added again further
		timer = new Timer("ClientsCleaner");
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run()
			{
				if (!clients.isEmpty())
					clients.remove(0);
			}
		}, 0, 30000);
	}
	
	@Override
	public void run()
	{
		while (!stopped.get())
		{
			MulticastSocket socket = null;
			try
			{
				socket = new MulticastSocket(multicastPort);
				socket.joinGroup(multicastGroup);
				
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				Client currentClient = new Client(packet);
				if (!clients.contains(currentClient))
					clients.add(currentClient);
				
				socket.leaveGroup(multicastGroup);
			}
			catch (Exception e)
			{
				// Don't stop searching, just write error description to log
				logger.error("Error on receiving clients statuses", e);
			}
			finally
			{
				Utils.closeResource(socket);
			}
		}
	}
	
	public void terminate()
	{
		stopped.set(true);
		timer.cancel();
	}
}
