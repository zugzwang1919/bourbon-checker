package com.wolfesoftware.bourbonchecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class BourbonCheckerSettings {

    // The Singleton
    private static BourbonCheckerSettings settings;

    // Attributes
    private String buffaloTraceCronExpression;
    private String heartbeatCronExpression;
    private String chromeDriverLocation;

    // Logger
    private static Logger logger = LoggerFactory.getLogger(BourbonCheckerSettings.class);

    // Private constructor so that no one can instantiate one of these from the outside
    private BourbonCheckerSettings(){}

    public static BourbonCheckerSettings getInstance() throws  IllegalStateException {
        if (settings == null) {
            File f = findConfigFile();
            logger.debug("Reading bourbon-checker.json into BourbonCheckerSettings");
            ObjectMapper objectMapper = new ObjectMapper();
            settings = new BourbonCheckerSettings();
            try {
                settings = objectMapper.readValue(f, BourbonCheckerSettings.class);
            }
            catch (Exception e) {
                logger.error("Config file was found but could not be marshalled into BourbonCheckerSettings object.");
                throw new IllegalStateException("Config file was found but could not be marshalled into BourbonCheckerSettings object.");
            }
        }

        return settings;
    }

    // Getters and Setters for Jackson
    public String getBuffaloTraceCronExpression() {
        return buffaloTraceCronExpression;
    }
    public void setBuffaloTraceCronExpression(String buffaloTraceCronExpression) {
        this.buffaloTraceCronExpression = buffaloTraceCronExpression;
    }
    public String getHeartbeatCronExpression() {
        return heartbeatCronExpression;
    }
    public void setHeartbeatCronExpression(String heartbeatCronExpression) {
        this.heartbeatCronExpression = heartbeatCronExpression;
    }
    public String getChromeDriverLocation() {
        return chromeDriverLocation;
    }
    public void setChromeDriverLocation(String chromeDriverLocation) {
        this.chromeDriverLocation = chromeDriverLocation;
    }

    // Private methods
    private static File findConfigFile()  {

        // Look for bourbon-checker.json in the jar (This will work when running inside intellij)
        URL url = BourbonCheckerSettings.class.getClassLoader().getResource("bourbon-checker.json");
        File f;
        try {
            f = (url == null) ? null : new File(url.toURI());
        }
        // If ANY type of exception occurs (not just the Null Pointer exception), we'll try to locate
        // the file explicitly below
        catch(Exception e) {
            f = null;
        }

        // If it's not found, look in the root directory. (This will work when running in prod)
        if (f != null) {
            logger.debug("bourbon-checker.json was found in the class path");
        }
        else {

            f = new File("./config/bourbon-checker.json");
            if (f.exists() && f.isFile()) {
                logger.debug("bourbon-checker.json was not found in the classpath, but was found in the config directory");
            }
            else {
                logger.error("bourbon-checker.json was not found anywhere.  Throwing exception.");
                throw new IllegalStateException("bourbon-checker.json was not found anywhere.");
            }
        }
        return f;
    }
}
