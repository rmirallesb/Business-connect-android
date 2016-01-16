package org.rmiralles.app.base;

public class User {

    private int id;
    private String username;
    private String password;
    private String business_title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusiness_title() {
        return business_title;
    }

    public void setBusiness_title(String business_title) {
        this.business_title = business_title;
    }
}
