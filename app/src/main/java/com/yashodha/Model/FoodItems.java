package com.yashodha.Model;

public class FoodItems {
    private String title;
    private int imageResource;
    private double fee;

    public FoodItems(String title, int imageResource, double fee) {
        this.title = title;
        this.imageResource = imageResource;
        this.fee = fee;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public double getFee() {
        return fee;
    }
}
