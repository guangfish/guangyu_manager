package com.bt.om.task;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adtime.common.lang.StringUtil;
import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.selenium.util.PageUtils;
import com.bt.om.service.ITkOrderInputJdService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.NumberUtil;

/**
 * 京东订单报表下载入库
 */
@Component
public class OrderFetchJdTask {
	private static final Logger logger = Logger.getLogger(OrderFetchJdTask.class);
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	@Autowired
	private ITkOrderInputJdService tkOrderInputJdService;

	private static String key = "";
	private static String value = "";
	static {		
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			key = "webdriver.chrome.driver";
			value = ".\\conf\\tools\\chromedriver.exe";
		} else {
			key = ConfigUtil.getString("selenium.drive.name");
			value = ConfigUtil.getString("selenium.drive.path");
		}
	}

	private static WebDriver driver;
	private static String baseUrl = "https://media.jd.com";
	private static String downloadUrl = "https://media.jd.com/rest/report/detail";

	private static int sleepTimeBegin = 1000;
	private static int sleepTimeEnd = 2000;
	private static boolean ifInit=false;
	
	private static void init(){
		ifInit=true;
		schedule();
		System.setProperty(key, value);
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			driver = new ChromeDriver();
		} else {
			driver = new FirefoxDriver();
		}
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
	}

//	static {
//		schedule();
//		System.setProperty(key, value);
//		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
//			driver = new ChromeDriver();
//		} else {
//			driver = new FirefoxDriver();
//		}
//		driver.get(baseUrl);
//		driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
//	}

	@Scheduled(cron = "0 0/2 * * * ?")
//	@Scheduled(cron = "0 0/20 7-23 * * ?")
	public void orderJdFetchTask() {
		String ifRun = GlobalVariable.resourceMap.get("OrderFetchJdTask");
		if ("1".equals(ifRun)) {
			if (ifInit == false) {
				init();
			}
			logger.info("京东订单报表下载入库");
			try {
				orderJdFetch();
			} catch (Exception e) {
				e.printStackTrace();
				// driver.navigate().refresh();
			}
		}
	}

	private void orderJdFetch() throws Exception {
		try {
//			driver.navigate().refresh();
//			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin * 2, sleepTimeEnd * 2));
			
			//先登录
			driver.get(baseUrl);
			WebElement iframe = driver.findElement(By.id("indexIframe"));
	        driver.switchTo().frame(iframe);
			String setValueJS =";document.getElementById('loginname').value='"+ConfigUtil.getString("jd.account","chj8023")+"';document.getElementById('nloginpwd').value='"+ConfigUtil.getString("jd.password","aineness3300656")+"';document.getElementById('paipaiLoginSubmit').click();";
			((JavascriptExecutor) driver).executeScript(setValueJS);	
			driver.switchTo().defaultContent();
			Thread.sleep(NumberUtil.getRandomNumber(10000, 20000));
			driver.get(downloadUrl);
			Thread.sleep(NumberUtil.getRandomNumber(10000, 20000));
			
			try {
				WebElement element0 = driver.findElement(By.xpath("/html/body/div[7]/div[1]/div[2]/a"));
				PageUtils.scrollToElementAndClick(element0, driver);
				Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println("京东右下角浮动窗口已关闭");
			}

			// 点击快捷日期
			WebElement element = driver.findElement(By.xpath("//*[@id='inShortcutDate']"));
			PageUtils.scrollToElementAndClick(element, driver);
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			// 点击过去60天
			driver.findElement(By.xpath("//*[@id='inShortcutDate']/option[7]")).click();
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			// 点击查询
			WebElement element1 = driver.findElement(By.xpath("//*[@id='inOrderQueryBtn']"));
			PageUtils.scrollToElementAndClick(element1, driver);
			// 点击导出报告
			WebElement element2 = driver.findElement(By.xpath("//*[@id='inOrderExportBtn']"));
			PageUtils.scrollToElementAndClick(element2, driver);
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin * 2, sleepTimeEnd * 2));
			
			Alert alt = driver.switchTo().alert();
	        alt.accept();

//			WebDriverWait wait = new WebDriverWait(driver, 10);
//			try {
//				Alert alert = wait.until(new ExpectedCondition<Alert>() {
//					@Override
//					public Alert apply(WebDriver driver) {
//						try {
//							return driver.switchTo().alert();
//						} catch (NoAlertPresentException e) {
//							return null;
//						}
//					}
//				});
//				alert.accept();
//			} catch (NullPointerException e) {
//				System.out.println("ff2 nullpoint");
//			}

			// 开始读取分析下载的报告
			String filePath = ConfigUtil.getString("report.file.path") + "引入订单明细报表"
					+ DateUtil.dateFormate(new Date(), DateUtil.DEFAULT_PATTERN) + ".csv";
			List<TkOrderInputJd> tkOrderInputJdList = readJdReport(filePath);

