package com.wolfesoftware.bourbonchecker;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MessageBirdSmsSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageBirdSmsSender.class);



    public void sendMessage(String message, List<BigInteger> recipients) {

        // Keys
        // LIVE - live_sahwgCrOvdJ5tG97dEslrdMxQ
        // TEST - test_QdkvPlfJClByiCPwI5oFKIDC9
        final MessageBirdService wsr = new MessageBirdServiceImpl("test_QdkvPlfJClByiCPwI5oFKIDC9");

        // Add the service to the client
        final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

        try {

            final MessageResponse response = messageBirdClient.sendMessage("12028516595", message, recipients);
            logger.debug("Response from MessageBird = {}", response.toString());

            // Let's make sure the messages were actually sent
            Thread.sleep(10000);
            final MessageResponse resultsResponse = messageBirdClient.viewMessage(response.getId());
            final int numberOfTextsSent = resultsResponse.getRecipients().getTotalSentCount();
            final int numberOfTextsDelivered = response.getRecipients().getTotalDeliveredCount();
            if (numberOfTextsSent == numberOfTextsDelivered){
                logger.info("All is good: {} messages sent.  {} messages delivered.", numberOfTextsSent, numberOfTextsDelivered);
            }
            else {
                logger.error("Message ID {} had issues: {} messages sent.  {} messages delivered.", response.getId(), numberOfTextsSent, numberOfTextsDelivered);
            }

        }
        catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Exception details: ", e);

        }


    }
}
