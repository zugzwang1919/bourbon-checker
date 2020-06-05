package com.wolfesoftware.bourbonchecker;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.objects.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class MessageBirdSmsSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageBirdSmsSender.class);



    public void sendMessage(String message, List<BigInteger> recipients) {

        // Keys
        // LIVE - live_sahwgCrOvdJ5tG97dEslrdMxQ
        // TEST - test_QdkvPlfJClByiCPwI5oFKIDC9
        final MessageBirdService wsr = new MessageBirdServiceImpl("live_sahwgCrOvdJ5tG97dEslrdMxQ");

        // Add the service to the client
        final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

        try {


            final MessageResponse response = messageBirdClient.sendMessage("12028516595", message, recipients);
            logger.debug("Response from MessageBird = {}", response.toString());


            // Let's make sure the messages were actually sent
            // FIXME - Some day, take a look to see why this always says 2 messages sent 0 delivered 0 failures.
            // FIXME - Is the client caching the response?
            /*
            Thread.sleep(60000);
            final MessageResponse resultsResponse = messageBirdClient.viewMessage(response.getId());
            final int numberOfTextsSent = resultsResponse.getRecipients().getTotalSentCount();
            final int numberOfTextsDelivered = response.getRecipients().getTotalDeliveredCount();
            final int numberOfTextsFailed = response.getRecipients().getTotalDeliveryFailedCount();
            if (numberOfTextsSent == numberOfTextsDelivered){
                logger.info("All is good: {} messages sent.  {} messages delivered.", numberOfTextsSent, numberOfTextsDelivered);
            }
            else {
                logger.error("Message ID {} had issues: {} messages sent.  {} messages delivered. {} messages failed.", response.getId(), numberOfTextsSent, numberOfTextsDelivered, numberOfTextsFailed);
            }
            */

        }
        catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Exception details: ", e);

        }


    }
}
