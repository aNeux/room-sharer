package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

public class UserBean extends RoomSharerBean
{
	public String getUserName()
	{
		return UserInfoUtils.getUserName();
	}
	
	public boolean isAdmin()
	{
		return UserInfoUtils.isAdmin();
	}
}
