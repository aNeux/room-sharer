package ru.ksu.room_sharer.server.web.misc;

import net.bootsfaces.utils.FacesMessages;

public class MessageUtils
{
	public static void addInfoMessage(final String summary, final String details)
	{
		FacesMessages.info(createSummary(summary), details);
	}
	
	public static void addWarningMessage(final String summary, final String details)
	{
		FacesMessages.warning(createSummary(summary), details);
	}
	
	public static void addErrorMessage(final String summary, final String details)
	{
		FacesMessages.error(createSummary(summary), details);
	}
	
	
	private static String createSummary(String summary)
	{
		return "<strong>" + summary + "</strong>";
	}
}
