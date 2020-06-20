package ru.ksu.room_sharer.server.streaming;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class StreamingHandler extends ChannelInboundHandlerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(StreamingHandler.class);
	
	private final ScreenshotBytesStorage screenshotBytesStorage;
	
	public StreamingHandler(ScreenshotBytesStorage screenshotBytesStorage)
	{
		this.screenshotBytesStorage = screenshotBytesStorage;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		try
		{
			// msg is already decoded to array of bytes. Just save it to specified storage
			screenshotBytesStorage.setCurrentScreenshot((byte[])msg);
		}
		finally
		{
			// Release msg object after processing
			ReferenceCountUtil.release(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		InetSocketAddress remoteAddress = ((InetSocketAddress)ctx.channel().remoteAddress());
		logger.error("Error occurred in channel with <{}:{}>. It will be closed soon",
				remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort(), cause);
		ctx.close();
	}
}
