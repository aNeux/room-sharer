package ru.ksu.room_sharer.client.services.streaming;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ksu.room_sharer.client.misc.Configuration;

public class StreamingServer
{
	private static final Logger logger = LoggerFactory.getLogger(StreamingServer.class);
	
	private final Configuration config;
	private EventLoopGroup bossGroup, workerGroup;
	
	public StreamingServer(Configuration config)
	{
		this.config = config;
	}
	
	public void start() throws InterruptedException
	{
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>()
				{
					@Override
					protected void initChannel(SocketChannel ch) throws Exception
					{
						ch.pipeline().addLast(new StreamingHandler(config.getCompressionQuality(),
								config.getImageSendingInterval()));
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		bootstrap.bind(config.getStreamingPort()).sync();
		logger.info("Streaming server has been started on port {}", config.getStreamingPort());
		logger.info("Screenshots compression quality is set to {}", config.getCompressionQuality());
		logger.info("Image sending interval = {} ms", config.getImageSendingInterval());
	}
	
	public void stop()
	{
		if (workerGroup != null)
			workerGroup.shutdownGracefully();
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		logger.info("Streaming server is going to be stopped");
	}
}
