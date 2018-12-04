package com.github.taymindis;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleThreadFactory implements ThreadFactory
{
   private AtomicInteger counter;
   private String       name;
   private List<Thread> stats;
 
   public ScheduleThreadFactory(String name) {
      counter = new AtomicInteger();
      this.name = name;
      stats = new ArrayList<Thread>();
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread t = new Thread(runnable, name + "-Thread_" + counter);
      this.counter.incrementAndGet();
      t.setName(String.format("Created thread %d with name %s on %s \n", t.getId(), t.getName(), new Date()));
      stats.add(t);
      return t;
   }

   public String getStats()  {
      StringBuffer buffer = new StringBuffer();
      Iterator<Thread> it = stats.iterator();
      while (it.hasNext())  {
         buffer.append(it.next().getName());
      }
      return buffer.toString();
   }
}