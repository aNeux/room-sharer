package ru.ksu.room_sharer.server.web.beans.init;

import ru.ksu.room_sharer.server.AppConfig;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.RoomSharerException;
import ru.ksu.room_sharer.server.web.misc.DeploymentConfig;

public class RoomSharerApplicationBean extends RoomSharerBean
{
	private static RoomSharerApplicationBean appInstance = null;
	private String appContextPath;
	
	public RoomSharerApplicationBean() throws RoomSharerException
	{
		synchronized (RoomSharerApplicationBean.class)
		{
			if (appInstance != null)
				throw new RoomSharerException("Room-Sharer application bean is already created");
			RoomSharerApplicationBean.appInstance = this;
		}
		
		System.out.println("Starting Room-Sharer web application..");
		AppConfig appConfig = new AppConfig("room-sharer.cfg");
		DeploymentConfig deploymentConfig = new DeploymentConfig(appConfig);
		appContextPath = deploymentConfig.getAppContextPath();
		new RoomSharer().init(appConfig, deploymentConfig);
	}
	
	
	public static RoomSharerApplicationBean getAppInstance()
	{
		return appInstance;
	}
	
	public String getAppContextPath()
	{
		return appContextPath;
	}
}
