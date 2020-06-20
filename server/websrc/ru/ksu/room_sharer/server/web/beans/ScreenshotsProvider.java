package ru.ksu.room_sharer.server.web.beans;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import ru.ksu.room_sharer.server.RoomSharer;
import ru.ksu.room_sharer.server.clients.Client;
import ru.ksu.room_sharer.server.streaming.StreamingClientsManager;
import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScreenshotsProvider extends RoomSharerBean
{
	private final StreamingClientsManager streamingClientsManager;
	
	private byte[] placeholderImageBytes;
	
	public ScreenshotsProvider()
	{
		RoomSharer app = RoomSharer.getInstance();
		streamingClientsManager = app.getStreamingClientsManager();
		
		File placeholderImageFile = new File(app.getAppRootRelative("../images/placeholder.png"));
		try
		{
			placeholderImageBytes = Files.readAllBytes(placeholderImageFile.toPath());
		}
		catch (IOException e)
		{
			getLogger().error("Couldn't load placeholder image '{}'", placeholderImageFile.getAbsoluteFile(), e);
		}
	}
	
	public StreamedContent getCurrentScreenshot()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE)
			return new DefaultStreamedContent(); // It is a phase to render HTML. No need to send really image here
		else
		{
			String clientDesc = facesContext.getExternalContext().getRequestParameterMap().get("clientDesc");
			byte[] imageToShow = streamingClientsManager.getCurrentScreenshot(new Client(clientDesc));
			if (imageToShow == null)
				imageToShow = placeholderImageBytes;
			return imageToShow != null ? new DefaultStreamedContent(new ByteArrayInputStream(imageToShow)) : new DefaultStreamedContent();
		}
	}
}
