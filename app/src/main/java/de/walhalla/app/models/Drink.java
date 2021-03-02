package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import de.walhalla.app.utils.Date_Time_Format_Manual;

@IgnoreExtraProperties
public class Drink implements Cloneable {
    private int id, who, amount, kind;
    private String date;

    public Drink() {
        this.id = 0;
        this.date = "";
        this.who = 0;
        this.amount = 0;
        this.kind = 0;
    }

    public Drink(int id, String date, int who, int amount, int kind) {
        this.id = id;
        this.date = date;
        this.who = who;
        this.amount = amount;
        this.kind = kind;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWho(int who) {
        this.who = who;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public Date getDate_Value() {
        return Date_Time_Format_Manual.parseDate(this.date);
    }

    public int getWho() {
        return who;
    }

    public Person getPerson() {
        return new Person();//TODO Find.Person(who);
    }

    public int getAmount() {
        return amount;
    }

    public int getKind() {
        return kind;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
