package com.bt.om.selenium;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CsvReadTest {

	public static void main(String[] args) {
		DataInputStream in = null;
		BufferedReader reader = null;
		try {
			in = new DataInputStream(new FileInputStream(new File("C:\\Users\\Lenovo\\Desktop\\引入订单明细报表20180503.csv")));
			reader = new BufferedReader(new InputStreamReader(in, "GBK"));
			// 第一行信息，为标题信息，不用,如果需要，注释掉
			reader.readLine();
			String line = null;
			while ((line = reader.readLine()) != null) {
				// CSV格式文件为逗号分隔符文件，这里根据逗号切分
				String[] items = line.split(",");
				for (String item : items) {
					System.out.println(item.replace("	", ""));
				}
				// int value = Integer.parseInt(last);//如果是数值，可以转化为数值
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
