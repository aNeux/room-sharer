package ru.ksu.room_sharer.server.streaming;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ImageBytesDecoder extends ByteToMessageDecoder
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		// Wait until the whole JPG image will be received
		if (in.readableBytes() < 4)
			return;
		in.markReaderIndex();
		int length = in.readableBytes();
		if (in.getByte(length - 2) != (byte)-1 || in.getByte(length - 1) != (byte)-39)
		{
			in.resetReaderIndex();
			return;
		}
		
		// Transform ByteBuf to simple bytes array and store it to output
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		out.add(bytes);
	}
}
