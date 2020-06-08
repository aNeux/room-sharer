package ru.ksu.room_sharer.server.web.misc;

import net.bootsfaces.utils.FacesMessages;

public class MessageUtils
{
	public static void addInfoMessage(String summary, String details)
	{
		FacesMessages.info(createSummary(summary), details);
	}
	
	public static void addInfoMessage(String details)
	{
		FacesMessages.info(details);
	}
	
	public static void addWarningMessage(String summary, String details)
	{
		FacesMessages.warning(createSummary(summary), details);
	}
	
	public static void addWarningMessage(String details)
	{
		FacesMessages.warning(details);
	}
	
	public static void addErrorMessage(String summary, String details)
	{
		FacesMessages.error(createSummary(summary), details);
	}
	
	public static void addErrorMessage(String details)
	{
		FacesMessages.error(details);
	}
	
	
	private static String createSummary(String summary)
	{
		return "<strong>" + summary + "</strong>";
	}
}
