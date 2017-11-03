package com.it.br.gameserver.io.filters;

import java.io.File;
import java.io.FileFilter;

public class HtmFilter implements FileFilter
{
	@Override
	public boolean accept(File file)
	{
		return file.isDirectory() || (file.getName().endsWith(".htm") || file.getName().endsWith(".html"));
	}
}
