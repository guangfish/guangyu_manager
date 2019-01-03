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

import com.bt.om.entity.TkPids;
import com.bt.om.mapper.TkPidsMapper;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class FetchPidSgbtTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TkPidsMapper tkPidsMapper;

	@Test
	public void createPid() {
		String key = "webdriver.chrome.driver";
		String value = ".\\conf\\tools\\chromedriver.exe";
		WebDriver driver;
		
		int page = 128;
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
						String pidName = driver
								.findElement(By.xpath("//*[@id=\"brix_brick_" + id.substring(id.lastIndexOf("_")+1) + "\"]/tbody/tr["+i+"]/td[1]"))
								.getText();			
						System.out.println(pidStr+"--"+pidName);
						TkPids tkPids = new TkPids();
						tkPids.setPid(pidStr.substring(pidStr.lastIndexOf("_")+1));
						tkPids.setPidName(pidName);
						try {
							tkPidsMapper.insert(tkPids);
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
