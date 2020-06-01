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
            // Build a TRIGGER for quartz
            String cronExpression = BourbonCheckerSettings.getInstance().getCronExpression();
            if (cronExpression == null) {
                logger.error("cronExpression not found in the BourbonCheckerSettings.  Exiting now.");
                return;
            }
            logger.debug("Cron Expression = {}", cronExpression);
            CronTrigger trigger = TriggerBuilder.newTrigger().
                    withIdentity("TheBourbonTrigger").
                    withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).
                    build();

            // Build a JOB_DETAIL for quartz
            JobDetail job = JobBuilder.newJob(BuffaloTraceQuartzJob.class).
                    withIdentity("TheBourbonJob").
                    build();

            Properties quartzStaticProperties = new Properties();
            quartzStaticProperties.put("org.quartz.threadPool.threadCount", "1");

            try {
                SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzStaticProperties);
                Scheduler scheduler = schedulerFactory.getScheduler();
                scheduler.start();
                scheduler.scheduleJob(job, trigger);
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
