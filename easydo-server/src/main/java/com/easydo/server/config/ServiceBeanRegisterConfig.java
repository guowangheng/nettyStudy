package com.easydo.server.config;

import com.easydo.common.annotation.server.EasydoService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

/**
 * 用于Spring动态注入
 * @author WeanGuo
 */
public class ServiceBeanRegisterConfig implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ClassPathScanningCandidateComponentProvider provider = scannerOwnAnnotation();
        provider.addIncludeFilter(new AnnotationTypeFilter(EasydoService.class));
        // TODO 确认是否有效
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.easydo.*");
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition definition = (AnnotatedBeanDefinition) beanDefinition;
                String beanClassName = definition.getBeanClassName();
                GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
                genericBeanDefinition.setBeanClass(EasydoServiceBean.class);
                genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                Map<String, Object> parameterMap = definition.getMetadata().getAnnotationAttributes(EasydoService.class.getCanonicalName());
                genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(parameterMap);
                genericBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                genericBeanDefinition.setAutowireCandidate(true);
                BeanDefinitionRegistry bdr = (BeanDefinitionRegistry)beanFactory;
                bdr.registerBeanDefinition(beanClassName, genericBeanDefinition);
            }
        }
    }

    public ClassPathScanningCandidateComponentProvider scannerOwnAnnotation () {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent();
            }
        };
    }

}