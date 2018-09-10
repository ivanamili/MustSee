package mosis.ivana.mustsee.DataModel;

public class SimpleLocation {

    private double latitude;
    private double lognitude;

    public SimpleLocation(double latitude, double lognitude){
        this.latitude=latitude;
        this.lognitude=lognitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLognitude() {
        return lognitude;
    }

    public void setLognitude(double lognitude) {
        this.lognitude = lognitude;
    }
}
