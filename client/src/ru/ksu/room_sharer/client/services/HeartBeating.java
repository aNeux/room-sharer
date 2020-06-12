package ru.ksu.room_sharer.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.client.misc.Configuration;
import ru.ksu.room_sharer.client.misc.LanNetworkInfo;
import ru.ksu.room_sharer.client.misc.Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartBeating extends Thread
{
	private static final Logger logger = LoggerFactory.getLogger(HeartBeating.class);
	
	private final LanNetworkInfo lanNetworkInfo;
	private final Configuration config;
	
	private AtomicBoolean stopped = new AtomicBoolean(false);
	
	public HeartBeating(LanNetworkInfo lanNetworkInfo, Configuration config)
	{
		super(HeartBeating.class.getSimpleName());
		this.lanNetworkInfo = lanNetworkInfo;
		this.config = config;
	}
	
	@Override
	public void run()
	{
		// Build message to broadcast
		String messageString = String.join(";", lanNetworkInfo.getHostName(),
				lanNetworkInfo.getLanIpAddress(), String.valueOf(config.getMulticastPort()));
		logger.info("Message to broadcast: {}", messageString);
		byte[] message = messageString.getBytes();
		
		while (!stopped.get())
		{
			DatagramSocket socket = null;
			try
			{
				InetAddress group = InetAddress.getByName(config.getMulticastGroupIpAddress());
				socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(message, message.length, group, config.getMulticastPort());
				socket.send(packet);
				
				// Wait specified interval before next beat
				Thread.sleep(config.getHeartbeatInterval());
			}
			catch (Exception e)
			{
				// Don't stop trying beating, just write error description to log
				logger.error("Exception occurred while trying to make next heartbeat", e);
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
	
	public boolean isStopped()
	{
		return stopped.get();
	}
}
