package paypal;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.WebDriver;

class Window {
	public static String subwindow(WebDriver driver) {
		String parentWindowHandler = driver.getWindowHandle();
		String subWindowHandler = null;
		
		Set<String> handlers = driver.getWindowHandles();
		Iterator<String> iterator = handlers.iterator();
		while (iterator.hasNext()) {
			subWindowHandler = iterator.next();
		}
		driver.switchTo().window(subWindowHandler);
		return parentWindowHandler;
	}
}

public class WindowHandlers {
	public static void main(String[] args) {
		// For Unit testing
	}
}
