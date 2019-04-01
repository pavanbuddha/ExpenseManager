package com.pavanbuddha.expensemanager;

import android.support.annotation.NonNull;

import java.util.Date;

public class Expenditure implements Comparable<Expenditure>
{

    private String title;
    private double amount;
    private Date date;
    private boolean expense;

    @Override
    public String toString() {
        return "Expenditure{" +
                "category=" + category +
                '}';
    }

    private int category;
    private String description;

    public Expenditure(String title, double amount, Date date, boolean expense, int category, String description) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.expense = expense;
        this.category = category;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isExpense() {
        return expense;
    }

    public void setExpense(boolean expense) {
        this.expense = expense;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int compareTo(@NonNull Expenditure o) {
        return getDate().compareTo(o.getDate());
    }
}