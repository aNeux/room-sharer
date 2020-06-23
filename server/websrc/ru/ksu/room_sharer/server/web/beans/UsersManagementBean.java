package ru.ksu.room_sharer.server.web.beans;

import org.apache.commons.lang3.StringUtils;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.misc.Utils;
import ru.ksu.room_sharer.server.users.User;
import ru.ksu.room_sharer.server.users.UsersManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UsersManagementBean extends RoomSharerBean
{
	private final UsersManager usersManager;
	
	private List<User> users;
	private Set<Integer> selectedIndexes = new HashSet<>();
	
	private User editableUser;
	private String editableUserPasswordText;
	private boolean creatingNewUser, notChangePassword, userDialogCanClose;
	
	public UsersManagementBean()
	{
		this.usersManager = RoomSharer.getInstance().getUsersManager();
		refreshUsersList();
		preNewUser();
	}
	
	private void refreshUsersList()
	{
		users = usersManager.getUsers();
		selectedIndexes.clear();
	}
	
	public void forceLoadingUsersFromFile()
	{
		try
		{
			usersManager.loadUsersList();
			refreshUsersList();
		}
		catch (IOException e)
		{
			getLogger().error("Error on loading users file", e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось загрузить список пользователей из файла");
		}
	}
	
	public List<User> getUsers()
	{
		return users;
	}
	
	
	public void onSelectRows(String indexes)
	{
		selectedIndexes.addAll(parseIndexesString(indexes));
	}
	
	public void onDeselectRows(String indexes)
	{
		selectedIndexes.removeAll(parseIndexesString(indexes));
	}
	
	public String getSelectedIndexesString()
	{
		return selectedIndexes.isEmpty() ? "-1" : selectedIndexes.stream().map(Objects::toString).collect(Collectors.joining(","));
	}
	
	public boolean isSomeSelectedUsersOnline()
	{
		return selectedIndexes.stream().anyMatch(id -> users.get(id).isOnline());
	}
	
	
	public void preNewUser()
	{
		editableUser = new User();
		editableUserPasswordText = "";
		creatingNewUser = true;
		notChangePassword = false;
		userDialogCanClose = false;
	}
	
	public void preEditUser()
	{
		editableUser = new User(users.get(selectedIndexes.iterator().next())); // Edit a copy, not an original object!
		editableUserPasswordText = "";
		creatingNewUser = false;
		notChangePassword = true;
		userDialogCanClose = false;
	}
	
	public User getEditableUser()
	{
		return editableUser;
	}
	
	public void setEditableUserPasswordText(String editableUserPasswordText)
	{
		this.editableUserPasswordText = editableUserPasswordText;
	}
	
	public String getEditableUserPasswordText()
	{
		return editableUserPasswordText;
	}
	
	public boolean isCreatingNewUser()
	{
		return creatingNewUser;
	}
	
	public void setNotChangePassword(boolean notChangePassword)
	{
		this.notChangePassword = notChangePassword;
	}
	
	public boolean isNotChangePassword()
	{
		return notChangePassword;
	}
	
	public boolean isUserDialogCanClose()
	{
		return userDialogCanClose;
	}
	
	public void saveEditableUser()
	{
		if (StringUtils.isBlank(editableUser.getUserName()) // Check if required username and password specified at all
				|| (creatingNewUser || !notChangePassword) && StringUtils.isBlank(editableUserPasswordText))
		{
			MessageUtils.addErrorMessage("Неверные данные", "Имя пользователя и пароль не могут быть пустыми");
			return;
		}
		else if (creatingNewUser)
		{
			// Here we should check if username of newly created user is already exists
			refreshUsersList();
			if (users.stream().anyMatch(u -> u.getUserName().equals(editableUser.getUserName())))
			{
				MessageUtils.addErrorMessage("Неверные данные", "Пользователь с таким именем уже существует");
				return;
			}
		}
		
		try
		{
			if (creatingNewUser || !notChangePassword) // Hash password by MD5 if needed
				editableUser.setPassword(Utils.md5(editableUserPasswordText));
			usersManager.saveUser(editableUser);
			refreshUsersList();
			userDialogCanClose = true;
			MessageUtils.addInfoMessage(creatingNewUser ? "Новый пользователь был успешно создан" : "Изменения были успешно сохранены");
		}
		catch (NoSuchAlgorithmException | IOException e)
		{
			getLogger().error("Couldn't save new user or edit selected one", e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось " + (creatingNewUser ? "создать нового пользователя" : "сохранить внесенные изменения"));
		}
	}
	
	public void removeSelectedUsers()
	{
		try
		{
			usersManager.removeUsers(selectedIndexes.stream().map(ind -> users.get(ind)).map(User::getUserName).collect(Collectors.toList()));
			refreshUsersList();
			MessageUtils.addInfoMessage("Учетные данные выбранных пользователей были успешно удалены");
		}
		catch (IOException e)
		{
			getLogger().error("Error occurred while removing users and saving changes in the file", e);
			MessageUtils.addErrorMessage("Произошла ошибка при удалении пользователей", e.getMessage());
		}
	}
	
	public void closeSelectedUsersSessions()
	{
		try
		{
			usersManager.closeUsersSessions(selectedIndexes.stream().map(ind -> users.get(ind)).map(User::getUserName).collect(Collectors.toList()));
			refreshUsersList();
			MessageUtils.addInfoMessage("Активные сессии у выбранных пользователей были успешно завершены");
		}
		catch (IOException e)
		{
			getLogger().error("Error on closing selected users sessions", e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось закрыть у сессии выбранных пользователей");
		}
	}
}
