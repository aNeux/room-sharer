package ru.ksu.room_sharer.client;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.client.misc.Configuration;
import ru.ksu.room_sharer.client.misc.LanNetworkInfo;
import ru.ksu.room_sharer.client.services.HeartBeating;

import java.io.File;

public class Starter
{
	private final static Logger logger = LoggerFactory.getLogger(Starter.class);
	private final static int HEARTBEAT_INTERVAL_DEFAULT = 3000;
	
	public static void main(String[] args) throws Exception
	{
		// Set logger configuration file
		PropertyConfigurator.configureAndWatch("log.properties");
		
		// Load configuration and local network information
		Configuration config = new Configuration(new File("config.cfg"));
		LanNetworkInfo lanNetworkInfo = new LanNetworkInfo();
		
		// Start heart beating
		logger.info("Starting hartbeat thread with interval = {} ms", config.getHeartbeatInterval());
		new HeartBeating(lanNetworkInfo, config).start();
	}
}
