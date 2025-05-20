package com.example.csvprocessor.model;

import lombok.Data;

@Data
public class HeaderColumn {
    private String name;
    private boolean selected;

    public HeaderColumn(String name) {
        this.name = name;
        this.selected = false;
    }

    public HeaderColumn() {
    }
}
