package com.listen.hytrix.rxjava;


import com.listen.hytrix.rxjava.retry.RetryResource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Rxjava {

  private static ExecutorService executor = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>());

  public static void main(String[] args) {
    RetryResource resource = new RetryResource();
    try {
      Thread.sleep(100000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void hi(){
    System.out.println("say hi");
    throw new IllegalArgumentException("goodbye");
  }


}
