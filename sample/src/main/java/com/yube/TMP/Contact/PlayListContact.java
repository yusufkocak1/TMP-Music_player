package com.yube.TMP.Contact;

/**
 * Created by Tolga on 12.10.2017.
 */

public class PlayListContact {
    String name;
    String path;
    String category;

    public PlayListContact(String name, String path, String category) {
        this.name = name;
        this.path = path;
        this.category = category;
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
