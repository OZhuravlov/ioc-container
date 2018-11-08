package com.study.ioccontainer.context.definition.reader.impl;

import com.study.ioccontainer.context.definition.handler.XmlConfigurationHandler;
import com.study.ioccontainer.context.definition.reader.BeanDefinitionReader;
import com.study.ioccontainer.entity.BeanDefinition;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Map;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {
    private static final String DEFAULT_XML_CONFIG_FILENAME = "applicationContext.xml";
    private InputStream definitionInputStream;

    public XmlBeanDefinitionReader() {
        this(DEFAULT_XML_CONFIG_FILENAME);
    }

    public XmlBeanDefinitionReader(String definitionFilename) {
        this.definitionInputStream = getInputStreamFromFile(definitionFilename);
    }

    public Map<String, BeanDefinition> readBeanDefinitions() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XmlConfigurationHandler handler = new XmlConfigurationHandler();
            parser.parse(definitionInputStream, handler);
            return handler.getBeanDefinitions();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read Bean definitions", e);
        }
    }

    private InputStream getInputStreamFromFile(String definitionFilename) {
        File file = new File(definitionFilename);
        if(!file.exists()) {
            file = new File(this.getClass().getClassLoader().getResource(definitionFilename).getFile());
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Application Configuration file not found", e);
        }
    }

}
