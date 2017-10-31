package com.yube.TMP.Contact;

/**
 * Created by Tolga on 25.10.2017.
 */

public class PlayListContact {
    String name;
    String path;
    String category;
    int id;
    public PlayListContact(String name, String path, String category,int id) {
        this.name = name;
        this.path = path;
        this.category = category;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
