package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.users.User;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;
import ru.ksu.room_sharer.server.web.misc.users.UserSessionCollector;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class LoginBean extends RoomSharerBean
{
	public static final String AUTH_KEY = "user.auth_key", IS_ADMIN_KEY = "user.is_admin",
			LOGIN_PAGE = "/ui/login.jsf", ROOMS_PAGE = "/ui/restricted/rooms.jsf";
	
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
		User user = RoomSharer.getInstance().getUsersManager().isUserAllowed(userName, password);
		if (user == null)
		{
			MessageUtils.addErrorMessage("Ошибка входа", "Неверное имя пользователя или пароль");
			saveAndRedirectAfterWrongLogin();
			return;
		}
		
		UserSessionCollector.registerCurrentSession(userName);
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put(AUTH_KEY, user.getUserName());
		sessionMap.put(IS_ADMIN_KEY, user.isAdmin());
		
		initLogger();
		getLogger().info("Logged in");
		
		try
		{
			String context = getContext();
			getLogger().trace("Context: " + context);
			
			// If user tried to access resources when he wasn't authorized, use requestUrl from AuthenticationFilter to redirect user to that resource
			if (requestUrl != null)
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect(requestUrl);
				requestUrl = null;
			}
			else
				FacesContext.getCurrentInstance().getExternalContext().redirect(context + ROOMS_PAGE);
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
			String context = getContext();
			getLogger().trace("Context: {}", context);
			FacesContext.getCurrentInstance().getExternalContext().redirect(context + LOGIN_PAGE);
		}
		catch (IOException e)
		{
			getLogger().error("Error occurred while trying to redirect after log out", e);
		}
	}
	
	
	private String getContext()
	{
		String result = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		return (result == null) ? "" : (!result.startsWith("/") ? "/" : "") + result;
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
}
