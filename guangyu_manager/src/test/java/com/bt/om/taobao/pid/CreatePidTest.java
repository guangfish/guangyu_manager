package com.bt.om.taobao.pid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.adtime.common.lang.DateUtil;
import com.bt.om.entity.TaobaoPids;
import com.bt.om.mapper.TaobaoPidsMapper;
import com.bt.om.selenium.util.PageUtils;
import com.bt.om.util.NumberUtil;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class CreatePidTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void createPid() {
		String key = "webdriver.chrome.driver";
		String value = ".\\conf\\tools\\chromedriver.exe";
		WebDriver driver;
		String baseUrl = "https://pub.alimama.com/promo/search/index.htm?queryType=2&q=%E5%A5%B3%E8%A3%85";

		System.setProperty(key, value);
		driver = new ChromeDriver();
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


		while (true) {
			try {
				Thread.sleep(NumberUtil.getRandomNumber(1000, 3000));

				// 点击立即推广按钮
				WebElement element = driver
						.findElement(By.xpath("//*[@id=\"J_search_results\"]/div/div[1]/div[4]/a[1]"));
				PageUtils.scrollToElementAndClick(element, driver);

				Thread.sleep(NumberUtil.getRandomNumber(1000, 3000));

				WebElement element0 = driver
						.findElement(By.xpath("//*[@id=\"zone-form\"]/div[2]/div/button/span[2]/span[1]"));
				PageUtils.scrollToElementAndClick(element0, driver);

				element0 = driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[2]/div/div/ul/li[2]/a"));
				PageUtils.scrollToElementAndClick(element0, driver);

				// 点击“新建推广位”
				WebElement element1 = driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[3]/div/label[2]/input"));
				PageUtils.scrollToElementAndClick(element1, driver);

				// 输入广告位名称
				driver.findElement(By.xpath("//*[@id=\"zone-form\"]/div[4]/input"))
						.sendKeys("yt-" + DateUtil.formatDate(new Date(), DateUtil.FULL_PATTERN));

				 Thread.sleep(NumberUtil.getRandomNumber(1000, 3000));

				// 点击确定按钮
				WebElement element2 = driver.findElement(By.xpath("//*[@id=\"J_global_dialog\"]/div/div[3]/button[1]"));
				PageUtils.scrollToElementAndClick(element2, driver);

				Thread.sleep(NumberUtil.getRandomNumber(1000, 3000));

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
					Thread.sleep(NumberUtil.getRandomNumber(60000 * 1, 60000 * 2));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
