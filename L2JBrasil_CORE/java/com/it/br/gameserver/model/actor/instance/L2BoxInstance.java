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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class L2BoxInstance extends L2NpcInstance {

	@SuppressWarnings("rawtypes")
	private class L2BoxItem implements Comparable {
		public int itemid;
		public int id;
		public int count;
		@SuppressWarnings("unused")
		public int enchant;
		public String name;
		
		@SuppressWarnings("unused")
		public L2BoxItem()
		{
			//
		}
		public L2BoxItem(int _itemid, int _count, String _name, int _id, int _enchant)
		{
			itemid = _itemid;
			count = _count;
			name = _name;
			id = _id;
			enchant = _enchant;
		}
	
		public int compareTo(Object o)
		{
			int r = name.compareToIgnoreCase(((L2BoxItem)o).name);
			if (r != 0)
				return r;
			if (id < ((L2BoxItem)o).id)
				return -1;
			return 1;
		}
	}

    //private static Logger _log = Logger.getLogger(L2BoxInstance.class.getName());
	private static final int MAX_ITEMS_PER_PAGE = 25;
	private static final String INSERT_GRANT = "INSERT INTO boxaccess (charname,spawn) VALUES(?,?)";
	private static final String DELETE_GRANT = "DELETE FROM boxaccess WHERE charname=? AND spawn=?";
	private static final String LIST_GRANT = "SELECT charname FROM boxaccess WHERE spawn=?";
    private static final String VARIABLE_PREFIX = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public L2BoxInstance(int objectId, L2NpcTemplate _template)
	{
		super(objectId, _template);
	}


	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		String playerName = player.getName();
		boolean access = hasAccess(playerName);

		if (command.startsWith("Withdraw"))
		{
			if (access)
				showWithdrawWindow(player, command.substring(9));
		}
		else if (command.startsWith("Deposit"))
		{
			if (access)
				showDepositWindow(player, command.substring(8));
		}
		else if (command.startsWith("InBox"))
		{
			if (access)
				putInBox(player, command.substring(6));
		}
		else if (command.startsWith("OutBox"))
		{
			if (access)
				takeOutBox(player, command.substring(7));
		}
		else super.onBypassFeedback(player,command);
		//else _log.info("onBypassFeedback unknown command "+command);
	}


	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}


	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		return "data/html/custom/" + pom + ".htm";
	}

	public boolean hasAccess(String player)
	{
		Connection con = null;
		boolean result = false;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement("SELECT spawn, charname FROM boxaccess WHERE charname=? AND spawn=?");
			st.setString(1, player);
			st.setInt(2, getSpawn().getId());
			ResultSet rs = st.executeQuery();
			if (rs.next())
				result = true;
			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			_log.info("hasAccess failed: "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List getAccess()
	{
		Connection con = null;
		List<String> acl = new ArrayList<>();
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(LIST_GRANT);
			st.setInt(1, getSpawn().getId());
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				acl.add(rs.getString("charname"));
			}
			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			_log.info("getAccess failed: "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) { }
		}
		return acl;
	}

	public boolean grantAccess(String player, boolean what)
	{
		Connection con = null;
		boolean result = false;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			String _query;
			if (what)
				_query = INSERT_GRANT;
			else
				_query = DELETE_GRANT;

			PreparedStatement st = con.prepareStatement(_query);
			st.setString(1, player);
			st.setInt(2, getSpawn().getId());
			st.execute();
			st.close();
		}
		catch (Exception e)
		{
			result = false;
		}
		finally
		{
			try { con.close(); } catch (Exception e) { }
		}
		return result;
	}

	private void showWithdrawWindow(L2PcInstance player, String command)
	{
		String drawername = "trash";
		if (command == null)
			return;
		String[] cmd = command.split(" ");
		int startPos = 0;
		if (cmd != null)
			drawername = cmd[0];
		if (cmd.length>1)
			startPos = Integer.parseInt(cmd[1]);

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int nitems = 0;
		Set<L2BoxItem> _items = getItems(drawername);
		if (startPos >= _items.size())
			startPos = 0;
		String button = "<button value=\"Withdraw\" width=80 height=15 action=\"bypass -h npc_"+getObjectId()+"_OutBox "+drawername;
		String next = "<button value=\"next\" width=50 height=15 action=\"bypass -h npc_"+getObjectId()+"_Withdraw "+drawername+" "+(startPos+MAX_ITEMS_PER_PAGE)+"\">";
		String back = "<button value=\"back\" width=50 height=15 action=\"bypass -h npc_"+getObjectId()+"_Chat 0\">";
		String content = "<html><body>Drawer "+drawername+":<br>"+next+" "+back+"<table width=\"100%\">";
		content += "<tr><td>Item</td><td>Count</td><td>Withdraw</td></tr>";
		for (L2BoxItem i : _items)
		{
			nitems++;
			if (nitems < startPos)
				continue;
			String varname = VARIABLE_PREFIX.charAt(nitems-startPos)+String.valueOf(i.itemid);
			content += "<tr><td>"+i.name+"</td><td align=\"right\">"+i.count+"</td>";
			content += "<td><edit var=\""+varname+"\" width=30></td></tr>";
			button += " ,"+varname+" $"+varname;
			if ((nitems - startPos) >= MAX_ITEMS_PER_PAGE)
				break;
		}
		button += "\">";
		content += "</table><br>"+button+"</body></html>";
		_log.fine("setHtml("+content+"); items="+nitems);
		html.setHtml(content);
		player.sendPacket(html);
		player.sendPacket(new ActionFailed());
	}

	private void showDepositWindow(L2PcInstance player, String command)
	{
		String drawername = "trash";
		if (command == null)
			return;
		String[] cmd = command.split(" ");
		int startPos = 0;
		if (cmd != null)
			drawername = cmd[0];
		if (cmd.length>1)
			startPos = Integer.parseInt(cmd[1]);

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int nitems = 0;
		Set<L2BoxItem> _items = new HashSet<>();
		for (L2ItemInstance i : player.getInventory().getItems())
		{
			if (i.getItemId() == 57 || i.isEquipped())
				continue;
			L2BoxItem bi = new L2BoxItem(i.getItemId(), i.getCount(), i.getItem().getName(), i.getObjectId(), i.getEnchantLevel());
			_items.add(bi);
		}
		if (startPos >= _items.size())
			startPos = 0;
		String button = "<button value=\"Deposit\" width=80 height=15 action=\"bypass -h npc_"+getObjectId()+"_InBox "+drawername;
		String next = "<button value=\"next\" width=50 height=15 action=\"bypass -h npc_"+getObjectId()+"_Deposit "+drawername+" "+(startPos+MAX_ITEMS_PER_PAGE)+"\">";
		String back = "<button value=\"back\" width=50 height=15 action=\"bypass -h npc_"+getObjectId()+"_Chat 0\">";
		String content = "<html><body>Drawer "+drawername+":<br>"+next+" "+back+"<table width=\"100%\">";
		content += "<tr><td>Item</td><td>Count</td><td>Deposit</td></tr>";
		for (L2BoxItem i : _items)
		{
			nitems++;
			if (nitems < startPos)
				continue;
			String varname = VARIABLE_PREFIX.charAt(nitems-startPos)+String.valueOf(i.itemid);
			content += "<tr><td>"+i.name+"</td><td align=\"right\">"+i.count+"</td>";
			content += "<td><edit var=\""+varname+"\" width=30></td></tr>";
			button += " ,"+varname+" $"+varname;
			if ((nitems - startPos) >= MAX_ITEMS_PER_PAGE)
				break;
		}
		button += "\">";
		content += "</table><br>"+button+"</body></html>";
		_log.fine("setHtml("+content+"); items="+nitems);
		html.setHtml(content);
		player.sendPacket(html);

		player.sendPacket( new ActionFailed() );
	}

	private Set<L2BoxItem> getItems(String drawer)
	{
		Set<L2BoxItem> it = new HashSet<>();
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id, spawn, npcid, drawer, itemid, name, count, enchant FROM boxes where spawn=? and npcid=? and drawer=?");
			statement.setInt(1, getSpawn().getId());
			statement.setInt(2, getNpcId());
			statement.setString(3, drawer);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				_log.fine("found: itemid="+rs.getInt("itemid")+", count="+rs.getInt("count"));
				it.add(new L2BoxItem(rs.getInt("itemid"),rs.getInt("count"),rs.getString("name"),rs.getInt("id"),rs.getInt("enchant")));
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.info("getItems failed: "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
		return it;
	}

	private void putInBox(L2PcInstance player, String command)
	{/* NOTE: Item storing in box is currently not implemented
		String[] cmd = command.split(",");
		if (cmd.length<=1)
			return;
		String drawername = cmd[0];
		for (int i = 1; i < cmd.length; i++)
		{
			String[] part = cmd[i].split(" ");
			if (part == null || part.length < 2)
				continue;
			try
			{
				int id = Integer.parseInt(part[0].substring(1));
				int count = Integer.parseInt(part[1]);
				if (count <= 0)
					continue;
				int realCount = player.getInventory().getItemByItemId(id).getCount();
				if (count < realCount)
					realCount = count;
				L2ItemInstance item = player.getInventory().destroyItemByItemId("Box", id, realCount, player, this);
				// other than previous l2j, destroyItemByItemId does not return the count destroyed
				// and we cannot just use the returned item as we cannot change the count
				L2ItemInstance newItem = ItemTable.getInstance().createItem(id);
				newItem.setCount(realCount);
				newItem.setEnchantLevel(item.getEnchantLevel());
				putItemInBox(player, drawername, newItem);
			}
			catch (Exception e)
			{
				_log.fine("putInBox "+command+" failed: "+e);
			}
		}
	}

	private void putItemInBox(L2PcInstance player, String drawer, L2ItemInstance item)
	{
		String charname = player.getName();
		Connection con = null;
        int foundId = 0;
        int foundCount = 0;
		try
		{
            con = L2DatabaseFactory.getInstance().getConnection();
            if (item.isStackable())
            {
                PreparedStatement st2 = con.prepareStatement("SELECT id,count FROM boxes where spawn=? and npcid=? and drawer=? and itemid=?");
                st2.setInt(1, getSpawn().getId());
                st2.setInt(2, getNpcId());
                st2.setString(3, drawer);
                st2.setInt(4, item.getItemId());
                ResultSet rs = st2.executeQuery();
                if (rs.next())
                {
                    foundId = rs.getInt("id");
                    foundCount = rs.getInt("count");
                }
                rs.close();
                st2.close();
            }
            if (foundCount == 0)
            {
    			PreparedStatement statement = con.prepareStatement("INSERT INTO boxes (spawn,npcid,drawer,itemid,name,count,enchant) VALUES(?,?,?,?,?,?,?)");
    			statement.setInt(1, getSpawn().getId());
    			statement.setInt(2, getNpcId());
    			statement.setString(3, drawer);
    			statement.setInt(4, item.getItemId());
    			statement.setString(5, item.getItem().getName());
    			statement.setInt(6, item.getCount());
    			statement.setInt(7, item.getEnchantLevel());
    			statement.execute();
    			statement.close();
            }
            else
            {
                PreparedStatement statement = con.prepareStatement("UPDATE boxes SET count=? WHERE id=?");
                statement.setInt(1, foundCount + item.getCount());
                statement.setInt(2, foundId);
                statement.execute();
                statement.close();
            }
		}
		catch (Exception e)
		{
			_log.info("could not store item to box "+getSpawn().getId()+"-"+drawer+" for char "+charname);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}*/
	}

	private void takeOutBox(L2PcInstance player, String command)
	{/* NOTE: Item storing in box is currently not implemented
		String[] cmd = command.split(",");
		if (cmd.length<=1)
			return;
		String drawername = cmd[0];
		L2BoxItem bi = null;
		for (int i = 1; i < cmd.length; i++)
		{
			String[] part = cmd[i].split(" ");
			if (part == null || part.length < 2)
				continue;
			try
			{
				int id = Integer.parseInt(part[0].substring(1));
				int count = Integer.parseInt(part[1]);
				if (count <= 0)
					continue;
				L2ItemInstance item = ItemTable.getInstance().createItem(id);
				item.setCount(count);
				bi = takeItemOutBox(player, drawername, item);
				if (bi.count > 0)
				{
					item.setCount(bi.count);
					item.setEnchantLevel(bi.enchant);
					player.getInventory().addItem("Box", item, player, this);
				}
			}
			catch (Exception e)
			{
				_log.fine("takeOutBox "+command+" failed: "+e);
			}
		}
	}

	private L2BoxItem takeItemOutBox(L2PcInstance player, String drawer, L2ItemInstance item)
	{
		String charname = player.getName();
		Connection con = null;
		L2BoxItem bi = new L2BoxItem();
		bi.count = 0;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id,count,enchant FROM boxes WHERE spawn=? AND npcid=? AND drawer=? AND itemid=? AND count>=?");
			statement.setInt(1, getSpawn().getId());
			statement.setInt(2, getNpcId());
			statement.setString(3, drawer);
			statement.setInt(4, item.getItemId());
			statement.setInt(5, item.getCount());
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				if (rs.getInt("count") == item.getCount())
				{
					bi.count = item.getCount();
					bi.itemid = item.getItemId();
					bi.enchant = rs.getInt("enchant");
					PreparedStatement st2 = con.prepareStatement("DELETE FROM boxes WHERE id=?");
					st2.setInt(1, rs.getInt("id"));
					st2.execute();
					st2.close();
					break;
				}
				if (rs.getInt("count") > item.getCount())
				{
					bi.count = item.getCount();
					bi.itemid = item.getItemId();
					bi.enchant = rs.getInt("enchant");
					PreparedStatement st2 = con.prepareStatement("UPDATE boxes SET count=? WHERE id=?");
					st2.setInt(1, rs.getInt("count") - bi.count);
					st2.setInt(2, rs.getInt("id"));
					st2.execute();
					st2.close();
					break;
				}
			}
            rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.info("could not delete/update item, box "+getSpawn().getId()+"-"+drawer+" for char "+charname+": "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
		return bi;*/
	}
}
