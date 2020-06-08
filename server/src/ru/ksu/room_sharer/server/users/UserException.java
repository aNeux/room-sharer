package ru.ksu.room_sharer.server.users;

public class UserException extends Exception
{
	private static final long serialVersionUID = 3191607689002203432L;
	
	public UserException() { }
	
	public UserException(String message)
	{
		super(message);
	}
	
	public UserException(Throwable cause)
	{
		super(cause);
	}
	
	public UserException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
