package ru.ksu.room_sharer.client.services.streaming;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StreamingHandler extends ChannelInboundHandlerAdapter
{
	private final static Logger logger = LoggerFactory.getLogger(StreamingHandler.class);
	
	private final float compressionQuality;
	private final int imageSendingInterval;
	
	private ScheduledFuture scheduledFuture;
	
	public StreamingHandler(float compressionQuality, int imageSendingInterval)
	{
		this.compressionQuality = compressionQuality;
		this.imageSendingInterval = imageSendingInterval;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		logger.debug("Client <{}> connected. Start streaming..", getRemoteHostCredentials(ctx));
		scheduledFuture = ctx.channel().eventLoop().scheduleAtFixedRate(new StreamingContentWriter(ctx,
				compressionQuality), 0, imageSendingInterval, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		logger.debug("Client <{}> disconnected. Stop streaming and close channel..", getRemoteHostCredentials(ctx));
		if (scheduledFuture != null)
			scheduledFuture.cancel(true);
		ctx.close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		logger.error("Error occurred in channel with client <{}>. It will be closed soon", getRemoteHostCredentials(ctx), cause);
		ctx.close();
	}
	
	
	private String getRemoteHostCredentials(ChannelHandlerContext ctx)
	{
		InetSocketAddress remoteAddress = ((InetSocketAddress)ctx.channel().remoteAddress());
		return String.format("%s:%d", remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
	}
}
