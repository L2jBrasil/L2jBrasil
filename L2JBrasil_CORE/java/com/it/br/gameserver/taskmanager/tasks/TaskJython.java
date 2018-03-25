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
package com.it.br.gameserver.taskmanager.tasks;

import com.it.br.gameserver.taskmanager.Task;
import com.it.br.gameserver.taskmanager.TaskManager.ExecutedTask;
import org.python.util.PythonInterpreter;

/**
 * @author Layane
 *
 */
public class TaskJython extends Task
{
    public static final String NAME = "jython";

    private final PythonInterpreter _python = new PythonInterpreter();

    /* (non-Javadoc)
     * @see com.it.br.gameserver.taskmanager.Task#getName()
     */

    @Override
	public String getName()
    {
        return NAME;
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.taskmanager.Task#onTimeElapsed(com.it.br.gameserver.taskmanager.TaskManager.ExecutedTask)
     */

    @Override
	public void onTimeElapsed(ExecutedTask task)
    {
        _python.cleanup();
        _python.exec("import sys");
        _python.execfile("data/jscript/cron/" + task.getParams()[2]);
    }

}
