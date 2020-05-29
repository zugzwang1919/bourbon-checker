package com.wolfesoftware.bourbonchecker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class WebPageRetriever {

    // Keeper of all WebPageRetrievers
    private static Map<String, WebPageRetriever> webPageRetrievers = new HashMap<>();

    // Private data members
    private WebDriver driver;
    private String bodyText;

    // Logger
    private static Logger logger = LoggerFactory.getLogger(WebPageRetriever.class);


    public static WebPageRetriever getInstance(String sessionName){
        if (!webPageRetrievers.containsKey(sessionName)) {
            WebPageRetriever webPageRetriever = new WebPageRetriever();
            webPageRetrievers.put(sessionName, webPageRetriever);
        }
        return webPageRetrievers.get(sessionName);
    }


    private WebPageRetriever() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);

        String executablePath = BourbonCheckerProperties.getProperties().getProperty("chromeDriverLocation");
        System.setProperty("webdriver.chrome.driver", executablePath);
        driver =  new ChromeDriver(chromeOptions);
    }

    public void loadPage(String url) {

        // Get the page
        driver.get(url);

        // Grab the body of the HTML and save it
        bodyText = driver.findElement(By.tagName("body")).getText();

    }

    public boolean isStringPresent(String stringInQuestion) {
        boolean result = bodyText.contains(stringInQuestion);
        if (logger.isDebugEnabled()) {
            String message = result ?   "The string '" + stringInQuestion + "' WAS FOUND in the body of the web page" :
                                        "The string '" + stringInQuestion + "' was not found in the body of the web page";
            logger.debug(message);
        }
        return result;
    }

}
