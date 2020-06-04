package ru.ksu.room_sharer.server.web.beans.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

public class RoomSharerBean
{
	private Logger logger;
	
	public RoomSharerBean()
	{
		initLogger();
	}
	
	protected void initLogger()
	{
		String loggerName = this.getClass().getName();
		if (UserInfoUtils.isLoggedIn())
			loggerName += "User: <" + UserInfoUtils.getUserName() + ">";
		logger = LoggerFactory.getLogger(loggerName);
	}
	
	public Logger getLogger()
	{
		return logger;
	}
}
