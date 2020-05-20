package ru.ksu.room_sharer.server.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MainBean
{
	private String someText;
	
	public void setSomeText(String someText)
	{
		this.someText = someText;
	}
	
	public String getSomeText()
	{
		return someText;
	}
	
	public void showMessage()
	{
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Your text", someText);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
