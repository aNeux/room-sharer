package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;

import javax.faces.context.FacesContext;

import static ru.ksu.room_sharer.server.web.misc.NavigationUtils.*;

public class GuiBean extends RoomSharerBean
{
	public String getCommonRoomsPagePath()
	{
		return getRequestContextPath() + COMMON_ROOMS_PAGE;
	}
	
	public String getUserRoomsPagePath()
	{
		return getRequestContextPath() + USER_ROOMS_PAGE;
	}
	
	public String getUsersManagementPagePath()
	{
		return getRequestContextPath() + USERS_MANAGEMENT_PAGE;
	}
	
	public String getSettingsPagePath()
	{
		return getRequestContextPath() + SETTINGS_PAGE;
	}
	
	public String getWatchPagePath()
	{
		return getRequestContextPath() + WATCH_PAGE;
	}
	
	
	public boolean isCommonRoomsPage()
	{
		return getPageName().equals(COMMON_ROOM_PAGE_SHORT);
	}
	
	public boolean isUserRoomsPage()
	{
		return getPageName().equals(USER_ROOMS_PAGE_SHORT);
	}
	
	public boolean isUsersManagementPage()
	{
		return getPageName().equals(USERS_MANAGEMENT_PAGE_SHORT);
	}
	
	public boolean isSettingsPage()
	{
		return getPageName().equals(SETTINGS_PAGE_SHORT);
	}
	
	public boolean isWatchPage()
	{
		return getPageName().equals(WATCH_PAGE_SHORT);
	}
	
	private String getPageName()
	{
		String pageName = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		return pageName.substring(pageName.lastIndexOf('/') + 1, pageName.lastIndexOf('.'));
	}
}
