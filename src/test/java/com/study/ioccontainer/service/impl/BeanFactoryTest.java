package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.class_for_test.Test1;
import com.study.ioccontainer.class_for_test.Test2;

import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BeanFactoryTest {
    private Map<String, BeanDefinition> beanDefinitionMap;

    @Before
    public void setUp(){
        beanDefinitionMap = new HashMap<>();

        // Bean test1
        BeanDefinition beanDefinition1 = new BeanDefinition();

        beanDefinition1.setId("test1");
        beanDefinition1.setClassName("com.study.ioccontainer.class_for_test.Test1");

        Map<String, String> valueDependencies1 = new HashMap<>();
        valueDependencies1.put("testName1", "testValue1");
        beanDefinition1.setValueDependencies(valueDependencies1);

        Map<String, String> refDependencies1 = new HashMap<>();
        refDependencies1.put("testName2", "test2");
        beanDefinition1.setRefDependencies(refDependencies1);
        beanDefinitionMap.put(beanDefinition1.getId(), beanDefinition1);

        // Bean test2
        BeanDefinition beanDefinition2 = new BeanDefinition();

        beanDefinition2.setId("test2");
        beanDefinition2.setClassName("com.study.ioccontainer.class_for_test.Test2");

        Map<String, String> valueDependencies2 = new HashMap<>();
        valueDependencies2.put("testName1", "testValue2");
        beanDefinition2.setValueDependencies(valueDependencies2);

        Map<String, String> refDependencies2 = new HashMap<>();
        refDependencies2.put("testName2", "test1");
        beanDefinition2.setRefDependencies(refDependencies2);
        beanDefinitionMap.put(beanDefinition2.getId(), beanDefinition2);

    }

    @Test
    public void constructBeans() {
        BeanFactoryImpl beanFactory = new BeanFactoryImpl();
        Map<String, Bean> beans = beanFactory.constructBeans(beanDefinitionMap);
        assertEquals(2, beans.size());

        String key1 = "test1";
        Bean bean1 = beans.get("test1");
        Object object1 = bean1.getValue();
        assertEquals(Test1.class, object1.getClass());
        assertNotNull(bean1.getId());
        assertNotNull(bean1.getValue());
        assertEquals(key1, bean1.getId());

        String key2 = "test2";
        Bean bean2 = beans.get("test2");
        Object object2 = bean2.getValue();
        assertEquals(Test2.class, object2.getClass());
        assertNotNull(bean2.getId());
        assertNotNull(bean2.getValue());
        assertEquals(key2, bean2.getId());
    }

    @Test
    public void ConstructAndInjectValueDependencies() {
        BeanFactoryImpl beanFactory = new BeanFactoryImpl();
        Map<String, Bean> beans = beanFactory.constructBeans(beanDefinitionMap);

        beans = beanFactory.injectValueDependencies(beanDefinitionMap, beans);

        // bean1
        String key1 = "test1";
        Bean bean1 = beans.get(key1);
        Object object1 = bean1.getValue();
        Test1 test1 = (Test1) object1;

        // Value dependency
        Object paramObject1 = test1.getTestName1();
        assertEquals(String.class, paramObject1.getClass());
        String paramValue1 = (String) paramObject1;
        assertEquals("testValue1", paramValue1);

        // Ref dependency
        assertNull(test1.getTestName2());

        //bean2
        String key2 = "test2";
        Bean bean2 = beans.get(key2);
        Object object2 = bean2.getValue();
        Test2 test2 = (Test2) object2;

        // Value dependency
        Object paramObject2 = test2.getTestName1();
        assertEquals(String.class, paramObject2.getClass());
        String paramValue2 = (String) paramObject2;
        assertEquals("testValue2", paramValue2);

        // Ref dependency
        assertNull(test2.getTestName2());
    }

    @Test
    public void ConstructAndInjectRefDependencies() {
        BeanFactoryImpl beanFactory = new BeanFactoryImpl();
        Map<String, Bean> beans = beanFactory.constructBeans(beanDefinitionMap);

        beans = beanFactory.injectRefDependencies(beanDefinitionMap, beans);

        // bean1
        String key1 = "test1";
        Bean bean1 = beans.get(key1);
        Object object1 = bean1.getValue();
        Test1 test1 = (Test1) object1;

        // Value dependency
        assertNull(test1.getTestName1());

        // Ref dependency
        Object paramObject1 = test1.getTestName2();
        assertEquals(Test2.class, paramObject1.getClass());
        Test2 paramValue1 = (Test2) paramObject1;
        assertEquals(beans.get("test2").getValue().getClass(), paramValue1.getClass());

        //bean2
        String key2 = "test2";
        Bean bean2 = beans.get(key2);
        Object object2 = bean2.getValue();
        Test2 test2 = (Test2) object2;

        // Value dependency
        assertNull(test2.getTestName1());

        // Ref dependency
        Object paramObject2 = test2.getTestName2();
        assertEquals(Test1.class, paramObject2.getClass());
        Test1 paramValue2 = (Test1) paramObject2;
        assertEquals(beans.get(key1).getValue().getClass(), paramValue2.getClass());

    }
}