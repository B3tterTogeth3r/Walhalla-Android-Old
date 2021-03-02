package de.walhalla.app.models;

import org.jetbrains.annotations.NotNull;

public class Budget implements Cloneable {
    private final Event event;
    private final AllEvents allEvents;

    public Budget() {
        this.event = new Event();
        this.allEvents = new AllEvents();
    }

    public Budget(Event event, AllEvents allEvents) {
        this.event = event;
        this.allEvents = allEvents;
    }

    public String getDate() {
        //TODO return event.getDateProgramm();
        return "";
    }

    public String getName() {
        if (allEvents != null) {
            return allEvents.getName();
        } else {
            return null;
        }
    }

    public float getExpectedIncome() {
        return (float) (allEvents.getAfter() + allEvents.getBefore());
    }

    public float getExpectedExpense() {
        return (float) (allEvents.getAfter() + allEvents.getBefore());
    }

    public float getRate() {
        return (float) (allEvents.getBefore());
    }

    public float getSubsidy() {
        return (float) (allEvents.getAfter());
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
