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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.datatables.sql.CharNameTable;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.CharTemplateTable;
import com.it.br.gameserver.datatables.xml.SkillTreeTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.QuestManager;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2ShortCut;
import com.it.br.gameserver.model.L2SkillLearn;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.serverpackets.CharCreateFail;
import com.it.br.gameserver.network.serverpackets.CharCreateOk;
import com.it.br.gameserver.network.serverpackets.CharSelectInfo;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.templates.L2PcTemplate;
import com.it.br.gameserver.templates.L2PcTemplate.PcTemplateItem;
import com.it.br.gameserver.util.Util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * This class ...
 *
 * @version $Revision: 1.9.2.3.2.8 $ $Date: 2005/03/27 15:29:30 $
 */
@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	private static final String _C__0B_CHARACTERCREATE = "[C] 0B CharacterCreate";
	//private static Logger _log = LoggerFactory.getLogger(CharacterCreate.class);

	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	private long _exp;
	
	@Override
	protected void readImpl()
	{
		_name      = readS();
		_race      = readD();
		_sex       = (byte)readD();
		_classId   = readD();
		_int       = readD();
		_str       = readD();
		_con       = readD();
		_men       = readD();
		_dex       = readD();
		_wit       = readD();
		_hairStyle = (byte)readD();
		_hairColor = (byte)readD();
		_face      = (byte)readD();
		_exp	   = 25314000000L;
	}

	@Override
	protected void runImpl()
	{
        if ((_name.length() < 3) || (_name.length() > 16) || !Util.isAlphaNumeric(_name) || !isValidName(_name))
		{
		if (Config.DEBUG)
			_log.debug("charname: " + _name + " is invalid. creation failed.");
		CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS);
		sendPacket(ccf);
		return;
	    }
        
        for(String st : getSettings(L2JBrasilSettings.class).getForbiddenNames()) {
        	if(_name.toLowerCase().contains(st.toLowerCase()))
        	{
        		sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
        		return;
        	}
        }
		
        L2PcInstance newChar = null;
		L2PcTemplate template = null;
		
		/*
		* Since checks for duplicate names are done using SQL,
		* lock must be held until data is written to DB as well.
		*/
		synchronized (CharNameTable.getInstance()) {
			ServerSettings serverSettings = getSettings(ServerSettings.class);
			int maxCharacters = serverSettings.getCharacterMaxCount();
        if (CharNameTable.getInstance().accountCharNumber(getClient().getAccountName()) >= maxCharacters
			        && maxCharacters != 0)
			{
				if (Config.DEBUG)
					_log.debug("Max number of characters reached. Creation failed.");
				CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
				sendPacket(ccf);
				return;
			}
			else if (CharNameTable.getInstance().doesCharNameExist(_name))
 			{
				if (Config.DEBUG)
					_log.debug("charname: " + _name + " already exists. creation failed.");
				CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
				sendPacket(ccf);
				return;
			}
			
			template = CharTemplateTable.getInstance().getTemplate(_classId);
			
			if (Config.DEBUG)
				_log.debug("charname: " + _name + " classId: " + _classId + " template: " + template);
			
			if (template == null || template.classBaseLevel > 1)
			{
				CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED);
				sendPacket(ccf);
				return;
			}
			
			int objectId = IdFactory.getInstance().getNextId();
			newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, _sex != 0);
		}
		
		newChar.setCurrentHp(template.baseHpMax);
		newChar.setCurrentCp(template.baseCpMax);
		newChar.setCurrentMp(template.baseMpMax);
		//newChar.setMaxLoad(template.baseLoad);

		// send acknowledgement
		CharCreateOk cco = new CharCreateOk();
		sendPacket(cco);

		initNewChar(getClient(), newChar);
	}

    private boolean isValidName(String text)
    {
            boolean result = true;
            String test = text;
            Pattern pattern;
            try
            {
            	ServerSettings serverSettings = getSettings(ServerSettings.class);
                pattern = Pattern.compile(serverSettings.getCharacterNameTemplate());
            }
            catch (PatternSyntaxException e) // case of illegal pattern
            {
            	_log.warn("ERROR : Character name pattern of config is wrong!");
                pattern = Pattern.compile(".*");
            }
            Matcher regexp = pattern.matcher(test);
            if (!regexp.matches())
            {
                    result = false;
            }
            return result;
    }

	private void initNewChar(L2GameClient client, L2PcInstance newChar) {
		if (Config.DEBUG)
			_log.debug("Character init start");
		L2World.getInstance().storeObject(newChar);

		L2PcTemplate template = newChar.getTemplate();
		
		L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
		
		newChar.addAdena("Init", l2jBrasilSettings.getStartingAdena(), null, false);
		newChar.addItem("Init", l2jBrasilSettings.getStartingGBId(), l2jBrasilSettings.getStartingGBCount(), null, false);
		
		
		int levelOnEnter = l2jBrasilSettings.getLevelOnEnter();
		
		if (levelOnEnter > 1) {
			newChar.getStat().addLevel((byte) (levelOnEnter - 1));
		}
		
		
		if (l2jBrasilSettings.getsPOnEnter() > 0) {
			newChar.getStat().addSp(l2jBrasilSettings.getsPOnEnter());
		}
		
		if (l2jBrasilSettings.isCustomStarterItemsEnabled()) {
			Map<Integer, Integer> startItens;
			if (newChar.isMageClass()) {
				startItens = l2jBrasilSettings.getStartingItemsMage();
			} else {
				startItens = l2jBrasilSettings.getStartingItemsFighter();
			}
			
			for(Entry<Integer, Integer> item : startItens.entrySet()) {
				if (ItemTable.getInstance().createDummyItem(item.getKey()).isStackable()) {
					newChar.getInventory().addItem("Starter Items", item.getKey(), item.getValue(), newChar, null);
				} else {
					for (int i = 0; i < item.getValue(); ++i) {
						newChar.getInventory().addItem("Starter Items", item.getKey(), 1, newChar, null);
					}
				}
			}
		}
		
		if (l2jBrasilSettings.isCustomSpawnEnabled()) {
			newChar.setXYZInvisible(l2jBrasilSettings.getCustomSpawnX(), l2jBrasilSettings.getCustomSpawnY(), l2jBrasilSettings.getCustomSpawnZ());
		} else
			newChar.setXYZInvisible(template.spawnX, template.spawnY, template.spawnZ);

		if (l2jBrasilSettings.isNewCharTitleEnabled()) {
			newChar.setTitle(l2jBrasilSettings.getCharTitle());
		} else
			newChar.setTitle("");

		L2ShortCut shortcut;
		// add attack shortcut
		shortcut = new L2ShortCut(0, 0, 3, 2, -1, 1);
		newChar.registerShortCut(shortcut);
		// add take shortcut
		shortcut = new L2ShortCut(3, 0, 3, 5, -1, 1);
		newChar.registerShortCut(shortcut);
		// add sit shortcut
		shortcut = new L2ShortCut(10, 0, 3, 0, -1, 1);
		newChar.registerShortCut(shortcut);

		ItemTable itemTable = ItemTable.getInstance();
		for (PcTemplateItem ia : template.getItems()) {
			L2ItemInstance item = newChar.getInventory().addItem("Init", ia.getItemId(), ia.getAmount(), newChar, null);
			if (item.getItemId() == 5588) {
				// add tutbook shortcut
				shortcut = new L2ShortCut(11, 0, 1, item.getObjectId(), -1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (item.isEquipable()) {
				if (newChar.getActiveWeaponItem() == null || !(item.getItem().getType2() != L2Item.TYPE2_WEAPON))
					newChar.getInventory().equipItemAndRecord(item);
			}
		}

		L2SkillLearn[] startSkills = SkillTreeTable.getInstance().getAvailableSkills(newChar, newChar.getClassId());
		for (int i = 0; i < startSkills.length; i++) {
			newChar.addSkill(SkillTable.getInstance().getInfo(startSkills[i].getId(), startSkills[i].getLevel()), true);
			if (startSkills[i].getId() == 1001 || startSkills[i].getId() == 1177) {
				shortcut = new L2ShortCut(1, 0, 2, startSkills[i].getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (startSkills[i].getId() == 1216) {
				shortcut = new L2ShortCut(10, 0, 2, startSkills[i].getId(), 1, 1);
				newChar.registerShortCut(shortcut);
			}
			if (Config.DEBUG)
				_log.debug("adding starter skill:" + startSkills[i].getId() + " / " + startSkills[i].getLevel());
		}

		if (!Config.ALT_DEV_NO_TUTORIAL)
			startTutorialQuest(newChar);

		L2GameClient.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory
		L2World.getInstance().removeObject(newChar);

		// send char list
		CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.getConnection().sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
		if (Config.DEBUG)
			_log.debug("Character init end");
	}
 
        public void startTutorialQuest(L2PcInstance player) 
        { 
	        QuestState qs = player.getQuestState("255_Tutorial"); 
	        Quest q = null; 
        if (qs == null) 
	        q = QuestManager.getInstance().getQuest("255_Tutorial"); 
	        if (q != null) 
	            q.newQuestState(player); 
	}

	@Override
	public String getType()
	{
		return _C__0B_CHARACTERCREATE;
	}
}
