package com.study.ioccontainer.context.impl;

import com.google.common.annotations.VisibleForTesting;
import com.study.ioccontainer.context.ApplicationContext;
import com.study.ioccontainer.context.definition.reader.BeanDefinitionReader;
import com.study.ioccontainer.context.definition.reader.xml.XmlBeanDefinitionReader;
import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.GetBeanException;
import com.study.ioccontainer.service.impl.BeanFactory;
import com.study.ioccontainer.service.impl.BeanFactoryImpl;

import java.io.InputStream;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Bean> beans;

    public ClassPathApplicationContext(String configFileName) {
        InputStream inputStream = this.getClass().getResourceAsStream(configFileName);
        beanDefinitions = readBeanDefinitions(new XmlBeanDefinitionReader(inputStream));
        BeanFactory beanFactory = new BeanFactoryImpl();
        beans = beanFactory.constructBeans(beanDefinitions);
        beans = beanFactory.injectValueDependencies(beanDefinitions, beans);
        beans = beanFactory.injectRefDependencies(beanDefinitions, beans);
    }

    public Object getBean(String id) {
        return beans.get(id).getValue();
    }

    public <T> T getBean(Class<T> clazz) {
        T beanObject = null;
        int i = 0;
        for (String beanId : beans.keySet()) {
            Object value = beans.get(beanId).getValue();
            if (clazz.isAssignableFrom(value.getClass())) {
                beanObject = (T) value;
                i++;
            }
        }
        if(i > 1){
            throw new GetBeanException("There are two Beans assignable from class");
        }
        return beanObject;
    }

    public <T> T getBean(String id, Class<T> clazz) {
        Object value = beans.get(id).getValue();
        return (T) value;
    }
    Map<String, BeanDefinition> readBeanDefinitions(BeanDefinitionReader beanDefinitionReader) {
        return beanDefinitionReader.readBeanDefinitions();
    }

    @VisibleForTesting
    Map<String, Bean> getBeans() {
        return beans;
    }

    @VisibleForTesting
    Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
