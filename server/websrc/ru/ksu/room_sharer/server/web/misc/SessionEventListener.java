package ru.ksu.room_sharer.server.web.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.web.misc.users.UserSessionCollector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.EventListener;

public class SessionEventListener implements HttpSessionListener, EventListener
{
	private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent event)
	{
		logger.debug("Created a new session '{}'", event.getSession().getId());
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event)
	{
		HttpSession destroyedSession = event.getSession();
		logger.debug("Destroyed session '{}'", destroyedSession.getId());
		UserSessionCollector.removeSession(destroyedSession);
	}
}
