package com.study.ioccontainer.context.impl;

import com.study.ioccontainer.context.ApplicationContext;
import com.study.ioccontainer.context.definition.reader.BeanDefinitionReader;
import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.BeanInstantiationException;
import org.apache.commons.text.WordUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private BeanDefinitionReader beanDefinitionReader;
    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Bean> beans;

    public void readBeanDefinitions(){
        beanDefinitions = beanDefinitionReader.readBeanDefinitions();
    }

    public Object getBean(String id) {
        return beans.get(id).getValue();
    }

    public <T> T getBean(Class<T> clazz) {
        for (String beanId : beans.keySet()) {
            Object value = beans.get(beanId).getValue();
            if (clazz == value.getClass()){
                return (T)value;
            }
        }
        return null;
    }

    public <T> T getBean(String id, Class<T> clazz){
        Object value = beans.get(id).getValue();
        return (T)value;
    }

    public void constructBeans() {
        beans = new HashMap<>();
        for (String key : beanDefinitions.keySet()) {
            String className = null;
            try {
                BeanDefinition beanDefinition = beanDefinitions.get(key);
                className = beanDefinition.getClassName();
                Class clazz = Class.forName(className);
                Bean bean = new Bean();
                bean.setId(key);
                Object object = clazz.newInstance();
                bean.setValue(object);
                beans.put(key, bean);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new BeanInstantiationException("Class " + className + " not found", e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new BeanInstantiationException("Cannot access constructor for " + className, e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new BeanInstantiationException("Cannot create instance of " + className, e);
            }
        }
    }

    public void injectValueDependencies() {
        for (String key : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(key);
            Bean bean = beans.get(key);
            Object object = bean.getValue();
            Class clazz = object.getClass();
            Map<String, String> valueDependencies = beanDefinition.getValueDependencies();

            for (String dependencyName : valueDependencies.keySet()) {
                Method method = getSetter(clazz, dependencyName);
                if (method == null) {
                    throw new BeanInstantiationException("Setter method not found");
                }
                String value = valueDependencies.get(dependencyName);
                invokeValueParameter(object, method, value);
            }
        }
    }

    public void injectRefDependencies() {
        for (String key : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(key);
            Bean bean = beans.get(key);
            Object object = bean.getValue();
            Class clazz = object.getClass();
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();

            for (String dependencyName : refDependencies.keySet()) {
                Method method = getSetter(clazz, dependencyName);
                if (method == null) {
                    throw new BeanInstantiationException("Setter method not found");
                }
                String valueBeanId = refDependencies.get(dependencyName);
                Object value = beans.get(valueBeanId).getValue();
                invokeRefParameter(object, method, value);
            }
        }
    }

    private Method getSetter(Class clazz, String dependencyName) {
        String setterName = "set" + WordUtils.capitalize(dependencyName);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private void invokeValueParameter(Object object, Method method, String value) {
        Class paramType = method.getParameterTypes()[0];
        try {
            if (paramType == Integer.TYPE || paramType == Integer.class) {
                method.invoke(object, Integer.parseInt(value));
            } else if (paramType == String.class) {
                method.invoke(object, value);
            } else if (paramType == Double.TYPE || paramType == Double.class) {
                method.invoke(object, Double.parseDouble(value));
            } else if (paramType == Boolean.TYPE || paramType == Boolean.class) {
                method.invoke(object, "true".equalsIgnoreCase(value));
            } else if (paramType == Byte.TYPE || paramType == Byte.class) {
                method.invoke(object, Byte.parseByte(value));
            } else if (paramType == Character.TYPE || paramType == Character.class) {
                if (value.length() != 1) {
                    throw new BeanInstantiationException("Error casting property to char");
                }
                method.invoke(object, value.charAt(0));
            } else {
                throw new BeanInstantiationException("Unknown property type + " + paramType);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot access setter method", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot invoke setter method", e);
        }
    }

    private void invokeRefParameter(Object object, Method method, Object value) {
        Class paramType = method.getParameterTypes()[0];

        if (!paramType.isAssignableFrom(value.getClass())) {
            throw new BeanInstantiationException("Invalid ref parameter type");
        }
        try {
            method.invoke(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot access setter method", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot invoke setter method", e);
        }
    }

    public Map<String, Bean> getBeans() {
        return beans;
    }

    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitionReader = beanDefinitionReader;
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
