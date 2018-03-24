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
package com.it.br.gameserver.instancemanager;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Couple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author evill33t
 */
public class CoupleManager {
    private static final Logger _log = LoggerFactory.getLogger(CoupleManager.class);

    private static CoupleManager _instance;

    public static final CoupleManager getInstance() {
        if (_instance == null) {
            _instance = new CoupleManager();
            _instance.load();
        }
        return _instance;
    }

    private List<Couple> _couples;

    public void reload() {
        getCouples().clear();
        load();
    }

    private final void load() {
        Connection con = null;
        try {
            PreparedStatement statement;
            ResultSet rs;

            con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("Select id from mods_wedding order by id");
            rs = statement.executeQuery();

            while (rs.next()) {
                getCouples().add(new Couple(rs.getInt("id")));
            }

            statement.close();

            _log.info("Loaded: {} couple(s)", getCouples().size());
        } catch (Exception e) {
            _log.error(e.getMessage(), e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    // =========================================================
    // Property - Public
    public final Couple getCouple(int coupleId) {
        int index = getCoupleIndex(coupleId);
        if (index >= 0) return getCouples().get(index);
        return null;
    }

    public void createCouple(L2PcInstance player1, L2PcInstance player2) {
        if (player1 != null && player2 != null) {
            if (player1.getPartnerId() == 0 && player2.getPartnerId() == 0) {
                int _player1id = player1.getObjectId();
                int _player2id = player2.getObjectId();

                Couple _new = new Couple(player1, player2);
                getCouples().add(_new);
                player1.setPartnerId(_player2id);
                player2.setPartnerId(_player1id);
                player1.setCoupleId(_new.getId());
                player2.setCoupleId(_new.getId());
            }
        }
    }

    public void deleteCouple(int coupleId) {
        int index = getCoupleIndex(coupleId);
        Couple couple = getCouples().get(index);
        if (couple != null) {
            L2PcInstance player1 = (L2PcInstance) L2World.getInstance().findObject(couple.getPlayer1Id());
            L2PcInstance player2 = (L2PcInstance) L2World.getInstance().findObject(couple.getPlayer2Id());
            L2ItemInstance item = null;
            if (player1 != null) {
                player1.setPartnerId(0);
                player1.setMarried(false);
                player1.setCoupleId(0);
                item = player1.getInventory().getItemByItemId(9140);
                if (player1.isOnline() == 1 && item != null) {
                    player1.destroyItem("Removing Cupids Bow", item, player1, true);
                    player1.getInventory().updateDatabase();
                }
                if (player1.isOnline() == 0 && item != null) {
                    Integer PlayerId = player1.getObjectId();
                    Integer ItemId = 9140;
                    Connection con = null;
                    try {
                        con = L2DatabaseFactory.getInstance().getConnection();
                        PreparedStatement statement = con.prepareStatement("delete from items where owner_id = ? and item_id = ?");
                        statement.setInt(1, PlayerId);
                        statement.setInt(2, ItemId);
                        statement.execute();
                        statement.close();
                    } catch (Exception e) {
                    } finally {
                        try {
                            con.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if (player2 != null) {
                player2.setPartnerId(0);
                player2.setMarried(false);
                player2.setCoupleId(0);
                item = player2.getInventory().getItemByItemId(9140);
                if (player2.isOnline() == 1 && item != null) {
                    player2.destroyItem("Removing Cupids Bow", item, player2, true);
                    player2.getInventory().updateDatabase();
                }
                if (player2.isOnline() == 0 && item != null) {
                    Integer Player2Id = player2.getObjectId();
                    Integer ItemId = 9140;
                    Connection con = null;
                    try {
                        con = L2DatabaseFactory.getInstance().getConnection();
                        PreparedStatement statement = con.prepareStatement("delete from items where owner_id = ? and item_id = ?");
                        statement.setInt(1, Player2Id);
                        statement.setInt(2, ItemId);
                        statement.execute();
                        statement.close();
                    } catch (Exception e) {
                    } finally {
                        try {
                            con.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            couple.divorce();
            getCouples().remove(index);
        }
    }

    public final int getCoupleIndex(int coupleId) {
        int i = 0;
        for (Couple temp : getCouples()) {
            if (temp != null && temp.getId() == coupleId) return i;
            i++;
        }
        return -1;
    }

    public final List<Couple> getCouples() {
        if (_couples == null) _couples = new ArrayList<>();
        return _couples;
    }
}
