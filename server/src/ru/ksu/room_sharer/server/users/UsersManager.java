package ru.ksu.room_sharer.server.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UsersManager
{
	private static final Logger logger = LoggerFactory.getLogger(UsersManager.class);
	
	private final File usersFile;
	private volatile List<User> usersList;
	private final Object usersListMonitor = new Object(); // Required as usersList is reset on reload
	
	public UsersManager(File usersFile) throws IOException
	{
		this.usersFile = usersFile;
		loadUsersList();
	}
	
	public void loadUsersList() throws IOException
	{
		synchronized (usersListMonitor)
		{
			usersList = new ObjectMapper().readValue(usersFile, new TypeReference<List<User>>(){ });
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
