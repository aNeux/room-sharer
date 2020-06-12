package ru.ksu.room_sharer.server.web.beans.rooms;

import org.apache.commons.lang3.StringUtils;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.clients.Client;
import ru.ksu.room_sharer.server.clients.ClientsManager;
import ru.ksu.room_sharer.server.rooms.Room;
import ru.ksu.room_sharer.server.rooms.RoomsManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RoomsBean extends RoomSharerBean
{
	protected final ClientsManager clientsManager;
	protected final RoomsManager roomsManager;
	
	protected List<Room> rooms;
	protected int onlineClientsCount, totalClientsCount;
	
	protected Room editableRoom;
	protected String editableRoomName;
	protected Set<Integer> clientsSelectedIndexes = new HashSet<>();
	protected boolean creatingNewRoom, editRoomDialogCanClose;
	
	public RoomsBean()
	{
		clientsManager = RoomSharer.getInstance().getClientsManager();
		roomsManager = RoomSharer.getInstance().getRoomsManager();
		refreshRoomsList();
		preNewRoom();
	}
	
	/* Rooms list loading and statistics */
	
	protected void refreshRoomsList()
	{
		rooms = roomsManager.getRooms(roomsAvailableFor());
		
		// Rooms could has similar clients so we should count only unique ones
		Set<Client> onlineClients = new HashSet<>(), allClients = new HashSet<>();
		for (Room room : rooms)
		{
			onlineClients.addAll(room.getClients().stream().filter(Client::isOnline).collect(Collectors.toSet()));
			allClients.addAll(room.getClients());
		}
		onlineClientsCount = onlineClients.size();
		totalClientsCount = allClients.size();
	}
	
	public void forceLoadingRoomsFromFile()
	{
		try
		{
			roomsManager.loadRoomsList(roomsAvailableFor());
			refreshRoomsList();
		}
		catch (IOException e)
		{
			getLogger().error("Error on loading rooms", e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось загрузить список классов из файла");
		}
	}
	
	public List<Room> getRooms()
	{
		return rooms;
	}
	
	public int getOnlineClientsCount()
	{
		return onlineClientsCount;
	}
	
	public int getTotalClientsCount()
	{
		return totalClientsCount;
	}
	
	public int getOnlineClientsCount(Room room)
	{
		return (int)room.getClients().stream().filter(Client::isOnline).count();
	}
	
	
	/* Adding and editing room dialog */
	
	public void preNewRoom()
	{
		editableRoom = new Room();
		editableRoom.setClients(clientsManager.getClients()); // Add all online clients to display in new room dialog
		editableRoomName = "";
		clientsSelectedIndexes.clear();
		creatingNewRoom = true;
		editRoomDialogCanClose = false;
	}
	
	public void preEditRoom(Room editableRoom)
	{
		this.editableRoom = editableRoom;
		// Add not existing online clients to the end of already added list
		this.editableRoom.getClients().addAll(clientsManager.getClients().stream()
				.filter(c -> !this.editableRoom.getClients().contains(c)).collect(Collectors.toList()));
		editableRoomName = this.editableRoom.getName();
		
		// Auto select already added clients on top
		clientsSelectedIndexes.clear();
		for (int i = 0; i < editableRoom.getClients().size(); i++)
			clientsSelectedIndexes.add(i);
		
		creatingNewRoom = false;
		editRoomDialogCanClose = false;
	}
	
	public Room getEditableRoom()
	{
		return editableRoom;
	}
	
	public void setEditableRoomName(String editableRoomName)
	{
		this.editableRoomName = editableRoomName;
	}
	
	public String getEditableRoomName()
	{
		return editableRoomName;
	}
	
	public void onSelectClients(String indexes)
	{
		clientsSelectedIndexes.addAll(parseIndexesString(indexes));
	}
	
	public void onDeselectClients(String indexes)
	{
		clientsSelectedIndexes.removeAll(parseIndexesString(indexes));
	}
	
	public String getSelectedClientsIndexesString()
	{
		return clientsSelectedIndexes.isEmpty() ? "-1" : clientsSelectedIndexes.stream().map(Objects::toString).collect(Collectors.joining(","));
	}
	
	public boolean isCreatingNewRoom()
	{
		return creatingNewRoom;
	}
	
	public boolean isEditRoomDialogCanClose()
	{
		return editRoomDialogCanClose;
	}
	
	public void saveEditableRoom()
	{
		refreshRoomsList(); // We should be able to check if new room name is unique
		if (StringUtils.isBlank(editableRoomName))
		{
			MessageUtils.addErrorMessage("Неверные данные", "Имя класса не может быть пустым");
			return;
		}
		else if (rooms.stream().map(Room::getName).anyMatch(n -> n.equals(editableRoomName) && (creatingNewRoom || !n.equals(editableRoom.getName()))))
		{
			MessageUtils.addErrorMessage("Неверные данные", "Класс с таким именем уже существует");
			return;
		}
		else if (clientsSelectedIndexes.isEmpty())
		{
			MessageUtils.addErrorMessage("Неверные данные", "Класс должен содержать хотя бы один компьютер");
			return;
		}
		
		try
		{
			roomsManager.saveRoom(roomsAvailableFor(), editableRoom, editableRoomName);
			refreshRoomsList();
			editRoomDialogCanClose = true;
			MessageUtils.addInfoMessage("Класс был успешно создан");
		}
		catch (IOException e)
		{
			getLogger().error("Error occurred while adding new room", e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось создать новый класс");
		}
	}
	
	
	/* Remove selected room */
	
	public void preRemoveSelectedRoom(Room removingRoom)
	{
		this.editableRoom = removingRoom;
	}
	
	public void removeSelectedRoom()
	{
		String roomName = editableRoom.getName();
		try
		{
			roomsManager.removeRoom(roomsAvailableFor(), roomName);
			refreshRoomsList();
			MessageUtils.addInfoMessage("Класс '" + roomName + "' был успешно удален");
		}
		catch (IOException e)
		{
			getLogger().error("Error occurred on removing room '{}'", roomName, e);
			MessageUtils.addErrorMessage("Ошибка", "Не удалось удалить класс '" + roomName + "'");
		}
	}
	
	
	/** Return {@code null} if using common rooms or user name if need to load rooms available for current user. */
	protected abstract String roomsAvailableFor();
}
