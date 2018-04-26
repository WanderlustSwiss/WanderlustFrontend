package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * Helper object for backend communication
 *
 * @author Gashi Rilind, Simon Kaspar
 * @license MIT
 */
public class RatingStatistic {

    private final float rateAvg;
    private final int[] rateByValue;
    private final int rateTotal;

    public RatingStatistic(float rateAvg, int[] rateByValue, int rateTotal) {
        this.rateAvg = rateAvg;
        this.rateByValue = rateByValue;
        this.rateTotal = rateTotal;
    }

    public float getRateAvg() {
        return rateAvg;
    }

    public int[] getRateByValue() {
        return rateByValue;
    }

    public int getRateTotal() {
        return rateTotal;
    }

}
