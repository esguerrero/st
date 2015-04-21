package com.example.android.effectivenavigation.db;

/**
 * Created by Esteban on 2015-04-21.
 */
public class ActivityEntitiy {
     private int _id, _iduser;
    private String activity_name;


    public ActivityEntitiy(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }

    public int get_iduser() {
        return _iduser;
    }

    public void set_iduser(int _iduser) {
        this._iduser = _iduser;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }
}
