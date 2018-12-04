package com.github.taymindis.test;

import com.github.taymindis.SchedulerCommand;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by taymindis on 1/12/18.
 */
public class WorkerTest<T extends Date> implements SchedulerCommand<T> {
    private Date value;
    private boolean succuess;

    public WorkerTest(T i) {
        value = i;
    }

    @Override
    public void run() {

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        long time = new Date().getTime();
        succuess = !(time > value.getTime());

        String a = String.format("%d, value = %d", time, value.getTime());
        System.out.println(a);
    }


    public boolean isPassed() {
        return succuess;
    }


}
