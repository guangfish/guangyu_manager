package com.bt.om.selenium;

import org.apache.poi.hssf.usermodel.HSSFCell;  
import org.apache.poi.hssf.usermodel.HSSFRow;  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.poifs.filesystem.POIFSFileSystem;  
import org.apache.poi.ss.usermodel.CellStyle;  
import org.apache.poi.ss.usermodel.CellType;  
  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
  
/** 
 * Created by M_WBCG on 2017/7/14. 
 */  
public class ReadExcelForHSSF {  
  
    public void readTaobaoReport(String filePath) {  
        File file = new File(filePath);  
        if (!file.exists())  
            System.out.println("文件不存在");  
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
                //获取Excel工作表的行  
                HSSFRow hssfRow1 = hssfSheet.getRow(i);  
                for (int j = 0; j < colLength; j++) {  
                    //获取指定单元格  
                    HSSFCell hssfCell1 = hssfRow1.getCell(j);  
  
                    //Excel数据Cell有不同的类型，当我们试图从一个数字类型的Cell读取出一个字符串时就有可能报异常：  
                    //Cannot get a STRING value from a NUMERIC cell  
                    //将所有的需要读的Cell表格设置为String格式  
                    if (hssfCell1 != null) {  
                        hssfCell1.setCellType(CellType.STRING);  
                    }  
  
                    //获取每一列中的值  
                    System.out.print(hssfCell1.getStringCellValue() + "\t");  
                }  
                System.out.println();  
            }  
            
//            file.delete();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args) {  
        new ReadExcelForHSSF().readTaobaoReport("C:\\Users\\Lenovo\\Downloads\\TaokeDetail-2018-05-02.xls");  
    }  
} 
