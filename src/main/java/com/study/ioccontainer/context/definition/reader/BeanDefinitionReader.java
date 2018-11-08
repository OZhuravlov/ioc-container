package com.study.ioccontainer.context.definition.reader;

import com.study.ioccontainer.entity.BeanDefinition;

import java.util.Map;

public interface BeanDefinitionReader {

    Map<String, BeanDefinition> readBeanDefinitions();
}
