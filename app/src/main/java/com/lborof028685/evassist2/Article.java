package com.lborof028685.evassist2;

import android.net.Uri;

import java.util.Date;

public class Article {
    String title;
    String link;
    Date datePosted;
    String description;
    String operator;
    Uri webIcon;

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

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public Uri getWebIcon() {
        return webIcon;
    }

    public void setWebIcon(Uri webIcon) {
        this.webIcon = webIcon;
    }
}
