package com.drf.predictor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.drf.proservice.service.EntriesService;
import com.drf.proservice.service.ResultsService;

@SpringBootApplication
@ComponentScan({ "com.drf.predictor" })
@PropertySource("classpath:default.properties")
public class DrfPredictorApplication {

    @Value("${proservice-server-host}")
    private String proServiceHost;

    public static void main(String[] args) {
        SpringApplication.run(DrfPredictorApplication.class, args);
    }

    @Bean
    public HttpInvokerProxyFactoryBean httpInvokerEntriesProxy() {
        HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
        proxy.setServiceInterface(EntriesService.class);
        proxy.setServiceUrl(proServiceHost + "/proEntriesService.http");
        return proxy;
    }

    @Bean
    public HttpInvokerProxyFactoryBean httpInvokerResultsProxy() {
        HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
        proxy.setServiceInterface(ResultsService.class);
        proxy.setServiceUrl(proServiceHost + "/proResultsService.http");
        return proxy;
    }

}
