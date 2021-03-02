package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app.utils.Variables;

@IgnoreExtraProperties
public class News implements Cloneable {
    private int id;
    private String date, title, matter, forRank, picture;

    public News() {
    }

    public News(int id, String date, String title, String matter, String picture, String forRank) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.matter = matter;
        this.picture = picture;
        this.forRank = forRank;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public void setForRank(String forRank) {
        this.forRank = forRank;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getMatter() {
        return matter;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getForRank() {
        return forRank;
    }

    public String getDate_Output() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Variables.LOCALE);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm", Variables.LOCALE);
        Date datum;
        String datumNeu;
        try {
            datum = formatter2.parse(date);
            datumNeu = format.format(datum);
            return datumNeu;
        } catch (ParseException e) {
            return date;
        }
    }

    @Nullable
    public Date getDate_Date() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Variables.LOCALE);
        try {
            return formatter2.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", getDate_Date());
        data.put("title", getTitle());
        data.put("matter", getMatter());
        data.put("picture", getPicture());
        data.put("forRank", getForRank());

        return data;
    }
}
