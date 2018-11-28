package com.bt.om.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adtime.common.lang.StringUtil;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;

/**
 * 从系统目录下读取阿里妈妈订单报表文件，导入数据库
 */
@Component
public class TaobaoOrderInputTask {
	private static final Logger logger = Logger.getLogger(TaobaoOrderInputTask.class);

	@Autowired
	private ITkOrderInputService tkOrderInputService;

	@Scheduled(cron = "0 0/5 * * * ?")
	public void orderInputTask() {
		logger.info("从系统目录下读取阿里妈妈订单报表文件，导入数据库");
		try {
			orderTaobaoInput();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void orderTaobaoInput() throws Exception {
		try {
			// 开始读取分析下载的报告
			String filePath = ConfigUtil.getString("report.file.path") + "TaokeDetail-"
					+ DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN) + ".xls";
			List<TkOrderInput> tkOrderInputList = readTaobaoReport(filePath);
			if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
				// 先清空表中数据，然后再插入数据
				tkOrderInputService.truncateTkOrderInput();
				// 先删除本台服务器配置的淘宝联盟账号下的订单数据
				// tkOrderInputService.deleteByAccount(ConfigUtil.getString("alimama.account"));
				for (TkOrderInput tkOrderInput : tkOrderInputList) {
					tkOrderInputService.insert(tkOrderInput);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<TkOrderInput> readTaobaoReport(String filePath) {
		List<TkOrderInput> tkOrderInputList = new ArrayList<TkOrderInput>();
		File file = new File(filePath);
		if (!file.exists())
			logger.info("文件不存在");
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			// 1.读取Excel的对象
			POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
			// 2.Excel工作薄对象
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
			// 3.Excel工作表对象
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			// 总行数
			int rowLength = hssfSheet.getLastRowNum() + 1;
			// 4.得到Excel工作表的行
			HSSFRow hssfRow = hssfSheet.getRow(0);
			// 总列数
			int colLength = hssfRow.getLastCellNum();
			// 得到Excel指定单元格中的内容
			HSSFCell hssfCell = hssfRow.getCell(0);
			// 得到单元格样式
			CellStyle cellStyle = hssfCell.getCellStyle();

			for (int i = 1; i < rowLength; i++) {
				TkOrderInput tkOrderInput = new TkOrderInput();
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
				tkOrderInput.setProductNum((int) (hssfCell7.getNumericCellValue()));
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
				if (StringUtil.isNotEmpty(hssfCell11.getStringCellValue())) {
					tkOrderInput
							.setIncomeRate(Double.valueOf(((hssfCell11.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell12 = hssfRow1.getCell(11);
				if (hssfCell12 != null) {
					hssfCell12.setCellType(CellType.STRING);
				}
				if (StringUtil.isNotEmpty(hssfCell12.getStringCellValue())) {
					tkOrderInput
							.setDivideRate(Double.valueOf(((hssfCell12.getStringCellValue()).replace("%", "")).trim()));
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
				if (hssfCell17 != null) {
					tkOrderInput.setSettleTime(hssfCell17.getStringCellValue());
				}
				HSSFCell hssfCell18 = hssfRow1.getCell(17);
				if (hssfCell18 != null) {
					hssfCell18.setCellType(CellType.STRING);
				}
				if (StringUtil.isNotEmpty(hssfCell18.getStringCellValue())) {
					tkOrderInput.setCommissionRate(
							Double.valueOf(((hssfCell18.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell19 = hssfRow1.getCell(18);
				tkOrderInput.setCommissionMoney(hssfCell19.getNumericCellValue());

				HSSFCell hssfCell20 = hssfRow1.getCell(19);
				if (hssfCell20 != null) {
					hssfCell20.setCellType(CellType.STRING);
					if (StringUtil.isNotEmpty(hssfCell20.getStringCellValue())) {
						tkOrderInput.setTechService(
								Double.valueOf(((hssfCell20.getStringCellValue()).replace("%", "")).trim()));
					}
				} else {
					tkOrderInput.setTechService(0d);
				}

				HSSFCell hssfCell21 = hssfRow1.getCell(20);
				if (hssfCell21 != null) {
					hssfCell21.setCellType(CellType.STRING);
				}
				if (StringUtil.isNotEmpty(hssfCell21.getStringCellValue())) {
					tkOrderInput.setSubsidyRate(
							Double.valueOf(((hssfCell21.getStringCellValue()).replace("%", "")).trim()));
				}
				HSSFCell hssfCell22 = hssfRow1.getCell(21);
				tkOrderInput.setSubsidyMoney(hssfCell22.getNumericCellValue());
				HSSFCell hssfCell23 = hssfRow1.getCell(22);
				tkOrderInput.setSubsidyType(hssfCell23.getStringCellValue());
				HSSFCell hssfCell24 = hssfRow1.getCell(23);
				tkOrderInput.setDealPlatform(hssfCell24.getStringCellValue());
				HSSFCell hssfCell25 = hssfRow1.getCell(24);
				tkOrderInput.setThirdServiceFrom(hssfCell25.getStringCellValue());
				HSSFCell hssfCell26 = hssfRow1.getCell(25);
				tkOrderInput.setOrderId(hssfCell26.getStringCellValue());
				HSSFCell hssfCell27 = hssfRow1.getCell(26);
				tkOrderInput.setCatName(hssfCell27.getStringCellValue());
				HSSFCell hssfCell28 = hssfRow1.getCell(27);
				tkOrderInput.setSourceMediaId(hssfCell28.getStringCellValue());
				HSSFCell hssfCell29 = hssfRow1.getCell(28);
				tkOrderInput.setSourceMediaName(hssfCell29.getStringCellValue());
				HSSFCell hssfCell30 = hssfRow1.getCell(29);
				tkOrderInput.setAdId(hssfCell30.getStringCellValue());
				HSSFCell hssfCell31 = hssfRow1.getCell(30);
				tkOrderInput.setAdName(hssfCell31.getStringCellValue());

				tkOrderInput.setUpdateTime(new Date());
				tkOrderInput.setAccount(ConfigUtil.getString("alimama.account"));

				tkOrderInputList.add(tkOrderInput);
			}
			file.delete();
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return tkOrderInputList;
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((TaobaoOrderInputTask) ctx.getBean("taobaoOrderInputTask")).orderInputTask();
	}
}