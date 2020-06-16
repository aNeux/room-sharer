package ru.ksu.room_sharer.server.web.misc;

import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.web.misc.users.UserInfoUtils;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExpiredSessionPhaseListener implements PhaseListener
{
	private static final long serialVersionUID = -418100930178106626L;
	private static final Logger logger = LoggerFactory.getLogger(ExpiredSessionPhaseListener.class);
	
	@Override
	public void afterPhase(PhaseEvent event) { }
	
	@Override
	public void beforePhase(PhaseEvent event)
	{
		if (!UserInfoUtils.isLoggedIn())
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			RequestContext rc = RequestContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			HttpServletResponse response = (HttpServletResponse)ec.getResponse();
			HttpServletRequest request = (HttpServletRequest)ec.getRequest();
			if (ec.isResponseCommitted())
				return;
			
			String url = ec.getRequestContextPath() + NavigationUtils.SESSION_EXPIRED_PAGE;
			try
			{
				if ((rc != null && PrimeFaces.current().isAjaxRequest() || fc.getPartialViewContext().isPartialRequest())
						&& fc.getResponseWriter() == null && fc.getRenderKit() == null)
				{
					response.setCharacterEncoding(request.getCharacterEncoding());
					
					RenderKitFactory renderKitFactory = (RenderKitFactory)FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
					ViewHandler viewHandler = fc.getApplication().getViewHandler();
					RenderKit renderKit = renderKitFactory.getRenderKit(fc, viewHandler.calculateRenderKitId(fc));
					ResponseWriter responseWriter = renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
					fc.setResponseWriter(responseWriter);
					
					if (fc.getViewRoot() == null)
						fc.setViewRoot(viewHandler.createView(fc, ""));
					ec.redirect(url);
				}
			}
			catch (Exception e)
			{
				logger.error("Redirect to the specified page '" + url + "' failed");
				throw new FacesException(e);
			}
		}
	}
	
	@Override
	public PhaseId getPhaseId()
	{
		return PhaseId.RESTORE_VIEW;
	}
}
