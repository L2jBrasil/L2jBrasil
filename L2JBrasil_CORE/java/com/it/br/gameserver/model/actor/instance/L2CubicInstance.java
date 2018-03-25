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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.model.actor.instance;

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.handler.SkillHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class L2CubicInstance
{
    protected static final Logger _log = LoggerFactory.getLogger(L2CubicInstance.class);

    public static final int STORM_CUBIC = 1;
    public static final int VAMPIRIC_CUBIC = 2;
    public static final int LIFE_CUBIC = 3;
    public static final int VIPER_CUBIC = 4;
    public static final int POLTERGEIST_CUBIC = 5;
    public static final int BINDING_CUBIC = 6;
    public static final int AQUA_CUBIC = 7;
    public static final int SPARK_CUBIC = 8;
    public static final int ATTRACT_CUBIC = 9;

    protected L2PcInstance _owner;
    protected L2Character _target;

    protected int _id;
    protected int _level = 1;

    protected List<Integer> _skills = new ArrayList<>();

    private Future<?> _disappearTask;
    private Future<?> _actionTask;

    public L2CubicInstance(L2PcInstance owner, int id, int level)
    {
        _owner = owner;
        _id = id;
        _level = level;

        switch (_id)
        {
            case STORM_CUBIC:
                _skills.add(4049);
                break;
            case VAMPIRIC_CUBIC:
                _skills.add(4050);
                break;
            case LIFE_CUBIC:
                _skills.add(4051);
                _disappearTask = ThreadPoolManager.getInstance().scheduleGeneral(new Disappear(), 3600000); // disappear in 60 mins
                doAction(_owner);
                break;
            case VIPER_CUBIC:
                _skills.add(4052);
                break;
            case POLTERGEIST_CUBIC:
                _skills.add(4053);
                _skills.add(4054);
                _skills.add(4055);
                break;
            case BINDING_CUBIC:
                _skills.add(4164);
                break;
            case AQUA_CUBIC:
                _skills.add(4165);
                break;
            case SPARK_CUBIC:
                _skills.add(4166);
                break;
            case ATTRACT_CUBIC:
                _skills.add(5115);
                _skills.add(5116);
                break;
        }
        if (_disappearTask == null)
            _disappearTask = ThreadPoolManager.getInstance().scheduleGeneral(new Disappear(), 1200000); // disappear in 20 mins
    }

    public void doAction(L2Character target)
    {
        if (_target == target) return;
        stopAction();
        _target = target;
        switch (_id)
        {
            case STORM_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(12), 0, 10000);
                break;
            case VAMPIRIC_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(8), 0, 15000);
                break;
            case VIPER_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(30), 0, 20000);
                break;
            case POLTERGEIST_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(30), 0, 8000);
                break;
            case BINDING_CUBIC:
            case AQUA_CUBIC:
            case SPARK_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(30), 0, 8000);
                break;
            case LIFE_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Heal(50), 0, 30000);
                break;
            case ATTRACT_CUBIC:
                _actionTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Action(30), 0, 8000);
                break;
        }
    }

    public int getId()
    {
        return _id;
    }

    public void setLevel(int level)
    {
        _level = level;
    }

    public void stopAction()
    {
        _target = null;
        if (_actionTask != null)
        {
            _actionTask.cancel(true);
            _actionTask = null;
        }
    }

    public void cancelDisappear()
    {
        if (_disappearTask != null)
        {
            _disappearTask.cancel(true);
            _disappearTask = null;
        }
    }

    private class Action implements Runnable
    {
        private int _chance;

        Action(int chance)
        {
            _chance = chance;
            // run task
        }


		public void run()
        {
            if (_owner.isDead() || _target.isDead() || _owner.getTarget() != _target)
            {
                stopAction();
                if (_owner.isDead())
                {
                    _owner.delCubic(_id);
                    _owner.broadcastUserInfo();
                    cancelDisappear();
                }
                return;
            }
            if (_target != null)
            {
                try
                {
                    if (Rnd.get(1, 100) < _chance)
                    {
                        L2Skill skill = SkillTable.getInstance().getInfo(_skills.get(Rnd.get(_skills.size())),_level);
                        if (skill != null)
                        {
                            L2Character[] targets = {_target};
                            ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());

                            int x, y, z;
                            // temporary range check until real behavior of cubics is known/coded
                            int range = _target.getTemplate().collisionRadius + 400; //skill.getCastRange();

                            x = (_owner.getX() - _target.getX());
                            y = (_owner.getY() - _target.getY());
                            z = (_owner.getZ() - _target.getZ());
                            if ((x * x) + (y * y) + (z * z) <= (range * range))
                            {
                                if (handler != null)
                                {
                                    handler.useSkill(_owner, skill, targets);
                                }
                                else
                                {
                                    skill.useSkill(_owner, targets);
                                }

                                MagicSkillUser msu = new MagicSkillUser(_owner, _target, skill.getId(),_level, 0, 0);
                                _owner.broadcastPacket(msu);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    _log.error( "", e);
                }
            }
        }
    }

    private class Heal implements Runnable
    {
        private int _chance;

        Heal(int chance)
        {
            _chance = chance;
            // run task
        }


		public void run()
        {
            if (_owner.isDead())
            {
                stopAction();
                _owner.delCubic(_id);
                _owner.broadcastUserInfo();
                cancelDisappear();
                return;
            }
            try
            {
                if (Rnd.get(1, 100) < _chance)
                {
                    L2Skill skill = SkillTable.getInstance().getInfo(_skills.get(Rnd.get(_skills.size())),_level);
                    if (skill != null)
                    {
                        L2Character target, caster;
                        target = null;
                        if (_owner.isInParty())
                        {
                            caster = _owner;
                            L2PcInstance player = _owner;
                            L2Party party = player.getParty();
                            double percentleft = 100.0;
                            if (party != null)
                            {
                                // Get all visible objects in a spheric area near the L2Character
                                // Get a list of Party Members
                                List<L2PcInstance> partyList = party.getPartyMembers();
                                L2Character partyMember = null;
                                int x, y, z;
                                // temporary range check until real behavior of cubics is known/coded
                                int range = 400; //skill.getCastRange();
                                for (int i = 0; i < partyList.size(); i++)
                                {
                                    partyMember = partyList.get(i);
                                    if (!partyMember.isDead())
                                    {
                                        //if party member not dead, check if he is in castrange of heal cubic
                                        x = (caster.getX() - partyMember.getX());
                                        y = (caster.getY() - partyMember.getY());
                                        z = (caster.getZ() - partyMember.getZ());
                                        if ((x * x) + (y * y) + (z * z) > range * range) continue;

                                        //member is in cubic casting range, check if he need heal and if he have the lowest HP
                                        if (partyMember.getCurrentHp() < partyMember.getMaxHp())
                                        {
                                            if (percentleft > (partyMember.getCurrentHp() / partyMember.getMaxHp()))
                                            {
                                                percentleft = (partyMember.getCurrentHp() / partyMember.getMaxHp());
                                                target = partyMember;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (_owner.getCurrentHp() < _owner.getMaxHp()) target = _owner;
                        }
                        if (target != null)
                        {
                            L2Character[] targets = {target};
                            ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());
                            if (handler != null)
                            {
                                handler.useSkill(_owner, skill, targets);
                            }
                            else
                            {
                                skill.useSkill(_owner, targets);
                            }
                            MagicSkillUser msu = new MagicSkillUser(_owner, target, skill.getId(),_level, 0, 0);
                            _owner.broadcastPacket(msu);
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    private class Disappear implements Runnable
    {
        Disappear()
        {
            // run task
        }


		public void run()
        {
            stopAction();
            _owner.delCubic(_id);
            _owner.broadcastUserInfo();
        }
    }
}
