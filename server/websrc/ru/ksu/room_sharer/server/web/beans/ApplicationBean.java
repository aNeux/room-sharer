package ru.ksu.room_sharer.server.web.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationBean
{
	private Logger logger;
	
	public ApplicationBean()
	{
		String loggerName = this.getClass().getName();
		// TODO: add user name here
		logger = LoggerFactory.getLogger(loggerName);
	}
	
	protected Logger getLogger()
	{
		return logger;
	}
}
