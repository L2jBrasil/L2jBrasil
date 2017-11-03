/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.util;

import java.util.Random;

@SuppressWarnings("synthetic-access")
public final class Rnd
{
	private static final class L2Random extends Random
	{
		private static final long serialVersionUID = 2089256427272977088L;
		/**
		 * Copied from java.util.Random.
		 */
		private static final long multiplier = 0x5DEECE66DL;
		private static final long addend = 0xBL;
		private static final long mask = (1L << 48) - 1;
		private long seed;

	
		@Override
		public synchronized void setSeed(long newSeed)
		{
			seed = (newSeed ^ multiplier) & mask;
			super.setSeed(newSeed);
		}

	
		@Override
		protected int next(int bits)
		{
			long nextseed = seed = seed * multiplier + addend & mask;
			return (int) (nextseed >>> 48 - bits);
		}

	
		@Override
		public double nextDouble()
		{
			return (double) next(31) / 0x7fffffff;
		}

		public double nextDouble(double n)
		{
			return (double) (next(31) - 1) / 0x7fffffff * n;
		}

	
		@Override
		public int nextInt(int n)
		{
			return (int) ((double) (next(31) - 1) / 0x7fffffff * n);
		}
	}

	private static final L2Random RND = new L2Random();

	/**
	 * Get random number from 0.0 to 1.0
	 */
	public static double nextDouble()
	{
		return RND.nextDouble();
	}

	/**
	 * Get random number from 0 to n-1
	 */
	public static int nextInt(int n)
	{
		return RND.nextInt(n);
	}

	/**
	 * Get random number from 0 to n-1
	 */
	public static int get(int n)
	{
		return RND.nextInt(n);
	}

	/**
	 * Get random number from min to max <b>(not max-1)</b>
	 */
	public static int get(int min, int max)
	{
		if (min < max) {
			return min + RND.nextInt(max - min + 1);
		} else {
			return max + RND.nextInt(min - max + 1);
		}
	}

	public static boolean calcChance(int chance, int maxChance)
	{
		return chance > RND.nextInt(maxChance);
	}

	public static boolean calcChance(double chance, int maxChance)
	{
		return chance > RND.nextDouble(maxChance);
	}

	public static boolean calcChance(double chance)
	{
		return chance > RND.nextDouble();
	}

	public static double nextGaussian()
	{
		return RND.nextGaussian();
	}

	public static boolean nextBoolean()
	{
		return RND.nextBoolean();
	}

	public static byte[] nextBytes(byte[] array)
	{
		RND.nextBytes(array);
		return array;
	}
}