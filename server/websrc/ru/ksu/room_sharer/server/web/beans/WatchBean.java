package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.clients.Client;
import ru.ksu.room_sharer.server.clients.ClientsManager;
import ru.ksu.room_sharer.server.misc.Utils;
import ru.ksu.room_sharer.server.rooms.Room;
import ru.ksu.room_sharer.server.streaming.StreamingClientsManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;
import ru.ksu.room_sharer.server.web.misc.MessageUtils;
import ru.ksu.room_sharer.server.web.misc.NavigationUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WatchBean extends RoomSharerBean
{
	public static final String WATCHING_ROOM_KEY = "watching.room.obj.key";
	private static int DEFAULT_POLL_INTERVAL = 5;
	
	private Room watchingRoom = null;
	private final ClientsManager clientsManager;
	private final StreamingClientsManager streamingClientsManager;
	
	private final Set<Client> listeningClients = new HashSet<>();
	private int pollRefreshInterval = DEFAULT_POLL_INTERVAL;
	
	public WatchBean()
	{
		RoomSharer app = RoomSharer.getInstance();
		clientsManager = app.getClientsManager();
		streamingClientsManager = app.getStreamingClientsManager();
	}
	
	@PostConstruct
	public void obtainWatchingRoomObj()
	{
		watchingRoom = (Room)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(WATCHING_ROOM_KEY);
		if (watchingRoom == null)
		{
			// Could't load watching room from session map, just try to redirect to common rooms page
			try
			{
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(externalContext.getRequestContextPath() + NavigationUtils.COMMON_ROOMS_PAGE);
			}
			catch (IOException e)
			{
				getLogger().error("Couldn't redirect to common rooms page due to not found room object to watch", e);
			}
		}
	}
	
	
	public Room getWatchingRoom()
	{
		return watchingRoom;
	}
	
	public void refreshClients()
	{
		clientsManager.refreshClientsStatuses(watchingRoom.getClients());
	}
	
	public int getOnlineClientsCount()
	{
		return (int)watchingRoom.getClients().stream().filter(Client::isOnline).count();
	}
	
	public int getCountOfCurrentlyWatching()
	{
		return listeningClients.size();
	}
	
	public int getPollRefreshInterval()
	{
		return pollRefreshInterval;
	}
	
	public String getClientFullName(Client client)
	{
		return Utils.getClientFullName(client);
	}
	
	public String getClientId(Client client)
	{
		// Return IP-address modified some way to apply HTML element identifier
		return client.getAddress().replaceAll("\\.", "");
	}
	
	
	public void startWatching(Client client)
	{
		String clientName = getClientFullName(client);
		try
		{
			streamingClientsManager.connect(client);
			listeningClients.add(client);
			pollRefreshInterval = 1;
			MessageUtils.addInfoMessage("Соединение с '" + clientName + "' установлено");
		}
		catch (InterruptedException e)
		{
			MessageUtils.addErrorMessage("Не удалось подключиться к '" + clientName + "'");
		}
	}
	
	public boolean isWatchingForClient(Client client)
	{
		return listeningClients.contains(client);
	}
	
	public void stopWatching(Client client)
	{
		streamingClientsManager.disconnect(client);
		listeningClients.remove(client);
		if (listeningClients.size() == 0)
			pollRefreshInterval = DEFAULT_POLL_INTERVAL; // Reset refresh interval to default value
		MessageUtils.addInfoMessage("Соединение с '" + getClientFullName(client) + "' было успешно закрыто");
	}
}
