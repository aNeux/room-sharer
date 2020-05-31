package ru.ksu.room_sharer.client.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static void closeResource(AutoCloseable closeable)
	{
		try
		{
			if (closeable != null)
				closeable.close();
		}
		catch (Exception e)
		{
			logger.error("Unable to close resource", e);
		}
	}
}
