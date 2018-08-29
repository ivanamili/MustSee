package mosis.ivana.mustsee.DataModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FriendRequestMessage {
    private String userName;
    private String id;
    private String pictureURI;
    private MessageTag TAG;

    public FriendRequestMessage(String userName, String id, String pictureURI){
        this.userName=userName;
        this.id=id;
        this.pictureURI=pictureURI;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public MessageTag getTAG() {
        return TAG;
    }

    public void setTAG(MessageTag TAG) {
        this.TAG = TAG;
    }

    //used to send message over bluetooth
    public byte[] serialize()throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();
    }

    //used to retrieve message received through bluetooth
    public static FriendRequestMessage deserialize(byte[] bytes)throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return (FriendRequestMessage) o.readObject();
    }

}

