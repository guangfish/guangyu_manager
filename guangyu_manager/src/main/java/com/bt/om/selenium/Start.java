package com.bt.om.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.bt.om.selenium.util.PageUtils;

public class Start {
	private static String key = "webdriver.chrome.driver";
	private static String value = ".\\conf\\tools\\chromedriver.exe";
	private static WebDriver driver;
	private static String baseUrl;

	static {
		System.setProperty(key, value);
		driver = new ChromeDriver();
		baseUrl = "https://pub.alimama.com/promo/search/index.htm";
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {
		while (true) {
			try {
//				driver.findElement(By.id("q")).click();
				driver.findElement(By.id("q")).clear();
				driver.findElement(By.id("q")).sendKeys(
						"https://detail.tmall.com/item.htm?id=565344963620");
				driver.findElement(By.xpath("//div[@id='magix_vf_header']/div/div/div[2]/div[2]/button")).click();
				
				Thread.sleep(2000);							
				WebElement element1=driver.findElement(By.linkText("立即推广"));
				PageUtils.scrollToElementAndClick(element1, driver);
				
				Thread.sleep(2000);
				WebElement element2=driver.findElement(By.xpath("//div[@id='J_global_dialog']/div/div[3]/button"));
				PageUtils.scrollToElementAndClick(element2, driver);
				
//				Thread.sleep(2000);
//				WebElement element3=driver.findElement(By.xpath("(//button[@type='button'])[6]"));
//				PageUtils.scrollToElementAndClick(element3, driver);
				
				System.out.println(driver.findElement(By.id("clipboard-target-1")).getAttribute("value"));
				
				Thread.sleep(2000);
				WebElement element4=driver.findElement(By.xpath("//div[@id='magix_vf_code']/div/div[3]/button"));
				PageUtils.scrollToElementAndClick(element4, driver);												
				
			} catch (Exception e) {
				e.printStackTrace();
			}			

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
