package com.study.ioccontainer.context.impl;

import com.study.ioccontainer.context.definition.reader.impl.XmlBeanDefinitionReader;
import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ClassPathApplicationContextTest {

    ClassPathApplicationContext applicationContext;
    String xmlFilename = "unit_test.xml";

    @Before
    public void setUp(){
        String xmlDocument = "<beans>" +
                "<bean id=\"test1\" class=\"com.study.ioccontainer.context.definition.handler.XmlConfigurationHandler\">" +
                "   <property name=\"testName1\" value=\"testValue1\" />" +
                "   <property name=\"testName2\" ref=\"test2\" />" +
                "</bean>" +
                "<bean id=\"test2\" class=\"com.study.ioccontainer.context.definition.reader.impl.XmlBeanDefinitionReader\">" +
                "   <property name=\"testName1\" value=\"testValue2\" />" +
                "   <property name=\"testName2\" ref=\"test1\" />" +
                "</bean>" +
                "</beans>";

        try (FileWriter fileWriter = new FileWriter(xmlFilename)){
            fileWriter.write(xmlDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        applicationContext = new ClassPathApplicationContext();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(xmlFilename);
        applicationContext.setBeanDefinitionReader(xmlBeanDefinitionReader);
    }

    @Test
    public void readBeanDefinitions() {

        applicationContext.readBeanDefinitions();
        Map<String, BeanDefinition> beanDefinitions = applicationContext.getBeanDefinitions();

        assertEquals(2, beanDefinitions.size());

        BeanDefinition beanDefinition1 = beanDefinitions.get("test1");
        assertEquals("com.test1", beanDefinition1.getClassName());

        assertEquals(1, beanDefinition1.getValueDependencies().size());
        String value1 = beanDefinition1.getValueDependencies().get("testName1");
        assertEquals("testValue1", value1);

        assertEquals(1, beanDefinition1.getRefDependencies().size());
        String value2 = beanDefinition1.getRefDependencies().get("testName2");
        assertEquals("test2", value2);

    }

    @Test
    public void readDefinitionsAndConstructBeans() {
        applicationContext.readBeanDefinitions();
        applicationContext.constructBeans();

        Map<String, Bean> beans = applicationContext.getBeans();
        assertEquals(2, beans.size());

        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            assertNotNull(entry.getValue());
            assertNotNull(entry.getValue().getId());
            assertNotNull(entry.getValue().getValue());
            assertEquals(entry.getKey(), entry.getValue().getId());
        }

    }

//    @Test
//    public void injectValueDependencies() {
//    }
//
//    @Test
//    public void injectRefDependencies() {
//    }

}