package com.bt.om;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.bt.om.selenium.util.PageUtils;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.NumberUtil;

public class CreateTaobaoPid {

	public static void main(String[] args) {
		String key = ConfigUtil.getString("selenium.drive.name");
		String value = ConfigUtil.getString("selenium.drive.path");
		WebDriver driver;
		String baseUrl = "https://pub.alimama.com/promo/search/index.htm?queryType=2&q=%E5%A5%B3%E8%A3%85";

		System.setProperty(key, value);
		driver = new FirefoxDriver();
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(System.in));
		String sid;
		try {
			sid = br.readLine();
			System.out.println(sid);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int i = 1;
		while (true) {
			try {
				Thread.sleep(NumberUtil.getRandomNumber(1000, 2000));

				// 点击立即推广按钮
				WebElement element = driver
						.findElement(By.xpath("//*[@id=\"J_search_results\"]/div/div[1]/div[4]/a[1]"));
				PageUtils.scrollToElementAndClick(element, driver);

				Thread.sleep(NumberUtil.getRandomNumber(1000, 2000));

				WebElement element0 = driver
						.findElement(By.xpath("//*[@id=\"zone-form\"]/div[2]/div/button/span[2]/span[1]"));
				PageUtils.scrollToElementAndClick(element0, driver);

				element0 = driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[2]/div/div/ul/li[2]/a"));
				PageUtils.scrollToElementAndClick(element0, driver);

				// 点击“新建推广位”
				WebElement element1 = driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[3]/div/label[2]/input"));
				PageUtils.scrollToElementAndClick(element1, driver);

				// 输入广告位名称
				driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[4]/input")).sendKeys("大推网ADB" + i);

				Thread.sleep(NumberUtil.getRandomNumber(1000, 2000));

				// 点击确定按钮
				WebElement element2 = driver.findElement(By.xpath("//*[@id=\"J_global_dialog\"]/div/div[3]/button[1]"));
				PageUtils.scrollToElementAndClick(element2, driver);

				Thread.sleep(NumberUtil.getRandomNumber(1000, 2000));

				try {
					// 点击关闭按钮
					WebElement element3 = driver
							.findElement(By.xpath("//*[@id=\"magix_vf_code\"]/div/div[3]/button[2]"));
					PageUtils.scrollToElementAndClick(element3, driver);
				} catch (Exception e) {
					// 点击关闭按钮
					WebElement element3 = driver.findElement(By.xpath("//*[@id=\"magix_vf_code\"]/div/div[3]/button"));
					PageUtils.scrollToElementAndClick(element3, driver);
				}
			} catch (Exception e) {
				e.printStackTrace();
				driver.navigate().refresh();
				try {
					Thread.sleep(60000 * 2);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			i++;
		}
	}

}
