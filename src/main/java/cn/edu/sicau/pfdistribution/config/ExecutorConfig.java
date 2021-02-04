package cn.edu.sicau.pfdistribution.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);
    @Value("${thread.corePoolSize}")
    private Integer corePoolSize;
    @Value("${thread.maxPoolSize}")
    private Integer maxPoolSize;
    @Value("${thread.queueCapacity}")
    private Integer queueCapacity;
    @Bean(name = "netRouterExecutor")
    public Executor netRouterExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        核心线程数
        executor.setCorePoolSize(corePoolSize);
//        最大线程数
        executor.setMaxPoolSize(maxPoolSize);
//        队列大小
        executor.setQueueCapacity(queueCapacity);
//        配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("调度线程-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        logger.info("开始调度线程！");
        return executor;
    }

    @Bean(name = "dataCalculationExecutor")
    public Executor dataCalculationExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        核心线程数
        executor.setCorePoolSize(corePoolSize);
//        最大线程数
        executor.setMaxPoolSize(maxPoolSize);
//        队列大小
        executor.setQueueCapacity(queueCapacity);
//        配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("计算线程-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        logger.info("开始计算线程！");
        return executor;
    }

    @Bean(name = "dataStorage")
    public Executor dataStorageExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        核心线程数
        executor.setCorePoolSize(corePoolSize);
//        最大线程数
        executor.setMaxPoolSize(maxPoolSize);
//        队列大小
        executor.setQueueCapacity(queueCapacity);
//        配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("存储线程-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        logger.info("开始存储线程！");
        return executor;
    }
}
