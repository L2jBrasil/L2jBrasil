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

/**
 * @author Forsaiken
 */

import com.it.br.gameserver.ai.CtrlEvent;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.MagicSkillLaunched;
import com.it.br.gameserver.network.serverpackets.NpcInfo;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.Formulas;

import java.util.ArrayList;
import java.util.List;

public class EffectSignetMDam extends EffectSignet
{
    private int _state = 0;
    public EffectSignetMDam(Env env, EffectTemplate template)
    {
        super(env, template);
    }
    

    @Override
	public EffectType getEffectType()
    {
        return EffectType.SIGNET_GROUND;
    }


    @Override
	public boolean onActionTime()
    {
        // on offi the zone get created and the first wave starts later
        // there is also an first hit animation to the caster
        switch (_state)
        {
            case 0:
            case 2:
                _state++;
                return true;
            case 1:
                getEffected().broadcastPacket(new MagicSkillLaunched(getEffected(), getSkill().getId(), getSkill().getLevel(), new L2Object[]{getEffected()}));
                _state++;
                return true;
        }
        
        int mpConsume = getSkill().getMpConsume();
        
        L2PcInstance caster = (L2PcInstance)getEffected();
        
        boolean ss = false;
        boolean bss = false;
        
        L2ItemInstance weaponInst = caster.getActiveWeaponInstance();
        if (weaponInst != null)
        {
            switch (weaponInst.getChargedSpiritshot())
            {
                case L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT:
                        weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
                        bss = true;
                    break;
                case L2ItemInstance.CHARGED_SPIRITSHOT:
                        weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
                        ss = true;
                    break;
            }
        }
        
        if (!bss && !ss)
            caster.rechargeAutoSoulShot(false, true, false);
        
        List<L2Character> targets = new ArrayList<>();
        
        for (L2Character cha : zone.getCharactersInZone())
        {
            if (cha == null || cha == getEffected())
                continue;
            
            if (cha instanceof L2Attackable || cha instanceof L2PlayableInstance)
            {
                if (cha.isAlikeDead())
                    continue;
                
                if (mpConsume > caster.getCurrentMp())
                {
                    caster.sendPacket(new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
                    return false;
                }
                else
                    caster.reduceCurrentMp(mpConsume);
                
                targets.add(cha);
            }
        }
        
        if (targets.size() > 0)
        {
            caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill().getId(), getSkill().getLevel(), targets.toArray(new L2Character[targets.size()])));
            for (L2Character target : targets)
            {
                boolean mcrit = Formulas.getInstance().calcMCrit(caster.getMCriticalHit(target, getSkill()));
                int mdam = (int)Formulas.getInstance().calcMagicDam(caster, target, getSkill(), ss, bss, mcrit);
                
                if (target instanceof L2Summon)
                {
                    caster.equals(((L2Summon)target).getOwner());
                        caster.sendPacket(new NpcInfo((L2Summon)target, caster));
                }
                
                if (mdam > 0)
                {
                    if (!target.isRaid() && Formulas.getInstance().calcAtkBreak(target, mdam))
                    {
                        target.breakAttack();
                        target.breakCast();
                    }
                    caster.sendDamageMessage(target, mdam, mcrit, false, false);
                    target.reduceCurrentHp(mdam, caster);
                }
                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
            }
        }
        return true;
    }
}
