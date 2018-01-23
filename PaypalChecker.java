package paypal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaypalChecker {
	static final int CONFIG_NUM = 1;			// choose one configuration from CONF_NAME
	static final int WAIT_TIME_SEC = 10;		// wait for PayPal's response
	static final int SLEEP_TIME_MILSEC = 5000;	// wait for a web page's loading 
	static final int LOOP_SIZE = 4;				// used for looping a test
	static final int NUM_ERROR = 1;				// how many errors are expected
	static final String FILE_PATH = System.getProperty("user.dir")+"\\src\\paypal";
	static final String CONF_NAME = "ConfigurationSheet.xlsx";
	static final String CHEC_NAME = "CheckListSheet.xlsx";
	static final String REPO_NAME = "ReportSheet.xlsx";
	static final String SHEET_NAME = "Sheet1";
	static final String STARTUP_PAGE = "https://account.ncsoft.com/settings/index";
	
	static int countScreenshots = 0;
	static int countChecklist = 0;
	static boolean[] isDone = new boolean[1];
	static String[] loopPath = new String[LOOP_SIZE];
	static String[] loopResult = new String[LOOP_SIZE];
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException, IOException {
		/*
		 * STEP 1. Load all the configuration. Passwords are scanned from System.in.
		 * Please make sure your current IP address is authorized to access you NCSOFT account.
		 * To send the testing report by email, please make sure SMTP is configured 
		 * (https://support.google.com/a/answer/176600?hl=en).
		*/
		List<String> confFile = Read.configuration(FILE_PATH, CONF_NAME, SHEET_NAME).get(CONFIG_NUM);
		String reportFromEmail = confFile.get(0);
		String reportToEmail = confFile.get(1);
		String sub = confFile.get(2);
		String msg = confFile.get(3);
		String ncsoftEmail = confFile.get(4);
		String paypalEmail = confFile.get(5);
		String sendEmail = confFile.get(6);
		
		String oldWindow = null;
		String reportPassword = null;
		String ncsoftPassword = null;
		String paypalPassword = null;
		Map<String, String> contentMap = new HashMap<>();
		
		Scanner sc = new Scanner(System.in);
		if (sendEmail.equals("Y")) {
			System.out.println("Password for the email " + reportFromEmail + " to send report: ");
			reportPassword = sc.next();
		}
		System.out.println("Password for the email " + ncsoftEmail + " to log in NCSOFT: ");
		ncsoftPassword = sc.next();
		System.out.println("Password for the email " + paypalEmail + " to log in PayPal: ");
		paypalPassword = sc.next();
		contentMap.put("ncsoftEmail", ncsoftEmail);
		contentMap.put("paypalEmail", paypalEmail);
		contentMap.put("ncsoftPassword", ncsoftPassword);
		contentMap.put("paypalPassword", paypalPassword);
		
		/*
		 * STEP 2. Initialize WebDriver and WebElement. And load all the checklist entries.
		 * If all entries are "Pass", the testing will be bypassed.
		 * An entry can be a (1) xPath: will be recognized as clicking; 
		 * (2) xPath and an email/password: will be recognized as sending keys;
		 * (3) xPath and more xPath: will be recognized as detecting errors/looping a test,
		 * NUM_ERROR/LOOP_SIZE needs to be configured, read/write methods may need to be adjusted.
		 */
		List<String> filenames = new ArrayList<>();
		filenames.add("" + FILE_PATH + "\\" + REPO_NAME);
		String fileName;
		WebDriver driver = new ChromeDriver();
		driver.get(STARTUP_PAGE);
		driver.manage().window().maximize();
		// create new web element
		WebElement element;	
		// load checklist file
		isDone[0] = false;
		List<String> checFile = Read.checklist(FILE_PATH, CHEC_NAME, SHEET_NAME, loopPath, isDone);
		if (isDone[0] == true) {
			if (sendEmail.equals("Y")) {
				Mailer.send(reportFromEmail, reportPassword, reportToEmail, sub, msg, filenames);
			}
			return;
		}
		
		/*
		 * STEP 3. Testing navigating the NCSOFT Billing Platform.
		 * WebDriver switches to a new window if necessary.
		 * WebDriver takes a screenshot of each step. 
		 * Store the results of all checklist entries.
		 */
		List<String> dataToWrite = new ArrayList<>();
		for (int i = countChecklist; i < checFile.size() - NUM_ERROR; i++) {
			try {
				String curPath = checFile.get(i);
				if (curPath.contains(",")) {
					String[] fill = curPath.split(",");
					element = driver.findElement(By.xpath(fill[0]));
					element.clear();
					element.sendKeys(contentMap.get(fill[1]));
				} else {
					element = driver.findElement(By.xpath(curPath));
					element.click();
					String preWindow = Window.subwindow(driver);
					if (oldWindow == null) {
						oldWindow = preWindow;
					}
				}
				Thread.sleep(SLEEP_TIME_MILSEC);
				dataToWrite.add("Pass");
				fileName = Screen.take(driver, FILE_PATH, ++countScreenshots);
				filenames.add(new String(fileName));			
			} catch (Exception E) {
				dataToWrite.add("Fail");
				continue;
			}
		}
		
		/*
		 * STEP 4. Detecting the "Something went wrong on our end" ERROR(5300) when using PayPal.
		 * Please make sure you have an active PayPal account and enough fund for minimal NCoin.
		 * Switch back to NCSOFT page if the error occurs.
		 */
		for (int i = checFile.size() - NUM_ERROR; i < checFile.size(); i++) {
			String[] error = checFile.get(i).split(",");
			String msgPath = error[0];
			String clickPath = error[1];
			try {
				element = (new WebDriverWait(driver, WAIT_TIME_SEC)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(msgPath)));
				if (element.getText().contains("wrong")) {
					dataToWrite.add("Fail");
					element = driver.findElement(By.xpath(clickPath));
					element.click();
					if (i == checFile.size() - 1) {
						driver.switchTo().window(oldWindow);
					}
					Thread.sleep(SLEEP_TIME_MILSEC); 
				} else {
					dataToWrite.add("Pass");
				}
			} catch (org.openqa.selenium.TimeoutException timeEx) {
				System.out.println("No response from PayPal after: " +  WAIT_TIME_SEC + " seconds.");
				continue;
			} finally {
				fileName = Screen.take(driver, FILE_PATH, ++countScreenshots);
				filenames.add(new String(fileName));
			}
		}
		
		/*
		 * STEP 5. Write the results to checklist and conclude the testing to report.
		 */
		Write.checklist(FILE_PATH, CHEC_NAME, SHEET_NAME, dataToWrite, NUM_ERROR);
		Write.report(FILE_PATH, REPO_NAME, SHEET_NAME, dataToWrite, NUM_ERROR);
		if (sendEmail.equals("Y")) {
			Mailer.send(reportFromEmail, reportPassword, reportToEmail, sub, msg, filenames);
		}
	}
}
