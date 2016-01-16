package org.rmiralles.app.base;

import java.sql.Timestamp;

public class Text {

    private int id;
    private String text;
    private String business_title;
    private Timestamp datetime;
    private String latitude;
    private String longitude;
    private User id_user;

    public Text() {
    }

    public User getId_user() {
        return id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBusiness_title() {
        return business_title;
    }

    public void setBusiness_title(String business_title) {
        this.business_title = business_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
