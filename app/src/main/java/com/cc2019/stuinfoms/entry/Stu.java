package com.cc2019.stuinfoms.entry;

public class Stu {
    private int id;
    private String name;
    private String classinfo;
    private String score;
    private String techcomment;

    public Stu(){}
    public Stu(int id, String name, String classinfo, String score, String techcomment) {
        this.id = id;
        this.name = name;
        this.classinfo = classinfo;
        this.score = score;
        this.techcomment = techcomment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassinfo() {
        return classinfo;
    }

    public void setClassinfo(String classinfo) {
        this.classinfo = classinfo;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTechcomment() {
        return techcomment;
    }

    public void setTechcomment(String techcomment) {
        this.techcomment = techcomment;
    }
}
