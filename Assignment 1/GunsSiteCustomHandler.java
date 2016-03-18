package org.apache.nutch.protocol.interactiveselenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GunsSiteCustomHandler implements InteractiveSeleniumHandler {

	private static final String USERNAME = "gsapptester";
	private static final String PASSWORD = "gsapptester";
	private static final String EMAIL = "gsapptester@gmail.com";


	public void processDriver(WebDriver driver) {		

		//This string is passed to JavascriptExecutor which contains the dump of all HTML page sources
		String pagesData = "";
		
		/**
		 * Below are methods written specific to each website. We compare the current url and call the methods 
		 */		
		//http://www.arguntrader.com/
		if(driver.getCurrentUrl().startsWith("http://www.arguntrader.com/")) {
			pagesData = pagesData + processArgunTrader(driver);			
		}
		//http://www.budsgunshop.com/
		if(driver.getCurrentUrl().startsWith("http://www.budsgunshop.com/")){
			pagesData = pagesData + processBudsGunShop(driver);
		}
		//http://buyusedguns.net/
		if(driver.getCurrentUrl().startsWith("http://buyusedguns.net/")){
			pagesData = pagesData + processBuyUsedGuns(driver);
		}
		//http://freegunclassifieds.com
		if(driver.getCurrentUrl().startsWith("http://freegunclassifieds.com/")){
			pagesData = pagesData + processFreeGunClassifieds(driver);
		}
		//http://www.gandermountain.com/guns/
		if(driver.getCurrentUrl().startsWith("http://www.gandermountain.com/guns/")){
			pagesData = pagesData + processGanderMountain(driver);
		}
		//http://www.gunauction.com/
		if(driver.getCurrentUrl().contains("http://gunauction.com/")){
			pagesData = processGunAuction(driver);
		}

		try {
			// loop and get multiple pages in this string
			// append the string to page's driver
			JavascriptExecutor jsx = (JavascriptExecutor) driver;
			jsx.executeScript("document.body.innerHTML=document.body.innerHTML " + pagesData + ";");
		} catch (Exception e) {
			//		LOG.info(StringUtils.stringifyException(e));
		}

	}

	public boolean shouldProcessURL(String URL) {	
		return true;
	}

	/**
	 * Individual selenium methods for each website
	 */
	private String processBudsGunShop(WebDriver driver) {
		StringBuffer sb = new StringBuffer();

		sb.append(driver.getPageSource());

		//Get all the links using selenium css selector and iterate the WebElements
		List<WebElement> links = driver.findElements(By.cssSelector("a[href*='product_info.php']"));		
		for(WebElement _link : links){						
			//click each link and append pageSource and pass it to JavascriptExecutor
			sb.append("http://www.budsgunshop.com/catalog/" +_link.getAttribute("href"));
			_link.click();	
			driver.get("http://www.budsgunshop.com/catalog/" +_link.getAttribute("href"));
			sb.append(driver.getPageSource());
		}	
		//Get the page source and append it to string and return
		return sb.toString();		
	}


	private String processBuyUsedGuns(WebDriver driver) {
		StringBuffer sb = new StringBuffer();

		sb.append(driver.getPageSource());

		//For Landing page Categories
		//Get all the links using selenium css selector and iterate the WebElements
		List<WebElement> links = driver.findElements(By.cssSelector("a[href*='category.php']"));
		for(WebElement link : links) {
			driver.get("http://buyusedguns.net/" + link.getAttribute("href"));
			sb.append(driver.getPageSource());

			if(driver.getCurrentUrl().startsWith("http://www.buyusedguns.net/login.php")){
				WebElement username = driver.findElement(By.name("username"));
				WebElement password = driver.findElement(By.name("password"));

				username.sendKeys(USERNAME);
				password.sendKeys(PASSWORD);
				username.submit();

				sb.append(driver.getPageSource());				
			}
		}
		//For Pagination
		List<WebElement> linksPagination = driver.findElements(By.cssSelector("a[href*='used-gun-listings.php']"));
		for(WebElement link : linksPagination) {
			driver.get("http://buyusedguns.net/" + link.getAttribute("href"));
			sb.append(driver.getPageSource());

			if(driver.getCurrentUrl().startsWith("http://www.buyusedguns.net/login.php")){
				WebElement username = driver.findElement(By.name("username"));
				WebElement password = driver.findElement(By.name("password"));

				username.sendKeys(USERNAME);
				password.sendKeys(PASSWORD);

				username.submit();

				sb.append(driver.getPageSource());				
			}
		}
		//Get the page source and append it to string and return
		return sb.toString();
	}

	private String processArgunTrader(WebDriver driver){
		StringBuffer sb = new StringBuffer();

		//Login to arguntrader using credentials and submit form
		WebElement username = driver.findElement(By.name("username"));
		WebElement password = driver.findElement(By.name("password"));
		username.sendKeys(USERNAME);
		password.sendKeys(PASSWORD);
		username.submit();

		//Get the page source and append it to string and return
		sb.append(driver.getPageSource());		

		return sb.toString();
	}

	private String processFreeGunClassifieds(WebDriver driver) {
		StringBuffer sb = new StringBuffer();

		//For this site it redirects to a dashboard page, so exclude login and register urls 
		if(!driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/login")
				&& !driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/register")){
			
			//Get the page source and append it to string and return
			sb.append(driver.getPageSource());

			//For Landing page Categories and Pagination as same string present
			List<WebElement> links = driver.findElements(By.cssSelector("a[href*='guns-for-sale']"));
			for(WebElement link : links) {
				if(!driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/login")
						&& !driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/register")){

					//Driver get each items using the href value
					driver.get(link.getAttribute("href"));
					sb.append(driver.getPageSource());

					List<WebElement> sublinks = driver.findElements(By.cssSelector("a[href*='http://freegunclassifieds.com/']"));
					List<String> subLinkHrefs = new ArrayList<String>();
					//Populate Hrefs
					for(WebElement sublink : sublinks){
						if(!driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/login")
								&& !driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/register")){
							subLinkHrefs.add(sublink.getAttribute("href"));
						}
					}
					//This is required as the state changes after first driver get, so need to maintain a list of all urls
					for(String subLinkHref : subLinkHrefs) {
						if(!driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/login")
								&& !driver.getCurrentUrl().equals("http://freegunclassifieds.com/user/register")){
							driver.get(subLinkHref);
							sb.append(driver.getPageSource());
						}
					}
				}
			}
		}

		return sb.toString();
	}

	private String processGanderMountain(WebDriver driver) {
		StringBuffer sb = new StringBuffer();

		sb.append(driver.getPageSource());
		
		//Get the pagination next link using xpath which has a class 'page-numbers'. Using 50 iterations as max. no of pages seen in site
		for(int i=0; i < 50; i++){
			WebElement nextPage = driver.findElement(By.xpath("//p[contains(@class, 'page-numbers')]/a"));
			//driver.get(nextPage.getAttribute("href"));
			nextPage.click();
			sb.append(driver.getPageSource());
		}
		
		List<WebElement> items = driver.findElements(By.className("browse-pdp-link"));
		for(WebElement item : items){
			item.click();
			sb.append(driver.getPageSource());
		}
		
		//Get the page source and append it to string and return
		sb.append(driver.getPageSource());
		
		return sb.toString();
	}
	
	
	private String processGunAuction(WebDriver driver) {
		StringBuffer sb = new StringBuffer();

		sb.append(driver.getPageSource());
		//Get all the items using xpath, copy the href and append it to string returned
		List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class, 'image')]/a"));
		List<String> lists1 = new ArrayList<String>();
		for(WebElement item : items){
			lists1.add(item.getAttribute("href"));
		}
		for(String s : lists1) {
			driver.get(s);
			sb.append(driver.getPageSource());
		}
		
		List<WebElement> items1 = driver.findElements(By.className("lblk"));
		List<String> lists2 = new ArrayList<String>();
		for(WebElement item : items1){
			lists2.add("http://www.gunauction.com" + item.getAttribute("href"));
		}
		for(String s : lists2){
			driver.get(s);
			sb.append(driver.getPageSource());
		} 		
		
		sb.append(driver.getPageSource());
		
		return sb.toString();
	}



}
