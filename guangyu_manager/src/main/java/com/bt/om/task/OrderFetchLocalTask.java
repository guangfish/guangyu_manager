package com.bt.om.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.openqa.selenium.By;
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
import com.bt.om.entity.TkOrderInput;
import com.bt.om.selenium.util.PageUtils;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;
import com.google.gson.Gson;

/**
 * 淘宝订单报表下载入库
 */
//@Component
public class OrderFetchLocalTask {
	private static final Logger logger = Logger.getLogger(OrderFetchLocalTask.class);
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static String reportTb2DbUrl = ConfigUtil.getString("crawl.task.send.domain")
			+ ConfigUtil.getString("local.report.taobao.url");
	
	private static String key = "";
	private static String value = "";
	static {
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			reportTb2DbUrl = ConfigUtil.getString("crawl.task.send.domain.test")
					+ ConfigUtil.getString("local.report.taobao.url");
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
	private static String baseUrl = "http://pub.alimama.com/myunion.htm?spm=a2320.7388781.a214tr8.d006.6f372030YQy6Yz#!/report/detail/taoke";

	private static int sleepTimeBegin = 1000;
	private static int sleepTimeEnd = 2000;

	static {
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

	@Scheduled(cron = "0 0 7-23 * * ?")
//	@Scheduled(cron = "0 0/2 * * * ?")
	public void orderFetchTask() {
		logger.info("淘宝订单报表下载入库");
		try {
			orderTaobaoFetch();
		} catch (Exception e) {
			e.printStackTrace();
			driver.navigate().refresh();
		}
	}
	
	private void orderTaobaoFetch() throws Exception { 
		try{						
//			driver.navigate().refresh();
//			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin * 2, sleepTimeEnd * 2));
			
			WebElement element0 = driver.findElement(By.xpath("//*[@id='sitemapTimeRange']"));
			PageUtils.scrollToElementAndClick(element0, driver);
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin, sleepTimeEnd)); 
			
//			String idd = driver.findElement(By.cssSelector("a.quick-item[4]")).getAttribute("id");
			List<WebElement> element = driver.findElements(By.cssSelector("a.quick-item")); 
			PageUtils.scrollToElementAndClick(element.get(3), driver);
//			element.get(3).click();
			System.out.println(element.size());
//			driver.findElement(By.xpath("//*[@id="brix_1453"]/div/div/ul/li[4]/a")).click();			
            
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin*2, sleepTimeEnd*2));
			String id = driver.findElement(By.xpath("//*[@id='magix_vf_main']/div[1]")).getAttribute("id");
			System.out.println(id);
			//点击下载报告			
			WebElement element1 = driver.findElement(By.xpath("//*[@id='"+id+"']/div[2]/div[1]/div[1]/a"));
			PageUtils.scrollToElementAndClick(element1, driver);
			//点击下载报告后等待时间
			Thread.sleep(NumberUtil.getRandomNumber(sleepTimeBegin*5, sleepTimeEnd*5));
			
			
			//开始读取分析下载的报告			
			String filePath=ConfigUtil.getString("report.file.path")+"TaokeDetail-"+DateUtil.dateFormate(new Date(),DateUtil.CHINESE_PATTERN)+".xls";						
			List<TkOrderInput> tkOrderInputList = readTaobaoReport(filePath);

			Gson gson = new Gson();
			String gString = gson.toJson(tkOrderInputList);
			// System.out.println(gString);
			List<NameValuePair> nvpList = new ArrayList<>();
			nvpList.add(new BasicNameValuePair("gString", gString));
			// 发送结果数据
			HttpcomponentsUtil.postReq(nvpList, reportTb2DbUrl);

//			for(TkOrderInput tkOrderInput:tkOrderInputList){
//				List<TkOrderInput> tkOrderInputExist = tkOrderInputService.selectByOrderId(tkOrderInput.getOrderId());
//				if(tkOrderInputExist!=null && tkOrderInputExist.size()>0){
//					TkOrderInput tkOrderInput1 = tkOrderInputExist.get(0);
//					if(!(tkOrderInput1.getOrderStatus()).equals(tkOrderInput.getOrderStatus())){
//						tkOrderInputService.updateByOrderId(tkOrderInput);
//					}					
//				}else{
//					tkOrderInputService.insert(tkOrderInput);
//				}
//			}
			
