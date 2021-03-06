package org.jpokemon;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.zachtaylor.myna.Myna;

public class Clock implements Runnable {
  /** Indicates the queue of events to be triggered. Lock on this object */
  private static final Queue<Calendar> times = new PriorityQueue<Calendar>();

  /** Indicates which calendar events map to which callbacks */
  private static final Map<Calendar, Runnable> callbacks = new HashMap<Calendar, Runnable>();

  /** Indicates when the clock was started */
  private static final Calendar clockStart = Calendar.getInstance();

  /** Indicates the acceleration of the game time to real life time */
  public static int timespeed = 1;

  static {
    Myna.configure(Clock.class, "org.jpokemon.server.time");
    new Thread(new Clock()).start();
  }

  /** Provides a private constructor */
  private Clock() {
  }

  public static void timeout(int seconds, Runnable callback) {
    Calendar time = getGameTime();
    time.add(Calendar.SECOND, seconds);
    schedule(time, callback);
  }

  public static void schedule(Calendar time, Runnable callback) {
    synchronized (times) {
      times.add(time);
      callbacks.put(time, callback);
    }
  }

  public static Calendar getGameTime() {
    Calendar realTime = Calendar.getInstance();
    int timeDiff = (int) (realTime.getTimeInMillis() - clockStart.getTimeInMillis()) * timespeed;

    Calendar gameTime = ((Calendar) clockStart.clone());
    gameTime.add(Calendar.MILLISECOND, timeDiff);

    return gameTime;
  }

  @Override
  public void run() {
    for (;;) {
      synchronized (times) {
        while (!times.isEmpty() && times.peek().getTimeInMillis() < getGameTime().getTimeInMillis()) {
          new Thread(callbacks.remove(times.remove())).start();
        }
      }

      try {
        Thread.sleep(1000 / timespeed);
      } catch (InterruptedException e) {
      }
    }
  }

}