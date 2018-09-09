package mosis.ivana.mustsee.DataModel;

import android.location.Location;

public class Place {
    private String placeID;
    private String name;
    private String photoURL;
    private int commentCount;
    private int likeCount;
    private String country;
    private String city;
    private String description;

    private Location coordinates;

    public Place()
    {

    }
    public Place(String name, String photoURL, int commentCount, int likeCount,
                 String country, String city, String description,
                 Location coordinates)
    {
        this.name=name;
        this.photoURL=photoURL;
        this.commentCount=commentCount;
        this.likeCount=likeCount;
        this.country=country;
        this.city=city;
        this.description=description;
        this.coordinates=coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Location coordinates) {
        this.coordinates = coordinates;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }
}