			driver.navigate().refresh();
		}catch(Exception e){
			e.printStackTrace();
			driver.navigate().refresh();
			return;
		}
	}
	
	private static List<TkOrderInput> readTaobaoReport(String filePath) {  
		List<TkOrderInput> tkOrderInputList=new ArrayList<TkOrderInput>();
        File file = new File(filePath);  
        if (!file.exists())  
        	logger.info("文件不存在");  
        try {  
            //1.读取Excel的对象  
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));  
            //2.Excel工作薄对象  
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);  
            //3.Excel工作表对象  
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);  
            //总行数  
            int rowLength = hssfSheet.getLastRowNum()+1;  
            //4.得到Excel工作表的行  
            HSSFRow hssfRow = hssfSheet.getRow(0);  
            //总列数  
            int colLength = hssfRow.getLastCellNum();  
            //得到Excel指定单元格中的内容  
            HSSFCell hssfCell = hssfRow.getCell(0);  
            //得到单元格样式  
            CellStyle cellStyle = hssfCell.getCellStyle();  
  
			for (int i = 1; i < rowLength; i++) {
				TkOrderInput tkOrderInput=new TkOrderInput();
				// 获取Excel工作表的行
				HSSFRow hssfRow1 = hssfSheet.getRow(i);
				// 获取指定单元格
				HSSFCell hssfCell1 = hssfRow1.getCell(0);
				tkOrderInput.setCreateTime(hssfCell1.getStringCellValue());
				HSSFCell hssfCell2 = hssfRow1.getCell(1);
				tkOrderInput.setClickTime(hssfCell2.getStringCellValue());
				HSSFCell hssfCell3 = hssfRow1.getCell(2);
				tkOrderInput.setProductInfo(hssfCell3.getStringCellValue());
				HSSFCell hssfCell4 = hssfRow1.getCell(3);
				tkOrderInput.setProductId(hssfCell4.getStringCellValue());
				HSSFCell hssfCell5 = hssfRow1.getCell(4);
				tkOrderInput.setSellerWangwang(hssfCell5.getStringCellValue());
				HSSFCell hssfCell6 = hssfRow1.getCell(5);
				tkOrderInput.setShopName(hssfCell6.getStringCellValue());
				HSSFCell hssfCell7 = hssfRow1.getCell(6);
				tkOrderInput.setProductNum((int)(hssfCell7.getNumericCellValue()));
				HSSFCell hssfCell8 = hssfRow1.getCell(7);
				tkOrderInput.setPrice(hssfCell8.getNumericCellValue());
				HSSFCell hssfCell9 = hssfRow1.getCell(8);
				tkOrderInput.setOrderStatus(hssfCell9.getStringCellValue());
				HSSFCell hssfCell10 = hssfRow1.getCell(9);
				tkOrderInput.setOrderType(hssfCell10.getStringCellValue());
				HSSFCell hssfCell11 = hssfRow1.getCell(10);
				if (hssfCell11 != null) {  
                    hssfCell11.setCellType(CellType.STRING);  
                }
				if(StringUtil.isNotEmpty(hssfCell11.getStringCellValue())){
					tkOrderInput.setIncomeRate(Double.valueOf(((hssfCell11.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell12 = hssfRow1.getCell(11);
				if (hssfCell12 != null) {  
                    hssfCell12.setCellType(CellType.STRING);  
                }
				if(StringUtil.isNotEmpty(hssfCell12.getStringCellValue())){
					tkOrderInput.setDivideRate(Double.valueOf(((hssfCell12.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell13 = hssfRow1.getCell(12);
				tkOrderInput.setPayMoney(hssfCell13.getNumericCellValue());
				HSSFCell hssfCell14 = hssfRow1.getCell(13);
				tkOrderInput.setEffectEstimate(hssfCell14.getNumericCellValue());
				HSSFCell hssfCell15 = hssfRow1.getCell(14);
				tkOrderInput.setSettleMoney(hssfCell15.getNumericCellValue());
				HSSFCell hssfCell16 = hssfRow1.getCell(15);
				tkOrderInput.setEstimateIncome(hssfCell16.getNumericCellValue());
				HSSFCell hssfCell17 = hssfRow1.getCell(16);
				if(hssfCell17 != null){
					tkOrderInput.setSettleTime(hssfCell17.getStringCellValue());
				}
				HSSFCell hssfCell18 = hssfRow1.getCell(17);
				if (hssfCell18 != null) {  
                    hssfCell18.setCellType(CellType.STRING);  
                }
				if(StringUtil.isNotEmpty(hssfCell18.getStringCellValue())){
					tkOrderInput.setCommissionRate(Double.valueOf(((hssfCell18.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell19 = hssfRow1.getCell(18);
				tkOrderInput.setCommissionMoney(hssfCell19.getNumericCellValue());
				HSSFCell hssfCell20 = hssfRow1.getCell(19);
				if (hssfCell20 != null) {  
                    hssfCell20.setCellType(CellType.STRING);  
                }
				if(StringUtil.isNotEmpty(hssfCell20.getStringCellValue())){
					tkOrderInput.setSubsidyRate(Double.valueOf(((hssfCell20.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell21 = hssfRow1.getCell(20);
				tkOrderInput.setSubsidyMoney(hssfCell21.getNumericCellValue());
				HSSFCell hssfCell22 = hssfRow1.getCell(21);
				tkOrderInput.setSubsidyType(hssfCell22.getStringCellValue());
				HSSFCell hssfCell23 = hssfRow1.getCell(22);
				tkOrderInput.setDealPlatform(hssfCell23.getStringCellValue());
				HSSFCell hssfCell24 = hssfRow1.getCell(23);
				tkOrderInput.setThirdServiceFrom(hssfCell24.getStringCellValue());
				HSSFCell hssfCell25 = hssfRow1.getCell(24);
				tkOrderInput.setOrderId(hssfCell25.getStringCellValue());
				HSSFCell hssfCell26 = hssfRow1.getCell(25);
				tkOrderInput.setCatName(hssfCell26.getStringCellValue());
				HSSFCell hssfCell27 = hssfRow1.getCell(26);
				tkOrderInput.setSourceMediaId(hssfCell27.getStringCellValue());
				HSSFCell hssfCell28 = hssfRow1.getCell(27);
				tkOrderInput.setAdId(hssfCell28.getStringCellValue());
				HSSFCell hssfCell29 = hssfRow1.getCell(28);
				tkOrderInput.setAdName(hssfCell29.getStringCellValue());
				
				tkOrderInput.setUpdateTime(new Date());
				
				tkOrderInputList.add(tkOrderInput);
			}			
            file.delete();
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;
        }  
        return tkOrderInputList;
    }
	
	private static void schedule() {
		// 延迟3-5分钟，间隔3-5分钟执行
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("OrderFetchTask refresh...");
					driver.navigate().refresh();
				} catch (Exception e) {
					logger.error("OrderFetchTask refresh error:[{}]", e);
				}
			}
		}, NumberUtil.getRandomNumber(5, 10), NumberUtil.getRandomNumber(5, 10), TimeUnit.MINUTES);
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((OrderFetchLocalTask) ctx.getBean("orderFetchLocalTask")).orderFetchTask();
	}
}
