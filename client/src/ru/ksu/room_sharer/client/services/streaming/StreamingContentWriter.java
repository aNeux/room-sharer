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
	
	// Variables that will be used a lot of times being the same
	private final BufferedImage cursorImage;
	private final Robot robot;
	private final Rectangle rectangle;
	
	public StreamingContentWriter(ChannelHandlerContext chContext, float compressionQuality) throws IOException, AWTException
	{
		this.chContext = chContext;
		this.compressionQuality = compressionQuality;
		
		cursorImage = ImageIO.read(new File("images/cursor.png"));
		robot = new Robot();
		rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	}
	
	@Override
	public void run()
	{
		try
		{
			chContext.writeAndFlush(makeScreenshot());
		}
		catch (IOException e)
		{
			logger.error("Error occurred while making next screenshot", e);
		}
	}
	
	private ByteBuf makeScreenshot() throws IOException
	{
		Graphics2D graphics2D = null;
		ImageWriter jpgWriter = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ImageOutputStream ios = new MemoryCacheImageOutputStream(baos))
		{
			// Make screenshot and place mouse pointer to the necessary place
			BufferedImage screenshot = robot.createScreenCapture(rectangle);
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			graphics2D = screenshot.createGraphics();
			graphics2D.drawImage(cursorImage, mouse.x, mouse.y, 10, 16, null);
			
			// Compress and write prepared image to bytes array
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
			// Dispose used objects to allow garbage collector do its work
			if (jpgWriter != null)
				jpgWriter.dispose();
			if (graphics2D != null)
				graphics2D.dispose();
		}
	}
}
