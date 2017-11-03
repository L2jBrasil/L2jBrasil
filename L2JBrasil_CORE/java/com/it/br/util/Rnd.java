/* This program is free software: you can redistribute it and/or modify it under
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
package com.it.br.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Forsaiken
 */
public final class Rnd
{
	/**
	 * This class extends {@link java.util.Random} but do not compare and store atomically.<br>
	 * Instead it`s using a simple volatile flag to ensure reading and storing the whole 64bit seed chunk.<br>
	 * This implementation is much faster on parallel access, but may generate the same seed for 2 threads.
	 * 
	 * @author Forsaiken
	 * @see java.util.Random
	 */
	public static final class NonAtomicRandom extends Random
	{
		public static final long serialVersionUID = 1L;
		public volatile long _seed;
		
		public NonAtomicRandom()
		{
			this(++SEED_UNIQUIFIER + System.nanoTime());
		}
		
		public NonAtomicRandom(final long seed)
		{
			setSeed(seed);
		}
		
		@Override
		public final int next(final int bits)
		{
			return (int) ((_seed = (_seed * MULTIPLIER + ADDEND) & MASK) >>> (48 - bits));
		}
		
		@Override
		public final void setSeed(final long seed)
		{
			_seed = (seed ^ MULTIPLIER) & MASK;
		}
	}
	
	/**
	 * 
	 * @author Forsaiken
	 *
	 */
	public static final class RandomContainer
	{
		public final Random _random;
		
		public RandomContainer(final Random random)
		{
			_random = random;
		}
		
		public final Random directRandom()
		{
			return _random;
		}
		
		/**
		 * Get a random double number from 0 to 1
		 * 
		 * @return A random double number from 0 to 1
		 * @see #nextDouble()
		 */
		public final double get()
		{
			return _random.nextDouble();
		}
		
		/**
		 * Gets a random integer number from 0(inclusive) to n(exclusive)
		 * 
		 * @param n
		 *            The superior limit (exclusive)
		 * @return A random integer number from 0 to n-1
		 */
		public final int get(final int n)
		{
			return (int) (_random.nextDouble() * n);
		}
		
		/**
		 * Gets a random integer number from min(inclusive) to max(inclusive)
		 * 
		 * @param min
		 *            The minimum value
		 * @param max
		 *            The maximum value
		 * @return A random integer number from min to max
		 */
		public final int get(final int min, final int max)
		{
			return min + (int) (_random.nextDouble() * (max - min + 1));
		}
		
		/**
		 * Gets a random long number from min(inclusive) to max(inclusive)
		 * 
		 * @param min
		 *            The minimum value
		 * @param max
		 *            The maximum value
		 * @return A random long number from min to max
		 */
		public final long get(final long min, final long max)
		{
			return min + (long) (_random.nextDouble() * (max - min + 1));
		}
		
		/**
		 * Get a random boolean state (true or false)
		 * 
		 * @return A random boolean state (true or false)
		 * @see java.util.Random#nextBoolean()
		 */
		public final boolean nextBoolean()
		{
			return _random.nextBoolean();
		}
		
		/**
		 * Fill the given array with random byte numbers from Byte.MIN_VALUE(inclusive) to Byte.MAX_VALUE(inclusive)
		 * 
		 * @param array
		 *            The array to be filled with random byte numbers
		 * @see java.util.Random#nextBytes(byte[] bytes)
		 */
		public final void nextBytes(final byte[] array)
		{
			_random.nextBytes(array);
		}
		
		/**
		 * Get a random double number from 0 to 1
		 * 
		 * @return A random double number from 0 to 1
		 * @see java.util.Random#nextDouble()
		 */
		public final double nextDouble()
		{
			return _random.nextDouble();
		}
		
		/**
		 * Get a random float number from 0 to 1
		 * 
		 * @return A random integer number from 0 to 1
		 * @see java.util.Random#nextFloat()
		 */
		public final float nextFloat()
		{
			return _random.nextFloat();
		}
		
