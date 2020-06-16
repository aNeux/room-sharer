package ru.ksu.room_sharer.server.web.misc;

import javax.faces.context.FacesContext;

public class NavigationUtils
{
	private static final String UI = "/ui/", RESTRICTED = UI + "restricted/", JSF_EXT = ".jsf";
	
	public static final String LOGIN_PAGE_SHORT = "login",
			SESSION_EXPIRED_PAGE_SHORT = "session_expired",
			COMMON_ROOM_PAGE_SHORT = "common_rooms",
			USER_ROOMS_PAGE_SHORT = "user_rooms",
			WATCH_PAGE_SHORT = "watch",
			USERS_MANAGEMENT_PAGE_SHORT = "users_management",
			SETTINGS_PAGE_SHORT = "settings";
	
	public static final String LOGIN_PAGE = UI + LOGIN_PAGE_SHORT + JSF_EXT,
			SESSION_EXPIRED_PAGE = UI + SESSION_EXPIRED_PAGE_SHORT + JSF_EXT,
			COMMON_ROOMS_PAGE = RESTRICTED + COMMON_ROOM_PAGE_SHORT + JSF_EXT,
			USER_ROOMS_PAGE = RESTRICTED + USER_ROOMS_PAGE_SHORT + JSF_EXT,
			WATCH_PAGE = RESTRICTED + WATCH_PAGE_SHORT + JSF_EXT,
			USERS_MANAGEMENT_PAGE = RESTRICTED + USERS_MANAGEMENT_PAGE_SHORT + JSF_EXT,
			SETTINGS_PAGE = RESTRICTED + SETTINGS_PAGE_SHORT + JSF_EXT;
	
	
	public static String getRequestContextPath()
	{
		String result = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		return (result == null) ? "" : (!result.isEmpty() && !result.startsWith("/") ? "/" : "") + result;
	}
}
