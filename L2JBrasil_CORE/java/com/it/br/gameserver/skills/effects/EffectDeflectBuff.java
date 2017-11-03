package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;

public final class EffectDeflectBuff extends L2Effect
{
	/**
	 * @param env
	 * @param template
	 */
	public EffectDeflectBuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#getEffectType()
	 */

	@Override
	public EffectType getEffectType()
	{
		return EffectType.PREVENT_BUFF;
	}
        
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onActionTime()
	 */

	@Override
	public boolean onActionTime()
	{
		// Only cont skills shouldn't end
		if(getSkill().getSkillType() != SkillType.CONT)
			return false;

		double manaDam = calc();

		if(manaDam > getEffected().getCurrentMp())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			getEffected().sendPacket(sm);
			return false;
		}

		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
        
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onstart()
	 */
	public void onstart()
	{
		getEffected().setIsBuffProtected(true);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onExit()
	 */

	@Override
	public void onExit()
	{
		getEffected().setIsBuffProtected(false);
	}
}