package com.github.taymindis;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by woonsh on 10/12/2018.
 */
public class ScheduleWeekCommand implements Runnable {

    private DayOfWeek dayOfWeek;
    private long periodOfWeek;
    private SchedulerCommand cmd;
    private boolean scheduleNextJobBeforeExecuteTask;
    private int hour, min, sec;
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void run() {
        if (scheduleNextJobBeforeExecuteTask) {
            if (periodOfWeek > 0) {
                ZoneId currentZone = ZoneId.systemDefault();
                LocalDate nextTarget = LocalDate.now().plus(periodOfWeek, ChronoUnit.WEEKS);
                ZonedDateTime zonedNextTarget = ZonedDateTime.of(nextTarget.atTime(hour, min, sec), currentZone);
                scheduledExecutorService.schedule(this, Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone), zonedNextTarget).toMillis(), TimeUnit.MILLISECONDS);
            }
            cmd.run();
        } else {
            cmd.run();
            if (periodOfWeek > 0) {
                ZoneId currentZone = ZoneId.systemDefault();
                LocalDate nextTarget = LocalDate.now().plus(periodOfWeek, ChronoUnit.WEEKS);
                ZonedDateTime zonedNextTarget = ZonedDateTime.of(nextTarget.atTime(hour, min, sec), currentZone);
                scheduledExecutorService.schedule(this, Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone), zonedNextTarget).toMillis(), TimeUnit.MILLISECONDS);
            }
        }
    }

    public ScheduleWeekCommand(ScheduledExecutorService scheduledExecutorService, DayOfWeek dayOfWeek, long periodOfWeek, SchedulerCommand cmd,
                               boolean scheduleNextJobBeforeExecuteTask, int hour, int min, int sec) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.dayOfWeek = dayOfWeek;
        this.periodOfWeek = periodOfWeek;
        this.cmd = cmd;
        this.scheduleNextJobBeforeExecuteTask = scheduleNextJobBeforeExecuteTask;
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public long getPeriodOfWeek() {
        return periodOfWeek;
    }

    public void setPeriodOfWeek(long periodOfWeek) {
        this.periodOfWeek = periodOfWeek;
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
