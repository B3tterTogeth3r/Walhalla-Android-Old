package de.walhalla.app.models;

//import com.example.walhalla.utils.Find;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

@IgnoreExtraProperties
public class Meeting implements Cloneable {
    private int id, cc, ahc, bc, ac;

    public Meeting() {
        this.id = 0;
        this.cc = 0;
        this.ahc = 0;
        this.bc = 0;
        this.ac = 0;
    }

    public Meeting(int id, int cc, int ahc, int bc, int ac) {
        this.id = id;
        this.cc = cc;
        this.ahc = ahc;
        this.bc = bc;
        this.ac = ac;
    }

    public int getId() {
        return id;
    }

    public int getCc() {
        return cc;
    }

    public int getAhc() {
        return ahc;
    }

    public int getBc() {
        return bc;
    }

    public int getAc() {
        return ac;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public void setAhc(int ahc) {
        this.ahc = ahc;
    }

    public void setBc(int bc) {
        this.bc = bc;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
