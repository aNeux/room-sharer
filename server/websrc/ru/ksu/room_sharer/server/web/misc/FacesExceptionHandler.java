package ru.ksu.room_sharer.server.web.misc;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;
import java.util.Map;

public class FacesExceptionHandler extends ExceptionHandlerWrapper
{
	private ExceptionHandler wrapped;
	
	public FacesExceptionHandler(ExceptionHandler wrapped)
	{
		this.wrapped = wrapped;
	}
	
	@Override
	public ExceptionHandler getWrapped()
	{
		return wrapped;
	}
	
	@Override
	public void handle() throws FacesException
	{
		Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();
		while (it.hasNext())
		{
			ExceptionQueuedEvent event = it.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext)event.getSource();
			Throwable throwable = context.getException();
			if (throwable == null)
				continue;
			
			do
			{
				if (!(throwable instanceof ViewExpiredException))
					throwable = throwable.getCause();
				else
				{
					ViewExpiredException vee = (ViewExpiredException)throwable;
					FacesContext facesContext = FacesContext.getCurrentInstance();
					Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
					NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
					try
					{
						requestMap.put("currentViewId", vee.getViewId());
						navigationHandler.handleNavigation(facesContext, null, "/ui/session_expired.jsf");
						facesContext.renderResponse();
					}
					finally
					{
						it.remove();
					}
					break;
				}
			}
			while (throwable != null);
		}
		
		getWrapped().handle();
	}
}

