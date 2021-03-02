package de.walhalla.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Find;

@IgnoreExtraProperties
public class Helper implements Cloneable {
    int event, kind;
    String person;

    public Helper() {
    }

    public Helper(int event, int kind, String person) {
        this.event = event;
        this.person = person;
        this.kind = kind;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getEvent() {
        return event;
    }

    public Event getEventClean() {
        return Find.Event(event);
    }

    public int getKind() {
        return kind;
    }

    public String getJob() {
        ArrayList<HelperKind> helperKinds = Database.getHelperKindArrayList();
        for (int i = 0; i < helperKinds.size(); i++) {
            if (helperKinds.get(i).getId() == kind) {
                return helperKinds.get(i).getTag();
            }
        }
        return "";
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String id) {
        this.person = id;
    }

    public Person getPersonClean() {
        ArrayList<Person> persons = Database.getPersonArrayList();
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).getId().equals(person))
                return persons.get(i);
        }
        return new Person();
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("person", getPerson());
        data.put("kind", getKind());
        return data;
    }

}
