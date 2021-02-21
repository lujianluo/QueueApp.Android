package com.example.queueapp;

public class info {
    public String identifier;
    public int current;
    public int issued;
    public int waiting;

    public info() {
    }

    public info(String identifier, int current, int issued, int waiting) {
        this.identifier = identifier;
        this.current = current;
        this.issued = issued;
        this.waiting = waiting;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getIssued() {
        return issued;
    }

    public void setIssued(int issued) {
        this.issued = issued;
    }

    public int getWaiting() {
        return waiting;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }
}
