package ru.ksu.room_sharer.server.web.beans;

public class LoginBean extends ApplicationBean
{
	public static final String AUTH_KEY = "user.auth_key", IS_ADMIN_KEY = "user.is_admin",
			LOGIN_PAGE = "/ui/login.jsf", ROOMS_PAGE = "/ui/rooms.jsf";
	
	private String userName, password;
	
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
	
	
	public void login()
	{
	
	}
	
	public void logout()
	{
	
	}
}
