package ru.ksu.room_sharer.server;

public class RoomSharer
{
	private static RoomSharer instance = null;
	
	public RoomSharer() throws Exception
	{
		if (RoomSharer.instance != null)
			throw new Exception("RoomSharer instance already created");
		
		RoomSharer.instance = this;
	}
	
	public static RoomSharer getInstance()
	{
		return instance;
	}
	
	
	public void beforeShutdown()
	{
	
	}
}
