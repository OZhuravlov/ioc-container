package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;

import java.util.Map;

public interface BeanFactory {

    Map<String, Bean> constructBeans(Map<String, BeanDefinition> beanDefinitions);

    Map<String, Bean> injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans);

    Map<String, Bean> injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans);
}
