package de.walhalla.app.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@IgnoreExtraProperties
public class Rank implements Cloneable {
    public static final String FIRST_FRATERNITY = "first_fraternity";
    public static final String FULL_MEMBER = "full_member";
    public static final String IN_LOCO = "in_loco";
    public static final String PRICE = "price_semester";
    public static final String NAME = "rank_name";
    private int id;
    private String rank_name;
    private boolean first_fraternity, full_member, in_loco;
    private Map<String, Object> price_semester;

    public Rank() {
    }

    @Exclude
    public int getId() {
        return id;
    }

    /**
     * The id is to get the correct element inside the rank map on the server
     *
     * @param id Object key of the parent element
     */
    @Exclude
    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getRank_name() {
        return rank_name;
    }

    public void setRank_name(String rank_name) {
        this.rank_name = rank_name;
    }

    public boolean isFirst_fraternity() {
        return first_fraternity;
    }

    public void setFirst_fraternity(boolean first_fraternity) {
        this.first_fraternity = first_fraternity;
    }

    public boolean isFull_member() {
        return full_member;
    }

    public void setFull_member(boolean full_member) {
        this.full_member = full_member;
    }

    public boolean isIn_loco() {
        return in_loco;
    }

    public void setIn_loco(boolean in_loco) {
        this.in_loco = in_loco;
    }

    public Map<String, Object> getPrice_semester() {
        return price_semester;
    }

    public void setPrice_semester(Map<String, Object> price_semester) {
        this.price_semester = price_semester;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
