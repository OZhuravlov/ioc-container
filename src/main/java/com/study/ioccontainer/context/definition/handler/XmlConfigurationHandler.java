package com.study.ioccontainer.context.definition.handler;

import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.InvalidBeanDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class XmlConfigurationHandler extends DefaultHandler {

    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    BeanDefinition beanDefinition = null;
    Map<String, String> valueDependencies = null;
    Map<String, String> refDependencies = null;

    private String name = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){
        if (qName.equalsIgnoreCase("bean")) {
            beanDefinition = new BeanDefinition();
            String id = attributes.getValue("id");
            String className = attributes.getValue("class");
            beanDefinition.setId(id);
            beanDefinition.setClassName(className);
            valueDependencies = new HashMap<>();
            refDependencies = new HashMap<>();
        } else if (qName.equals("property")) {
            String name = attributes.getValue("name");
            validatePropertyName(name);
            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");
            validatePropertyAttributes(value, ref);
            if(value != null){
                valueDependencies.put(name, value);
            } else {
                refDependencies.put(name, ref);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("bean")) {
            beanDefinition.setValueDependencies(valueDependencies);
            beanDefinition.setRefDependencies(refDependencies);
            beanDefinitions.put(beanDefinition.getId(), beanDefinition);
        }
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    private void validatePropertyName(String name){
        if(name == null){
            throw new InvalidBeanDefinition("property does not have name");
        } else if(valueDependencies.containsKey(name) || refDependencies.containsKey(name)){
            throw new InvalidBeanDefinition("duplicated property " + name);
        }
    }

    private void validatePropertyAttributes(String value, String ref){
        if(value == null && ref == null){
            throw new InvalidBeanDefinition("property " + name + " does not have value or ref");
        }
        if(value != null && ref != null){
            throw new InvalidBeanDefinition("property " + name + " have both value and ref");
        }
    }
}