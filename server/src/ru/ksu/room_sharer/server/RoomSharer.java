package ru.ksu.room_sharer.server;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.clients.ClientsManager;
import ru.ksu.room_sharer.server.rooms.RoomsManager;
import ru.ksu.room_sharer.server.users.UsersManager;
import ru.ksu.room_sharer.server.web.misc.DeploymentConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RoomSharer
{
	private static RoomSharer instance = null;
	private static Logger logger = LoggerFactory.getLogger(RoomSharer.class);
	
	private String appRoot, root;
	
	private AppConfig appConfig;
	private UsersManager usersManager;
	private ClientsManager clientsManager;
	private RoomsManager roomsManager;
	
	public RoomSharer() throws RoomSharerException
	{
		if (RoomSharer.instance != null)
			throw new RoomSharerException("Room-Sharer instance is already created");
		RoomSharer.instance = this;
	}
	
	/* Application initialization */
	
	public void init(AppConfig appConfig, DeploymentConfig deploymentConfig) throws RoomSharerException
	{
		if (root != null)
			throw new RoomSharerException("Application has been already initialized");
		
		this.appConfig = appConfig;
		appRoot = deploymentConfig.getAppRoot();
		root = deploymentConfig.getRoot();
		System.setOut(new ConsoleOutFilter(System.out));
		PropertyConfigurator.configureAndWatch(getRootRelative(appConfig.getLogsPropertiesFile()));
		
		try
		{
			appConfig.init();
			usersManager = new UsersManager(new File(getRootRelative(appConfig.getUsersFile())));
			clientsManager = new ClientsManager(appConfig);
			clientsManager.startListening();
			
			Files.createDirectories(Paths.get(getRootRelative(appConfig.getUsersRoomsDir())));
			roomsManager = new RoomsManager(new File(getRootRelative(appConfig.getCommonRoomsFile())),
					new File(getRootRelative(appConfig.getUsersRoomsDir())));
		}
		catch (Exception e)
		{
			String errMsg = "Fatal error occurred on application initialization";
			logger.error(errMsg, e);
			throw new RoomSharerException(errMsg, e);
		}
		logger.info("Web application has been successfully initialized");
	}
	
	public void beforeShutdown()
	{
		clientsManager.stopListening();
	}
	
	
	/* Working with files and paths routines */
	
	public String getAppRoot()
	{
		return appRoot;
	}
	
	public String getAppRootRelative(String fileName)
	{
		return (!new File(fileName).isAbsolute() ? appRoot : "") + fileName;
	}
	
	public String getRoot()
	{
		return root;
	}
	
	public String getRootRelative(String fileName)
	{
		return (!new File(fileName).isAbsolute() ? root : "") + fileName;
	}
	
	
	/* Application configuration and managers */
	
	public AppConfig getAppConfig()
	{
		return appConfig;
	}
	
	public UsersManager getUsersManager()
	{
		return usersManager;
	}
	
	public ClientsManager getClientsManager()
	{
		return clientsManager;
	}
	
	public RoomsManager getRoomsManager()
	{
		return roomsManager;
	}
	
	
	public static RoomSharer getInstance()
	{
		return instance;
	}
}
