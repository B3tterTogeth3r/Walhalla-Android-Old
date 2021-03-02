package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

@IgnoreExtraProperties
public class AllEvents implements Cloneable {
    private String name;
    private int before;
    private int after;

    public AllEvents() {
        this.name = "";
        this.after = 0;
        this.before = 0;
    }

    public AllEvents(String name, int before, int after) {
        this.name = name;
        this.before = before;
        this.after = after;
    }

    public String getName() {
        return name;
    }

    public int getBefore() {
        return before;
    }

    public int getAfter() {
        return after;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
