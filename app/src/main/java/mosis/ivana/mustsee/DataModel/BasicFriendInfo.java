package mosis.ivana.mustsee.DataModel;

public class BasicFriendInfo {
    private String id;
    private FriendsData basicInfo;

    public BasicFriendInfo(String id, FriendsData basicInfo)
    {
        this.id=id;
        this.basicInfo=basicInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FriendsData getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(FriendsData basicInfo) {
        this.basicInfo = basicInfo;
    }
}
