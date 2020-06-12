package ru.ksu.room_sharer.server.web.filters;

import ru.ksu.room_sharer.server.web.beans.LoginBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter
{
	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpSession session = servletRequest.getSession();
		String context = servletRequest.getContextPath();
		context = context == null ? "" : (!context.isEmpty() && !context.startsWith("/") ? "/" : "") + context;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		
		if (session.getAttribute(LoginBean.AUTH_KEY) == null)
		{
			// Seems, user isn't login. Redirect him to the login page
			servletResponse.sendRedirect(context + LoginBean.LOGIN_PAGE + "?requestUrl="
					+ servletRequest.getRequestURI().replace("/", "%2F"));
		}
		else
		{
			String requestedPage = servletRequest.getRequestURI();
			requestedPage = requestedPage.substring(requestedPage.lastIndexOf('/') + 1, requestedPage.lastIndexOf('.'));
			if (!(boolean)session.getAttribute(LoginBean.ADMIN_KEY) && (!("common_rooms".equals(requestedPage) || "user_rooms".equals(requestedPage))))
				servletResponse.sendRedirect(context + LoginBean.COMMON_ROOMS_PAGE); // Only admins could access users management and settings pages
			else
				chain.doFilter(request, servletResponse); // No restrictions found to process request
		}
	}
	
	@Override
	public void destroy() { }
}
