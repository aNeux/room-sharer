package ru.ksu.room_sharer.client;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.client.misc.Configuration;
import ru.ksu.room_sharer.client.misc.LanNetworkInfo;
import ru.ksu.room_sharer.client.services.HeartBeating;
import ru.ksu.room_sharer.client.services.streaming.StreamingServer;

import java.io.File;

public class Starter
{
	private final static Logger logger = LoggerFactory.getLogger(Starter.class);
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Starting Room-Sharer client application..");
		
		// Set logger configuration file
		PropertyConfigurator.configureAndWatch("log.properties");
		
		// Load configuration and local network information
		Configuration config = new Configuration(new File("room-sharer.cfg"));
		LanNetworkInfo lanNetworkInfo = new LanNetworkInfo();
		
		// Start heart beating
		new HeartBeating(lanNetworkInfo, config).start();
		
		// Start streaming server
		new StreamingServer(config).start();
	}
}
