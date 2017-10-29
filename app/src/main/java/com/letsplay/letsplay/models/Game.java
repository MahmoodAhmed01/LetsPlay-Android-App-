package com.letsplay.letsplay.models;

import java.util.Date;

/**
 * Created by Mahmood on 8/23/2017.
 */

public class Game {
    Integer id;
    String name;
    Date createdAt;
    Date updatedAt;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPlainName() {
        if(name == null) return  "";

        String[] words = name.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(Character.toUpperCase(word.charAt(0)));
            builder.append(word.substring(1));
            builder.append(" ");
        }

        return builder.toString().trim();
    }
}
