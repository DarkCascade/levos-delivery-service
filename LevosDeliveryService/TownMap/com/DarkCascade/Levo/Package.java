package com.DarkCascade.Levo;

/**
 * Created by JD on 5/20/13.
 */
public class Package {
    private boolean _active;
    private int _id;
    private GridLocation _source;
    private GridLocation _destination;

    public Package(int id, GridLocation source, GridLocation destination) {
        _active = true;

        _id = id;
        _source = source;
        _destination = destination;
    }

    public boolean is_active() {
        return _active;
    }

    public void set_active(boolean _active) {
        this._active = _active;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public GridLocation get_source() {
        return _source;
    }

    public void set_source(GridLocation _source) {
        this._source = _source;
    }

    public GridLocation get_destination() {
        return _destination;
    }

    public void set_destination(GridLocation _destination) {
        this._destination = _destination;
    }
}
