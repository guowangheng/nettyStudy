package com.easydo.client.config;

import com.easydo.common.annotation.client.EasydoReference;
import com.easydo.common.metadata.ReferenceMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * 用于Spring动态注入
 * @author WeanGuo
 */
public class ReferenceBeanRegisterConfig implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceBeanRegisterConfig.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        String[] definitionNames = beanFactory.getBeanDefinitionNames();
        for (String name : definitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            Class<?> classType = null;
            try {
                String beanClassName = beanDefinition.getBeanClassName();
                if (StringUtils.isEmpty(beanClassName)) {
                    continue;
                }
                classType = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("class not found", e);
            }
            ReferenceMetadata referenceMetadata = findReferenceMetadata(classType);
            Collection<InjectionMetadata.InjectedElement> elements = referenceMetadata.getInjectedElements();
            if (beanDefinition instanceof AnnotatedBeanDefinition && elements.size() > 0) {
                // 获得注解上的参数信息
                for (InjectionMetadata.InjectedElement element : elements) {
                    Field field = (Field) element.getMember();
                    String beanClassName = field.getType().getCanonicalName();
                    EasydoReference annotation = field.getAnnotation(EasydoReference.class);
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("retry", annotation.retry());
                    parameterMap.put("timeMsec", annotation.timeMsec());
                    BeanDefinitionRegistry bdr = (BeanDefinitionRegistry)beanFactory;
                    GenericBeanDefinition definition = new GenericBeanDefinition();
                    definition.setBeanClass(EasydoReferenceBean.class);
                    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                    definition.getConstructorArgumentValues().addGenericArgumentValue(parameterMap);
                    definition.setScope(BeanDefinition.SCOPE_SINGLETON);
                    definition.setAutowireCandidate(true);
                    String beanName = field.getName();
                    bdr.registerBeanDefinition(beanName, definition);
                }
            }
        }
    }

    private ReferenceMetadata findReferenceMetadata(Class<?> clazz) {
        ReferenceMetadata metadata = buildReferenceMetadata(clazz);
        return metadata;
    }

    private ReferenceMetadata buildReferenceMetadata(Class<?> beanClass) {
        Collection<ReferenceFieldElement> fieldElements = findFieldReferenceMetadata(beanClass);
        return new ReferenceMetadata(beanClass, combine(fieldElements));
    }

    private Collection<ReferenceFieldElement> findFieldReferenceMetadata(Class<?> beanClass) {
        final List<ReferenceFieldElement> elements = new LinkedList<>();

        ReflectionUtils.doWithFields(beanClass, field -> {
            EasydoReference reference = getAnnotation(field, EasydoReference.class);
            if (reference != null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("@Reference annotation is not supported on static fields: " + field);
                    }
                    return;
                }
                elements.add(new ReferenceFieldElement(field, reference));
            }
        });
        return elements;
    }

    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private Field field;

        private EasydoReference reference;

        protected ReferenceFieldElement(Field field, EasydoReference reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }
    }

    private static <T> Collection<T> combine(Collection<? extends T>... elements) {
        List<T> allElements = new ArrayList<T>();
        for (Collection<? extends T> e : elements) {
            allElements.addAll(e);
        }
        return allElements;
    }
}