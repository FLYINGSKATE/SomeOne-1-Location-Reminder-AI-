package com.someone.someone_v2;

public class UserDetails {
    String emailid;
    String user_name;
    boolean gym;
    String profession;

    public UserDetails(String emailid, String user_name, boolean gym, String profession) {
        this.emailid = emailid;
        this.user_name = user_name;
        this.gym = gym;
        this.profession = profession;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isGym() {
        return gym;
    }

    public void setGym(boolean gym) {
        this.gym = gym;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
