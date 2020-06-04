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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UsersManager
{
	private static final Logger logger = LoggerFactory.getLogger(UsersManager.class);
	
	private final File usersFile;
	private volatile List<User> usersList;
	private volatile Date lastUpdateDate;
	
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
			lastUpdateDate = new Date();
		}
	}
	
	public List<User> getUsers()
	{
		return usersList.stream().map(User::new).collect(Collectors.toList());
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
	
	public Date getLastUpdateDate()
	{
		return lastUpdateDate;
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
