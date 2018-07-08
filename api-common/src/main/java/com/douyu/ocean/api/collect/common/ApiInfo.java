package com.douyu.ocean.api.collect.common;

import java.util.List;

public class ApiInfo {

    String name;

    List<InterfaceInfo> interfaceInfos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InterfaceInfo> getInterfaceInfos() {
        return interfaceInfos;
    }

    public void setInterfaceInfos(List<InterfaceInfo> interfaceInfos) {
        this.interfaceInfos = interfaceInfos;
    }
}