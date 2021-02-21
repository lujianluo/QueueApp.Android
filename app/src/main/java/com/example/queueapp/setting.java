package com.example.queueapp;

public class setting {
    public String identifier;
    public int minPax;
    public int maxPax;
    public int queueLimit;

    public setting() {
    }

    public setting(String identifier, int minPax, int maxPax, int queueLimit) {
        this.identifier = identifier;
        this.minPax = minPax;
        this.maxPax = maxPax;
        this.queueLimit = queueLimit;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getMinPax() {
        return minPax;
    }

    public void setMinPax(int minPax) {
        this.minPax = minPax;
    }

    public int getMaxPax() {
        return maxPax;
    }

    public void setMaxPax(int maxPax) {
        this.maxPax = maxPax;
    }

    public int getQueueLimit() {
        return queueLimit;
    }

    public void setQueueLimit(int queueLimit) {
        this.queueLimit = queueLimit;
    }
}
