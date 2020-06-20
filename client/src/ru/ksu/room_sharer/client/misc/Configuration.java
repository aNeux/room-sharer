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
	
	private final Properties props;
	
	public Configuration(File configurationFile) throws IOException
	{
		props = new Properties();
		try (FileInputStream fis = new FileInputStream(configurationFile))
		{
			props.load(fis);
		}
	}
	
	public String getMulticastGroupIpAddress()
	{
		return props.getProperty(MULTICAST_GROUP_IP_ADDRESS,"226.186.248.17");
	}
	
	public int getMulticastPort()
	{
		return getIntProperty(MULTICAST_PORT, 7562);
	}
	
	public int getHeartbeatInterval()
	{
		return getIntProperty(HEARTBEAT_INTERVAL, 3000);
	}
	
	public int getStreamingPort()
	{
		return getIntProperty(STREAMING_PORT, 5482);
	}
	
	public float getCompressionQuality()
	{
		float defaultValue = 0.5f;
		try
		{
			float readValue = Float.parseFloat(props.getProperty(COMPRESSION_QUALITY));
			if (readValue >= 0 && readValue <= 1)
				return readValue;
		}
		catch (Exception e)
		{
			// Will use default compression quality
		}
		return defaultValue;
	}
	
	
	private int getIntProperty(String name, int defaultValue)
	{
		String res = props.getProperty(name);
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : defaultValue;
	}
}
