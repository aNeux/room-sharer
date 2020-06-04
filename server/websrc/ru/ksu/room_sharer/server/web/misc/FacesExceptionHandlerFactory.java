package ru.ksu.room_sharer.server.web.misc;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class FacesExceptionHandlerFactory extends ExceptionHandlerFactory
{
	private ExceptionHandlerFactory parent;
	
	public FacesExceptionHandlerFactory(ExceptionHandlerFactory parent)
	{
		this.parent = parent;
	}
	
	@Override
	public ExceptionHandler getExceptionHandler()
	{
		return new FacesExceptionHandler(parent.getExceptionHandler());
	}
}
