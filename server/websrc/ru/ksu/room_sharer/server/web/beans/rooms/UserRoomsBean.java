package ru.ksu.room_sharer.server.web.beans.rooms;

import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

public class UserRoomsBean extends RoomsBean
{
	@Override
	protected String roomsAvailableFor()
	{
		return UserInfoUtils.getUserName();
	}
}