		/**
		 * Get a random gaussian double number from 0 to 1
		 * 
		 * @return A random gaussian double number from 0 to 1
		 * @see java.util.Random#nextGaussian()
		 */
		public final double nextGaussian()
		{
			return _random.nextGaussian();
		}
		
		/**
		 * Get a random integer number from Integer.MIN_VALUE(inclusive) to Integer.MAX_VALUE(inclusive)
		 * 
		 * @return A random integer number from Integer.MIN_VALUE to Integer.MAX_VALUE
		 * @see java.util.Random#nextInt()
		 */
		public final int nextInt()
		{
			return _random.nextInt();
		}
		
		/**
		 * Get a random long number from Long.MIN_VALUE(inclusive) to Long.MAX_VALUE(inclusive)
		 * 
		 * @return A random integer number from Long.MIN_VALUE to Long.MAX_VALUE
		 * @see java.util.Random#nextLong()
		 */
		public final long nextLong()
		{
			return _random.nextLong();
		}
	}
	
	/**
	 * 
	 * @author Forsaiken
	 *
	 */
	public static enum RandomType
	{
		/**
		 * For best random quality.
		 * 
		 * @see java.security.SecureRandom
		 */
		SECURE,

		/**
		 * For average random quality.
		 * 
		 * @see java.util.Random
		 */
		UNSECURE_ATOMIC,

		/**
		 * Like {@link RandomType#UNSECURE_ATOMIC}.<br>
		 * Each thread has it`s own random instance.<br>
		 * Provides best parallel access speed.
		 * 
		 * @see ThreadLocalRandom
		 */
		UNSECURE_THREAD_LOCAL,

		/**
		 * Like {@link #UNSECURE_ATOMIC}.<br>
		 * Provides much faster parallel access speed.
		 * 
		 * @see NonAtomicRandom
		 */
		UNSECURE_VOLATILE
	}
	
	/**
	 * This class extends {@link java.util.Random} but do not compare and store atomically.<br>
	 * Instead it`s using thread local ensure reading and storing the whole 64bit seed chunk.<br>
	 * This implementation is the fastest, never generates the same seed for 2 threads.<br>
	 * Each thread has it`s own random instance.
	 * 
	 * @author Forsaiken
	 * @see java.util.Random
	 */
	public static final class ThreadLocalRandom extends Random
	{
		public static final class Seed
		{
			long _seed;
			
			Seed(final long seed)
			{
				setSeed(seed);
			}
			
			final int next(final int bits)
			{
				return (int) ((_seed = (_seed * MULTIPLIER + ADDEND) & MASK) >>> (48 - bits));
			}
			
			final void setSeed(final long seed)
			{
				_seed = (seed ^ MULTIPLIER) & MASK;
			}
		}
		
		public static final long serialVersionUID = 1L;
		public final ThreadLocal<Seed> _seedLocal;
		
		public ThreadLocalRandom()
		{
			_seedLocal = new ThreadLocal<Seed>()
			{
				@Override
				public final Seed initialValue()
				{
					return new Seed(++SEED_UNIQUIFIER + System.nanoTime());
				}
			};
		}
		
		public ThreadLocalRandom(final long seed)
		{
			_seedLocal = new ThreadLocal<Seed>()
			{
				@Override
				public final Seed initialValue()
				{
					return new Seed(seed);
				}
			};
		}
		
		@Override
		public final int next(final int bits)
		{
			return _seedLocal.get().next(bits);
		}
		
		@Override
		public final void setSeed(final long seed)
		{
			if (_seedLocal != null)
				_seedLocal.get().setSeed(seed);
		}
	}
	
	public final static long ADDEND = 0xBL;
	
	public final static long MASK = (1L << 48) - 1;
	
	public final static long MULTIPLIER = 0x5DEECE66DL;
	
	public static final RandomContainer rnd = newInstance(RandomType.UNSECURE_THREAD_LOCAL);
	
	public static volatile long SEED_UNIQUIFIER = 8682522807148012L;
	
