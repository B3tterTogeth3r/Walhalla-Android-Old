package de.walhalla.app.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app.utils.Find;
import de.walhalla.app.utils.Variables;

@IgnoreExtraProperties
public class Cashbox implements Cloneable {
    private int id;
    private String date;
    private float income;
    private float expense;
    private int event;
    private String purpose;
    private String add;
    private int recipe;

    public Cashbox() {
        this.id = 0;
        this.date = "";
        this.expense = 0;
        this.income = 0;
        this.event = 0;
        this.purpose = "";
        this.add = "";
        this.recipe = 0;
    }

    public Cashbox(int id, String date, float expense, float income, int event, String purpose, String add, int recipe) {
        this.id = id;
        this.date = date;
        this.expense = expense;
        this.income = income;
        this.event = event;
        this.purpose = purpose;
        this.add = add;
        this.recipe = recipe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public Date getDate() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Variables.LOCALE);
        Date datum;
        try {
            datum = formatter2.parse(date);
            return datum;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDateFormat() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Variables.LOCALE);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy", Variables.LOCALE);
        Date datum;
        String datumNeu;
        try {
            datum = formatter2.parse(date);
            assert datum != null;
            datumNeu = format.format(datum);
            return datumNeu;
        } catch (ParseException e) {
            return date;
        }
    }

    public float getIncome() {
        return income;
    }

    public String getIncomeFormat() {
        String format = String.format(Variables.LOCALE, "%.2f", getIncome());
        return "€ " + format;
    }

    public float getExpense() {
        return expense;
    }

    public String getExpenseFormat() {
        String format = String.format(Variables.LOCALE, "%.2f", getExpense());
        return "€ " + format;
    }

    public int getEvent() {
        return event;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getAdd() {
        return add;
    }

    public int getRecipe() {
        return recipe;
    }

    public String getEventText() {
        switch (event) {
            case (0):
                return "Spende";
            case (-1):
                return "Keilabend";
            case (-2):
                return "von AH-Kasse";
            case (-3):
                return "Kassenstand bei Kassenübergabe";
            default:
                return Find.Event(event).getTitle();
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public String getDateString() {
        return date;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", getDate());
        data.put("expense", getExpense());
        data.put("income", getIncome());
        data.put("event", getEvent());
        data.put("purpose", getPurpose());
        data.put("add", getAdd());
        data.put("recipe", getRecipe());

        return data;
    }
}
