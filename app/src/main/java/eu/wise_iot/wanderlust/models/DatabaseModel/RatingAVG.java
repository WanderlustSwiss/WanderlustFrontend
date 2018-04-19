package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * Helper object for backend communication
 *
 * @author Gashi Rilind
 * @license MIT
 */
public class RatingAVG {

    private final float rateAvg;

    public RatingAVG(float ratingAVG) {
        this.rateAvg = ratingAVG;
    }

    public float getRateAvg() {
        return rateAvg;
    }

}
