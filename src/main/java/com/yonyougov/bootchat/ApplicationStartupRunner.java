package com.yonyougov.bootchat;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner, ApplicationContextAware {

    @Autowired
    private Environment env;
    private BeanFactory beanFactory;
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====================");
        System.out.println("容器启动成功");
        System.out.println("====================");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}