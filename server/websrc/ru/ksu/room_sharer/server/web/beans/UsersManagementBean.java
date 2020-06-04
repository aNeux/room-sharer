package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.users.User;
import ru.ksu.room_sharer.server.users.UsersManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;

import java.util.Date;
import java.util.List;

public class UsersManagementBean extends RoomSharerBean
{
	private final UsersManager usersManager;
	
	private List<User> users;
	private Date lastUpdateDate = null;
	
	public UsersManagementBean()
	{
		this.usersManager = RoomSharer.getInstance().getUsersManager();
	}
	
	public List<User> getUsers()
	{
		// Check if need to update users list first
		if (lastUpdateDate == null || usersManager.getLastUpdateDate().after(lastUpdateDate))
		{
			users = usersManager.getUsers();
			lastUpdateDate = new Date();
		}
		return users;
	}
}
