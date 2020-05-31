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
	
	public int getWorkingPort()
	{
		String res = props.getProperty("WORKING_PORT");
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : 7562;
	}
	
	public int getHeartbeatInterval()
	{
		String res = props.getProperty("HEARTBEAT_INTERVAL");
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : 30000;
	}
}
