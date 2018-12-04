package com.github.taymindis;

import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Author: woon
 * Scheduler framework name: Scheduler
 * year  the year to represent, from MIN_YEAR to MAX_YEAR
 * month  the month-of-year to represent, from 1 (January) to 12 (December)
 * dayOfMonth  the day-of-month to represent, from 1 to 31
 * hour  the hour-of-day to represent, from 0 to 23
 * minute  the minute-of-hour to represent, from 0 to 59
 * second  the second-of-minute to represent, from 0 to 59
 */
public class Scheduler {

    private static final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduleThreadFactory scheduleThreadFactory;

    /**
     * By Default it is using newScheduledThreadPool
     **/
    public Scheduler(int corePoolSize) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    /**
     * By Default it is using newScheduledThreadPool
     **/
    public Scheduler(int corePoolSize, String name) {
        scheduleThreadFactory = new ScheduleThreadFactory(name);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize, scheduleThreadFactory);
    }

    public Scheduler(ScheduledExecutorService svc) {
        this.scheduledExecutorService = svc;
    }

    public boolean scheduleNow(final SchedulerCommand command) {
        Runnable taskNow = new Runnable() {
            @Override
            public void run() {
                command.run();
            }
        };
        this.scheduledExecutorService.schedule(taskNow, 0, TimeUnit.MILLISECONDS);
        return true;
    }

    public boolean scheduleNow(final SchedulerCommand command, long period, TimeUnit unit, boolean scheduleNextJobBeforeExecuteTask) {
        if (unit.ordinal() < TimeUnit.SECONDS.ordinal()) {
            LOGGER.log(Level.SEVERE, "Scheduler minimum unit is seconds");
            return false;
        }
        Runnable taskNow = new Runnable() {
            @Override
            public void run() {
                command.run();
            }
        };
        if (scheduleNextJobBeforeExecuteTask) {
            scheduledExecutorService.scheduleAtFixedRate(taskNow, 0, unit.toMillis(period), TimeUnit.MILLISECONDS);
        } else {
            scheduledExecutorService.scheduleWithFixedDelay(taskNow, 0, unit.toMillis(period), TimeUnit.MILLISECONDS);
        }
        return true;
    }

    public boolean scheduleAtSpecificTime(SchedulerCommand command, int sec, int min, int hour, boolean repeatable) {
        return scheduleAtSpecificTime(command, sec, min, hour, repeatable ? 0 : -1, TimeUnit.SECONDS/*Unused*/, true);
    }

    public boolean scheduleAtSpecificTime(SchedulerCommand command, int sec, int min, int hour, long period, TimeUnit unit) {
        return scheduleAtSpecificTime(command, sec, min, hour, period, unit, true);
    }

    public boolean scheduleAtSpecificTime(final SchedulerCommand command, final int sec, final int min, final int hour, final long period,
                                          final TimeUnit unit, final boolean scheduleNextJobBeforeExecuteTask) {

        if (unit.ordinal() < TimeUnit.SECONDS.ordinal()) {
            LOGGER.log(Level.SEVERE, "Scheduler minimum unit is seconds");
            return false;
        }

        if (isValidParam(sec, min, hour)) {
            if (period <= 0) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        if (period == -1) { /**Not Repeatable**/
                            command.run();
                        } else if (scheduleNextJobBeforeExecuteTask) {
                            scheduleAtSpecificTime(command, sec, min, hour, period, unit, scheduleNextJobBeforeExecuteTask);
                            command.run();
                        } else {
                            command.run();
                            scheduleAtSpecificTime(command, sec, min, hour, period, unit, scheduleNextJobBeforeExecuteTask);
                        }
                    }
                };
                scheduledExecutorService.schedule(task, computeNextDelay(sec, min, hour), TimeUnit.MILLISECONDS);
            } else {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        command.run();
                    }
                };

                if (scheduleNextJobBeforeExecuteTask) {
                    scheduledExecutorService.scheduleAtFixedRate(task, computeNextDelay(sec, min, hour), unit.toMillis(period), TimeUnit.MILLISECONDS);
                } else {
                    scheduledExecutorService.scheduleWithFixedDelay(task, computeNextDelay(sec, min, hour), unit.toMillis(period), TimeUnit.MILLISECONDS);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean scheduleAtSpecificDateTime(SchedulerCommand command,
                                              int sec, int min, int hour,
                                              int day, int month, int year) {
        return scheduleAtSpecificDateTime(command, sec, min, hour, day, month, year, 0, TimeUnit.SECONDS/*unused*/, true);
    }

    public boolean scheduleAtSpecificDateTime(SchedulerCommand command,
                                              int sec, int min, int hour,
                                              int day, int month, int year,
                                              long period, TimeUnit unit) {
        return scheduleAtSpecificDateTime(command, sec, min, hour, day, month, year, period, unit, true);
    }

    public boolean scheduleAtSpecificDateTime(final SchedulerCommand command,
                                              final int sec, final int min, final int hour,
                                              final int day, final int month, final int year,
                                              final long period, final TimeUnit unit, final boolean scheduleNextJobBeforeExecuteTask) {
        if (unit.ordinal() < TimeUnit.SECONDS.ordinal()) {
            LOGGER.log(Level.SEVERE, "Scheduler minimum unit is seconds");
            return false;
        }

        /** Period only applicable to  >= 0, no repeatable at this function , only interval**/
        if (isValidParam(sec, min, hour, day, month, year) && period >= 0) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    command.run();
                }
            };

            if (period == 0) {
                scheduledExecutorService.schedule(task, computeNextDelay(sec, min, hour, day, month, year), TimeUnit.MILLISECONDS);
            } else if (scheduleNextJobBeforeExecuteTask) {
                scheduledExecutorService.scheduleAtFixedRate(task, computeNextDelay(sec, min, hour, day, month, year), unit.toMillis(period), TimeUnit.MILLISECONDS);
            } else {
                scheduledExecutorService.scheduleWithFixedDelay(task, computeNextDelay(sec, min, hour, day, month, year), unit.toMillis(period), TimeUnit.MILLISECONDS);
            }
            return true;
        } else {
            return false;
        }
    }

    public void forceShutdown() {
        scheduledExecutorService.shutdownNow();
    }

    public void shutdown(long maxWaitTimeMs) {
        scheduledExecutorService.shutdown();

        try {
            scheduledExecutorService.awaitTermination(maxWaitTimeMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, "Reach max wait time in ms " + maxWaitTimeMs, ex);
        }
    }

    public String getStatistic() {
        if(this.scheduleThreadFactory == null) {
            return null;
        }
        return scheduleThreadFactory.getStats();
    }


    private long computeNextDelay(int sec, int min, int hour) {
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(hour).withMinute(min).withSecond(sec).withNano(0);
        if (zonedNow.compareTo(zonedNextTarget) > 0) {
            zonedNextTarget = zonedNextTarget.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.toMillis();
    }

    private long computeNextDelay(int sec, int min, int hour, int day, int month, int year) {
        ZoneId currentZone = ZoneId.systemDefault();
//        ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), currentZone);
        ZonedDateTime zonedNextTarget = ZonedDateTime.of(year, month, day, hour, min, sec, 0/* LocalDateTime.now().getNano()*/, currentZone);
//        if (zonedNow.compareTo(zonedNextTarget) > 0) {
//            /**If it is pass, return 0 to start immediately **/
//            return 0;
//        }
//        Duration duration = Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone),  ZonedDateTime.of(year, month, day, hour, min, sec, 0, currentZone));
//        System.out.print(duration.getSeconds() + "\n");
        return Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone),  zonedNextTarget).toMillis();
    }

    private static boolean isValidParam(int... vars) {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i] < 0) {
                LOGGER.log(Level.SEVERE, " negative value is not allowed");
                return false;
            }
        }
        return true;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public ScheduleThreadFactory getScheduleThreadFactory() {
        return scheduleThreadFactory;
    }

    public void setScheduleThreadFactory(ScheduleThreadFactory scheduleThreadFactory) {
        this.scheduleThreadFactory = scheduleThreadFactory;
    }
}