	public static final Random directRandom()
	{
		return rnd.directRandom();
	}
	
	/**
	 * Get a random double number from 0 to 1
	 * 
	 * @return A random double number from 0 to 1
	 * @see #nextDouble()
	 */
	public static final double get()
	{
		return rnd.nextDouble();
	}
	
	/**
	 * Gets a random integer number from 0(inclusive) to n(exclusive)
	 * 
	 * @param n
	 *            The superior limit (exclusive)
	 * @return A random integer number from 0 to n-1
	 */
	public static final int get(final int n)
	{
		return rnd.get(n);
	}
	
	/**
	 * Gets a random integer number from min(inclusive) to max(inclusive)
	 * 
	 * @param min
	 *            The minimum value
	 * @param max
	 *            The maximum value
	 * @return A random integer number from min to max
	 */
	public static final int get(final int min, final int max)
	{
		return rnd.get(min, max);
	}
	
	/**
	 * Gets a random long number from min(inclusive) to max(inclusive)
	 * 
	 * @param min
	 *            The minimum value
	 * @param max
	 *            The maximum value
	 * @return A random long number from min to max
	 */
	public static final long get(final long min, final long max)
	{
		return rnd.get(min, max);
	}
	
	public static final RandomContainer newInstance(final RandomType type)
	{
		switch (type)
		{
			case UNSECURE_ATOMIC:
				return new RandomContainer(new Random());
				
			case UNSECURE_VOLATILE:
				return new RandomContainer(new NonAtomicRandom());
				
			case UNSECURE_THREAD_LOCAL:
				return new RandomContainer(new ThreadLocalRandom());
				
			case SECURE:
				return new RandomContainer(new SecureRandom());
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Get a random boolean state (true or false)
	 * 
	 * @return A random boolean state (true or false)
	 * @see java.util.Random#nextBoolean()
	 */
	public static final boolean nextBoolean()
	{
		return rnd.nextBoolean();
	}
	
	/**
	 * Fill the given array with random byte numbers from Byte.MIN_VALUE(inclusive) to Byte.MAX_VALUE(inclusive)
	 * 
	 * @param array
	 *            The array to be filled with random byte numbers
	 * @see java.util.Random#nextBytes(byte[] bytes)
	 */
	public static final void nextBytes(final byte[] array)
	{
		rnd.nextBytes(array);
	}
	
	/**
	 * Get a random double number from 0 to 1
	 * 
	 * @return A random double number from 0 to 1
	 * @see java.util.Random#nextDouble()
	 */
	public static final double nextDouble()
	{
		return rnd.nextDouble();
	}
	
	/**
	 * Get a random float number from 0 to 1
	 * 
	 * @return A random integer number from 0 to 1
	 * @see java.util.Random#nextFloat()
	 */
	public static final float nextFloat()
	{
		return rnd.nextFloat();
	}
	
	/**
	 * Get a random gaussian double number from 0 to 1
	 * 
	 * @return A random gaussian double number from 0 to 1
	 * @see java.util.Random#nextGaussian()
	 */
	public static final double nextGaussian()
	{
		return rnd.nextGaussian();
	}
	
	/**
	 * Get a random integer number from Integer.MIN_VALUE(inclusive) to Integer.MAX_VALUE(inclusive)
	 * 
	 * @return A random integer number from Integer.MIN_VALUE to Integer.MAX_VALUE
	 * @see java.util.Random#nextInt()
	 */
	public static final int nextInt()
	{
		return rnd.nextInt();
	}
	
	/**
	 * @param n 
	 * @return 
	 * @see #get(int n)
	 */
	public static final int nextInt(final int n)
	{
		return get(n);
	}
	
	/**
	 * Get a random long number from Long.MIN_VALUE(inclusive) to Long.MAX_VALUE(inclusive)
	 * 
	 * @return A random integer number from Long.MIN_VALUE to Long.MAX_VALUE
	 * @see java.util.Random#nextLong()
	 */
	public static final long nextLong()
	{
		return rnd.nextLong();
	}
}
