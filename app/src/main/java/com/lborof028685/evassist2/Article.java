package com.lborof028685.evassist2;

import java.util.Date;

public class Article {
    String title;
    String link;
    Date datePosted;
    String description;
    String operator;

    public Article() {
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
    public String getTitle() {
        return this.title;
    }

    public void setLink(String newLink){
        this.link = newLink;
    }
    public String getLink() {
        return this.link;
    }

    public void setDescription(String newDescription){
        this.link = newDescription;
    }
    public String getDescription() {
        return this.description;
    }

    public void setDate(String newDate){
        // Parse String date as yyyy-MM-dd hh:mm:ss
        //this.date = newDate;
    }
    public Date getDate(){
        return this.datePosted;
    }

    public void setOperator(String newOperator) {
        this.operator = newOperator;
    }
    public String getOperator() {
        return this.operator;
    }
}
