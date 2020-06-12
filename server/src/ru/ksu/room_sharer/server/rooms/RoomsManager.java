package ru.ksu.room_sharer.server.rooms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.Utils;
import ru.ksu.room_sharer.server.clients.Client;
import ru.ksu.room_sharer.server.clients.ClientsManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsManager
{
	private final File commonRoomsFile, usersRoomsDir;
	
	private volatile List<Room> commonRooms;
	private volatile Map<String, List<Room>> usersRooms = new HashMap<>();
	
	private Object commonRoomsMonitor = new Object(), usersRoomsMonitor = new Object();
	
	public RoomsManager(File commonRoomsFile, File usersRoomsDir) throws IOException
	{
		this.commonRoomsFile = commonRoomsFile;
		this.usersRoomsDir = usersRoomsDir;
		loadRoomsList(null); // Load only common rooms on application start
	}
	
	public void loadRoomsList(String userName) throws IOException
	{
		boolean isCommon = userName == null;
		File file = isCommon ? commonRoomsFile : new File(usersRoomsDir, userName + Utils.JSON_EXT);
		
		synchronized (isCommon ? commonRoomsMonitor : usersRoomsMonitor)
		{
			// File could be empty so just create new list
			List<Room> rooms = file.length() == 0 ? new ArrayList<>()
					: new ObjectMapper().readValue(file, new TypeReference<List<Room>>(){ });
			if (isCommon)
				commonRooms = rooms;
			else
				usersRooms.put(userName, rooms);
		}
	}
	
	public void saveRoomsList(String userName) throws IOException
	{
		boolean isCommon = userName == null;
		File file = isCommon ? commonRoomsFile : new File(usersRoomsDir, userName + Utils.JSON_EXT);
		
		synchronized (isCommon ? commonRoomsMonitor : usersRoomsMonitor)
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			// If user hasn't rooms yet, just save empty file
			mapper.writeValue(file, isCommon ? commonRooms : usersRooms.computeIfAbsent(userName, k -> new ArrayList<>()));
		}
	}
	
	
	public List<Room> getRooms(String userName)
	{
		ClientsManager clientsManager = RoomSharer.getInstance().getClientsManager();
		List<Room> from = userName == null ? commonRooms : usersRooms.computeIfAbsent(userName, k -> new ArrayList<>());
		
		List<Room> result = new ArrayList<>();
		for (Room room : from)
		{
			for (Client client : room.getClients()) // Need to check online status for each client
				client.setOnline(clientsManager.isClientOnline(client));
			result.add(room);
		}
		return result;
	}
	
	public void saveRoom(String userName, Room editedRoom, String editedRoomName) throws IOException
	{
		synchronized (userName == null ? commonRoomsMonitor : usersRoomsMonitor)
		{
			loadRoomsList(userName);
			List<Room> from = userName == null ? commonRooms : usersRooms.computeIfAbsent(userName, k -> new ArrayList<>());
			
			Room room = from.stream().filter(r -> r.getName().equals(editedRoom.getName())).findFirst().orElse(null);
			if (room == null)
			{
				// New room has been added
				editedRoom.setName(editedRoomName);
				from.add(editedRoom);
			}
			else
			{
				room.setName(editedRoomName);
				room.setClients(editedRoom.getClients());
			}
			saveRoomsList(userName);
			loadRoomsList(userName); // Reload file as changes have been applied
		}
	}
	
	public void removeRoom(String userName, String roomName) throws IOException
	{
		synchronized (userName == null ? commonRoomsMonitor : usersRoomsMonitor)
		{
			loadRoomsList(userName);
			List<Room> from = userName == null ? commonRooms : usersRooms.computeIfAbsent(userName, k -> new ArrayList<>());
			
			from.removeIf(r -> r.getName().equals(roomName));
			saveRoomsList(userName);
			loadRoomsList(userName); // Reload file as changes have been applied
		}
	}
}
