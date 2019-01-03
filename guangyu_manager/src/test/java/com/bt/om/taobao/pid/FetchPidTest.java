package com.bt.om.taobao.pid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.bt.om.entity.TaobaoPids;
import com.bt.om.mapper.TaobaoPidsMapper;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class FetchPidTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TaobaoPidsMapper taobaoPidsMapper;

	@Test
	public void createPid() {
		String key = "webdriver.chrome.driver";
		String value = ".\\conf\\tools\\chromedriver.exe";
		WebDriver driver;
		
		String account="chj8023";
		int page = 1623;
		
//		String account="ssj8023";
//		int page = 829;
		
		String baseUrl = "https://pub.alimama.com/myunion.htm#!/manage/zone/zone";

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
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		driver.get(baseUrl + "?toPage=" + page);
		
		while (true) {						
				try {
					for (int i = 1; i <= 40; i++) {
						String id = driver.findElement(By.xpath("//table[starts-with(@id,'brix_brick_')]")).getAttribute("id");
						System.out.println(id);
						String pidStr = driver
								.findElement(By.xpath("//*[@id=\"brix_brick_" + id.substring(id.lastIndexOf("_")+1) + "\"]/tbody/tr[" + i + "]/td[2]"))
								.getText();
						String siteStr = driver
								.findElement(By.xpath("//*[@id=\"brix_brick_" + id.substring(id.lastIndexOf("_")+1) + "\"]/tbody/tr["+i+"]/td[4]"))
								.getText();
						System.out.println(pidStr+"--"+siteStr);
						TaobaoPids taobaoPids = new TaobaoPids();
						taobaoPids.setPid(pidStr);
						taobaoPids.setPid1(pidStr.substring(pidStr.lastIndexOf("_")+1));
						taobaoPids.setSite(siteStr);
						taobaoPids.setAccount(account);
						try {
							taobaoPidsMapper.insert(taobaoPids);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			
			page = page + 1;
			driver.get(baseUrl + "?toPage=" + page);
		}
	}
}
