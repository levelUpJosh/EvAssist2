package com.lborof028685.evassist2;

import java.util.ArrayList;

public class ArticleList {
    ArrayList<Article> articles= new ArrayList<Article>();;
    public ArticleList() {

    }
    public ArrayList<String>  getAllTitles() {
        ArrayList<String> data = new ArrayList<String>();

        articles.forEach((article) ->{
            data.add(article.getTitle());

        });
        return data;
    }
    public void add(Article article) {
        articles.add(article);
    }
    public Article get(int id) {
        return articles.get(id);
    }
    public Article getLastArticle() {
        return articles.get(articles.size() - 1);
    }
}
