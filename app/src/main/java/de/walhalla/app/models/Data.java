package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

@IgnoreExtraProperties
public class Data implements Cloneable {
    private int id;
    private String name;

    public Data() {
        this.name = "";
        this.id = 0;
    }

    public Data(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

}
