package com.netoneze.ambientesreserva.modelo;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Room implements Parcelable {
    private Boolean automaticApproval;
    private Map<String, Boolean> specifications;
    private String name;
    private String responsibleUid;
    private String type;

    public Room(Boolean automaticApproval, Map<String, Boolean> specifications, String name, String responsibleUid, String type) {
        this.automaticApproval = automaticApproval;
        this.specifications = specifications;
        this.name = name;
        this.responsibleUid = responsibleUid;
        this.type = type;
    }

    public Room() {

    }

    Room(Parcel in){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.automaticApproval = in.readBoolean();
        }
        this.specifications = new HashMap<>();
        in.readMap(specifications, Map.class.getClassLoader());
        this.name = in.readString();
        this.responsibleUid = in.readString();
        this.type = in.readString();
    }

    public Boolean getAutomaticApproval() {
        return automaticApproval;
    }

    public void setAutomaticApproval(Boolean automaticApproval) {
        this.automaticApproval = automaticApproval;
    }

    public Map<String, Boolean> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, Boolean> specifications) {
        this.specifications = specifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponsibleUid() {
        return responsibleUid;
    }

    public void setResponsibleUid(String responsibleUid) {
        this.responsibleUid = responsibleUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(automaticApproval);
        }
        parcel.writeMap(specifications);
        parcel.writeString(name);
        parcel.writeString(responsibleUid);
        parcel.writeString(type);
    }
    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>(){

        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
