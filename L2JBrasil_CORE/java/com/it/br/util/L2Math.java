package com.it.br.util;

public final class L2Math
{
	private L2Math()
	{
	}
	
	public static int min(int n1, int n2, int n3)
	{
		return Math.min(n1, Math.min(n2, n3));
	}
	
	public static long min(long n1, long n2, long n3)
	{
		return Math.min(n1, Math.min(n2, n3));
	}
	
	public static float min(float n1, float n2, float n3)
	{
		return Math.min(n1, Math.min(n2, n3));
	}
	
	public static double min(double n1, double n2, double n3)
	{
		return Math.min(n1, Math.min(n2, n3));
	}
	
	public static int max(int n1, int n2, int n3)
	{
		return Math.max(n1, Math.max(n2, n3));
	}
	
	public static long max(long n1, long n2, long n3)
	{
		return Math.max(n1, Math.max(n2, n3));
	}
	
	public static float max(float n1, float n2, float n3)
	{
		return Math.max(n1, Math.max(n2, n3));
	}
	
	public static double max(double n1, double n2, double n3)
	{
		return Math.max(n1, Math.max(n2, n3));
	}
	
	public static int limit(int min, long value, int max)
	{
		return (int)Math.max(min, Math.min(value, max));
	}
	
	public static int limit(int min, int value, int max)
	{
		return Math.max(min, Math.min(value, max));
	}
	
	public static int limit(int min, double value, int max)
	{
		return (int)Math.max(min, Math.min(value, max));
	}
	
	public static int limit(int min, float value, int max)
	{
		return (int)Math.max(min, Math.min(value, max));
	}
	
	public static long limit(long min, long value, long max)
	{
		return Math.max(min, Math.min(value, max));
	}
	
	public static long limit(long min, double value, long max)
	{
		return (long)Math.max(min, Math.min(value, max));
	}
	
	public static long limit(long min, float value, long max)
	{
		return (long)Math.max(min, Math.min(value, max));
	}
	
	public static float limit(float min, double value, float max)
	{
		return (float)Math.max(min, Math.min(value, max));
	}
	
	public static float limit(float min, float value, float max)
	{
		return Math.max(min, Math.min(value, max));
	}
	
	public static double limit(double min, double value, double max)
	{
		return Math.max(min, Math.min(value, max));
	}
	
	public static double calculateDistance(int x1, int y1, int x2, int y2)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}
	
	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		final long diffZ = z1 - z2;
		
		return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
	}
	
	public static long calculateDistanceSq(int x1, int y1, int x2, int y2)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		
		return diffX * diffX + diffY * diffY;
	}
	
	public static long calculateDistanceSq(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		final long diffZ = z1 - z2;
		
		return diffX * diffX + diffY * diffY + diffZ * diffZ;
	}
	
	public static boolean isDistanceLessThan(int x1, int y1, int x2, int y2, int limit)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		
		return diffX * diffX + diffY * diffY <= limit * limit;
	}
	
	public static boolean isDistanceLessThan(int x1, int y1, int z1, int x2, int y2, int z2, int limit)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		final long diffZ = z1 - z2;
		
		return diffX * diffX + diffY * diffY + diffZ * diffZ <= limit * limit;
	}
	
	public static boolean isDistanceLessThanSq(int x1, int y1, int x2, int y2, long limitSq)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		
		return diffX * diffX + diffY * diffY <= limitSq;
	}
	
	public static boolean isDistanceLessThanSq(int x1, int y1, int z1, int x2, int y2, int z2, long limitSq)
	{
		final long diffX = x1 - x2;
		final long diffY = y1 - y2;
		final long diffZ = z1 - z2;
		
		return diffX * diffX + diffY * diffY + diffZ * diffZ <= limitSq;
	}
	
	public static long pow(final int base, int exponent)
	{
		if (exponent < 0)
			throw new IllegalArgumentException("Exponent must be non-negative!");
		
		long result = 1;
		
		while (exponent-- > 0)
			result *= base;
		
		return result;
	}
	

	public static double pow(final double base, int exponent)
	{
		if (exponent < 0)
			throw new IllegalArgumentException("Exponent must be non-negative!");
		
		double result = 1.0;
		
		while (exponent-- > 0)
			result *= base;
		
		return result;
	}
}