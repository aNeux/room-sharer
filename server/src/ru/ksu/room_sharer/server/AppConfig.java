package ru.ksu.room_sharer.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig
{
	private static final String CFG_DIR = "cfg/",
			LOGS_PROPERTIES_FILE = CFG_DIR + "log.properties",
			USERS_ROOMS_DIR = CFG_DIR + "users_rooms/", COMMON_ROOMS_FILE = CFG_DIR + "common-rooms.json",
			USERS_FILE = CFG_DIR + "users.json";
	
	private static final String MULTICAST_GROUP_IP_ADDRESS = "MULTICAST_GROUP_IP_ADDRESS",
			MULTICAST_PORT = "MULTICAST_PORT";
	
	private final String configFileName;
	private String multicastGroupIdAddress;
	private int multicastPort;
	
	public AppConfig(String configFileName)
	{
		this.configFileName = configFileName;
	}
	
	public void init() throws IOException
	{
		try (FileInputStream fis = new FileInputStream(RoomSharer.getInstance().getRootRelative(getConfigFilePath())))
		{
			Properties props = new Properties();
			props.load(fis);
			multicastGroupIdAddress = props.getProperty(MULTICAST_GROUP_IP_ADDRESS,"226.186.248.17");
			multicastPort = getIntProperty(props, MULTICAST_PORT, 7562);
		}
	}
	
	public String getMulticastGroupIpAddress()
	{
		return multicastGroupIdAddress;
	}
	
	public int getMulticastPort()
	{
		return multicastPort;
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
	
	public String getUsersRoomsDir()
	{
		return USERS_ROOMS_DIR;
	}
	
	public String getCommonRoomsFile()
	{
		return COMMON_ROOMS_FILE;
	}
	
	public String getUsersFile()
	{
		return USERS_FILE;
	}
	
	
	private int getIntProperty(Properties props, String name, int defaultValue)
	{
		try
		{
			return Integer.parseInt(props.getProperty(name));
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
}
