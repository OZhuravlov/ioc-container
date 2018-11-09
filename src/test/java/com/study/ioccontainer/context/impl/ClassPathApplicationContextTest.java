package com.study.ioccontainer.context.impl;

import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ClassPathApplicationContextTest {

    String appContextXmlFileName = "application-context.xml";

    @Test
    public void createBeans() {
        ClassPathApplicationContext applicationContext = new ClassPathApplicationContext(appContextXmlFileName);
        Map<String, BeanDefinition> beanDefinitions = applicationContext.getBeanDefinitions();

        Map<String, Bean> beans = applicationContext.getBeans();
        assertEquals(2, beans.size());

        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            assertNotNull(entry.getValue());
            assertNotNull(entry.getValue().getId());
            assertNotNull(entry.getValue().getValue());
            assertEquals(entry.getKey(), entry.getValue().getId());
        }

    }

}