package com.wolfesoftware.bourbonchecker;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BourbonCheckerProperties {

    private static Properties properties;

    private static Logger logger = LoggerFactory.getLogger(BourbonCheckerProperties.class);

    private BourbonCheckerProperties(){};

    public static Properties getProperties() {
        if (properties == null) {
            try (InputStream input = findInputStream()) {
                logger.debug("Starting BourbonCheckerApplication()");
                properties = new Properties();
                if (input == null) {
                    logger.error("application.properties could not be found.");
                }
                properties.load(input);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }

    private static InputStream findInputStream() {

        // Look for application.properties in the jar (This will work when running inside intellij)
        InputStream is = BourbonCheckerApplication.class.getClassLoader().getResourceAsStream("application.properties");

        // If it's not found, look in the root directory. (This will work when running in prod)
        if (is != null) {
            logger.debug("application.properties was found in the class path");
        }
        else {
            try {
                is = new FileInputStream("./config/application.properties");
                logger.debug("application.properties was not found in the classpath, but was found in the config directory");
            } catch (FileNotFoundException e) {
                logger.error("Well, it looks like we could not find an application.properties anywhere");
            }
        }
        return is;
    }
}
