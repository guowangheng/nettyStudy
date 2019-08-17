package com.easydo.common.metadata;

import org.springframework.beans.factory.annotation.InjectionMetadata;

import java.util.Collection;

public class ReferenceMetadata {

    private final Class<?> targetClass;

    private final Collection<InjectionMetadata.InjectedElement> injectedElements;

    public ReferenceMetadata(Class<?> targetClass, Collection<InjectionMetadata.InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Collection<InjectionMetadata.InjectedElement> getInjectedElements() {
        return injectedElements;
    }
}
