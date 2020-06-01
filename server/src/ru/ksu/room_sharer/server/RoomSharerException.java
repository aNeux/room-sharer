package ru.ksu.room_sharer.server;

public class RoomSharerException extends Exception
{
	private static final long serialVersionUID = 3191607689002203432L;
	
	public RoomSharerException() { }
	
	public RoomSharerException(String message)
	{
		super(message);
	}
	
	public RoomSharerException(Throwable cause)
	{
		super(cause);
	}
	
	public RoomSharerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
