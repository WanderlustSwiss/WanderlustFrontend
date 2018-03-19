package eu.wise_iot.wanderlust.models.DatabaseModel;

public class Weather {

    private float temp;
    private float maxTemp;
    private float minTemp;
    private float humidity;
    private int category;
    private String icon;
    private float windSpeed;
    private long dt;



    public float getTemp() {
        return Math.abs(temp - Float.valueOf("273.15"));
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public float getHumidity() {
        return humidity;
    }

    public int getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public long getDt() {return dt; }
}
