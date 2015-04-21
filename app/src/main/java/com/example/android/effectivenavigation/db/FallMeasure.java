package com.example.android.effectivenavigation.db;

/**
 * Created by Esteban on 2014-10-14.
 */
public class FallMeasure {

    private long id_fall, id_user;
    private double x,y,z,timestamp, grade;

    public FallMeasure(long id_fall) {
        this.id_fall = id_fall;
    }

    public FallMeasure() {
    }

    public long getId_fall() {
        return id_fall;
    }

    public void setId_fall(long id_fall) {
        this.id_fall = id_fall;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
