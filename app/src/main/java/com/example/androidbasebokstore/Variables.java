package com.example.androidbasebokstore;

/**
 * Created by mAni on 26/11/2017.
 */

public class Variables {

    private String BookTitle, AuthorName  , Image , TotalPages , Category , Comment , name , PostedBy , OrderNumber;

    private int Price;

    public Variables(){


    }


    public Variables(String bookTitle, String authorName, String image, String totalPages, String category, String comment, String name, String postedBy, String orderNumber, int price) {
        BookTitle = bookTitle;
        AuthorName = authorName;
        Image = image;
        TotalPages = totalPages;
        Category = category;
        Comment = comment;
        this.name = name;
        PostedBy = postedBy;
        OrderNumber = orderNumber;
        Price = price;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTotalPages() {
        return TotalPages;
    }

    public void setTotalPages(String totalPages) {
        TotalPages = totalPages;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(String postedBy) {
        PostedBy = postedBy;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }
}
