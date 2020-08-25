package com.rengu.toolintegrations.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * SpringBoot 线程池配置类
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-09-11 16:43
 **/
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(50);                         //当前线程数
        threadPoolTaskExecutor.setMaxPoolSize(100);                         //最大线程数
        threadPoolTaskExecutor.setQueueCapacity(150);                       //队列最大长度
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//拒绝策略（使用了四种中的其中一种：由调用线程处理改任务）
        threadPoolTaskExecutor.setThreadNamePrefix("OMS-Thread");           //线程名称前缀
        threadPoolTaskExecutor.initialize();                                //初始化
        System.out.println("》》》》》开启异常线程池！");
        return threadPoolTaskExecutor;

    }

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

        return new MyAsyncExceptionHandler();
    }

    /**
     * 自定义异常处理类
     *
     * @author ZhangQKun
     */
    class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        //手动处理捕获的异常
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            System.out.println("-------------》》》捕获线程异常信息");
            log.info("Exception message - " + throwable.getMessage());
            log.info("Method name - " + method.getName());
            for (Object param : obj) {
                log.info("Parameter value - " + param);
            }
        }
    }
}
