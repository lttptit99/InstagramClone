package com.example.instagram.model;

public class CommentModel {
    private String comment;
    private String user_comment;

    public CommentModel() {
    }

    public CommentModel(String comment, String user_comment) {
        this.comment = comment;
        this.user_comment = user_comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }
}
