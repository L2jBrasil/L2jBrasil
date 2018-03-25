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
package com.it.br.gameserver.pathfinding.geonodes;

import com.it.br.Config;
import com.it.br.gameserver.pathfinding.AbstractNodeLoc;
import com.it.br.gameserver.pathfinding.Node;
import com.it.br.gameserver.pathfinding.PathFinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 *
 * @author -Nemesiss-
 */
public class GeoPathFinding extends PathFinding
{
	private static Logger _log = LoggerFactory.getLogger(GeoPathFinding.class);
	private static GeoPathFinding _instance;
	private static Map<Short, ByteBuffer> _pathNodes = new HashMap<>();
	private static Map<Short, IntBuffer> _pathNodesIndex = new HashMap<>();

	public static GeoPathFinding getInstance()
	{
		if (_instance == null)
			_instance = new GeoPathFinding();
		return _instance;
	}

	@Override
	public boolean pathNodesExist(short regionoffset)
	{
		return _pathNodesIndex.containsKey(regionoffset);
	}


	@Override
	public List<AbstractNodeLoc> findPath(int gx, int gy, short z, int gtx, int gty, short tz)
	{
		Node start = readNode(gx,gy,z);
		Node end = readNode(gtx,gty,tz);
		if (start == null || end == null)
			return null;
		if (start == end)
			return null;

		//return searchAStar(start, end);
		return searchByClosest(start, end);
	}

	@Override
	public Node[] readNeighbors(short node_x,short node_y, int idx)
	{
		short regoffset = getRegionOffset(getRegionX(node_x),getRegionY(node_y));
		ByteBuffer pn = _pathNodes.get(regoffset);

		List<Node> Neighbors = new ArrayList<>(8);
		Node newNode;
		short new_node_x, new_node_y;

		//Region for sure will change, we must read from correct file
		byte neighbor = pn.get(idx); //N
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = node_x;
			new_node_y = (short)(node_y-1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //NE
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x+1);
			new_node_y = (short)(node_y-1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //E
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x+1);
			new_node_y = node_y;
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //SE
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x+1);
			new_node_y = (short)(node_y+1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //S
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = node_x;
			new_node_y = (short)(node_y+1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //SW
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x-1);
			new_node_y = (short)(node_y+1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //W
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x-1);
			new_node_y = node_y;
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		neighbor = pn.get(idx); //NW
		idx++;
		if(neighbor > 0)
		{
			neighbor--;
			new_node_x = (short)(node_x-1);
			new_node_y = (short)(node_y-1);
			newNode = readNode(new_node_x,new_node_y,neighbor);
			if (newNode != null) Neighbors.add(newNode);
		}
		Node[] result = new Node[Neighbors.size()];
		return Neighbors.toArray(result);
	}

	//Private

	private Node readNode(short node_x, short node_y, byte layer)
	{
		short regoffset = getRegionOffset(getRegionX(node_x),getRegionY(node_y));
		if (!this.pathNodesExist(regoffset)) return null;
		short nbx = getNodeBlock(node_x);
		short nby = getNodeBlock(node_y);
		int idx = _pathNodesIndex.get(regoffset).get((nby << 8)+nbx);
		ByteBuffer pn = _pathNodes.get(regoffset);
		//reading
		byte nodes = pn.get(idx);
		idx += layer*10+1;//byte + layer*10byte
		if (nodes < layer)
		{
			_log.warn("SmthWrong!");
		}
		short node_z = pn.getShort(idx);
		idx += 2;
		return new Node(new GeoNodeLoc(node_x,node_y,node_z), idx);
	}

	private Node readNode(int gx, int gy, short z)
	{
		short node_x = getNodePos(gx);
		short node_y = getNodePos(gy);
		short regoffset = getRegionOffset(getRegionX(node_x),getRegionY(node_y));
		if (!this.pathNodesExist(regoffset)) return null;
		short nbx = getNodeBlock(node_x);
		short nby = getNodeBlock(node_y);
		int idx = _pathNodesIndex.get(regoffset).get((nby << 8)+nbx);
		ByteBuffer pn = _pathNodes.get(regoffset);
		//reading
		byte nodes = pn.get(idx);
		idx++;//byte
		int idx2 = 0; //create index to nearlest node by z
		short last_z = Short.MIN_VALUE;
		while (nodes > 0)
		{
			short node_z = pn.getShort(idx);
			if (Math.abs(last_z - z) >  Math.abs(node_z -z))
			{
				last_z = node_z;
				idx2 = idx+2;
			}
			idx += 10; //short + 8 byte
			nodes--;
		}
		return new Node(new GeoNodeLoc(node_x,node_y,last_z), idx2);
	}

	private GeoPathFinding()
	{
		LineNumberReader lnr = null;
		try
		{
			_log.info("PathFinding Engine: - Loading Path Nodes...");
			File Data = new File("./data/pathnode/pn_index.txt");
			if (!Data.exists())
				return;

			lnr = new LineNumberReader(new BufferedReader(new FileReader(Data)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Failed to Load pn_index File.");
		}
		String line;
		try
		{
			while ((line = lnr.readLine()) != null) {
				if (line.trim().length() == 0)
					continue;
				StringTokenizer st = new StringTokenizer(line, "_");
				byte rx = Byte.parseByte(st.nextToken());
				byte ry = Byte.parseByte(st.nextToken());
				LoadPathNodeFile(rx,ry);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Failed to Read pn_index File.");
		}
	}

	private void LoadPathNodeFile(byte rx,byte ry)
	{
		String fname = "./data/pathnode/"+rx+"_"+ry+".pn";
		short regionoffset = getRegionOffset(rx,ry);
		_log.info("PathFinding Engine: - Loading: "+fname+" -> region offset: "+regionoffset+"X: "+rx+" Y: "+ry);
		File Pn = new File(fname);
		int node = 0,size, index = 0;
		try {
	        // Create a read-only memory-mapped file
	        FileChannel roChannel = new RandomAccessFile(Pn, "r").getChannel();
			size = (int)roChannel.size();
			MappedByteBuffer nodes;
			if (Config.FORCE_GEODATA) //Force O/S to Loads this buffer's content into physical memory.
				//it is not guarantee, because the underlying operating system may have paged out some of the buffer's data
				nodes = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
			else
				nodes = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);

			// Indexing pathnode files, so we will know where each block starts
			IntBuffer indexs = IntBuffer.allocate(65536);

			while(node < 65536)
			{
				byte layer = nodes.get(index);
		        indexs.put(node, index);
				node++;
				index += layer*10+1;
			}
			_pathNodesIndex.put(regionoffset, indexs);
			_pathNodes.put(regionoffset, nodes);
		} catch (Exception e)
		{
			e.printStackTrace();
			_log.warn("Failed to Load PathNode File: "+fname+"\n");
	    }

	}
}
