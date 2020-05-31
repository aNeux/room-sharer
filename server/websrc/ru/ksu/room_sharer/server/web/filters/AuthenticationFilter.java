package ru.ksu.room_sharer.server.web.filters;

import org.apache.commons.lang3.StringUtils;
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
		if (StringUtils.isNotEmpty(context) && !context.startsWith("/"))
			context = "/" + context;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		
		if (session.getAttribute(LoginBean.AUTH_KEY) == null)
		{
			// Seems, user isn't login.. Redirect him to the login page
			servletResponse.sendRedirect(context + LoginBean.LOGIN_PAGE + "?requestUrl="
					+ servletRequest.getRequestURI().replace("/", "%2F"));
		}
		else
		{
			boolean isAdmin = (boolean)session.getAttribute(LoginBean.IS_ADMIN_KEY);
			if (!isAdmin)
			{
				String requestUri = servletRequest.getRequestURI();
				// Only admin could access settings and users pages
				if (!("room-sharer/ui/restricted/" + LoginBean.ROOMS_PAGE).equals(requestUri))
					servletResponse.sendRedirect(context + LoginBean.ROOMS_PAGE);
			}
		}
		chain.doFilter(request, servletResponse);
	}
	
	@Override
	public void destroy() { }
}
