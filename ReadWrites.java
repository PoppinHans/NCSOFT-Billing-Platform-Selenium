package paypal;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class Read {
	public static Map<Integer, List<String>> configuration(String filePath, String fileName, String sheetName) throws IOException {
		Map<Integer, List<String>> ans = new HashMap<>();
	    File file = new File(filePath+"\\"+fileName);
	    FileInputStream inputStream = new FileInputStream(file);
	    Workbook guru99Workbook = null;

	    //Find the file extension by splitting file name in substring and getting only extension name
	    //Check condition if the file is xlsx file
	    String fileExtensionName = fileName.substring(fileName.indexOf("."));
	    
	    if(fileExtensionName.equals(".xlsx")) {
	    	guru99Workbook = new XSSFWorkbook(inputStream);
	    } else if (fileExtensionName.equals(".xls")) {
	        guru99Workbook = new HSSFWorkbook(inputStream);
	    }

	    //Read sheet inside the workbook by its name
	    Sheet guru99Sheet = guru99Workbook.getSheet(sheetName);

	    //Find number of rows in excel file
	    int rowCount = guru99Sheet.getLastRowNum() - guru99Sheet.getFirstRowNum();
	    
	    //Create a loop over all the rows of excel file to read it
	    for (int i = 1; i < rowCount+1; i++) {
	        Row row = guru99Sheet.getRow(i);
	        int entry = (int) (row.getCell(0).getNumericCellValue());
	        if (!ans.containsKey(entry)) {
	        	ans.put(entry, new ArrayList<String>());
	        }
	        List<String> list = ans.get(entry);
	        for (int j = 1; j < row.getLastCellNum(); j++) {
	        	if (row.getCell(j).getCellTypeEnum() == CellType.NUMERIC) {
	        		list.add(String.valueOf(row.getCell(j).getNumericCellValue()));
	        	}
	        	if (row.getCell(j).getCellTypeEnum() == CellType.STRING) {
	        		list.add(row.getCell(j).getStringCellValue());
	        	}
	        }
	    }
	    inputStream.close();
	    return ans;
	}
	
	public static List<String> checklist(String filePath, String fileName, String sheetName, String[] loopPath, boolean[] isDone) throws IOException {
		int xPathCol = 2;
		int statusCol = 3;
		List<String> ans = new ArrayList<>();
	    File file = new File(filePath+"\\"+fileName);
	    FileInputStream inputStream = new FileInputStream(file);
	    Workbook guru99Workbook = null;

	    //Find the file extension by splitting file name in substring and getting only extension name
	    //Check condition if the file is xlsx file
	    String fileExtensionName = fileName.substring(fileName.indexOf("."));
	    
	    if(fileExtensionName.equals(".xlsx")) {
	    	guru99Workbook = new XSSFWorkbook(inputStream);
	    } else if (fileExtensionName.equals(".xls")) {
	        guru99Workbook = new HSSFWorkbook(inputStream);
	    }

	    //Read sheet inside the workbook by its name
	    Sheet guru99Sheet = guru99Workbook.getSheet(sheetName);

	    //Find number of rows in excel file
	    int rowCount = guru99Sheet.getLastRowNum() - guru99Sheet.getFirstRowNum();

	    //Create a loop over all the rows of excel file to read it
	    isDone[0] = true;
	    for (int i = 1; i < rowCount; i++) {
	        Row row = guru99Sheet.getRow(i);
	        if (row.getCell(1).getStringCellValue().contains("Loop")) {
	        	String[] xPaths = row.getCell(2).getStringCellValue().split(",");
	        	int m = xPaths.length;
	    		for (int j = 0; j < m; j++) {
	    			loopPath[j] = xPaths[j];
	    		}
	        }
	        if (row.getLastCellNum() <= statusCol || !row.getCell(statusCol).getStringCellValue().equals("Pass")) {
	        	isDone[0] = false;
	        }
	        ans.add(row.getCell(xPathCol).getStringCellValue());
	    }
	    inputStream.close();
	    return ans;
	}
}

