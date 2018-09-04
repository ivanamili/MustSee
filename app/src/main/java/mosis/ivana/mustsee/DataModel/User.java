package mosis.ivana.mustsee.DataModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class User {

    //region Fields
    private String userId;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String profilePhotoUrl;
    private int xpPoints;
    private String joined;
    private int friendsCount;
    private int placesAddedCount;
    private int placesVisitedCount;
    private int mustSeeCount;

    //endregion

    //region Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public int getXpPoints() {
        return xpPoints;
    }

    public void setXpPoints(int xpPoints) {
        this.xpPoints = xpPoints;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public int getPlacesAddedCount() {
        return placesAddedCount;
    }

    public void setPlacesAddedCount(int placesAddedCount) {
        this.placesAddedCount = placesAddedCount;
    }

    public int getPlacesVisitedCount() {
        return placesVisitedCount;
    }

    public void setPlacesVisitedCount(int placesVisitedCount) {
        this.placesVisitedCount = placesVisitedCount;
    }

    public int getMustSeeCount() {
        return mustSeeCount;
    }

    public void setMustSeeCount(int mustSeeCount) {
        this.mustSeeCount = mustSeeCount;
    }


    //endregion

    public User(String id,String fullName, String username, String password, String email){
        this.userId=id;
        this.fullName=fullName;
        this.username=username;
        this.password=password;
        this.email=email;
        this.profilePhotoUrl="";
        this.xpPoints=0;

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        this.joined=df.format(c);

        this.friendsCount=0;
        this.placesAddedCount=0;
        this.placesVisitedCount=0;
        this.mustSeeCount=0;
    }

    public User(){

    }


    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }
}
