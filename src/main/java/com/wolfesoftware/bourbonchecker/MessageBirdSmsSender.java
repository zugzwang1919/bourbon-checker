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



    public void sendMessage(String message) {

        // Keys
        // LIVE - live_5DNJM45xRflSXYA88cdBvMEm1
        // TEST - test_QdkvPlfJClByiCPwI5oFKIDC9
        final MessageBirdService wsr = new MessageBirdServiceImpl("live_5DNJM45xRflSXYA88cdBvMEm1");

        // Add the service to the client
        final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

        try {

            List<BigInteger> recipients = new ArrayList();
            BigInteger theRecipient = new BigInteger("18593278846");
            recipients.add(theRecipient);
            final MessageResponse response = messageBirdClient.sendMessage("Russ Wolfe", message, recipients);
            logger.debug("Response from MessageBird = {}", response.toString());

        }
        catch (UnauthorizedException unauthorized) {
            if (unauthorized.getErrors() != null) {
                logger.error(unauthorized.getErrors().toString());
            }
            unauthorized.printStackTrace();
        }
        catch (GeneralException generalException) {
            if (generalException.getErrors() != null) {
                logger.error(generalException.getErrors().toString());
            }
            generalException.printStackTrace();
        }

    }
}
