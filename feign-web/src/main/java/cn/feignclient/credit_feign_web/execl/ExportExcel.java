package cn.feignclient.credit_feign_web.execl;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;



public class ExportExcel<T>  {  
    public void exportExcel(Collection<T> dataset, OutputStream out)  {  
        exportExcel("报表EXCEL", null, dataset, out, "yyyy-MM-dd");  
    }  
  
    public void exportExcel(String[] headers, Collection<T> dataset,  OutputStream out)  {  
        exportExcel("报表EXCEL", headers, dataset, out, "yyyy-MM-dd");  
    }  
  
    public void exportExcel(String[] headers, Collection<T> dataset, OutputStream out, String pattern)  {  
        exportExcel("报表EXCEL", headers, dataset, out, pattern);  
    }  
  
     
    @SuppressWarnings("unchecked")  
    public void exportExcel(String title, String[] headers,   Collection<T> dataset, OutputStream out, String pattern)  {  
      
        HSSFWorkbook workbook = new HSSFWorkbook();   
        HSSFSheet sheet = workbook.createSheet(title);     
        sheet.setDefaultColumnWidth((short) 15);  
        HSSFCellStyle style = workbook.createCellStyle();     
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);  
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
      
        HSSFFont font = workbook.createFont();  
        font.setColor(HSSFColor.VIOLET.index);  
        font.setFontHeightInPoints((short) 12);  
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
        style.setFont(font);  
        HSSFCellStyle style2 = workbook.createCellStyle();  
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);  
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
     
        HSSFFont font2 = workbook.createFont();  
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);     
        style2.setFont(font2);  
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();       
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,  0, 0, 0, (short) 4, 2, (short) 6, 5));      
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));        
        comment.setAuthor("leno");         
        
        //设置字体
        HSSFFont font3 = workbook.createFont();
        HSSFRow row = sheet.createRow(0);  
        for (short i = 0; i < headers.length; i++)  {  
            HSSFCell cell = row.createCell(i);  
            cell.setCellStyle(style);  
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);  
            cell.setCellValue(text);  
        }  
   
        Iterator<T> it=null;
 
         if(dataset !=null){  it = dataset.iterator();  }
         
       // Iterator<T> it = dataset.iterator();  
        int index = 0;  
        if(it!=null)
        while (it.hasNext()) {  
            index++;  
            row = sheet.createRow(index);  
            T t = (T) it.next();        
            Field[] fields = t.getClass().getDeclaredFields();  
            
            for (short i = 0; i < fields.length; i++)  {  
                HSSFCell cell = row.createCell(i);  
                cell.setCellStyle(style2);  
                Field field = fields[i];  
                String fieldName = field.getName();  
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);  
                try   {  
                    Class tCls = t.getClass();  
                    Method getMethod = tCls.getMethod(getMethodName,  new Class[] {});  
                    Object value = getMethod.invoke(t, new Object[] {});  
                    String textValue = null;  
                    
                  if (value instanceof Date){  
                        Date date = (Date) value;  
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  
                        textValue = sdf.format(date);  
                        
                    }  
                    else   if (value instanceof byte[]){                      
                        row.setHeightInPoints(60);  
                        sheet.setColumnWidth(i, (short) (35.7 * 80));  
                        byte[] bsValue = (byte[]) value;  
                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,  1023, 255, (short) 6, index, (short) 6, index);  
                        anchor.setAnchorType(2);  
                        patriarch.createPicture(anchor, workbook.addPicture( bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));  
                    } else {  
                    	 if(value != null){
                    		 textValue = value.toString(); 
                    	 }else{
                    		 textValue = ""; 
                    	 }
                        
                    }  
                   
                    if (textValue != null)  {  
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");  
                        Matcher matcher = p.matcher(textValue);  
                        if (matcher.matches()){  
                             
                            cell.setCellValue(Double.parseDouble(textValue));  
                        }else{  
                            HSSFRichTextString richString = new HSSFRichTextString( textValue);  
                           
                            font3.setColor(HSSFColor.BLUE.index);  
                            richString.applyFont(font3);  
                            cell.setCellValue(richString);  
                        }  
                    } 
                    }catch (SecurityException e) {  
                    e.printStackTrace();  
                }  
                catch (NoSuchMethodException e) {  
                    e.printStackTrace();  
                }  
                catch (IllegalArgumentException e){  
                    e.printStackTrace();  
                }  
                catch (IllegalAccessException e) {  
                    e.printStackTrace();  
                }  
                catch (InvocationTargetException e){  
                    e.printStackTrace();  
                }  
                finally  
                {  
                    // 清理资源  
                }  
            }  
        }  
        try  {  
            workbook.write(out);  
        }  catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    
}  