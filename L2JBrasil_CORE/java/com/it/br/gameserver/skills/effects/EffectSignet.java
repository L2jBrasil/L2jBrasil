/*
 * This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.skills.effects;

import java.util.logging.Logger;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.L2WorldRegion;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.form.ZoneCylinder;
import com.it.br.gameserver.model.zone.type.L2SignetZone;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.l2skills.L2SkillMagicOnGround;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.util.Point3D;


/**
 * @author Forsaiken
 */

public class EffectSignet extends L2Effect
{
	static Logger _log = Logger.getLogger(EffectSignet.class.getName());

	private L2Spawn _spawn;

	protected L2SignetZone zone;

	public EffectSignet(Env env, EffectTemplate template)
	{
		super(env, template);
	}


	@Override
	public boolean onStart()
	{
		int x = getEffected().getX();
		int y = getEffected().getY();
		int z = getEffected().getZ();

		if (getEffected() instanceof L2PcInstance && getSkill().getTargetType() == L2Skill.SkillTargetType.TARGET_SIGNET_GROUND)
		{
			Point3D wordPosition = ((L2PcInstance) getEffected()).getCurrentSkillWorldPosition();

			if (wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}

		L2NpcTemplate template = NpcTable.getInstance().getTemplate(((L2SkillMagicOnGround) getSkill()).effectNpcId);
		if (template != null)
		{
			try
			{
				_spawn = new L2Spawn(template);
				_spawn.setLocx(x);
				_spawn.setLocy(y);
				_spawn.setLocz(z);
				_spawn.setAmount(1);
				_spawn.setHeading(getEffector().getHeading());
				_spawn.setRespawnDelay(0);
				SpawnTable.getInstance().addNewSpawn(_spawn, false);
				_spawn.init();
				_spawn.stopRespawn();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		L2WorldRegion region = getEffected().getWorldRegion();

		L2Skill skill = SkillTable.getInstance().getInfo(((L2SkillMagicOnGround) getSkill()).triggerEffectId, getLevel());

		if (skill == null)
		{
			_log.warning("EffectSignet: Could not get the tigger effect " + ((L2SkillMagicOnGround) getSkill()).triggerEffectId);
			onExit();
			return false;
		}

		zone = new L2SignetZone(region, getEffected(), !getSkill().isOffensive(), getSkill().getId(), skill);

		zone.setZone(new ZoneCylinder(x, y, z - 200, z + 200, getSkill().getSkillRadius()));

		region.addZone(zone);

		for (L2Character c : getEffected().getKnownList().getKnownCharacters())
			zone.revalidateInZone(c);

		zone.revalidateInZone(getEffected());
		return true;
	}


	@Override
	public void onExit()
	{
		if (_spawn != null)
		{
			_spawn.getLastSpawn().deleteMe();
			SpawnTable.getInstance().deleteSpawn(_spawn, false);
		}

		if (zone != null)
			zone.remove();
	}


	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET;
	}


	@Override
	public boolean onActionTime()
	{
		int mpConsume = getSkill().getMpConsume();

		if (mpConsume > getEffected().getCurrentMp())
		{
			getEffected()
					.sendPacket(
							new SystemMessage(
									SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		} else
			getEffected().reduceCurrentMp(mpConsume);

		return true;
	}
}