package com.wolfesoftware.bourbonchecker;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


public class BuffaloTraceQuartzJob implements Job {

    // One static WebDriver to maintain a single session across instances of this class
    private static WebDriver driver;

    // Static classes to maintain state across instances of this class
    private static final ProductInfo blantonsInfo = new ProductInfo("lanton", "Blanton's", 3);
    private static final ProductInfo taylorInfo = new ProductInfo("aylor", "EH Taylor", 10);
    private static final ProductInfo eagleRareInfo = new ProductInfo("agle", "Eagle Rare", 24*60);

    private static final Logger logger = LoggerFactory.getLogger(BuffaloTraceQuartzJob.class);


    // Static initialization of the WebDriver
    static {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--window-size=1920,1000");

        String executablePath = BourbonCheckerSettings.getInstance().getChromeDriverLocation();
        System.setProperty("webdriver.chrome.driver", executablePath);
        driver =  new ChromeDriver(chromeOptions);
        // Make one call to the general website to get the initial set of cookies
        driver.get("https://buffalotracegiftshop.com");

    }

    public void execute(JobExecutionContext jobContext) {
        try {
            // Get the page with the gift shop inventory
            driver.get("https://buffalotracegiftshop.com/index.php?main_page=index&cPath=14");
            // driver.get("file:///C:/SoftwareProjects/bourbon-checker/misc-materials/SoldOut.html");

            blantonsInfo.sendTextIfNecessary();
            taylorInfo.sendTextIfNecessary();
            eagleRareInfo.sendTextIfNecessary();

        }
        catch (Exception e) {
            logger.error("ERROR!!! *****  Could not get to the Buffalo Trace website.");
            logger.error(e.getMessage());
        }
    }




    private static class ProductInfo {
        private String          searchableString;
        private String          productName;
        private int             repeatMessageWaitTime;
        private LocalDateTime   lastMessageSent = null;
        private boolean         soldOutMessageSent = false;

        // Constructor
        public ProductInfo(String searchableString, String productName, int repeatMessageWaitTime) {
            this.searchableString = searchableString;
            this.productName = productName;
            this.repeatMessageWaitTime = repeatMessageWaitTime;
        }

        // Getters and Setters (for Jackson)
        public String getSearchableString() {
            return searchableString;
        }
        public void setSearchableString(String searchableString) {
            this.searchableString = searchableString;
        }
        public String getProductName() {
            return productName;
        }
        public void setProductName(String productName) {
            this.productName = productName;
        }
        public int getRepeatMessageWaitTime() {
            return repeatMessageWaitTime;
        }
        public void setRepeatMessageWaitTime(int repeatMessageWaitTime) {
            this.repeatMessageWaitTime = repeatMessageWaitTime;
        }
        public LocalDateTime getLastMessageSent() {
            return lastMessageSent;
        }
        public void setLastMessageSent(LocalDateTime lastMessageSent) {
            this.lastMessageSent = lastMessageSent;
        }
        public boolean isSoldOutMessageSent() {
            return soldOutMessageSent;
        }
        public void setSoldOutMessageSent(boolean soldOutMessageSent) {
            this.soldOutMessageSent = soldOutMessageSent;
        }

        // Public

        public void sendTextIfNecessary() {
            ProductStatus result = productFound(searchableString);
            switch(result) {
                case PRODUCT_FOUND_AND_AVAILABLE:{
                    soldOutMessageSent = false;
                    if (lastMessageSent ==  null  ||  LocalDateTime.now().isAfter(lastMessageSent.plusMinutes(repeatMessageWaitTime))) {
                        String textMessage = productName + " is now available on the Buffalo Trace website.";
                        logger.info("Getting ready to send {}", textMessage);
                        new MessageBirdSmsSender().sendMessage(textMessage);
                        lastMessageSent = LocalDateTime.now();
                    }
                    else {
                        logger.debug("{} was found on the Buffalo Trace website, but no message will be sent due to recent sendings.", productName);
                    }
                    break;
                }
                case PRODUCT_FOUND_BUT_SOLD_OUT: {
                    if (!soldOutMessageSent) {
                        String textMessage = productName + " is now SOLD OUT on the Buffalo Trace website.";
                        logger.info("Getting ready to send {}", textMessage);
                        new MessageBirdSmsSender().sendMessage(textMessage);
                        soldOutMessageSent = true;
                    }
                    else {
                        logger.debug("{} was found SOLD OUT on the Buffalo Trace website, but no message will be sent due to recent sendings.", productName);
                    }
                    break;
                }
                case PRODUCT_NOT_FOUND: {
                    soldOutMessageSent = false;
                    lastMessageSent = null;
                    logger.debug("{} was not found on the Buffalo Trace website.", productName);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected result from productFound()");
            }

        }

        private ProductStatus productFound(String productKeyword) {
            String xpathString = "//img[contains(@src,'" + productKeyword + "')]";
            try {
                WebElement img = driver.findElement(By.xpath(xpathString));
                // The img is in an 'anchor' contained in a 'td' which is contained in a 'tr' go back to the 'tr'
                WebElement tr = img.findElement(By.xpath("./../../.."));
                // Now look in the tr for the Not Sold image in any of the tds
                try {
                    WebElement soldOutImg = tr.findElement(By.xpath(".//img[contains(@alt,'Sold Out')]"));
                    logger.debug("{} image was found, but is sold out", productKeyword);
                    return ProductStatus.PRODUCT_FOUND_BUT_SOLD_OUT;
                }
                catch (NoSuchElementException e) {
                    logger.debug("{} image was found and is available", productKeyword);
                    return ProductStatus.PRODUCT_FOUND_AND_AVAILABLE;
                }
            }
            catch (NoSuchElementException e) {
                logger.debug("{} image was not found", productKeyword);
                return ProductStatus.PRODUCT_NOT_FOUND;
            }

        }

        private enum ProductStatus {
            PRODUCT_FOUND_AND_AVAILABLE,
            PRODUCT_FOUND_BUT_SOLD_OUT,
            PRODUCT_NOT_FOUND
        }

    }
}
