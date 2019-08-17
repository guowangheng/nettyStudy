package com.easydo.common.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Getter
@Setter
@Component
public class Invoker<T> extends Packager {

    private Class<T> classType;

    private String clazzName;

    private String methodName;

    private Class<?>[] parameters;

    private Object[] params;

    private int retry;

    private long timeMsec;

    private Long key;

    public Invoker() {

    }

    public Invoker(int state) {
        super(state);
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public void setParameters(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public long getTimeMsec() {
        return timeMsec;
    }

    public void setTimeMsec(long timeMsec) {
        this.timeMsec = timeMsec;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Invoker{" +
                "classType=" + classType +
                ", clazzName='" + clazzName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", params=" + Arrays.toString(params) +
                ", retry=" + retry +
                ", timeMsec=" + timeMsec +
                ", key=" + key +
                '}';
    }
}