//			tkOrderInputJdService.truncateTkOrderInputJd();
			
			tkOrderInputJdService.deleteByAccount(ConfigUtil.getString("jd.account"));
			for (TkOrderInputJd tkOrderInputJd : tkOrderInputJdList) {
				tkOrderInputJdService.insert(tkOrderInputJd);
			}

		} catch (Exception e) {
			e.printStackTrace();
//			driver.navigate().refresh();
			return;
		}
	}

	private static List<TkOrderInputJd> readJdReport(String filePath) {
		List<TkOrderInputJd> tkOrderInputJdList = new ArrayList<TkOrderInputJd>();
		File file = new File(filePath);
		if (!file.exists())
			logger.info("京东报表文件不存在");
		DataInputStream in = null;
		BufferedReader reader = null;
		try {
			in = new DataInputStream(new FileInputStream(file));
			reader = new BufferedReader(new InputStreamReader(in, "GBK"));
			// 第一行信息，为标题信息，不用,如果需要，注释掉
			reader.readLine();
			String line = null;
			while ((line = reader.readLine()) != null) {
				// CSV格式文件为逗号分隔符文件，这里根据逗号切分
				String[] items = line.replaceAll("	", "").split(",");
				TkOrderInputJd tkOrderInputJd = new TkOrderInputJd();
				tkOrderInputJd.setOrderTime(items[0]);
				tkOrderInputJd.setOrderId(items[1]);
				tkOrderInputJd.setProductId(items[2]);
				tkOrderInputJd.setProductName(items[3]);
				tkOrderInputJd.setPrice(StringUtil.isEmpty(items[4]) ? 0 : Double.parseDouble(items[4]));
				tkOrderInputJd.setProductNum(StringUtil.isEmpty(items[5]) ? 0 : Integer.parseInt(items[5]));
				tkOrderInputJd.setAfterSaleNum(StringUtil.isEmpty(items[6]) ? 0 : Integer.parseInt(items[6]));
				tkOrderInputJd.setRejectedNum(StringUtil.isEmpty(items[7]) ? 0 : Integer.parseInt(items[7]));
				tkOrderInputJd.setOrderStatus(items[8]);
				tkOrderInputJd.setCommissionRate(
						StringUtil.isEmpty(items[9]) ? 0 : Double.parseDouble(items[9].replace("%", "")));
				tkOrderInputJd.setDivideRate(
						StringUtil.isEmpty(items[10]) ? 0 : Double.parseDouble(items[10].replace("%", "")));
				tkOrderInputJd.setSubsidyRate(
						StringUtil.isEmpty(items[11]) ? 0 : Double.parseDouble(items[11].replace("%", "")));
				tkOrderInputJd.setSubsidyType(items[12]);
				tkOrderInputJd.setActualDivideRate(
						StringUtil.isEmpty(items[13]) ? 0 : Double.parseDouble(items[13].replace("%", "")));
				tkOrderInputJd.setEstimateMoney(StringUtil.isEmpty(items[14]) ? 0 : Double.parseDouble(items[14]));
				tkOrderInputJd.setEstimateCommission(StringUtil.isEmpty(items[15]) ? 0 : Double.parseDouble(items[15]));
				tkOrderInputJd.setCompleteTime(items[16]);
				tkOrderInputJd.setActualMoney(StringUtil.isEmpty(items[17]) ? 0 : Double.parseDouble(items[17]));
				tkOrderInputJd.setActualCommission(StringUtil.isEmpty(items[18]) ? 0 : Double.parseDouble(items[18]));
				tkOrderInputJd.setSettleTime(items[19]);
				tkOrderInputJd.setPlatformType(items[20]);
				tkOrderInputJd.setPlusOrder(items[21]);
				tkOrderInputJd.setTongkuadian(items[22]);
				tkOrderInputJd.setFatherId(items[23]);
				tkOrderInputJd.setAdId(items[24]);
				tkOrderInputJd.setAdName(items[25]);
				tkOrderInputJd.setChannel(items[26]);
				tkOrderInputJd.setThirdServiceFrom(items[27]);
				tkOrderInputJd.setPid(items[28]);
				tkOrderInputJd.setSiteApp(items[29]);
				tkOrderInputJd.setUpdateTime(new Date());
				tkOrderInputJd.setAccount(ConfigUtil.getString("jd.account"));

				tkOrderInputJdList.add(tkOrderInputJd);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {				
			try {
				reader.close();
				in.close();
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tkOrderInputJdList;
	}
	
	private static void schedule() {
		// 延迟3-5分钟，间隔3-5分钟执行
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("OrderFetchJdTask refresh...");
					driver.navigate().refresh();
				} catch (Exception e) {
					logger.error("OrderFetchJdTask refresh error:[{}]", e);
				}
			}
		}, NumberUtil.getRandomNumber(3, 5), NumberUtil.getRandomNumber(3, 5), TimeUnit.MINUTES);
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((OrderFetchJdTask) ctx.getBean("orderFetchJdTask")).orderJdFetchTask();
	}
}
