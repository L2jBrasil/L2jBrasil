package com.it.br.gameserver.ai.special.group;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2AttackableAIScript;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2TamedBeastInstance;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.serverpackets.NpcSay;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.util.Rnd;

import java.util.HashMap;
import java.util.Map;

public class FeedableBeasts extends L2AttackableAIScript
{
	private static final int GOLDEN_SPICE = 6643;
	private static final int CRYSTAL_SPICE = 6644;
	private static final int SKILL_GOLDEN_SPICE = 2188;
	private static final int SKILL_CRYSTAL_SPICE = 2189;
    private static final int[] TAMED_BEASTS = {16013, 16014, 16015, 16016, 16017, 16018};
    
	private static final Map<Integer,Integer> FOODSKILL = new HashMap<>();
	static
	{
		FOODSKILL.put(GOLDEN_SPICE,SKILL_GOLDEN_SPICE);
		FOODSKILL.put(CRYSTAL_SPICE,SKILL_CRYSTAL_SPICE);
	}
    
    // all mobs that can eat...
    private static final int[] FEEDABLE_BEASTS = {
    	21451,21452,21453,21454,21455,21456,21457,21458,21459,21460,21461,21462,21463,
    	21464,21465,21466,21467,21468,21469,21470,21471,21472,21473,21474,21475,21476,
    	21477,21478,21479,21480,21481,21482,21483,21484,21485,21486,21487,21488,21489,
    	21490,21491,21492,21493,21494,21495,21496,21497,21498,21499,21500,21501,21502,
    	21503,21504,21505,21506,21507,21824,21825,21826,21827,21828,21829,16013,16014,
    	16015,16016,16017,16018
    };
    
    private static final Map<Integer,Integer> MAD_COW_POLYMORPH = new HashMap<>();
    static
    {
    	MAD_COW_POLYMORPH.put(21824,21468);
    	MAD_COW_POLYMORPH.put(21825,21469);
    	MAD_COW_POLYMORPH.put(21826,21487);
    	MAD_COW_POLYMORPH.put(21827,21488);
    	MAD_COW_POLYMORPH.put(21828,21506);
    	MAD_COW_POLYMORPH.put(21829,21507);
    }
    
    private static final String[][] TEXT = 
    {
    	{
    		"What did you just do to me?","You want to tame me, huh?",
    		"Do not give me this. Perhaps you will be in danger.",
    		"Bah bah. What is this unpalatable thing?",
    		"My belly has been complaining.  This hit the spot.",
    		"What is this? Can I eat it?","You don't need to worry about me.",
    		"Delicious food, thanks.","I am starting to like you!","Gulp"
    	},
    	{
    		"I do not think you have given up on the idea of taming me.",
    		"That is just food to me.  Perhaps I can eat your hand too.",
    		"Will eating this make me fat? Ha ha","Why do you always feed me?",
    		"Do not trust me.  I may betray you"
    	},
    	{
    		"Destroy","Look what you have done!",
    		"Strange feeling...!  Evil intentions grow in my heart...!",
    		"It is happenning!","This is sad...Good is sad...!"
    	}
    };
    
    private static final String[] TAMED_TEXT = 
    {
    	"Refills! Yeah!","I am such a gluttonous beast, it is embarrassing! Ha ha",
    	"Your cooperative feeling has been getting better and better.",
    	"I will help you!","The weather is really good.  Wanna go for a picnic?",
    	"I really like you! This is tasty...",
    	"If you do not have to leave this place, then I can help you.",
    	"What can I help you with?","I am not here only for food!",
    	"Yam, yam, yam, yam, yam!"
    };

    private static Map<Integer,Integer> _FeedInfo = new HashMap<>();
    private static Map<Integer,GrowthCapableMob> _GrowthCapableMobs = new HashMap<>();

    // all mobs that grow by eating
    private class GrowthCapableMob
    {
    	private int _growthLevel;
    	private int _chance;
    	
    	private Map<Integer, int[][]> _spiceToMob = new HashMap<>();
    	
    	public GrowthCapableMob(int growthLevel, int chance)
    	{
    		_growthLevel = growthLevel;
    		_chance = chance;
    	}
    	
    	public void addMobs(int spice, int[][] Mobs)
    	{
    		_spiceToMob.put(spice, Mobs);
    	}
    	
