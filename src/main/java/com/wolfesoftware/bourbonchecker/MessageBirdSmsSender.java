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
        // LIVE - live_sahwgCrOvdJ5tG97dEslrdMxQ
        // TEST - test_QdkvPlfJClByiCPwI5oFKIDC9
        final MessageBirdService wsr = new MessageBirdServiceImpl("live_sahwgCrOvdJ5tG97dEslrdMxQ");

        // Add the service to the client
        final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

        try {

            List<BigInteger> recipients = new ArrayList<>();
            BigInteger russ = new BigInteger("18593278846");
            BigInteger roger = new BigInteger("18593385240");
            recipients.add(russ);
            recipients.add(roger);
            final MessageResponse response = messageBirdClient.sendMessage("12028516595", message, recipients);
            logger.debug("Response from MessageBird = {}", response.toString());

        }
        catch (UnauthorizedException | GeneralException e) {
            logger.error(e.getMessage());
            logger.error("Exception details: ", e);
        }


    }
}
