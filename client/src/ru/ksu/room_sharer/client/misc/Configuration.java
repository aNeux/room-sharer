package ru.ksu.room_sharer.client.misc;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration
{
	private final static String MULTICAST_GROUP_IP_ADDRESS = "MULTICAST_GROUP_IP_ADDRESS",
			MULTICAST_PORT = "MULTICAST_PORT",
			HEARTBEAT_INTERVAL = "HEARTBEAT_INTERVAL",
			STREAMING_PORT = "STREAMING_PORT",
			COMPRESSION_QUALITY = "COMPRESSION_QUALITY";
	
	private String multicastGroupIpAddress;
	private int multicastPort, heartbeatInterval, streamingPort;
	private float compressionQuality;
	
	public Configuration(File configurationFile) throws IOException
	{
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(configurationFile))
		{
			props.load(fis);
			multicastGroupIpAddress = props.getProperty(MULTICAST_GROUP_IP_ADDRESS, "226.186.248.17");
			multicastPort = getIntProperty(props, MULTICAST_PORT, 7562);
			heartbeatInterval = getIntProperty(props, HEARTBEAT_INTERVAL, 3000);
			streamingPort = getIntProperty(props, STREAMING_PORT, 5482);
			compressionQuality = getFloatProperty(props, COMPRESSION_QUALITY, 0.7f);
		}
	}
	
	public String getMulticastGroupIpAddress()
	{
		return multicastGroupIpAddress;
	}
	
	public int getMulticastPort()
	{
		return multicastPort;
	}
	
	public int getHeartbeatInterval()
	{
		return heartbeatInterval;
	}
	
	public int getStreamingPort()
	{
		return streamingPort;
	}
	
	public float getCompressionQuality()
	{
		return compressionQuality;
	}
	
	
	private int getIntProperty(Properties props, String name, int defaultValue)
	{
		String res = props.getProperty(name);
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : defaultValue;
	}
	
	private float getFloatProperty(Properties props, String name, float defaultValue)
	{
		try
		{
			float readValue = Float.parseFloat(props.getProperty(name));
			return readValue >= 0 && readValue <= 1 ? readValue : defaultValue;
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
}
