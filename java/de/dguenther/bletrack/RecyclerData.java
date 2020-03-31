package de.dguenther.bletrack;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerData {
    String title;
    String description;
    RecyclerView data;
    public String getTitle() {
        return title;
    }
    public RecyclerData withTitle(String title) {
        this.title = title;
        return this;
    }
    public String getDescription() {
        return description;
    }
    public RecyclerData withDescription(String description) {
        this.description = description;
        return this;
    }

}