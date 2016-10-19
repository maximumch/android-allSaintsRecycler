package com.example.student1.allsaints;

public class Saint implements Comparable<Saint> {

    // Для того, чтобы экземпляры класса можно было отсортировать
    // класс должен имплементить Comparable
    @Override
    public int compareTo(Saint o) {
        return getName().compareTo(o.getName());
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getDod() {
        return dod;
    }

    public Saint(String name, String dob, String dod, float rating) {
        this.name = name;
        this.dob = dob;
        this.dod = dod;
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setDod(String dod) {
        this.dod = dod;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private String name;
    private String dob;
    private String dod;
    private float rating = 0f;
}
