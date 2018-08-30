package mosis.ivana.mustsee.DataModel;

public class FriendsData {

    private String friendUserName;
    private String firendPhotoURI;

    public FriendsData(String friendUserName, String friendPhotoURI){
        this.friendUserName=friendUserName;
        this.firendPhotoURI=friendPhotoURI;
    }


    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public String getFirendPhotoURI() {
        return firendPhotoURI;
    }

    public void setFirendPhotoURI(String firendPhotoURI) {
        this.firendPhotoURI = firendPhotoURI;
    }
}
