package paypal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class Screen {
	public static String take(WebDriver driver, String filePath, int count)
	{
		String fileName = "";
		// Take screenshot and store as a file format
		File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	
		try {
			// now copy the  screenshot to desired location using copyFile //method
			fileName = "" + filePath + "\\newscreen" + count + ".jpg";
			FileUtils.copyFile(src, new File(fileName));
		} 
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		return fileName;
	}
}

public class ScreenShots{    
	 public static void main(String[] args) throws FileNotFoundException {
		// For Unit testing
		/*
		WebDriver driver=new ChromeDriver(); 
		String filePath = System.getProperty("user.dir")+"\\src\\sample";
		int count = 0;
		 
		Screen.take(driver, filePath, count++);
		Screen.take(driver, filePath, count++);
		*/
	 }    
}