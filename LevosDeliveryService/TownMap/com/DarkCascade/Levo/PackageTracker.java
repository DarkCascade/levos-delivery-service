package com.DarkCascade.Levo;

import com.badlogic.gdx.scenes.scene2d.ui.List;

import com.badlogic.gdx.utils.Array;

/**
 * Created by JD on 5/20/13.
 */

// maintain active list of packages by ID, source, and destination
public class PackageTracker {
    private Array<GridLocation> _sources;
    private Array<GridLocation> _destinations;
    private Array<Package> _activePackages;

    private int _successfulDeliveryCount;

    public void Initialize(int maxNumPackages, Array<GridLocation> sources, Array<GridLocation> destinations) {
        _sources = sources;
        _destinations = destinations;

        _activePackages = new Array<Package>();

        _successfulDeliveryCount = 0;

        // generate maxNumPackages packages to start
    }

    public void Update() {
        // check package list for inactive packages and remove them
        Array<Package> updatedPkgs =  new Array<Package>();
        for (Package curPkg : _activePackages) {
            if (curPkg.is_active() == true)
                updatedPkgs.add(curPkg);
        }

        _activePackages = updatedPkgs;
    }

    public boolean DeliverPackage(GridLocation currentLoc) {
        // check if currentLoc is a destination for an active package
        // true:
        //  increment _successfulDeliveryCount
        //  set package to inactive
        //  return true
        // false:
        //  return false

        for (Package curPkg : _activePackages) {
            if (curPkg.get_destination() == currentLoc) {
                _successfulDeliveryCount++;
                curPkg.set_active(false);
                return true;
            }
        }

        return false;
    }
}
