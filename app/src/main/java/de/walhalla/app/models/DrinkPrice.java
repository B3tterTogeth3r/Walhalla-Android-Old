package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

@IgnoreExtraProperties
public class DrinkPrice implements Cloneable {
    private int id, from, over, ek, kind;
    private float price;

    public DrinkPrice() {
        this.id = 0;
        this.from = 0;
        this.kind = 0;
        this.over = 0;
        this.price = 0;
        this.ek = 0;
    }

    public DrinkPrice(int id, int from, int kind, int over, float price, int ek) {
        this.id = id;
        this.from = from;
        this.kind = kind;
        this.over = over;
        this.price = price;
        this.ek = ek;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setOver(int over) {
        this.over = over;
    }

    public void setEk(int ek) {
        this.ek = ek;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public int getKind() {
        return kind;
    }

    public int getOver() {
        return over;
    }

    public float getPrice() {
        return price;
    }

    public int getEk() {
        return ek;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
