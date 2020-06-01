package ru.ksu.room_sharer.server.web.misc.users;

import ru.ksu.room_sharer.server.web.beans.LoginBean;

import javax.faces.context.FacesContext;
import java.util.Map;

public class UserInfoUtils
{
	public static Map<String, Object> getSessionMap()
	{
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	}
	
	public static boolean isLoggedIn()
	{
		return getUserName() != null;
	}
	
	public static String getUserName()
	{
		return (String)getSessionMap().get(LoginBean.AUTH_KEY);
	}
	
	public static boolean isAdmin()
	{
		return (boolean)getSessionMap().get(LoginBean.IS_ADMIN_KEY);
	}
}
