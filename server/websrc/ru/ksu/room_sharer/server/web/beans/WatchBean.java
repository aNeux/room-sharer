package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.rooms.Room;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.NavigationUtils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class WatchBean extends RoomSharerBean
{
	public static final String WATCHING_ROOM_KEY = "watching.room.obj.key";
	
	private Room watchingRoom = null;
	
	public WatchBean()
	{
	
	}
	
	public void obtainWatchingRoomObj() throws IOException
	{
		watchingRoom = (Room)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(WATCHING_ROOM_KEY);
		if (watchingRoom == null)
		{
			// Could't load watching room from session map, just redirect to common rooms page
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(externalContext.getRequestContextPath() + NavigationUtils.COMMON_ROOMS_PAGE);
		}
	}
	
	
	public Room getWatchingRoom()
	{
		return watchingRoom;
	}
}
