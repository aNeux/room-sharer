package ru.ksu.room_sharer.server.web.misc;

import ru.ksu.room_sharer.server.RoomSharer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ShutDownHookListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce) { }
	
	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		System.out.println("Context has been destroyed!");
		RoomSharer.getInstance().beforeShutdown();
	}
}
