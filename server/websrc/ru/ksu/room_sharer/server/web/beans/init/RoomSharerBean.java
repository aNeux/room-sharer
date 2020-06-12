package ru.ksu.room_sharer.server.web.beans.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

import java.util.HashSet;
import java.util.Set;

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
			loggerName += " <" + UserInfoUtils.getUserName() + ">";
		logger = LoggerFactory.getLogger(loggerName);
	}
	
	public Logger getLogger()
	{
		return logger;
	}
	
	
	protected Set<Integer> parseIndexesString(String indexes)
	{
		Set<Integer> result = new HashSet<>();
		if (indexes.length() == 1)
			result.add(Integer.parseInt(indexes));
		else
		{
			String[] splitted = indexes.substring(1, indexes.length() - 1).split(",");
			for (String id : splitted)
				result.add(Integer.parseInt(id));
		}
		return result;
	}
}
