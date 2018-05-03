package com.bt.om.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumTest {
	private static String key="webdriver.chrome.driver";
	private static String value=".\\conf\\tools\\chromedriver.exe";
	
//	private static String key="webdriver.gecko.driver";
//	private static String value=".\\conf\\tools\\geckodriver.exe";

	public static void main(String[] args) {
		getTitle();
//		getElementById();
//		login();
//		getTKlink();
	}
	
	private static void getTKlink(){
		System.setProperty(key, value);
		WebDriver driver  = new ChromeDriver();
		String baseUrl = "https://pub.alimama.com/promo/search/index.htm";
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    
	    driver.get(baseUrl);
	    driver.findElement(By.id("q")).click();
	    driver.findElement(By.id("q")).clear();
	    driver.findElement(By.id("q")).sendKeys("http://item.taobao.com/item.htm?id=562642420572");
	    driver.findElement(By.xpath("//div[@id='magix_vf_header']/div/div/div[2]/div[2]/button")).click();
	    driver.findElement(By.linkText("立即推广")).click();
	    driver.findElement(By.xpath("//div[@id='J_global_dialog']/div/div[3]/button")).click();
	    driver.findElement(By.xpath("(//button[@type='button'])[6]")).click();
	    driver.findElement(By.xpath("//div[@id='magix_vf_code']/div/div[3]/button")).click();
	}
	
	private static void login(){
		System.setProperty(key, value);
		WebDriver driver  = new ChromeDriver();
        driver.get("https://www.alimama.com/index.htm");
        driver.manage().window().maximize();

        // 找到链接元素
        WebElement link1 = driver.findElement(By.id("J_menu_login"));       
        // 点击链接
        link1.click();
        
//        try {
//			Thread.sleep(15000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
       // 找到链接元素
        WebElement link2 = driver.findElement(By.id("J_Quick2Static"));       
        // 点击链接
        link2.click();
        
//        WebElement username = driver.findElement(By.id("TPL_username_1"));// 定位用户名输入框
//		username.sendKeys("chj8023");// 输入用户名
//		
//		WebElement password = driver.findElement(By.id("TPL_password_1"));// 定位密码输入框
//		password.sendKeys("chjssj1981822");// 输入密码
//		
//		WebElement loginbtn = driver.findElement(By.xpath("//*[@id='J_SubmitStatic']"));// 定位登录按钮，xpath相对路径
//		loginbtn.click();// 点击登录按钮
		
		driver.close();
    
	}
	
	private static void getElementById(){
		System.setProperty(key, value);
		WebDriver driver  = new ChromeDriver();
        driver.get("http://www.baidu.com");
         
        WebElement searchBox = driver.findElement(By.id("kw"));
        searchBox.sendKeys("小坦克 博客园");
        WebElement searchButton = driver.findElement(By.id("su"));
        searchButton.submit();
        
        driver.close();
	}
	
	private static void getTitle(){
		System.setProperty(key, value);  
        
        //初始化一个火狐浏览器实例，实例名称叫driver  
//        WebDriver driver = new FirefoxDriver();  
		WebDriver driver = new ChromeDriver();
        //最大化窗口  
        driver.manage().window().maximize();  
        //设置隐性等待时间  
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);  
          
        // get()打开一个站点  
        driver.get("https://www.baidu.com");  
        //getTitle()获取当前页面title的值  
        System.out.println("当前打开页面的标题是： "+ driver.getTitle());  
          
        //关闭并退出浏览器  
        driver.quit();  
	}

}
