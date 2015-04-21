package com.example.android.effectivenavigation.db;

/**
 * Created by Esteban on 2015-04-21.
 */
public class SubactivityEntity {

    private int _id, _idactivity;
    private String subactivity_name;

    public SubactivityEntity(int _id) {
        this._id = _id;
    }


    public int get_idactivity() {
        return _idactivity;
    }

    public void set_idactivity(int _idactivity) {
        this._idactivity = _idactivity;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSubactivity_name() {
        return subactivity_name;
    }

    public void setSubactivity_name(String subactivity_name) {
        this.subactivity_name = subactivity_name;
    }
}
