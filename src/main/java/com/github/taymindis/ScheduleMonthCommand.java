package com.github.taymindis;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by woonsh on 10/12/2018.
 */
public class ScheduleMonthCommand implements Runnable {

    private Month month;
    private long periodOfMonth;
    private SchedulerCommand cmd;
    private boolean scheduleNextJobBeforeExecuteTask;
    private int dayOfMonth, hour, min, sec;
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void run() {
        if (scheduleNextJobBeforeExecuteTask) {
            if (periodOfMonth > 0) {
                ZoneId currentZone = ZoneId.systemDefault();
                LocalDate nextTarget = LocalDate.now().plusMonths(periodOfMonth);
                ZonedDateTime zonedNextTarget = ZonedDateTime.of(nextTarget.withDayOfMonth(dayOfMonth).atTime(hour, min, sec), currentZone);
                scheduledExecutorService.schedule(this, Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone), zonedNextTarget).toMillis(), TimeUnit.MILLISECONDS);
            }
            cmd.run();
        } else {
            cmd.run();
            if (periodOfMonth > 0) {
                ZoneId currentZone = ZoneId.systemDefault();
                LocalDate nextTarget = LocalDate.now().plusMonths(periodOfMonth);
                ZonedDateTime zonedNextTarget = ZonedDateTime.of(nextTarget.withDayOfMonth(dayOfMonth).atTime(hour, min, sec), currentZone);
                scheduledExecutorService.schedule(this, Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone), zonedNextTarget).toMillis(), TimeUnit.MILLISECONDS);
            }
        }
    }

    public ScheduleMonthCommand(ScheduledExecutorService scheduledExecutorService, Month month, long periodOfMonth, SchedulerCommand cmd,
                                boolean scheduleNextJobBeforeExecuteTask, int dayOfMonth, int hour, int min, int sec) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.month = month;
        this.periodOfMonth = periodOfMonth;
        this.cmd = cmd;
        this.scheduleNextJobBeforeExecuteTask = scheduleNextJobBeforeExecuteTask;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public long getPeriodOfMonth() {
        return periodOfMonth;
    }

    public void setPeriodOfMonth(long periodOfMonth) {
        this.periodOfMonth = periodOfMonth;
    }

    public SchedulerCommand getCmd() {
        return cmd;
    }

    public void setCmd(SchedulerCommand cmd) {
        this.cmd = cmd;
    }

    public boolean isScheduleNextJobBeforeExecuteTask() {
        return scheduleNextJobBeforeExecuteTask;
    }

    public void setScheduleNextJobBeforeExecuteTask(boolean scheduleNextJobBeforeExecuteTask) {
        this.scheduleNextJobBeforeExecuteTask = scheduleNextJobBeforeExecuteTask;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }
}
