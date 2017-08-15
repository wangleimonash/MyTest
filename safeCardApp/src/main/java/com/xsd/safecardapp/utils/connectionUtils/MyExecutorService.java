package com.xsd.safecardapp.utils.connectionUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者： OnlyWay
 *
 * 作用：生成一个线程数个数为5的线程池   代替Thread.star()请求网络
 */
public class MyExecutorService {
	
	private static ExecutorService executorService = null;
	
	public static ExecutorService getExecutorService(){
		if(executorService == null){
			executorService = Executors.newFixedThreadPool(10);
		}
		return executorService;
	}
	
}
