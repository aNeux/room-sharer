package ru.ksu.room_sharer.server.streaming;

import java.util.Arrays;

public class ScreenshotBytesStorage
{
	private byte[] currentScreenshot;
	
	public synchronized void setCurrentScreenshot(byte[] currentScreenshot)
	{
		// Let it be the copy, not the original array
		this.currentScreenshot = Arrays.copyOf(currentScreenshot, currentScreenshot.length);
	}
	
	public synchronized byte[] getCurrentScreenshot()
	{
		return currentScreenshot;
	}
	
	public synchronized void dispose()
	{
		currentScreenshot = null;
	}
}
