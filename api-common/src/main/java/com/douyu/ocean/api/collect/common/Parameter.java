package com.douyu.ocean.api.collect.common;

public class Parameter {
        String name;
        String type;
        String desc;
        boolean notNull;

        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
            this.desc = name;
            this.notNull = false;
        }

        @Override
        public String toString() {
            return "com.douyu.ocean.api.collect.Parameter{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", desc='" + desc + '\'' +
                    ", notNull=" + notNull +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isNotNull() {
            return notNull;
        }

        public void setNotNull(boolean notNull) {
            this.notNull = notNull;
        }
    }