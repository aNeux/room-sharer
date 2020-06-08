package ru.ksu.room_sharer.server.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ksu.room_sharer.server.web.misc.users.UserSessionCollector;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class User implements Serializable
{
	private static final long serialVersionUID = 4036767858855957808L;
	
	private String userName, password, firstName, lastName, department;
	private boolean admin = false;
	
	public User() { }
	
	public User(User user)
	{
		this.userName = user.getUserName();
		this.password = user.getPassword();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.department = user.getDepartment();
		this.admin = user.isAdmin();
	}
	
	public User(String userName, String password, String firstName, String lastName, String department, boolean admin)
	{
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.department = department;
		this.admin = admin;
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
		return password;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setDepartment(String department)
	{
		this.department = department;
	}
	
	public String getDepartment()
	{
		return department;
	}
	
	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}
	
	public boolean isAdmin()
	{
		return admin;
	}
	
	@JsonIgnore
	public boolean isOnline()
	{
		return UserSessionCollector.getSessionsByLogin(getUserName()).size() != 0;
	}
	
	@JsonIgnore
	public int getSessionsCount()
	{
		return UserSessionCollector.getSessionsByLogin(getUserName()).size();
	}
	
	@JsonIgnore
	public void closeSessions()
	{
		UserSessionCollector.getSessionsByLogin(getUserName()).forEach(HttpSession::invalidate);
	}
}
