package com.life.equipmentlife.model.pojo;

import java.io.Serializable;


public class Equipment implements Serializable {

    private String id;

    private String brand;

    private String model;

    private String serialNumber;

    private String currentOwner;

    private String previousOwner;

    private String statusCurrentOwner;

    private String statusPreviousOwner;

    private String profileCurrentOwnerId;

    private String profilePreviousOwnerId;

    private String registrationDate;

    private String shortDescription;

    private String picture;

    public Equipment() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public String getPreviousOwner() {
        return previousOwner;
    }

    public void setPreviousOwner(String previousOwner) {
        this.previousOwner = previousOwner;
    }

    public String getStatusCurrentOwner() {
        return statusCurrentOwner;
    }

    public void setStatusCurrentOwner(String statusCurrentOwner) {
        this.statusCurrentOwner = statusCurrentOwner;
    }

    public String getStatusPreviousOwner() {
        return statusPreviousOwner;
    }

    public void setStatusPreviousOwner(String statusPreviousOwner) {
        this.statusPreviousOwner = statusPreviousOwner;
    }

    public String getProfileCurrentOwnerId() {
        return profileCurrentOwnerId;
    }

    public void setProfileCurrentOwnerId(String profileCurrentOwnerId) {
        this.profileCurrentOwnerId = profileCurrentOwnerId;
    }

    public String getProfilePreviousOwnerId() {
        return profilePreviousOwnerId;
    }

    public void setProfilePreviousOwnerId(String profilePreviousOwnerId) {
        this.profilePreviousOwnerId = profilePreviousOwnerId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
