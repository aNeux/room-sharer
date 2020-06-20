package ru.ksu.room_sharer.server;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.server.clients.Client;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils
{
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static final String EOL = "/r/n", UTF8 = "UTF-8", JSON_EXT = ".json";
	
	public static String md5(String originalString) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		// Generate bytes of MD5 hash
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(originalString.getBytes(UTF8));
		byte[] digest = md.digest();
		
		// Obtain String representation of generated hash
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		while (hashtext.length() < 32 )
			hashtext = "0" + hashtext;
		return hashtext;
	}
	
	public static void closeResource(AutoCloseable resource)
	{
		try
		{
			if (resource != null)
				resource.close();
		}
		catch (Exception e)
		{
			logger.error("Couldn't close resource", e);
		}
	}
	
	public static String getClientFullName(Client client)
	{
		String result = client.getHostName(), pseudoName = client.getPseudoName();
		if (StringUtils.isNotBlank(pseudoName))
			result = pseudoName + " (" + result + ")";
		return result;
	}
}
