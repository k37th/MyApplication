package com.example.keith.myapplication;

import org.mapsforge.core.model.LatLong;

import java.util.UUID;

public class POI {
//    private UUID mId;
    private String mId;
    private String mName;
    private LatLong mCoordinates;

//    public POI(String name){
    public POI(String id, String name, LatLong coordinates){
//        mId = UUID.randomUUID();
        mId = id;
        mName = name;
        mCoordinates = coordinates;
    }

//    public UUID getId(){
    public String getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    @Override
    public String toString(){
        return mName;
    }

    public LatLong getCoordinates(){
        return mCoordinates;
    }
}
