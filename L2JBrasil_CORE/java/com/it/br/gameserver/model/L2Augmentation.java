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

package com.it.br.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.datatables.AugmentationData;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.skills.funcs.FuncAdd;
import com.it.br.gameserver.skills.funcs.LambdaConst;

/**
 * Used to store an augmentation and its boni
 *
 * @author durgus
 */
public final class L2Augmentation
{
	private static final Logger _log = Logger.getLogger(L2Augmentation.class.getName());

	private L2ItemInstance _item;
	private int _effectsId = 0;
	private augmentationStatBoni _boni = null;
	private L2Skill _skill = null;

	public L2Augmentation(L2ItemInstance item, int effects, L2Skill skill, boolean save)
	{
		_item = item;
		_effectsId = effects;
		_boni = new augmentationStatBoni(_effectsId);
		_skill = skill;

		// write to DB if save is true
		if (save) saveAugmentationData();
	}

	public L2Augmentation(L2ItemInstance item, int effects, int skill, int skillLevel, boolean save)
	{
		this(item, effects, SkillTable.getInstance().getInfo(skill, skillLevel), save);
	}

	// =========================================================
	// Nested Class
	// =========================================================
	public class augmentationStatBoni
	{
		private Stats _stats[];
		private float _values[];
		private boolean _active;

		public augmentationStatBoni(int augmentationId)
		{
			_active = false;
			List<AugmentationData.AugStat> as = AugmentationData.getInstance().getAugStatsById(augmentationId);

			_stats = new Stats[as.size()];
			_values = new float[as.size()];

			int i=0;
			for (AugmentationData.AugStat aStat : as)
			{
				_stats[i] = aStat.getStat();
				_values[i] = aStat.getValue();
				i++;
			}
		}

		public void applyBonus(L2PcInstance player)
		{
			// make sure the bonuses are not applied twice..
			if (_active) return;

			for (int i=0; i < _stats.length; i++)
				((L2Character)player).addStatFunc(new FuncAdd(_stats[i], 0x40, this, new LambdaConst(_values[i])));

			_active = true;
		}

		public void removeBonus(L2PcInstance player)
		{
			// make sure the bonuses are not removed twice
			if (!_active) return;

			((L2Character)player).removeStatsOwner(this);

			_active = false;
		}
	}

	private void saveAugmentationData()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			PreparedStatement statement = con.prepareStatement("INSERT INTO augmentations (item_id,attributes,skill,level) VALUES (?,?,?,?)");
			statement.setInt(1, _item.getObjectId());
			statement.setInt(2, _effectsId);
			if (_skill != null)
			{
				statement.setInt(3, _skill.getId());
				statement.setInt(4, _skill.getLevel());
			} else
			{
				statement.setInt(3, 0);
				statement.setInt(4, 0);
			}

			statement.executeUpdate();
			statement.close();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Could not save augmentation for item: "+_item.getObjectId()+" from DB:", e);
		} finally {
			try { con.close(); } catch (Exception e) {}
		}
	}

	public void deleteAugmentationData()
	{
		if (!_item.isAugmented()) return;

		// delete the augmentation from the database
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM augmentations WHERE item_id=?");
			statement.setInt(1, _item.getObjectId());
			statement.executeUpdate();
			statement.close();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Could not delete augmentation for item: "+_item.getObjectId()+" from DB:", e);
		} finally 
                {
			try { con.close(); } catch (Exception e) {}
		}
	}

	/**
	 * Get the augmentation "id" used in serverpackets.
	 * @return augmentationId
	 */
	public int getAugmentationId()
	{
		return _effectsId;
	}

	public L2Skill getSkill()
	{
		return _skill;
	}

	/**
	 * Applies the bonuses to the player.
	 * @param player
	 */
	public void applyBonus(L2PcInstance player)
	{
		_boni.applyBonus(player);
	
		// add the skill if any
		if (_skill != null)
		{
			player.addSkill(_skill);
			player.sendSkillList();
            
                  // Iterate through all effects currently on the character. 
                  for (L2Effect currenteffect : player.getAllEffects()) 
                 { 
                         L2Skill effectSkill = currenteffect.getSkill(); 
                           
                          if (effectSkill.getId() == _skill.getId()) 
                           { 
                                  player.sendMessage("You feel the power of "+effectSkill.getName()+" leaving yourself."); 
                                  currenteffect.exit(); 
                          } 
                } 
		}
	}
	
	/**
	 * Removes the augmentation bonuses from the player.
	 * @param player
	 */
	public void removeBonus(L2PcInstance player)
	{
		_boni.removeBonus(player);
		
		// remove the skill if any
		if (_skill != null)
		{
			player.removeSkill(_skill);
			player.sendSkillList();
		}
	}
}
