package com.lian.iprd.ratelimit.test;

public class Test {

	public static void main(String[] args) {
		int[] m = {3,6,2,4,5};
		int[] v = {5,9,3,5,5};
		int[] result = new int[m.length];
		for (int i=0; i<m.length; i++) {
			result[i] = -1;
		}
		int a = packageDy(m.length-1, 6, m, v, result);
		System.out.println(a);
	}
	
	
	
	public static int packageDy(int index, int leftCaptity, 
			final int[] m, final int[] v, final int[] result) {
		if (index < 0) {
			return 0;
		}
		if (index >= 0 && result[index] >= 0) {
			return result[index];
		}
		int v1 = packageDy(index-1, leftCaptity, m, v, result);
		if (leftCaptity - m[index] < 0) {
			return v1;
		}
		int v2 = packageDy(index-1, leftCaptity - m[index], m, v, result) + v[index];
		return Math.max(v1, v2);
	}
	

}
