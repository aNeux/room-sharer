package ru.ksu.room_sharer.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.regex.Pattern;

public class ConsoleOutFilter extends PrintStream
{
	private final Pattern pattern = Pattern.compile("Unable to find (state for )?+component with clientId '.*', no( need to remove|t restoring) it\\.");
	
	public ConsoleOutFilter(OutputStream out)
	{
		super(out);
	}
	
	@Override
	public void print(String s)
	{
		if (s == null)
			s = "null";
		if (!pattern.matcher(s).matches())
			super.print(s);
	}
	
	@Override
	public void println(String x)
	{
		if (x == null)
			x = "null";
		if (!pattern.matcher(x).matches())
			super.println(x);
	}
	
	@Override
	public PrintStream printf(String format, Object... args)
	{
		if (!pattern.matcher(format).matches())
			return super.printf(format, args);
		return this;
	}
	
	@Override
	public PrintStream printf(Locale l, String format, Object... args)
	{
		if (!pattern.matcher(format).matches())
			return super.printf(l, format, args);
		return this;
	}
	
	@Override
	public PrintStream append(CharSequence csq)
	{
		if (!pattern.matcher(csq.toString()).matches())
			return super.append(csq);
		return this;
	}
	
	@Override
	public PrintStream append(CharSequence csq, int start, int end)
	{
		if (!pattern.matcher(csq.toString()).matches())
			return super.append(csq, start, end);
		return this;
	}
	
	@Override
	public void write(byte[] b) throws IOException
	{
		if (!pattern.matcher(new String(b)).matches())
			super.write(b);
	}
}
