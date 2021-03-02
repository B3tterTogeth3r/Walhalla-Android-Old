package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

@IgnoreExtraProperties
public class HelperKind implements Cloneable {
    private int id;
    private String tag;

    public HelperKind() {
        this.tag = "";
        this.id = 0;
    }

    public HelperKind(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
