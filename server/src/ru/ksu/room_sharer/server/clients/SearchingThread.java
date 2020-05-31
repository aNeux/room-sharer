package ru.ksu.room_sharer.server.clients;

import ru.ksu.room_sharer.client.misc.Utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchingThread extends Thread
{
	private final Vector<String> clientsDesc;
	private final AtomicBoolean stopped = new AtomicBoolean(false);
	
	public SearchingThread(Vector<String> clientsDesc)
	{
		this.clientsDesc = clientsDesc;
	}
	
	@Override
	public void run()
	{
		while (!stopped.get())
		{
			MulticastSocket socket = null;
			try
			{
				socket = new MulticastSocket(Constants.MULTICAST_PORT);
				InetAddress group = InetAddress.getByName(Constants.MULTICAST_GROUP_ADDRESS);
				socket.joinGroup(group);
				
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				String clientDesc = new String(packet.getData(), 0, packet.getLength());
				if (!clientsDesc.contains(clientDesc))
					clientsDesc.add(clientDesc);
				
				socket.leaveGroup(group);
			}
			catch (Exception e)
			{
				// Don't stop searching, just write error description to log
				e.printStackTrace(); // FIXME: replace with logger
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
	}
}