    	public Integer getMob(int spice,int mobType, int classType)
    	{
    		if (_spiceToMob.containsKey(spice))
    		{
    			return _spiceToMob.get(spice)[mobType][classType];
    		}
    		return null;
    	}
    	
    	public Integer getRandomMob(int spice)
    	{
    		int[][] temp;
    		temp = _spiceToMob.get(spice);
    		int rand = Rnd.get(temp[0].length);
    		return temp[0][rand];
    	}
    	
    	public Integer getChance()
    	{
    		return _chance;
    	}
    	
    	public Integer getGrowthLevel()
    	{
    		return _growthLevel;
    	}
    }

	public FeedableBeasts (int questId, String name, String descr)
	{
		super(questId, name, descr);        
        this.registerMobs(FEEDABLE_BEASTS);
        
        GrowthCapableMob temp;

        final int[][] Kookabura_0_Gold = {{21452,21453, 21454, 21455}};
        final int[][] Kookabura_0_Crystal = {{21456,21457, 21458, 21459}};
        final int[][] Kookabura_1_Gold_1= {{21460,21462}};
        final int[][] Kookabura_1_Gold_2 = {{21461,21463}};
        final int[][] Kookabura_1_Crystal_1 = {{21464,21466}};
        final int[][] Kookabura_1_Crystal_2 = {{21465,21467}};
        final int[][] Kookabura_2_1 = {{21468,21824},{16017,16018}};
        final int[][] Kookabura_2_2 = {{21469,21825},{16017,16018}};
        
        final int[][] Buffalo_0_Gold = {{21471,21472, 21473, 21474}};
        final int[][] Buffalo_0_Crystal = {{21475,21476, 21477, 21478}};
        final int[][] Buffalo_1_Gold_1 = {{21479,21481}};
        final int[][] Buffalo_1_Gold_2 = {{21481,21482}};
        final int[][] Buffalo_1_Crystal_1 = {{21483,21485}};
        final int[][] Buffalo_1_Crystal_2 = {{21484,21486}};
        final int[][] Buffalo_2_1 = {{21487,21826},{16013,16014}};
        final int[][] Buffalo_2_2 = {{21488,21827},{16013,16014}};

        final int[][] Cougar_0_Gold = {{21490,21491, 21492, 21493}};
        final int[][] Cougar_0_Crystal = {{21494,21495, 21496, 21497}};
        final int[][] Cougar_1_Gold_1 = {{21498,21500}};
        final int[][] Cougar_1_Gold_2 = {{21499,21501}};
        final int[][] Cougar_1_Crystal_1 = {{21502,21504}};
        final int[][] Cougar_1_Crystal_2 = {{21503,21505}};
        final int[][] Cougar_2_1 = {{21506,21828},{16015,16016}};
        final int[][] Cougar_2_2 = {{21507,21829},{16015,16016}};

        // Alpen Kookabura
        temp = new GrowthCapableMob(0,100);
        temp.addMobs(GOLDEN_SPICE,Kookabura_0_Gold);
        temp.addMobs(CRYSTAL_SPICE,Kookabura_0_Crystal);
        _GrowthCapableMobs.put(21451, temp);
        
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Kookabura_1_Gold_1);
        _GrowthCapableMobs.put(21452, temp);
        _GrowthCapableMobs.put(21454, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Kookabura_1_Gold_2);
        _GrowthCapableMobs.put(21453, temp);
        _GrowthCapableMobs.put(21455, temp);        

        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Kookabura_1_Crystal_1);
        _GrowthCapableMobs.put(21456, temp);
        _GrowthCapableMobs.put(21458, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Kookabura_1_Crystal_2);
        _GrowthCapableMobs.put(21457, temp);
        _GrowthCapableMobs.put(21459, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Kookabura_2_1);
        _GrowthCapableMobs.put(21460, temp);
        _GrowthCapableMobs.put(21462, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Kookabura_2_2);
        _GrowthCapableMobs.put(21461, temp);
        _GrowthCapableMobs.put(21463, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Kookabura_2_1);
        _GrowthCapableMobs.put(21464, temp);
        _GrowthCapableMobs.put(21466, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Kookabura_2_2);
        _GrowthCapableMobs.put(21465, temp);
        _GrowthCapableMobs.put(21467, temp);
        
        // Alpen Buffalo
        temp = new GrowthCapableMob(0,100);
        temp.addMobs(GOLDEN_SPICE,Buffalo_0_Gold);
        temp.addMobs(CRYSTAL_SPICE,Buffalo_0_Crystal);
        _GrowthCapableMobs.put(21470, temp);
        
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Buffalo_1_Gold_1);
        _GrowthCapableMobs.put(21471, temp);
        _GrowthCapableMobs.put(21473, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Buffalo_1_Gold_2);
        _GrowthCapableMobs.put(21472, temp);
        _GrowthCapableMobs.put(21474, temp);        

        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Buffalo_1_Crystal_1);
        _GrowthCapableMobs.put(21475, temp);
        _GrowthCapableMobs.put(21477, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Buffalo_1_Crystal_2);
        _GrowthCapableMobs.put(21476, temp);
        _GrowthCapableMobs.put(21478, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Buffalo_2_1);
        _GrowthCapableMobs.put(21479, temp);
        _GrowthCapableMobs.put(21481, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Buffalo_2_2);
        _GrowthCapableMobs.put(21480, temp);
        _GrowthCapableMobs.put(21482, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Buffalo_2_1);
        _GrowthCapableMobs.put(21483, temp);
        _GrowthCapableMobs.put(21485, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Buffalo_2_2);
        _GrowthCapableMobs.put(21484, temp);
        _GrowthCapableMobs.put(21486, temp);
        
        // Alpen Cougar
        temp = new GrowthCapableMob(0,100);
        temp.addMobs(GOLDEN_SPICE,Cougar_0_Gold);
        temp.addMobs(CRYSTAL_SPICE,Cougar_0_Crystal);
        _GrowthCapableMobs.put(21489, temp);
        
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Cougar_1_Gold_1);
        _GrowthCapableMobs.put(21490, temp);
        _GrowthCapableMobs.put(21492, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(GOLDEN_SPICE,Cougar_1_Gold_2);
        _GrowthCapableMobs.put(21491, temp);
        _GrowthCapableMobs.put(21493, temp);        

        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Cougar_1_Crystal_1);
        _GrowthCapableMobs.put(21494, temp);
        _GrowthCapableMobs.put(21496, temp);
                
        temp = new GrowthCapableMob(1,40);
        temp.addMobs(CRYSTAL_SPICE,Cougar_1_Crystal_2);
        _GrowthCapableMobs.put(21495, temp);
        _GrowthCapableMobs.put(21496, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Cougar_2_1);
        _GrowthCapableMobs.put(21498, temp);
        _GrowthCapableMobs.put(21500, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(GOLDEN_SPICE,Cougar_2_2);
        _GrowthCapableMobs.put(21499, temp);
        _GrowthCapableMobs.put(21501, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Cougar_2_1);
        _GrowthCapableMobs.put(21502, temp);
        _GrowthCapableMobs.put(21504, temp);
                
        temp = new GrowthCapableMob(2,25);
        temp.addMobs(CRYSTAL_SPICE,Cougar_2_2);
        _GrowthCapableMobs.put(21503, temp);
        _GrowthCapableMobs.put(21505, temp);        
	}
	
    public void spawnNext(L2NpcInstance npc, int growthLevel, L2PcInstance player, int food)
    {
        int npcId = npc.getNpcId();
        int nextNpcId = 0;

        // find the next mob to spawn, based on the current npcId, growthlevel, and food.
        if (growthLevel == 2)
        {
            // if tamed, the mob that will spawn depends on the class type (fighter/mage) of the player!
            if (Rnd.get(2) == 0)
            {
                if (player.getClassId().isMage())
                {
                    nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 1, 1);
                }
                else
                {
                    nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 1, 0);
                }
            }  
            else
            {
                // if not tamed, there is a small chance that have "mad cow" disease.
                // that is a stronger-than-normal animal that attacks its feeder
                if (Rnd.get(5) == 0)
                {
                    nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 0, 1);
                }
                else
                {
                    nextNpcId = _GrowthCapableMobs.get(npcId).getMob(food, 0, 0);
                }
            }
        }
        else
        {
            // all other levels of growth are straight-forward
        	nextNpcId = _GrowthCapableMobs.get(npcId).getRandomMob(food);
        }
        
        // remove the feedinfo of the mob that got despawned, if any
        if (_FeedInfo.containsKey(npc.getObjectId())) 
        {
            if (_FeedInfo.get(npc.getObjectId()) == player.getObjectId())
                _FeedInfo.remove(npc.getObjectId());
        }
        // despawn the old mob
        if (_GrowthCapableMobs.get(npcId).getGrowthLevel() == 0)
        {
            npc.onDecay();
        }
        else
        {
            npc.deleteMe();
        }
        
        // if this is finally a trained mob, then despawn any other trained mobs that the
        // player might have and initialize the Tamed Beast.
        if (contains(TAMED_BEASTS,nextNpcId))
        {
            L2TamedBeastInstance oldTrained = player.getTrainedBeast();
            if (oldTrained != null)
            {
                oldTrained.doDespawn();
            }
                
            L2NpcTemplate template = NpcTable.getInstance().getTemplate(nextNpcId);
            L2TamedBeastInstance nextNpc = new L2TamedBeastInstance(IdFactory.getInstance().getNextId(), template, player, FOODSKILL.get(food), npc.getX(), npc.getY(), npc.getZ());
            nextNpc.setRunning();

            int objectId = nextNpc.getObjectId();
            
            QuestState st = player.getQuestState("20_BringUpWithLove");
            if (st != null)
            {
                if (Rnd.get(100) <= 5 && st.getQuestItemsCount(7185) == 0)
                {
                	//if player has quest 20 going, give quest item
                	//it's easier to hardcode it in here than to try and repeat this stuff in the quest
                    st.giveItems(7185,1);
                    st.set("cond","2");		
                }
            }
            // also, perform a rare random chat
            int rand = Rnd.get(20);
            if (rand == 0)
            {
            	npc.broadcastPacket(new NpcSay(objectId,0,nextNpc.getNpcId(), player.getName()+", will you show me your hideaway?"));
            }
            else if (rand == 1)
            {
            	npc.broadcastPacket(new NpcSay(objectId,0,nextNpc.getNpcId(), player.getName()+", whenever I look at spice, I think about you."));
            }
            else if (rand == 2)
            {
            	npc.broadcastPacket(new NpcSay(objectId,0,nextNpc.getNpcId(), player.getName()+", you do not need to return to the village.  I will give you strength"));
            }
            else if (rand == 3)
            {
            	npc.broadcastPacket(new NpcSay(objectId,0,nextNpc.getNpcId(), "Thanks, "+player.getName()+".  I hope I can help you"));
            }
            else if (rand == 4)
            {
            	npc.broadcastPacket(new NpcSay(objectId,0,nextNpc.getNpcId(), player.getName()+", what can I do to help you?"));
            }
        }
        else
        {
            // if not trained, the newly spawned mob will automatically be agro against its feeder
            // (what happened to "never bite the hand that feeds you" anyway?!)
            L2Attackable nextNpc = (L2Attackable) this.addSpawn(nextNpcId,npc);

            if (MAD_COW_POLYMORPH.containsKey(nextNpcId))
            {
                this.startQuestTimer("polymorph Mad Cow", 10000, nextNpc, player);
            }
            
            // register the player in the feedinfo for the mob that just spawned
            _FeedInfo.put(nextNpc.getObjectId(),player.getObjectId());
            nextNpc.setRunning();
            nextNpc.addDamageHate(player,0,99999);
            nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
        }
    }

	public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
	{
        if (event.equalsIgnoreCase("polymorph Mad Cow") && npc != null && player != null)
        {
        	if( MAD_COW_POLYMORPH.containsKey(npc.getNpcId()))
        	{
                // remove the feed info from the previous mob
        		if (_FeedInfo.get(npc.getObjectId()) == player.getObjectId())
        		{
        			_FeedInfo.remove(npc.getObjectId());
        		}
                // despawn the mad cow
                npc.deleteMe();
                // spawn the new mob 
                L2Attackable nextNpc = (L2Attackable) this.addSpawn(MAD_COW_POLYMORPH.get(npc.getNpcId()),npc);
                
                // register the player in the feedinfo for the mob that just spawned
                _FeedInfo.put(nextNpc.getObjectId(), player.getObjectId());
                nextNpc.setRunning();
                nextNpc.addDamageHate(player,0,99999);
                nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
        	}
        }
		return super.onAdvEvent(event,npc,player);
	}

	public String onSkillUse (L2NpcInstance npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
        // this behavior is only run when the target of skill is the passed npc (chest)
        // i.e. when the player is attempting to open the chest using a skill
        if (!contains(targets,npc))
        {
        	return super.onSkillUse(npc,caster,skill,targets,isPet);
        }
        // gather some values on local variables
        int npcId = npc.getNpcId();
        int skillId = skill.getId();
        // check if the npc and skills used are valid for this script.  Exit if invalid.
        if (!contains(FEEDABLE_BEASTS,npcId) || (skillId !=SKILL_GOLDEN_SPICE && skillId != SKILL_CRYSTAL_SPICE))
        {
        	return super.onSkillUse(npc,caster,skill,targets,isPet);
        }

        // first gather some values on local variables
        int objectId = npc.getObjectId();
        int growthLevel = 3;  // if a mob is in FEEDABLE_BEASTS but not in _GrowthCapableMobs, then it's at max growth (3)
        if (_GrowthCapableMobs.containsKey(npcId))
        {
            growthLevel = _GrowthCapableMobs.get(npcId).getGrowthLevel();
        }

        // prevent exploit which allows 2 players to simultaneously raise the same 0-growth beast
        // If the mob is at 0th level (when it still listens to all feeders) lock it to the first feeder!       
        if (growthLevel==0 && _FeedInfo.containsKey(objectId))
        {
        	return super.onSkillUse(npc,caster,skill,targets,isPet);
        }
        else
        {
            _FeedInfo.put(objectId,caster.getObjectId());
        }

        int food = 0;
        if (skillId == SKILL_GOLDEN_SPICE)
        {
            food = GOLDEN_SPICE;
        }
        else if (skillId == SKILL_CRYSTAL_SPICE)
        {
            food = CRYSTAL_SPICE;
        }

        // display the social action of the beast eating the food.
        npc.broadcastPacket(new SocialAction(objectId,2));

        // if this pet can't grow, it's all done.
        if (_GrowthCapableMobs.containsKey(npcId))
        {
            // do nothing if this mob doesn't eat the specified food (food gets consumed but has no effect).
            if (_GrowthCapableMobs.get(npcId).getMob(food, 0, 0) == null )
            {
            	return super.onSkillUse(npc,caster,skill,targets,isPet);
            }

            // rare random talk...
            if (Rnd.get(20) == 0 )
            {
                npc.broadcastPacket(new NpcSay(objectId,0,npc.getNpcId(),TEXT[growthLevel][Rnd.get(TEXT[growthLevel].length)]));
            }

            if (growthLevel > 0 && _FeedInfo.get(objectId) != caster.getObjectId())
            {
                // check if this is the same player as the one who raised it from growth 0.
                // if no, then do not allow a chance to raise the pet (food gets consumed but has no effect).
                return super.onSkillUse(npc,caster,skill,targets,isPet);
            }

            // Polymorph the mob, with a certain chance, given its current growth level
            if (Rnd.get(100) < _GrowthCapableMobs.get(npcId).getChance())
            {
                this.spawnNext(npc, growthLevel,caster,food);
            }
        }
        else if (contains(TAMED_BEASTS,npcId) && npc instanceof L2TamedBeastInstance)
        {
        	L2TamedBeastInstance beast = ((L2TamedBeastInstance)npc);
            if (skillId == beast.getFoodType())
            {
                beast.onReceiveFood();
                beast.broadcastPacket(new NpcSay(objectId,0,npcId,TAMED_TEXT[Rnd.get(TAMED_TEXT.length)]));
            }
        }
        return super.onSkillUse(npc,caster,skill,targets,isPet);
	}

	public String onKill (L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
        // remove the feedinfo of the mob that got killed, if any
        if (_FeedInfo.containsKey(npc.getObjectId()))
        {
            _FeedInfo.remove(npc.getObjectId());
        }
        return super.onKill(npc,killer,isPet);
	}
	
    public static void main(String[] args)
    {
		// now call the constructor (starts up the ai)
		new FeedableBeasts(-1,"feedable_beasts","ai");
    }
}