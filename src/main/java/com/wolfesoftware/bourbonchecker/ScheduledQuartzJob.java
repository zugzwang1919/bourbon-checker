package com.wolfesoftware.bourbonchecker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ScheduledQuartzJob implements Job {
    private static final String BUFFALO_TRACE_SESSION_NAME = "BUFFALO_TRACE";
    private static final Logger logger = LoggerFactory.getLogger(ScheduledQuartzJob.class);

    private static LocalDateTime blantonsTextLastSent;
    private static final int BLANTON_TEXT_WAIT_TIME = 3;

    private static LocalDateTime taylorTextLastSent;
    private static final int TAYLOR_TEXT_WAIT_TIME = 3;

    private static LocalDateTime eagleTextLastSent;
    private static final int EAGLE_TEXT_WAIT_TIME = 24*60;

    private WebPageRetriever wpr;
    private StringBuilder textMessageBuilder;


    public void execute(JobExecutionContext jobContext) {
        try {
            wpr = WebPageRetriever.getInstance(BUFFALO_TRACE_SESSION_NAME);
            wpr.loadPage("https://buffalotracegiftshop.com/index.php?main_page=index&cPath=14");
            textMessageBuilder = new StringBuilder();

            // Build the Text Message if anything of interest was found
            blantonsTextLastSent = processOneProduct("lanton", "Blantons", blantonsTextLastSent, BLANTON_TEXT_WAIT_TIME);
            taylorTextLastSent = processOneProduct("aylor", "EH Taylor", taylorTextLastSent, TAYLOR_TEXT_WAIT_TIME);
            eagleTextLastSent = processOneProduct("agle", "Eagle Rare", eagleTextLastSent, EAGLE_TEXT_WAIT_TIME);

            if (textMessageBuilder.length() != 0)
                textMessageBuilder.append(" found on the Buffalo Trace website.");


            // Send the Text Message if one was built
            if (textMessageBuilder.length() != 0) {
                String textMessage = textMessageBuilder.toString();
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                logger.info("*************  Sending {}  ***************", textMessage);
                //new MessageBirdSmsSender().sendMessage(textMessage);
            }
        }
        catch (Exception e) {
            logger.error("ERROR!!! *****  Could not get to the Buffalo Trace website.");
            logger.error(e.getMessage());
        }
    }

    private LocalDateTime processOneProduct(String stringToSearchFor, String foundProductName,
                                          LocalDateTime lastTimeProductMessageSent, int repeatMessageWaitTime) {
        // If we've recently sent a message about this product, just return
        if (lastTimeProductMessageSent != null &&  lastTimeProductMessageSent.isAfter(LocalDateTime.now().minusMinutes(repeatMessageWaitTime)))
            return lastTimeProductMessageSent;

        // Now, see if we need to send a message, if we do update the text message & indicate that the text message was sent
        if (wpr.isStringPresent(stringToSearchFor)) {
            optionallyAppendAndWithString(foundProductName);
            return LocalDateTime.now();
        }

        return lastTimeProductMessageSent;
    }

    private void optionallyAppendAndWithString(String s) {
        if (textMessageBuilder.length() != 0)
            textMessageBuilder.append(" and ");
        textMessageBuilder.append(s);
    }

}
