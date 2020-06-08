package ru.ksu.room_sharer.server.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class UsersManager
{
	private static final Logger logger = LoggerFactory.getLogger(UsersManager.class);
	
	private final File usersFile;
	private volatile List<User> usersList;
	
	public UsersManager(File usersFile) throws IOException
	{
		this.usersFile = usersFile;
		loadUsersList();
	}
	
	public void loadUsersList() throws IOException
	{
		synchronized (UsersManager.class)
		{
			usersList = new ObjectMapper().readValue(usersFile, new TypeReference<List<User>>(){ });
		}
	}
	
	public void saveUsersList() throws IOException
	{
		synchronized (UsersManager.class)
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(usersFile, usersList);
		}
	}
	
	
	public List<User> getUsers()
	{
		// Return new list (just a copy of original one)
		return usersList.stream().map(User::new).collect(Collectors.toList());
	}
	
	public void changePassword(String userName, String newPassword) throws IOException, UserException
	{
		synchronized (UsersManager.class)
		{
			loadUsersList();
			// Find user to change password
			User user = usersList.stream().filter(u -> u.getUserName().equals(userName)).findFirst().orElse(null);
			if (user != null)
				user.setPassword(newPassword);
			else
				throw new UserException("User '" + userName + "' doesn't exists");
			saveUsersList();
			loadUsersList(); // Reload users list as file has been changed
		}
	}
	
	public void saveUser(User editedUser) throws IOException
	{
		synchronized (UsersManager.class)
		{
			loadUsersList();
			User user = usersList.stream().filter(u -> u.getUserName().equals(editedUser.getUserName())).findFirst().orElse(null);
			if (user == null) // New user has been created
				usersList.add(editedUser);
			else
			{
				// Update all properties of edited user
				user.setUserName(editedUser.getUserName());
				user.setPassword(editedUser.getPassword());
				user.setFirstName(editedUser.getFirstName());
				user.setLastName(editedUser.getLastName());
				user.setDepartment(editedUser.getDepartment());
				user.setAdmin(editedUser.isAdmin());
			}
			saveUsersList();
			loadUsersList(); // Reload users list as file has been changed
		}
	}
	
	public void removeUsers(List<String> usersNamesToRemove) throws IOException
	{
		synchronized (UsersManager.class)
		{
			loadUsersList();
			for (Iterator<User> iter = usersList.iterator(); iter.hasNext(); )
			{
				User probableUser = iter.next();
				if (usersNamesToRemove.contains(probableUser.getUserName()))
				{
					probableUser.closeSessions();
					iter.remove();
					logger.info("User '{}' has been removed", probableUser.getUserName());
				}
			}
			saveUsersList();
			loadUsersList(); // Reload users list as file has been changed
		}
	}
	
	public void closeUsersSessions(List<String> usersNamesToCloseSessions) throws IOException
	{
		synchronized (UsersManager.class)
		{
			loadUsersList();
			for (User user : usersList)
			{
				if (usersNamesToCloseSessions.contains(user.getUserName()))
				{
					user.closeSessions();
					logger.info("Sessions for user '{}' have been closed", user.getUserName());
				}
			}
		}
	}
	
	public User isUserAllowed(String login, String password)
	{
		if (CollectionUtils.isEmpty(usersList))
		{
			logger.error("No one user is defined in '{}'", usersFile.getAbsoluteFile());
			return null;
		}
		
		for (User user : usersList)
		{
			if (user.getUserName().equals(login) && checkPassword(password, user.getPassword()))
				return user;
		}
		return null;
	}
	
	
	private boolean checkPassword(String input, String expected)
	{
		try
		{
			if (expected != null)
				return expected.equals(Utils.md5(input));
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			logger.error("Password check failed on MD5 encryption", e);
		}
		return false;
	}
}
