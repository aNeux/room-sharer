package ru.ksu.room_sharer.server.web.beans;

import org.apache.commons.lang3.StringUtils;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.misc.Utils;
import ru.ksu.room_sharer.server.users.User;
import ru.ksu.room_sharer.server.users.UserException;
import ru.ksu.room_sharer.server.users.UsersManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class UserBean extends RoomSharerBean
{
	private final UsersManager usersManager;
	private User user;
	
	private String oldPassword, newPassword, newPasswordRepeated;
	private boolean changePasswordDialogCanClose;
	
	public UserBean()
	{
		usersManager = RoomSharer.getInstance().getUsersManager();
		refreshUserObject();
		preChangePassword();
	}
	
	public void preChangePassword()
	{
		oldPassword = newPassword = newPasswordRepeated = "";
		changePasswordDialogCanClose = false;
	}
	
	public void setOldPassword(String oldPassword)
	{
		this.oldPassword = oldPassword;
	}
	
	public String getOldPassword()
	{
		return oldPassword;
	}
	
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}
	
	public String getNewPassword()
	{
		return newPassword;
	}
	
	public void setNewPasswordRepeated(String newPasswordRepeated)
	{
		this.newPasswordRepeated = newPasswordRepeated;
	}
	
	public String getNewPasswordRepeated()
	{
		return newPasswordRepeated;
	}
	
	public boolean isChangePasswordDialogCanClose()
	{
		return changePasswordDialogCanClose;
	}
	
	public void changePassword()
	{
		try
		{
			if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword) || StringUtils.isBlank(newPasswordRepeated))
			{
				MessageUtils.addErrorMessage("Ошибка ввода", "Необходимо заполнить все поля");
				return;
			}
			
			refreshUserObject();
			String oldPasswordHash = Utils.md5(oldPassword), newPasswordHash = Utils.md5(newPassword);
			if (!user.getPassword().equals(oldPasswordHash))
			{
				MessageUtils.addErrorMessage("Неверные данные", "Старый пароль указан неверно");
				return;
			}
			else if (!newPassword.equals(newPasswordRepeated))
			{
				MessageUtils.addErrorMessage("Неверные данные", "Новый пароль повторен неправильно");
				return;
			}
			else if (newPasswordHash.equals(user.getPassword()))
			{
				MessageUtils.addErrorMessage("Неверные данные", "Новый пароль совпадает с предыдущим");
				return;
			}
			
			usersManager.changePassword(user.getUserName(), newPasswordHash);
			refreshUserObject();
			changePasswordDialogCanClose = true;
			MessageUtils.addInfoMessage("Ваш пароль был успешно изменен");
		}
		catch (NoSuchAlgorithmException | IOException | UserException e)
		{
			MessageUtils.addErrorMessage("Ошибка", "Не удалось изменить пароль");
			getLogger().error("Couldn't change password", e);
		}
	}
	
	
	public String getUserName()
	{
		return UserInfoUtils.getUserName();
	}
	
	public boolean isAdmin()
	{
		return UserInfoUtils.isAdmin();
	}
	
	
	private void refreshUserObject()
	{
		user = usersManager.getUsers().stream().filter(u -> u.getUserName().equals(getUserName())).findFirst().orElse(null);
	}
}
