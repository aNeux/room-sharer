package ru.ksu.room_sharer.server.web.filters;

import ru.ksu.room_sharer.server.Utils;

import javax.servlet.*;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter
{
	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		request.setCharacterEncoding(Utils.UTF8);
		response.setCharacterEncoding(Utils.UTF8);
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() { }
}
