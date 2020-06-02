package ru.ksu.room_sharer.server.web.beans;

import ru.ksu.room_sharer.server.web.beans.init.RoomSharerBean;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class GuiBean extends RoomSharerBean
{
	public String getPageName()
	{
		String pageName = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		return pageName.substring(pageName.lastIndexOf('/') + 1, pageName.lastIndexOf('.'));
	}
}
