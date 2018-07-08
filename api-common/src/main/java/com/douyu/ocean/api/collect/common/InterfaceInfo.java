package com.douyu.ocean.api.collect.common;

import java.util.List;

public class InterfaceInfo {

        String path;

        String headers;

        String methods;

        String consumes;

        String produces;

        List<Parameter> parameters;

        public InterfaceInfo(String path, String headers, String methods, String consumes, String produces, List<Parameter> parameters) {
            this.path = path;
            this.headers = headers;
            this.methods = methods;
            this.consumes = consumes;
            this.produces = produces;
            this.parameters = parameters;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getHeaders() {
            return headers;
        }

        public void setHeaders(String headers) {
            this.headers = headers;
        }

        public String getMethods() {
            return methods;
        }

        public void setMethods(String methods) {
            this.methods = methods;
        }

        public String getConsumes() {
            return consumes;
        }

        public void setConsumes(String consumes) {
            this.consumes = consumes;
        }

        public String getProduces() {
            return produces;
        }

        public void setProduces(String produces) {
            this.produces = produces;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(List<Parameter> parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "com.douyu.ocean.api.collect.InterfaceInfo{" +
                    "path='" + path + '\'' +
                    ", headers='" + headers + '\'' +
                    ", methods='" + methods + '\'' +
                    ", consumes='" + consumes + '\'' +
                    ", produces='" + produces + '\'' +
                    ", parameters=" + parameters +
                    '}';
        }
    }