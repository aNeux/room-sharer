package ru.ksu.room_sharer.server;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig
{
	private static final String CFG_DIR = "cfg/",
			LOGS_PROPERTIES_FILE = CFG_DIR + "log.properties",
			ROOMS_DIR = "rooms/", COMMON_ROOMS_FILE = CFG_DIR + "common-rooms.json",
			USERS_FILE = CFG_DIR + "users.json";
	
	private final String configFileName;
	private Properties props;
	
	public AppConfig(String configFileName)
	{
		this.configFileName = configFileName;
	}
	
	public void init() throws IOException
	{
		props = new Properties();
		try (FileInputStream fis = new FileInputStream(RoomSharer.getInstance().getRootRelative(getConfigFilePath())))
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
		String res = props.getProperty("MULTICAST_PORT");
		return StringUtils.isNotBlank(res) ? Integer.parseInt(res) : 7562;
	}
	
	
	public String getCfgDir()
	{
		return CFG_DIR;
	}
	
	public String getConfigFilePath()
	{
		return CFG_DIR + configFileName;
	}
	
	public String getLogsPropertiesFile()
	{
		return LOGS_PROPERTIES_FILE;
	}
	
	public String getRoomsDir()
	{
		return ROOMS_DIR;
	}
	
	public String getCommonRoomsFile()
	{
		return COMMON_ROOMS_FILE;
	}
	
	public String getUsersFile()
	{
		return USERS_FILE;
	}
}
