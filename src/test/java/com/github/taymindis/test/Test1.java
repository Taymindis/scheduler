package com.github.taymindis.test;

import com.github.taymindis.Scheduler;

import org.junit.Test;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by taymindis on 1/12/18.
 */
public class Test1 {

    @Test
    public void testRun() throws IOException, InterruptedException {

        Scheduler scheduler = new Scheduler(10, "TEST");
        WorkerTest a = new WorkerTest<Date>(new Date());

////        scheduler.scheduleAtSpecificTime(a, 0, 58, 16, 1, TimeUnit.SECONDS);
//        scheduler.scheduleNow(a, 1, TimeUnit.SECONDS, true);
//        scheduler.scheduleNow(a, 1, TimeUnit.SECONDS, true);
//        scheduler.scheduleAtSpecificDateTime(a, 50, 54, 2, 4, 12, 2018, 1, TimeUnit.SECONDS, false);
//        scheduler.scheduleAtSpecificTime(a, 0, 40, 11, false);


//        assertTrue(a.isPassed());
//        ZoneId currentZone = ZoneId.systemDefault();
//
//        LocalDate nextTarget =  LocalDate.now().plus(1, ChronoUnit.MONTHS);
//
//        ZonedDateTime zonedNextTarget = ZonedDateTime.of(nextTarget.atTime(11, 54, 15), currentZone);
//        System.out.println(Duration.between(ZonedDateTime.of(LocalDateTime.now(), currentZone),  zonedNextTarget).getSeconds());
//        scheduler.scheduleAtDayOfWeekTime(a, 28, 24, 15, new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY}, 1, true);
        scheduler.scheduleAtMonthAndTime(a, 0, 31, 15, 10, new Month[]{Month.DECEMBER}, 1, true);

        while (true) {
            Thread.sleep(30000);
//            System.out.println(scheduler.getStatistic());
        }

    }
}
