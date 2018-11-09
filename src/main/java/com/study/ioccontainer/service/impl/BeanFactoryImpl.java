package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.BeanInstantiationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanFactoryImpl implements BeanFactory {

    public Map<String, Bean> constructBeans(Map<String, BeanDefinition> beanDefinitions) {
        Map<String, Bean> beans = new HashMap<>();
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
        return beans;
    }

    public Map<String, Bean> injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        for (String key : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(key);
            Bean bean = beans.get(key);
            Object object = bean.getValue();
            Class clazz = object.getClass();
            Map<String, String> valueDependencies = beanDefinition.getValueDependencies();

            for (String dependencyName : valueDependencies.keySet()) {
                Class dependencyClass;
                try {
                    Field field = clazz.getDeclaredField(dependencyName);
                    dependencyClass = field.getType();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new BeanInstantiationException("Field not found", e);
                }
                Method method = getSetter(clazz, dependencyName, dependencyClass);
                if (method == null) {
                    throw new BeanInstantiationException("Setter method not found");
                }
                String value = valueDependencies.get(dependencyName);
                invokeValueParameter(object, method, dependencyClass, value);
            }
        }
        return beans;
    }

    public Map<String, Bean> injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        for (String key : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(key);
            Bean bean = beans.get(key);
            Object object = bean.getValue();
            Class clazz = object.getClass();
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();

            for (String dependencyName : refDependencies.keySet()) {
                Class dependencyClass;
                try {
                    Field field = clazz.getDeclaredField(dependencyName);
                    dependencyClass = field.getType();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new BeanInstantiationException("Field not found", e);
                }
                Method method = getSetter(clazz, dependencyName, dependencyClass);
                if (method == null) {
                    throw new BeanInstantiationException("Appropriate setter method not found");
                }
                String valueBeanId = refDependencies.get(dependencyName);
                Object value = beans.get(valueBeanId).getValue();
                invokeRefParameter(object, method, dependencyClass, value);
            }
        }
        return beans;
    }

    private Method getSetter(Class clazz, String dependencyName, Class dependencyClass) {
        String setterName = "set" + Character.toUpperCase(dependencyName.charAt(0)) + dependencyName.substring(1);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterName)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == dependencyClass
                    ) {
                return method;
            }
        }
        return null;
    }

    private void invokeValueParameter(Object object, Method method, Class valueClass, String value) {
        try {
            if (valueClass == int.class || valueClass == Integer.class) {
                method.invoke(object, Integer.parseInt(value));
            } else if (valueClass == double.class || valueClass == Double.class) {
                method.invoke(object, Double.parseDouble(value));
            } else if (valueClass == boolean.class || valueClass == Boolean.class) {
                method.invoke(object, Boolean.parseBoolean(value));
            } else if (valueClass == byte.class || valueClass == Byte.class) {
                method.invoke(object, Byte.parseByte(value));
            } else if (valueClass == char.class || valueClass == Character.class) {
                if (value.length() != 1) {
                    throw new BeanInstantiationException("Error casting property to char");
                }
                method.invoke(object, value.charAt(0));
            } else if (valueClass == String.class) {
                method.invoke(object, value);
            } else {
                throw new BeanInstantiationException("Unknown property type + " + valueClass);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot access setter method", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new BeanInstantiationException("Cannot invoke setter method", e);
        }
    }

    private void invokeRefParameter(Object object, Method method, Class valueClass, Object value) {
        if (!valueClass.isAssignableFrom(value.getClass())) {
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

}
