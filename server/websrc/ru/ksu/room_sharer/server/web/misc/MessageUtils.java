package ru.ksu.room_sharer.server.web.misc;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtils
{
	public static void addMessage(FacesMessage.Severity severity, final String summary, final String details)
	{
		FacesMessage msg = new FacesMessage(severity, summary, details);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public static void addErrorMessage(final String summary, final String details)
	{
		addMessage(FacesMessage.SEVERITY_ERROR, summary, details);
	}
	
	public static void addInfoMessage(final String summary, final String details)
	{
		addMessage(FacesMessage.SEVERITY_INFO, summary, details);
	}
	
	public static void addWarningMessage(final String summary, final String details)
	{
		addMessage(FacesMessage.SEVERITY_WARN, summary, details);
	}
}
