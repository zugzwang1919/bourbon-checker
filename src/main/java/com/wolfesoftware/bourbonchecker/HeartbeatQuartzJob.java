package com.wolfesoftware.bourbonchecker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class HeartbeatQuartzJob implements Job {

    // Every recipient that will receive heartbeat messages
    private static List<BigInteger> RECIPIENTS;

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatQuartzJob.class);

    // Static initialization of the RECIPIENTS
    static {
        RECIPIENTS = new ArrayList<>();
        BigInteger russ = new BigInteger("18593278846");
        RECIPIENTS.add(russ);
    }

    public void execute(JobExecutionContext jobContext) {
        try {
            String textMessage = "The Bourbon Checker is up and running.";
            new MessageBirdSmsSender().sendMessage(textMessage, RECIPIENTS);
        }
        catch (Exception e) {
            logger.error("ERROR!!! *****  Something went wrong sending the heartbeat.");
            logger.error(e.getMessage());
            logger.error("Exception details: ", e);
        }
    }

}
