package com.easydo.common.RegisterTable;

import com.easydo.common.utils.ConcurrentHashSet;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterTableService {

    private static final ConcurrentHashMap<String, Set<AbstractWrapper>> serverTable = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Set<AbstractWrapper>> clientTable = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Set<AbstractWrapper>> getServerTable() {
        return serverTable;
    }

    public static ConcurrentHashMap<String, Set<AbstractWrapper>> getClientTable() {
        return clientTable;
    }

    public static void setServerTable(String className, AbstractWrapper serverWrapper) {
        Set<AbstractWrapper> wrapperSet = serverTable.get(className);
        if (null == wrapperSet) {
            serverTable.putIfAbsent(className, new ConcurrentHashSet<>());
            wrapperSet = serverTable.get(className);
        }
        wrapperSet.add(serverWrapper);
    }

    public static void setClientTable(String className, AbstractWrapper clientWrapper) {
        Set<AbstractWrapper> wrapperSet = clientTable.get(className);
        if (null == wrapperSet) {
            clientTable.putIfAbsent(className, new ConcurrentHashSet<>());
            wrapperSet = clientTable.get(className);
        }
        wrapperSet.add(clientWrapper);
    }

    public static Set getByType(String clazzName) {
        return serverTable.get(clazzName);
    }
}
