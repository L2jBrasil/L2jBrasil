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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.Config;
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.SevenSignsFestival;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Seven Signs Record Update
 *
 * packet type id 0xf5
 * format:
 *
 * c cc	(Page Num = 1 -> 4, period)
 *
 * 1: [ddd cc dd ddd c ddd c]
 * 2: [hc [cd (dc (S))]
 * 3: [ccc (cccc)]
 * 4: [(cchh)]
 *
 * @author Tempy
 *
 */
public class SSQStatus extends L2GameServerPacket
{
    private static Logger _log = LoggerFactory.getLogger(SSQStatus.class);

    private static final String _S__F5_SSQStatus = "[S] F5 RecordUpdate";
    private L2PcInstance _activevChar;
    private int _page;

    public SSQStatus(L2PcInstance player, int recordPage)
    {
    	_activevChar = player;
        _page = recordPage;
    }


	@Override
	protected final void writeImpl()
    {
    	int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
    	int totalDawnMembers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DAWN);
    	int totalDuskMembers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DUSK);

        writeC(0xf5);

        writeC(_page);
        writeC(SevenSigns.getInstance().getCurrentPeriod()); // current period?

        int dawnPercent = 0;
        int duskPercent = 0;

        switch (_page)
        {
            case 1:
                // [ddd cc dd ddd c ddd c]
                writeD(SevenSigns.getInstance().getCurrentCycle());

                int currentPeriod = SevenSigns.getInstance().getCurrentPeriod();

                switch (currentPeriod)
                {
                    case SevenSigns.PERIOD_COMP_RECRUITING:
                        writeD(SystemMessageId.INITIAL_PERIOD.getId());
                        break;
                    case SevenSigns.PERIOD_COMPETITION:
                        writeD(SystemMessageId.QUEST_EVENT_PERIOD.getId());
                        break;
                    case SevenSigns.PERIOD_COMP_RESULTS:
                        writeD(SystemMessageId.RESULTS_PERIOD.getId());
                        break;
                    case SevenSigns.PERIOD_SEAL_VALIDATION:
                        writeD(SystemMessageId.VALIDATION_PERIOD.getId());
                        break;
                }

                switch (currentPeriod)
                {
                    case SevenSigns.PERIOD_COMP_RECRUITING:
                    case SevenSigns.PERIOD_COMP_RESULTS:
                		writeD(SystemMessageId.UNTIL_TODAY_6PM.getId());
                        break;
                    case SevenSigns.PERIOD_COMPETITION:
                    case SevenSigns.PERIOD_SEAL_VALIDATION:
                		writeD(SystemMessageId.UNTIL_MONDAY_6PM.getId());
                        break;
                }

                writeC(SevenSigns.getInstance().getPlayerCabal(_activevChar));
                writeC(SevenSigns.getInstance().getPlayerSeal(_activevChar));

                writeD(SevenSigns.getInstance().getPlayerStoneContrib(_activevChar)); // Seal Stones Turned-In
                writeD(SevenSigns.getInstance().getPlayerAdenaCollect(_activevChar)); // Ancient Adena to Collect

                double dawnStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DAWN);
                int dawnFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DAWN);

                double duskStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DUSK);
                int duskFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DUSK);

                double totalStoneScore = duskStoneScore + dawnStoneScore;

                /*
                 * Scoring seems to be proportionate to a set base value, so base this on
                 * the maximum obtainable score from festivals, which is 500.
                 */
                int duskStoneScoreProp = 0;
                int dawnStoneScoreProp = 0;

                if (totalStoneScore != 0)
                {
	                duskStoneScoreProp = Math.round(((float)duskStoneScore / (float)totalStoneScore) * 500);
	                dawnStoneScoreProp = Math.round(((float)dawnStoneScore / (float)totalStoneScore) * 500);
                }

                int duskTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DUSK);
                int dawnTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DAWN);

                int totalOverallScore = duskTotalScore + dawnTotalScore;

                if (totalOverallScore != 0)
                {
	                dawnPercent = Math.round(((float)dawnTotalScore / (float)totalOverallScore) * 100);
	                duskPercent = Math.round(((float)duskTotalScore / (float)totalOverallScore) * 100);
                }

                if (Config.DEBUG) {
                    _log.info("Dusk Stone Score: " + duskStoneScore + " - Dawn Stone Score: "
                              + dawnStoneScore);
                    _log.info("Dusk Festival Score: " + duskFestivalScore + " - Dawn Festival Score: "
                              + dawnFestivalScore);
                    _log.info("Dusk Score: " + duskTotalScore + " - Dawn Score: " + dawnTotalScore);
                    _log.info("Overall Score: " + totalOverallScore);
                    _log.info("");
                    if (totalStoneScore == 0)
                    	_log.info("Dusk Prop: 0 - Dawn Prop: 0");
                    else
                    	_log.info("Dusk Prop: " + ((duskStoneScore / totalStoneScore) * 500) + " - Dawn Prop: "
                                  + ((dawnStoneScore / totalStoneScore) * 500));
                    _log.info("Dusk %: " + duskPercent + " - Dawn %: " + dawnPercent);
                }

                /* DUSK */
                writeD(duskStoneScoreProp); // Seal Stone Score
                writeD(duskFestivalScore); // Festival Score
                writeD(duskTotalScore); // Total Score

                writeC(duskPercent); // Dusk %

                /* DAWN */
                writeD(dawnStoneScoreProp); // Seal Stone Score
                writeD(dawnFestivalScore); // Festival Score
                writeD(dawnTotalScore); // Total Score

                writeC(dawnPercent); // Dawn %
                break;
            case 2:
                // c cc hc [cd (dc (S))]
                writeH(1);

                writeC(5); // Total number of festivals

                for (int i = 0; i < 5; i++)
                {
                    writeC(i + 1); // Current client-side festival ID
                    writeD(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i]);

                    int duskScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DUSK, i);
                    int dawnScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DAWN, i);

                    // Dusk Score \\
                    writeD(duskScore);

                    StatsSet highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DUSK, i);
                    String[] partyMembers = highScoreData.getString("members").split(",");

                    if (partyMembers != null)
                    {
                        writeC(partyMembers.length);

                        for (String partyMember : partyMembers)
                            writeS(partyMember);
                    }
                    else
                    {
                        writeC(0);
                    }

                    // Dawn Score \\
                    writeD(dawnScore);

                    highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DAWN, i);
                    partyMembers = highScoreData.getString("members").split(",");

                    if (partyMembers != null)
                    {
                        writeC(partyMembers.length);

                        for (String partyMember : partyMembers)
                            writeS(partyMember);
                    }
                    else
                    {
                        writeC(0);
                    }
                }
                break;
            case 3:
                // c cc [ccc (cccc)]
                writeC(10); // Minimum limit for winning cabal to retain their seal
                writeC(35); // Minimum limit for winning cabal to claim a seal
                writeC(3); // Total number of seals

                for (int i = 1; i < 4; i++)
                {
                    int dawnProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
                    int duskProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);

                    if (Config.DEBUG)
                        _log.info(SevenSigns.getSealName(i, true) + " = Dawn Prop: " + dawnProportion + "("
                        + ((dawnProportion / totalDawnMembers) * 100) + "%)" + ", Dusk Prop: "
                        + duskProportion + "(" + ((duskProportion / totalDuskMembers) * 100) + "%)");

                    writeC(i);
                    writeC(SevenSigns.getInstance().getSealOwner(i));

                    if (totalDuskMembers == 0)
                    {
                    	if (totalDawnMembers == 0)
                    	{
                    		writeC(0);
                    		writeC(0);
                    	}
                    	else
                    	{
                    		writeC(0);
                    		writeC(Math.round(((float)dawnProportion / (float)totalDawnMembers) * 100));
                    	}
                    }
                    else
                    {
                    	if (totalDawnMembers == 0)
                    	{
                    		writeC(Math.round(((float)duskProportion / (float)totalDuskMembers) * 100));
                    		writeC(0);
                    	}
                    	else
                    	{
                    		writeC(Math.round(((float)duskProportion / (float)totalDuskMembers) * 100));
                    		writeC(Math.round(((float)dawnProportion / (float)totalDawnMembers) * 100));
                    	}
                    }
                }
                break;
            case 4:
                // c cc [cc (cchh)]
                writeC(winningCabal); // Overall predicted winner
                writeC(3); // Total number of seals

                for (int i = 1; i < 4; i++)
                {
                    int dawnProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
                    int duskProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);
                    dawnPercent = Math.round((dawnProportion / (totalDawnMembers == 0 ? 1 : (float)totalDawnMembers)) * 100);
                    duskPercent = Math.round((duskProportion / (totalDuskMembers == 0 ? 1 : (float)totalDuskMembers)) * 100);
                    int sealOwner = SevenSigns.getInstance().getSealOwner(i);

                    writeC(i);

                    switch (sealOwner)
                    {
                    	case SevenSigns.CABAL_NULL:
                    		switch(winningCabal)
                    		{
                    			case SevenSigns.CABAL_NULL:
                    				writeC(SevenSigns.CABAL_NULL);
                    				writeH(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
                    				break;
                    			case SevenSigns.CABAL_DAWN:
                    				if (dawnPercent >= 35)
                    				{
                    					writeC(SevenSigns.CABAL_DAWN);
                    					writeH(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
                    				}
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_NOT_OWNED_35_LESS_VOTED.getId());
                    				}
                    				break;
                    			case SevenSigns.CABAL_DUSK:
                    				if (duskPercent >= 35)
                    				{
                    					writeC(SevenSigns.CABAL_DUSK);
                    					writeH(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
                    				}
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_NOT_OWNED_35_LESS_VOTED.getId());
                    				}
                    				break;
                    		}
                    		break;
                    	case SevenSigns.CABAL_DAWN:
                    		switch(winningCabal)
                    		{
                    			case SevenSigns.CABAL_NULL:
                                    if (dawnPercent >= 10)
                                    {
                                        writeC(SevenSigns.CABAL_DAWN);
                                        writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                                        break;
                                    }
                                    else
                                    {
                                        writeC(SevenSigns.CABAL_NULL);
                                        writeH(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
                                        break;
                                    }
                    			case SevenSigns.CABAL_DAWN:
                    				if (dawnPercent >= 10)
                    				{
                    					writeC(sealOwner);
                    					writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                    				}
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
                    				}
                    				break;
                    			case SevenSigns.CABAL_DUSK:
                    				if (duskPercent >= 35)
                    				{
                    					writeC(SevenSigns.CABAL_DUSK);
                    					writeH(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
                    				}
                                    else if (dawnPercent >= 10)
                                    {
                                        writeC(SevenSigns.CABAL_DAWN);
                                        writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                                    }
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
                    				}
                    				break;
                    		}
                    		break;
                    	case SevenSigns.CABAL_DUSK:
                    		switch(winningCabal)
                    		{
                    			case SevenSigns.CABAL_NULL:
                                    if (duskPercent >= 10)
                                    {
                                        writeC(SevenSigns.CABAL_DUSK);
                                        writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                                        break;
                                    }
                                    else
                                    {
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.COMPETITION_TIE_SEAL_NOT_AWARDED.getId());
                        				break;
                                    }
                    			case SevenSigns.CABAL_DAWN:
                    				if (dawnPercent >= 35)
                    				{
                    					writeC(SevenSigns.CABAL_DAWN);
                    					writeH(SystemMessageId.SEAL_NOT_OWNED_35_MORE_VOTED.getId());
                    				}
                    				else if (duskPercent >= 10)
                    				{
                    					writeC(sealOwner);
                    					writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                    				}
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
                    				}
                    				break;
                    			case SevenSigns.CABAL_DUSK:
                    				if (duskPercent >= 10)
                    				{
                    					writeC(sealOwner);
                    					writeH(SystemMessageId.SEAL_OWNED_10_MORE_VOTED.getId());
                    				}
                    				else
                    				{
                        				writeC(SevenSigns.CABAL_NULL);
                        				writeH(SystemMessageId.SEAL_OWNED_10_LESS_VOTED.getId());
                    				}
                    				break;
                    		}
                    		break;
                    }
                    writeH(0);
                }
                break;
        }
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__F5_SSQStatus;
    }
}