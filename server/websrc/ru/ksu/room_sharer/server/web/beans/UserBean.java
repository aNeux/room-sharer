package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

public class UserBean extends RoomSharerBean
{
	public boolean isAdmin()
	{
		return UserInfoUtils.isAdmin();
	}
}
