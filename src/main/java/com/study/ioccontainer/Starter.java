package com.study.ioccontainer;

import com.study.ioccontainer.context.definition.reader.impl.XmlBeanDefinitionReader;
import com.study.ioccontainer.context.impl.ClassPathApplicationContext;
import com.study.ioccontainer.entity.Bean;

import java.util.Map;

public class Starter {

    public static void main(String[] args) {
        ClassPathApplicationContext applicationContext = new ClassPathApplicationContext();
        applicationContext.setBeanDefinitionReader(new XmlBeanDefinitionReader());
        applicationContext.readBeanDefinitions();
        applicationContext.constructBeans();
        applicationContext.injectValueDependencies();
        applicationContext.injectRefDependencies();
        Map<String, Bean> beans = applicationContext.getBeans();
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
