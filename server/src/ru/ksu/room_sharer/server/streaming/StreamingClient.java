package ru.ksu.room_sharer.server.streaming;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingClient
{
	private final Logger logger = LoggerFactory.getLogger(StreamingClient.class);
	
	private final String host;
	private final int port;
	private final ScreenshotBytesStorage screenshotBytesStorage;
	
	private EventLoopGroup workerGroup;
	
	public StreamingClient(String host, int port)
	{
		this.host = host;
		this.port = port;
		this.screenshotBytesStorage = new ScreenshotBytesStorage();
	}
	
	public void start() throws InterruptedException
	{
		workerGroup = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new ChannelInitializer<SocketChannel>()
				{
					@Override
					protected void initChannel(SocketChannel ch) throws Exception
					{
						ch.pipeline().addLast(new ImageBytesDecoder(), new StreamingHandler(screenshotBytesStorage));
					}
				});
		
		bootstrap.connect(host, port).sync();
		logger.info("Started streaming client for <{}:{}>", host, port);
	}
	
	public boolean isRunning()
	{
		return workerGroup != null && !workerGroup.isShuttingDown();
	}
	
	public void stop()
	{
		if (workerGroup != null && !workerGroup.isShuttingDown())
			workerGroup.shutdownGracefully();
		screenshotBytesStorage.dispose();
		logger.info("Streaming client for <{}:{}> is going to be stopped", host, port);
	}
	
	
	public ScreenshotBytesStorage getScreenshotBytesStorage()
	{
		return screenshotBytesStorage;
	}
}
