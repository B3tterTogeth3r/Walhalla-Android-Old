package de.walhalla.app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Event implements Cloneable {
    public static final String ADDITION = "addition";
    public static final String BUDGET = "budget";
    public static final String COLLAR = "collar";
    public static final String DESCRIPTION = "description";
    public static final String END = "end";
    public static final String HELP = "help";
    public static final String LOCATION_COORDINATES = "location_coordinates";
    public static final String LOCATION_NAME = "location_name";
    public static final String MEETING = "meeting";
    public static final String PUNCTUALITY = "punctuality";
    public static final String START = "start";
    public static final String TITLE = "title";

    private Map<String, Object> addition = null;
    private Map<String, Object> budget = new HashMap<>();
    private Map<String, Object> help = null;
    private String id = "";
    private String collar = "";
    private String description = "";
    private String location_name = "";
    private String punctuality = "";
    private String title = "";
    private Timestamp start = new Timestamp(Calendar.getInstance().getTime());
    private Timestamp end = new Timestamp(Calendar.getInstance().getTime());
    private GeoPoint location_coordinates = new GeoPoint(49.784389, 9.924648);
    private boolean meeting = false;

    public Event() {
        budget.put("after", 0);
        budget.put("before", 0);
        budget.put("current", 0);
    }

    public Event(Map<String, Object> addition, Map<String, Object> budget, Map<String, Object> help, String collar, String description, String location_name, String punctuality, String title, Timestamp start, Timestamp end, GeoPoint location_coordinates, boolean meeting) {
        this.budget.put("after", 0);
        this.budget.put("before", 0);
        this.budget.put("current", 0);

        this.id = "";
        this.addition = addition;
        this.budget = budget;
        this.help = help;
        this.collar = collar;
        this.description = description;
        this.location_name = location_name;
        this.punctuality = punctuality;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location_coordinates = location_coordinates;
        this.meeting = meeting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public Map<String, Object> getAddition() {
        return addition;
    }

    public void setAddition(Map<String, Object> addition) {
        this.addition = addition;
    }

    public Map<String, Object> getBudget() {
        return budget;
    }

    public void setBudget(Map<String, Object> budget) {
        this.budget = budget;
    }

    @Nullable
    public Map<String, Object> getHelp() {
        return help;
    }

    public void setHelp(Map<String, Object> help) {
        this.help = help;
    }

    public String getCollar() {
        return collar;
    }

    public void setCollar(String collar) {
        this.collar = collar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(String punctuality) {
        this.punctuality = punctuality;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public GeoPoint getLocation_coordinates() {
        return location_coordinates;
    }

    public void setLocation_coordinates(GeoPoint location_coordinates) {
        this.location_coordinates = location_coordinates;
    }

    public boolean isMeeting() {
        return meeting;
    }

    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put(ADDITION, getAddition());
        data.put(BUDGET, getBudget());
        data.put(HELP, getHelp());
        data.put(COLLAR, getCollar());
        data.put(DESCRIPTION, getDescription());
        data.put(LOCATION_NAME, getLocation_name());
        data.put(PUNCTUALITY, getPunctuality());
        data.put(TITLE, getTitle());
        data.put(START, getStart());
        data.put(END, getEnd());
        data.put(LOCATION_COORDINATES, getLocation_coordinates());
        data.put(MEETING, isMeeting());

        return data;
    }

}
