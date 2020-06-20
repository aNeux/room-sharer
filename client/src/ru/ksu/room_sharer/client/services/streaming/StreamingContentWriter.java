package ru.ksu.room_sharer.client.services.streaming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class StreamingContentWriter implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(StreamingContentWriter.class);
	
	private final ChannelHandlerContext chContext;
	private final float compressionQuality;
	
	public StreamingContentWriter(ChannelHandlerContext chContext, float compressionQuality)
	{
		this.chContext = chContext;
		this.compressionQuality = compressionQuality;
	}
	
	@Override
	public void run()
	{
		try
		{
			chContext.writeAndFlush(makeScreenshot());
		}
		catch (AWTException | IOException e)
		{
			logger.error("Error occurred while making screenshot", e);
		}
	}
	
	private ByteBuf makeScreenshot() throws AWTException, IOException
	{
		BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Graphics2D graphics2D = screenshot.createGraphics();
		graphics2D.drawImage(ImageIO.read(new File("images/cursor.png")), mouse.x, mouse.y, 10, 16, null);
		
		// Compress and write prepared image to bytes array
		ImageWriter jpgWriter = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ImageOutputStream ios = new MemoryCacheImageOutputStream(baos))
		{
			jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality(compressionQuality);
			jpgWriter.setOutput(ios);
			jpgWriter.write(null, new IIOImage(screenshot, null, null), jpgWriteParam);
			return Unpooled.copiedBuffer(baos.toByteArray());
		}
		finally
		{
			if (jpgWriter != null)
				jpgWriter.dispose();
		}
	}
}
