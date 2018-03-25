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

import com.it.br.Config;
import com.it.br.gameserver.taskmanager.TaskManager.ExecutedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;


/**
 * @author Layane
 *
 */
public abstract class Task
{
    private static Logger _log = LoggerFactory.getLogger(Task.class);

    public void initializate()
    {
        if (Config.DEBUG)
            _log.info("Task" + getName() + " inializate");
    }

    public ScheduledFuture<?> launchSpecial(ExecutedTask instance)
    {
        return null;
    }

    public abstract String getName();
    public abstract void onTimeElapsed(ExecutedTask task);

    public void onDestroy()
    {
    }
}
