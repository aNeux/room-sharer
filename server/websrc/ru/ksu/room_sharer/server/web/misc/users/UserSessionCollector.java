package ru.ksu.room_sharer.server.web.misc.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.*;

public class UserSessionCollector
{
	private static final Logger logger = LoggerFactory.getLogger(UserSessionCollector.class);
	
	private static final Map<String, List<HttpSession>> sessionsByLogin = new HashMap<>();
	private static final Map<String, String> loginBySessionId = new HashMap<>();
	
	public static List<HttpSession> getSessionsByLogin(String login)
	{
		List<HttpSession> sessions;
		synchronized (UserSessionCollector.class)
		{
			sessions = sessionsByLogin.get(login);
		}
		return sessions == null ? Collections.emptyList() : new ArrayList<>(sessions);
	}
	
	public static void registerCurrentSession(String login)
	{
		HttpSession currentSession = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String currentSessionId = currentSession.getId();
		
		synchronized (UserSessionCollector.class)
		{
			loginBySessionId.put(currentSessionId, login);
			List<HttpSession> sessions = sessionsByLogin.computeIfAbsent(login, k -> new ArrayList<>());
			sessions.add(currentSession);
		}
		
		logger.debug("Session '{}' has been registered as '{}'", currentSessionId, login);
	}
	
	public static void removeSession(HttpSession session)
	{
		synchronized (UserSessionCollector.class)
		{
			String sessionId = session.getId(), sessionLogin = loginBySessionId.remove(sessionId);
			List<HttpSession> sessionsForThisLogin = sessionsByLogin.get(sessionLogin);
			if (sessionsForThisLogin != null)
				sessionsForThisLogin.remove(session);
		}
	}
}
