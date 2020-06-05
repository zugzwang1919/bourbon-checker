package com.wolfesoftware.bourbonchecker;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class BourbonCheckerApplication {

    private static final Logger logger = LoggerFactory.getLogger(BourbonCheckerApplication.class);

    public static void main(String[] args) {

        try {
            // Build a TRIGGER and JOB_DETAIL for a Heartbeat
            String heartbeatCronExpression = BourbonCheckerSettings.getInstance().getHeartbeatCronExpression();
            if (heartbeatCronExpression == null) {
                logger.error("heartbeatCronExpression not found in the BourbonCheckerSettings.  Exiting now.");
                return;
            }
            logger.debug("Heartbeat Cron Expression = {}", heartbeatCronExpression);
            CronTrigger heartbeatTrigger = TriggerBuilder.newTrigger().
                    withIdentity("TheHeartbeatTrigger").
                    withSchedule(CronScheduleBuilder.cronSchedule(heartbeatCronExpression)).
                    build();
            JobDetail heartbeatJob = JobBuilder.newJob(HeartbeatQuartzJob.class).
                    withIdentity("TheHeartbeatJob").
                    build();

            // Build a TRIGGER and JOB_DETAIL for Buffalo Trace Website Monitoring
            String buffaloTraceCronExpression = BourbonCheckerSettings.getInstance().getBuffaloTraceCronExpression();
            if (buffaloTraceCronExpression == null) {
                logger.error("buffaloTraceCronExpression not found in the BourbonCheckerSettings.  Exiting now.");
                return;
            }
            logger.debug("Buffalo Trace Cron Expression = {}", buffaloTraceCronExpression);
            CronTrigger buffaloTraceTrigger = TriggerBuilder.newTrigger().
                    withIdentity("TheBourbonTrigger").
                    withSchedule(CronScheduleBuilder.cronSchedule(buffaloTraceCronExpression)).
                    build();
            JobDetail buffaloTraceJob = JobBuilder.newJob(BuffaloTraceQuartzJob.class).
                    withIdentity("TheBourbonJob").
                    build();



            try {
                Properties quartzStaticProperties = new Properties();
                quartzStaticProperties.put("org.quartz.threadPool.threadCount", "2");
                SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzStaticProperties);
                Scheduler scheduler = schedulerFactory.getScheduler();
                scheduler.start();
                scheduler.scheduleJob(buffaloTraceJob, buffaloTraceTrigger);
                scheduler.scheduleJob(heartbeatJob, heartbeatTrigger);
            } catch (Exception e) {
                logger.error("An unanticipated error occurred.  It's message is {} ", e.getMessage());
            }
            logger.debug("Leaving BourbonCheckerApplication()");
        }
        catch (Exception e) {
            logger.error("An exception occurred and will cause this software to terminate.  Details follow:");
            logger.error(e.getMessage());
            logger.error("Exception details: ", e);

        }
    }

}
