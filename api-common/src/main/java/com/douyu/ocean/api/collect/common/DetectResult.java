package com.douyu.ocean.api.collect.common;

/**
 * ${DESCRIPTION}
 *
 * @author sxp
 * @create at 2018/7/8.12:07
 */
public class DetectResult {

    int update;

    int add;

    String project;

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public DetectResult(int update, int add, String project) {
        this.update = update;
        this.add = add;
        this.project = project;
    }

    @Override
    public String toString() {
        return "DetectResult{" +
                "update=" + update +
                ", add=" + add +
                ", project='" + project + '\'' +
                '}';
    }
}
