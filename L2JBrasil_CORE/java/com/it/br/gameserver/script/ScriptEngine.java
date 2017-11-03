package com.it.br.gameserver.script;

import java.util.Hashtable;

import com.it.br.gameserver.script.faenor.FaenorInterface;



public class ScriptEngine
{
	protected EngineInterface _utils = FaenorInterface.getInstance();
	public static final Hashtable<String, ParserFactory> parserFactories = new Hashtable<String, ParserFactory>();
	
	protected static Parser createParser(String name) throws ParserNotCreatedException
	{
		ParserFactory s = parserFactories.get(name);
		if (s == null) // shape not found
		{
			try
			{
				Class.forName("com.it.br.gameserver.script." + name);
				// By now the static block with no function would
				// have been executed if the shape was found.
				// the shape is expected to have put its factory
				// in the hashtable.
				
				s = parserFactories.get(name);
				if (s == null) // if the shape factory is not there even now
				{
					throw (new ParserNotCreatedException());
				}
			}
			catch (ClassNotFoundException e)
			{
				// We'll throw an exception to indicate that
				// the shape could not be created
				throw (new ParserNotCreatedException());
			}
		}
		return (s.create());
	}
}
