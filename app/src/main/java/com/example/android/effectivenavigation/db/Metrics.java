package com.example.android.effectivenavigation.db;

/**
 * Created by Esteban on 2015-04-21.
 */
public class Metrics {

    private double weight;
    private double height;
    private double maxPower;
    private double avgPower;
    private double avgTime; //average time of the performance
    private double maxForce;
    private double avgForce;
    private double maxDistance; //accumulated
    private double avgDistance;
    private int _id, _id_subactivity;


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    public double getAvgPower() {
        return avgPower;
    }

    public void setAvgPower(double avgPower) {
        this.avgPower = avgPower;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    public double getAvgForce() {
        return avgForce;
    }

    public void setAvgForce(double avgForce) {
        this.avgForce = avgForce;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getAvgDistance() {
        return avgDistance;
    }

    public void setAvgDistance(double avgDistance) {
        this.avgDistance = avgDistance;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long get_id_activity() {
        return _id_subactivity;
    }

    public void set_id_activity(int _id_activity) {
        this._id_subactivity = _id_activity;
    }

    public Metrics(int _id) {
        this._id = _id;
    }
}
