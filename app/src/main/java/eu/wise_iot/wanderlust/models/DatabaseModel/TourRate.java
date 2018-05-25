package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.util.List;

/**
 * Helper object for backend communication
 *
 * @author Gashi Rilind, Simon Kaspar
 * @license MIT
 */
public class TourRate {

    private final float rateAvg;
    private final List<Integer> rateByValue;
    private final int rateTotal;
    private final int userRate;

    public TourRate(float rateAvg, List<Integer> rateByValue, int rateTotal, int userRate) {
        this.rateAvg = rateAvg;
        this.rateByValue = rateByValue;
        this.rateTotal = rateTotal;
        this.userRate = userRate;
    }

    public float getRateAvg() {
        return rateAvg;
    }

    public List<Integer> getRateByValue() {
        return rateByValue;
    }

    public int getRateTotal() {
        return rateTotal;
    }

    public int getUserRate() {
        return userRate;
    }

}
