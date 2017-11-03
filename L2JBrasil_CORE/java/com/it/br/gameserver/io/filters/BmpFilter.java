package com.it.br.gameserver.io.filters;

import java.io.File;
import java.io.FileFilter;

public class BmpFilter implements FileFilter
{
	@Override
	public boolean accept(File file)
	{
		return file.getName().endsWith(".bmp");
	}
}
