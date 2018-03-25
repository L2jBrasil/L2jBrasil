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

import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.taskmanager.Task;
import com.it.br.gameserver.taskmanager.TaskManager;
import com.it.br.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.it.br.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Layane
 *
 */
public class TaskRecom extends Task
{
    private static final Logger _log = LoggerFactory.getLogger(TaskRecom.class);
    private static final String NAME = "sp_recommendations";

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
        for (L2PcInstance player: L2World.getInstance().getAllPlayers())
        {
            player.restartRecom();
            player.sendPacket(new UserInfo(player));
        }
        _log.info("Recommendation Global Task: launched.");
    }


	@Override
	public void  initializate()
    {
        super.initializate();
        TaskManager.addUniqueTask(NAME,TaskTypes.TYPE_GLOBAL_TASK,"1","13:00:00","");
    }

}
