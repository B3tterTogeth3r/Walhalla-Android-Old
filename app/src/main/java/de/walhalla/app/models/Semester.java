package de.walhalla.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Semester {
    @Exclude
    private int ID;
    private String Short;
    private String Long;
    private Date Begin, End;
    private ArrayList<Object> greeting = new ArrayList<>();
    private ArrayList<Object> notes = new ArrayList<>();

    public Semester() {
    }

    public Semester(int ID, String Short, String Long, Date Begin, Date End) {
        this.ID = ID;
        this.Short = Short;
        this.Long = Long;
        this.Begin = Begin;
        this.End = End;
    }

    @Exclude
    public int getID() {
        return ID;
    }

    @Exclude
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getShort() {
        return Short;
    }

    public void setShort(String aShort) {
        Short = aShort;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public Date getBegin() {
        return Begin;
    }

    public void setBegin(Date begin) {
        Begin = begin;
    }

    public Date getEnd() {
        return End;
    }


    public void setEnd(Date end) {
        End = end;
    }

    public void setGreeting(ArrayList<Object> greeting) {
        this.greeting = greeting;
    }

    public void setNotes(ArrayList<Object> notesList) {
        this.notes = notesList;
    }

    public ArrayList<Object> getGreeting() {
        return greeting;
    }

    public ArrayList<Object> getNotes() {
        return notes;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("long", getLong());
        data.put("short", getShort());
        data.put("begin", getBegin());
        data.put("end", getEnd());

        return data;
    }
}
