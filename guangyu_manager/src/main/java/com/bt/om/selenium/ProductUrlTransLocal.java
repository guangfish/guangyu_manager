package com.bt.om.selenium;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.bt.om.entity.TkInfoTask;
import com.bt.om.queue.disruptor.DisruptorQueueImpl;
import com.bt.om.selenium.util.PageUtils;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class ProductUrlTransLocal {
	private static final Logger logger = Logger.getLogger(ProductUrlTransLocal.class);

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

	private static String receiveApi = ConfigUtil.getString("crawl.task.send.domain")
			+ ConfigUtil.getString("local.task.result.send.url");

	private static String key = "";
	private static String value = "";
	static {
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			receiveApi = ConfigUtil.getString("crawl.task.send.domain.test")
					+ ConfigUtil.getString("local.task.result.send.url");
		}

		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			key = "webdriver.chrome.driver";
			value = ".\\conf\\tools\\chromedriver.exe";
		} else {
			key = ConfigUtil.getString("selenium.drive.name");
			value = ConfigUtil.getString("selenium.drive.path");
		}
	}

	private static WebDriver driver;
	private static WebDriver jdDriver;
	private static String baseUrl = "https://pub.alimama.com/promo/search/index.htm";
	private static String jdBaseUrl = "https://media.jd.com/gotoadv/goods";

	private static int sleepTimeBegin = 100;
	private static int sleepTimeEnd = 500;

	// 初始化队列，定义队列长度
	final static DisruptorQueueImpl queue = new DisruptorQueueImpl("name", ProducerType.SINGLE, 256,
			new BlockingWaitStrategy());

	static {
		init();
		scheduleTaobao();
		scheduleJd();
		System.setProperty(key, value);
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			driver = new ChromeDriver();
			jdDriver = new ChromeDriver();
		} else {
			driver = new FirefoxDriver();
			jdDriver = new FirefoxDriver();
		}
		driver.get(baseUrl);
		jdDriver.get(jdBaseUrl);
		driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
		jdDriver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
	}

	public static void init() {
		Thread consumer = new Thread(new Runnable() {// 消费者
			@Override
			public void run() {
				while (true) {
					TkInfoTask tkInfoTask = null;
					try {
						tkInfoTask = (TkInfoTask) queue.take();
						logger.info("consumer..");
						// 淘宝通过URl搜索商品
						if (tkInfoTask.getProductUrl().contains("http")) {
							getTKUrl(tkInfoTask);
						}
						// 京东通过商品ID搜索商品
						else {
							getJdTKUrl(tkInfoTask);
						}
					} catch (Exception e) {
						logger.info(e.getMessage());
						// e.printStackTrace();
						String gString = GsonUtil.GsonString(tkInfoTask);
						List<NameValuePair> nvpList = new ArrayList<>();
						nvpList.add(new BasicNameValuePair("gString", gString));
						// 发送结果数据
						try {
							HttpcomponentsUtil.sendHttps(nvpList, receiveApi);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		consumer.start();
	}

	public static WebDriver getDriver() {
		return driver;
	}

	public static void put(TkInfoTask tkInfoTask) {
		logger.info("publish..");
		queue.publish(tkInfoTask);
	}

	public static Object get() {
		logger.info("consumer..");
		return queue.poll();
	}

	public static void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	private static void getTKUrl(TkInfoTask tkInfoTask) throws Exception {
		try {
			driver.findElement(By.id("q")).clear();
			driver.findElement(By.id("q")).sendKeys(tkInfoTask.getProductUrl());

			driver.findElement(By.xpath("//div[@id='magix_vf_header']/div/div/div[2]/div[2]/button")).click();
			// 点击搜索按钮后sleep
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));

			// 滚动到图片元素
			WebElement element0 = driver.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[1]/a/img"));
			PageUtils.scrollToElementAndPick(element0, driver);

			String productImgUrl = driver.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[1]/a/img"))
					.getAttribute("src");
			String productName = driver
					.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[1]/p/a/span")).getText();
			String price = "0";
			String sales = "0";
			String commision = "0";
			String rate = "0";
			String shopName = "";
			String quanMianzhi="0";
			shopName = driver.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[3]/div[1]/span/a/span"))
					.getText();
			try {
				// 存在优惠券的处理方式
				quanMianzhi=driver
						.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[2]/span[1]/span[2]/span"))
						.getText();
				price = driver
						.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[1]/span[2]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[1]/span[4]"))
								.getText();
				sales = driver
						.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[2]/span[2]/span"))
						.getText();
				commision = driver
						.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[4]/span[2]/span[2]/span[2]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[4]/span[2]/span[2]/span[4]"))
								.getText();
				rate = driver
						.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[4]/span[1]/span[2]/span[1]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[4]/span[1]/span[2]/span[3]"))
								.getText();
			} catch (Exception e) {
				// 不存在优惠券的处理方式
				price = driver
						.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[2]/span[1]/span[2]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[2]/span[1]/span[4]"))
								.getText();
				sales = driver
						.findElement(By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[2]/span[2]/span[2]/span"))
						.getText();
				commision = driver
						.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[2]/span[2]/span[2]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[2]/span[2]/span[4]"))
								.getText();
				rate = driver
						.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[1]/span[2]/span[1]"))
						.getText()
						+ "."
						+ driver.findElement(
								By.xpath("//*[@id='J_search_results']/div/div/div[2]/div[3]/span[1]/span[2]/span[3]"))
								.getText();
			}

			tkInfoTask.setProductName(productName);
			tkInfoTask.setProductImgUrl(productImgUrl);
			tkInfoTask.setPrice(Double.valueOf(price.replace(",", "")));
			tkInfoTask.setSales(Integer.parseInt(sales));
			tkInfoTask.setCommision(Double.valueOf(commision));
			tkInfoTask.setRate(Double.valueOf(rate));
			tkInfoTask.setShopName(shopName);
			tkInfoTask.setQuanMianzhi(Double.valueOf(quanMianzhi));

			// 点击立即推广按钮
			WebElement element1 = driver.findElement(By.linkText("立即推广"));
			PageUtils.scrollToElementAndClick(element1, driver);

			// 点击确定按钮
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			WebElement element2 = driver.findElement(By.xpath("//div[@id='J_global_dialog']/div/div[3]/button"));
			PageUtils.scrollToElementAndClick(element2, driver);

			// 点击复制按钮
			// Thread.sleep(2000);
			// WebElement
			// element3=driver.findElement(By.xpath("(//button[@type='button'])[6]"));
			// PageUtils.scrollToElementAndClick(element3, driver);
			String tkurl = "";
			String quanurl = "";
			try {
				tkurl = driver.findElement(By.id("clipboard-target-1")).getAttribute("value");
				quanurl = driver.findElement(By.id("clipboard-target-2")).getAttribute("value");
			} catch (Exception e) {
				tkurl = driver.findElement(By.xpath("//*[@id='clipboard-target']")).getAttribute("value");
			}

			// 点击淘口令按钮
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			WebElement element3 = driver.findElement(By.xpath("//*[@id='magix_vf_code']/div/div[1]/ul/li[4]"));
			PageUtils.scrollToElementAndClick(element3, driver);

			String tcode = "";
			String quancode = "";

			try {
				tcode = driver.findElement(By.xpath("//*[@id='clipboard-target-1']")).getAttribute("value");
				quancode = driver.findElement(By.xpath("//*[@id='clipboard-target-2']")).getAttribute("value");

				// 点击关闭按钮
				Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
				WebElement element4 = driver.findElement(By.xpath("//div[@id='magix_vf_code']/div/div[3]/button"));
				PageUtils.scrollToElementAndClick(element4, driver);
			} catch (Exception e) {
				tcode = driver.findElement(By.xpath("//*[@id='clipboard-target']")).getAttribute("value");

				// 点击关闭按钮
				Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
				WebElement element4 = driver.findElement(By.xpath("//*[@id='magix_vf_code']/div/div[3]/button[2]"));
				PageUtils.scrollToElementAndClick(element4, driver);

			}

			tkInfoTask.setTkurl(tkurl);
			tkInfoTask.setQuanUrl(quanurl);
			tkInfoTask.setTcode(tcode);
			tkInfoTask.setQuanCode(quancode);

			// 调用HTTP接口 返回结果数据
			String gString = GsonUtil.GsonString(tkInfoTask);
			List<NameValuePair> nvpList = new ArrayList<>();
			nvpList.add(new BasicNameValuePair("gString", gString));
			// 发送结果数据
			HttpcomponentsUtil.sendHttps(nvpList, receiveApi);
		} catch (Exception e) {
			e.printStackTrace();
			tkInfoTask.setStatus(1);
			// 调用HTTP接口 返回结果数据
			String gString = GsonUtil.GsonString(tkInfoTask);
			List<NameValuePair> nvpList = new ArrayList<>();
			nvpList.add(new BasicNameValuePair("gString", gString));
			// 发送结果数据
			HttpcomponentsUtil.sendHttps(nvpList, receiveApi);
			driver.navigate().refresh();
			return;
		}
	}

	private static void getJdTKUrl(TkInfoTask tkInfoTask) throws Exception {
		try {
			jdDriver.findElement(By.id("keyword")).clear();
			jdDriver.findElement(By.id("keyword")).sendKeys(tkInfoTask.getProductUrl());
			jdDriver.findElement(By.xpath("//*[@id='b_search']")).click();

			// 点击搜索按钮后sleep
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));

			// 滚动到图片元素
			WebElement element0 = jdDriver.findElement(
					By.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[1]/a/img"));
			PageUtils.scrollToElementAndPick(element0, jdDriver);

			// 选择无线，无线佣金比例高
			jdDriver.findElement(By
					.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[2]/select/option[2]"))
					.click();

			String productImgUrl = jdDriver
					.findElement(
							By.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[1]/a/img"))
					.getAttribute("src");
			String productName = jdDriver
					.findElement(By.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/a"))
					.getText();
			String price = "0";
			String sales = "0";
			String commision = "0";
			String rate = "0";
			String shopName = "";
			String quanMianzhi="0";
			price = jdDriver
					.findElement(By
							.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[2]/span[2]/span"))
					.getText();
			sales = jdDriver
					.findElement(By
							.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[3]/div[5]/em"))
					.getText();
			commision = jdDriver
					.findElement(By
							.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[3]/div[4]/em"))
					.getText();
			rate = jdDriver
					.findElement(By
							.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[3]/div[2]/em"))
					.getText();
			shopName = jdDriver
					.findElement(By
							.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[1]/div[2]/div[4]/a"))
					.getText();
			price = price.replace("￥", "");
			commision = commision.replace("￥", "");
			rate = rate.replace("%", "");

			tkInfoTask.setProductName(productName);
			tkInfoTask.setProductImgUrl(productImgUrl);
			tkInfoTask.setPrice(Double.valueOf(price.replace(",", "")));
			tkInfoTask.setSales(Integer.parseInt(sales));
			tkInfoTask.setCommision(Double.valueOf(commision));
			tkInfoTask.setRate(Double.valueOf(rate));
			tkInfoTask.setShopName(shopName);
			tkInfoTask.setQuanMianzhi(Double.valueOf(quanMianzhi));

			// 点击我要推广按钮
			WebElement element1 = jdDriver
					.findElement(By.xpath("//*[@id='goodsQueryForm']/div[2]/div/div/div/div[2]/ul/li/div[2]/a"));
			PageUtils.scrollToElementAndClick(element1, jdDriver);

			// 点击选择推广类型、推广位名称
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			jdDriver.findElement(By.xpath("//*[@id='adtTypeDiv']/div[2]/label[4]")).click();
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			jdDriver.findElement(By.xpath("//*[@id='spaceName']/option[2]")).click();

			// 点击获取代码按钮
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd));
			WebElement element2 = jdDriver.findElement(By.xpath("//*[@id='getcode-btn']"));
			PageUtils.scrollToElementAndClick(element2, jdDriver);

			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin * 10, sleepTimeEnd * 4));
			String tkurl = "";

			tkurl = jdDriver.findElement(By.xpath("//*[@id='shotCodeHref']")).getAttribute("value");
			System.out.println(tkurl);

			// 点击关闭按钮
			// Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin,
			// sleepTimeEnd));
			WebElement element4 = jdDriver.findElement(By.xpath("//*[@id='getSJBCode']/div/div/div[3]/a[2]"));
			PageUtils.scrollToElementAndClick(element4, jdDriver);

			tkInfoTask.setTkurl(tkurl);
			tkInfoTask.setQuanUrl("");
			tkInfoTask.setTcode("");
			tkInfoTask.setQuanCode("");

			// 调用HTTP接口 返回结果数据
			String gString = GsonUtil.GsonString(tkInfoTask);
			List<NameValuePair> nvpList = new ArrayList<>();
			nvpList.add(new BasicNameValuePair("gString", gString));
			// 发送结果数据
			HttpcomponentsUtil.sendHttps(nvpList, receiveApi);

		} catch (Exception e) {
			e.printStackTrace();
			tkInfoTask.setStatus(1);
			// 调用HTTP接口 返回结果数据
			String gString = GsonUtil.GsonString(tkInfoTask);
			List<NameValuePair> nvpList = new ArrayList<>();
			nvpList.add(new BasicNameValuePair("gString", gString));
			// 发送结果数据
			HttpcomponentsUtil.sendHttps(nvpList, receiveApi);
			jdDriver.navigate().refresh();
			return;
		}
	}

	private static void scheduleTaobao() {
		// 延迟5-10分钟，间隔5-10分钟执行
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("taobao refresh...");
					driver.navigate().refresh();
					jdDriver.navigate().refresh();
				} catch (Exception e) {
					logger.error("taobao refresh error:[{}]", e);
				}
			}
		}, NumberUtil.getRandomNumber(5, 10), NumberUtil.getRandomNumber(5, 10), TimeUnit.MINUTES);
	}

	private static void scheduleJd() {
		// 延迟3-5分钟，间隔3-5分钟执行
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("jd refresh...");
					jdDriver.navigate().refresh();
				} catch (Exception e) {
					logger.error("jd refresh error:[{}]", e);
				}
			}
		}, NumberUtil.getRandomNumber(3, 5), NumberUtil.getRandomNumber(3, 5), TimeUnit.MINUTES);
	}
}
