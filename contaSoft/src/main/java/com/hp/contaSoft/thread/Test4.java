package com.hp.contaSoft.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.qos.logback.core.net.SyslogOutputStream;

public class Test4 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
		ExecutorService service = null;
		
		try {
			
			new Thread(new ParallelTask(1)).start();
			new Thread(new ParallelTask(2)).start();
			
			//service = Executors.newSingleThreadExecutor();
			service = Executors.newFixedThreadPool(4);
			System.out.println("begin");
			Future<Integer> result = service.submit(() -> 30+11);
			Future<Integer> result2 = service.submit(() -> 30+12);
			Future<Integer> result3 = service.submit(() -> 30+13);
			Future<Integer> result4 = service.submit(() -> 30+14);
			Future<String> result5 = (Future<String>) service.submit( () -> System.out.println("Hola"));
			System.out.println(result.get());
			System.out.println(result2.get());
			System.out.println(result3.get());
			System.out.println(result4.get());
			 
			 
		}
		finally {
			if(service != null) service.shutdown();
		}
		

	}

}