class Write {
	public static void checklist(String filePath, String fileName, String sheetName, List<String> dataToWrite, int NUM_ERROR) throws IOException {
        int statusCol = 3;
		File file = new File(filePath+"\\"+fileName);
        FileInputStream inputStream = new FileInputStream(file);
        Workbook guru99Workbook = null;

        String fileExtensionName = fileName.substring(fileName.indexOf("."));
        if(fileExtensionName.equals(".xlsx")){
        	guru99Workbook = new XSSFWorkbook(inputStream);
        } else if (fileExtensionName.equals(".xls")) {
            guru99Workbook = new HSSFWorkbook(inputStream);
        }

        //Read excel sheet by sheet name    
        Sheet sheet = guru99Workbook.getSheet(sheetName);

        //Get the current count of rows in excel file
        int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
        for (int i = 1; i < rowCount+1; i++) {
        	Row row = sheet.getRow(i);
        	Cell cell = row.createCell(statusCol);
        	if (i <= dataToWrite.size()) {
        		cell.setCellValue(dataToWrite.get(i - 1));
        	}
        }
        inputStream.close();

        //Create an object of FileOutputStream class to create write data in excel file
        FileOutputStream outputStream = new FileOutputStream(file);
        guru99Workbook.write(outputStream);
        outputStream.close();
	}
	
	public static void report(String filePath, String fileName, String sheetName, List<String> dataToWrite, int NUM_ERROR) throws IOException {
		int statusCol = 2;
		int last = 3;
		int endRow = dataToWrite.size() - NUM_ERROR;
		File file = new File(filePath+"\\"+fileName);
        FileInputStream inputStream = new FileInputStream(file);
        Workbook guru99Workbook = null;

        String fileExtensionName = fileName.substring(fileName.indexOf("."));
        if(fileExtensionName.equals(".xlsx")){
        	guru99Workbook = new XSSFWorkbook(inputStream);
        } else if (fileExtensionName.equals(".xls")) {
            guru99Workbook = new HSSFWorkbook(inputStream);
        }

        //Read excel sheet by sheet name    
        Sheet sheet = guru99Workbook.getSheet(sheetName);

        //Get the current count of rows in excel file
        int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
        List<String> step1 = dataToWrite.subList(0, endRow);
        List<String> step2 = dataToWrite.subList(endRow, dataToWrite.size());
        
        for (int i = 1; i < last; i++) {
        	Row row = sheet.getRow(i);
        	Cell cell = row.createCell(statusCol);
        	if (i == 1) {
        		if (!step1.contains("Fail")) {
        			cell.setCellValue("Pass");
        		} else {
        			cell.setCellValue("Fail");
        		}
        	} else {
        		if (!step2.contains("Fail")) {
        			cell.setCellValue("Pass");
        		} else {
        			cell.setCellValue("Fail");
        		}
        	}
        }
        inputStream.close();

        //Create an object of FileOutputStream class to create write data in excel file
        FileOutputStream outputStream = new FileOutputStream(file);
        guru99Workbook.write(outputStream);
        outputStream.close();
	}
}

public class ReadWrites {
	public static void main(String[] args) throws IOException {
		// For Unit testing
		/*
		int loopSize = 4;
		String filePath = System.getProperty("user.dir")+"\\src\\sample";
	    String confName = "ConfigurationSheet.xlsx";
	    String checName = "CheckListSheet.xlsx";
	    String repoName = "ReportSheet.xlsx";
	    String sheetName = "Sheet1";
	    String[] loop = new String[loopSize];
	    String[] loopResult = new String[] {"Pass", "Pass", "Pass", "Pass"};
	    boolean[] isDone = new boolean[1];
	    List<String> dataToWrite = new ArrayList<>();
	    isDone[0] = false;
	    dataToWrite.add("Pass");
	    dataToWrite.add("Fail");
	    dataToWrite.add("Pass");
	    dataToWrite.add("Pass");
	    dataToWrite.add("Pass");
	    dataToWrite.add("Pass");
	    dataToWrite.add("Pass");
	    
		Map<Integer, List<String>> confAns = new HashMap<>();
		confAns = Read.configuration(filePath, confName, sheetName);
		for (int i : confAns.keySet()) {
			System.out.println(i + " " + confAns.get(i));
		}
		
		List<String> checAns = new ArrayList<>();
		checAns = Read.checklist(filePath, checName, sheetName, loop, isDone);
		for (String s : loop) System.out.println(s);
		System.out.println(checAns);
		System.out.println(isDone[0]);
		
		System.out.println();
		Write.checklist(filePath, checName, sheetName, dataToWrite, loopResult);
		Write.report(filePath, repoName, sheetName, dataToWrite, loopResult);
		*/
	}
}
