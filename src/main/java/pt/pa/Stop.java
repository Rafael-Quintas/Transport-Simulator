package pt.pa;

public class Stop {
    private String stopCode;
    private String stopName;
    private double latitude;
    private double longitude;


    public Stop(String stopCode, String stopName, double latitude, double longitude) {
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStopCode() {
        return this.stopCode;
    }
}
