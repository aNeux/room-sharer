package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.Utils;
import ru.ksu.room_sharer.server.users.User;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;
import ru.ksu.room_sharer.server.web.misc.users.UserSessionCollector;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LoginBean extends RoomSharerBean
{
	public static final String AUTH_KEY = "user.data", ADMIN_KEY = "user.admin",
			LOGIN_PAGE = "/ui/login.jsf", COMMON_ROOMS_PAGE = "/ui/restricted/common_rooms.jsf";
	
	public static final String USER_NAME = "userName", PASSWORD = "password";
	
	private String userName, password, requestUrl = null;
	
	public void pullValuesFromFlash(ComponentSystemEvent event)
	{
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		userName = (String)flash.get(USER_NAME);
		password = (String)flash.get(PASSWORD);
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getPassword()
	{
		return ""; // Returning password could shows it at login page which isn't safe
	}
	
	public void setRequestUrl(String requestUrl)
	{
		this.requestUrl = requestUrl;
	}
	
	public String getRequestUrl()
	{
		return requestUrl;
	}
	
	
	public void login()
	{
		RoomSharer roomSharer = RoomSharer.getInstance();
		
		User user = roomSharer.getUsersManager().isUserAllowed(userName, password);
		if (user == null)
		{
			MessageUtils.addErrorMessage("Ошибка входа", "Неверное имя пользователя или пароль");
			saveAndRedirectAfterWrongLogin();
			return;
		}
		
		UserSessionCollector.registerCurrentSession(userName);
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put(AUTH_KEY, user.getUserName());
		sessionMap.put(ADMIN_KEY, user.isAdmin());
		
		initLogger();
		getLogger().info("Logged in");
		
		try
		{
			// Create file with user rooms if not exist
			File userRooms = new File(roomSharer.getRootRelative(roomSharer.getAppConfig().getUsersRoomsDir() + userName + Utils.JSON_EXT));
			if (!userRooms.exists())
				userRooms.createNewFile();
			
			// Load user rooms
			roomSharer.getRoomsManager().loadRoomsList(userName);
		}
		catch (IOException e)
		{
			getLogger().error("Couldn't load rooms available for user '{}'", userName, e);
		}
		
		try
		{
			String context = getRequestContextPath();
			getLogger().trace("Context: " + context);
			
			// If user tried to access resources not authorized, use requestUrl from AuthenticationFilter to redirect him after logging in
			if (requestUrl != null)
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect(requestUrl);
				requestUrl = null;
			}
			else
				FacesContext.getCurrentInstance().getExternalContext().redirect(context + COMMON_ROOMS_PAGE);
		}
		catch (IOException e)
		{
			getLogger().error("Couldn't redirect after successful log in", e);
		}
	}
	
	public void logout()
	{
		((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).invalidate();
		getLogger().info("Logged out");
		
		try
		{
			String context = getRequestContextPath();
			getLogger().trace("Context: {}", context);
			FacesContext.getCurrentInstance().getExternalContext().redirect(context + LOGIN_PAGE);
		}
		catch (IOException e)
		{
			getLogger().error("Error occurred while trying to redirect after log out", e);
		}
	}
	
	
	private void saveAndRedirectAfterWrongLogin()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Flash flash = facesContext.getExternalContext().getFlash();
		flash.setKeepMessages(true);
		flash.put(USER_NAME, userName);
		flash.put(PASSWORD, password);
		
		String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, facesContext.getViewRoot().getViewId());
		try
		{
			facesContext.getExternalContext().redirect(url);
		}
		catch (IOException e)
		{
			getLogger().error("Couldn't redirect to after wrong log in", url, e);
		}
	}
	
	private String getRequestContextPath()
	{
		String result = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		return (result == null) ? "" : (!result.isEmpty() && !result.startsWith("/") ? "/" : "") + result;
	}
}
