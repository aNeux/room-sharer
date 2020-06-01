package ru.ksu.room_sharer.server;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils
{
	public static final String EOL = "/r/n", UTF8 = "UTF-8";
	
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
}
