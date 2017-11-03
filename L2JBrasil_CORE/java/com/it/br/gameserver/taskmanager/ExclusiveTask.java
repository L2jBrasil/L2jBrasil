/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.it.br.gameserver.taskmanager;

import java.util.concurrent.Future;

import com.it.br.gameserver.ThreadPoolManager;

/**
 * @author NB4L1
 */
public abstract class ExclusiveTask
{
	private final boolean _returnIfAlreadyRunning;

	private Future<?> _future;
	private boolean _isRunning;
	private Thread _currentThread;

	protected ExclusiveTask(boolean returnIfAlreadyRunning)
	{
		_returnIfAlreadyRunning = returnIfAlreadyRunning;
	}

	protected ExclusiveTask()
	{
		this(false);
	}

	public synchronized boolean isScheduled()
	{
		return _future != null;
	}

	public synchronized final void cancel()
	{
		if(_future != null)
		{
			_future.cancel(false);
			_future = null;
		}
	}

	public synchronized final void schedule(long delay)
	{
		cancel();

		_future = ThreadPoolManager.getInstance().scheduleEffect(_runnable, delay);
	}

	public synchronized final void execute()
	{
		ThreadPoolManager.getInstance().executeTask(_runnable);
	}

	public synchronized final void scheduleAtFixedRate(long delay, long period)
	{
		cancel();

		_future = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(_runnable, delay, period);
	}

	private final Runnable _runnable = new Runnable() {

	
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			if(tryLock())
			{
				try
				{
					onElapsed();
				}
				finally
				{
					unlock();
				}
			}
		}
	};

	protected abstract void onElapsed();

	private synchronized boolean tryLock()
	{
		if(_returnIfAlreadyRunning)
			return !_isRunning;

		_currentThread = Thread.currentThread();

		for(;;)
		{
			try
			{
				notifyAll();

				if(_currentThread != Thread.currentThread())
					return false;

				if(!_isRunning)
					return true;

				wait();
			}
			catch(InterruptedException e)
			{}
		}
	}

	private synchronized void unlock()
	{
		_isRunning = false;
	}
}
