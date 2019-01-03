package com.bt.om.test;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		int[] a = { 18, 63, 25, 46, 3, 0, 99, 1, 2 };
		// quickSort(a);
		insertSort(a);
	}

	// 快速排序法
	public static void quickSort(int[] a) {
		Arrays.sort(a); // 从小到大的顺序进行排序。
		System.out.println(Arrays.toString(a));
	}

	// 直接插入排序
	public static void insertSort(int[] a) {
		int len = a.length;// 单独把数组长度拿出来，提高效率
		int insertNum;// 要插入的数
		for (int i = 1; i < len; i++) {// 因为第一次不用，所以从1开始
			insertNum = a[i];
			int j = i - 1;// 序列元素个数
			while (j >= 0 && a[j] > insertNum) {// 从后往前循环，将大于insertNum的数向后移动
				a[j + 1] = a[j];// 元素向后移动
				j--;
			}
			a[j + 1] = insertNum;// 找到位置，插入当前元素
		}
		System.out.println(Arrays.toString(a));
	}

	// 简单选择排序
	public void selectSort(int[] a) {
		int len = a.length;
		for (int i = 0; i < len; i++) {// 循环次数
			int value = a[i];
			int position = i;
			for (int j = i + 1; j < len; j++) {// 找到最小的值和位置
				if (a[j] < value) {
					value = a[j];
					position = j;
				}
			}
			a[position] = a[i];// 进行交换
			a[i] = value;
		}
		System.out.println(Arrays.toString(a));
	}

	// 冒泡排序
	public void bubbleSort(int[] a) {
		int len = a.length;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len - i - 1; j++) {// 注意第二重循环的条件
				if (a[j] > a[j + 1]) {
					int temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		}
	}

	// 二分法排序
	public static int binarySerch(int[] arr, int start, int end, int value) {
		int mid = -1;
		while (start <= end) {
			mid = (start + end) / 2;
			if (arr[mid] < value)
				start = mid + 1;
			else if (arr[mid] > value)
				end = mid - 1;
			else
				break;
		}
		if (arr[mid] < value)
			return mid + 1;
		else if (value < arr[mid])
			return mid;

		return mid + 1;

	}

	public static void binarySort(int[] arr, int start, int end) {
		for (int i = start + 1; i <= end; i++) {
			int value = arr[i];
			int insertLoc = binarySerch(arr, start, i - 1, value);
			for (int j = i; j > insertLoc; j--) {
				arr[j] = arr[j - 1];
			}
			arr[insertLoc] = value;
		}
	}

}
