package com.example;

public class TestResultData {
    private String applicationName;
    private String env;
    private String featureName;
    private String dateTime;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getFailPercent() {
        return failPercent;
    }

    public void setFailPercent(int failPercent) {
        this.failPercent = failPercent;
    }

    public int getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(int passPercent) {
        this.passPercent = passPercent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private int failPercent;
    private int passPercent;
    private String tag;

    // Getters and setters for the properties

    // You can also override equals and hashCode methods to properly group data
    // based on application and env
}