package com.example.queueapp;

public class restaurant {
    String id, name, menu, owner;

    public restaurant (String id, String name, String menu, String owner){
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.owner = owner;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
