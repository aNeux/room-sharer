package ru.ksu.room_sharer.client.misc;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration
{
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
		return props.getProperty("MULTICAST_GROUP_IP_ADDRESS","226.186.248.17");
	}
	
	public int getMulticastPort()
	{
		return getIntProperty("MULTICAST_PORT", 7562);
	}
	
	public int getHeartbeatInterval()
	{
		return getIntProperty("HEARTBEAT_INTERVAL", 3000);
	}
	
	public int getStreamingPort()
	{
		return getIntProperty("STREAMING_PORT", 7563);
	}
	
	
	private int getIntProperty(String name, int defaultValue)
	{
		String res = props.getProperty(name);
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : defaultValue;
	}
}
