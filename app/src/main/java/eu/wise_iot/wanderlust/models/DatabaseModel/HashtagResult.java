package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * HashtagResult with Tag and Hash ID
 *
 * @author TODO ??
 * @license GPL-3.0
 */
public class HashtagResult {

    /**
     * Create a new Hashtag Result
     */
    public HashtagResult(int hash_id, String tag) {

        this.hash_id = hash_id;
        this.tag = tag;
    }

    private int hash_id;
    private String tag;


    /**
     * Gets tag ID
     *
     * @return tag id
     */
    public int getHashId() {
        return hash_id;
    }


    /**
     * Sets the tag name of the hash ID
     *
     * @param hash_id The hash ID of the tag
     */
    public void setHashId(int hash_id) {
        this.hash_id = hash_id;
    }


    /**
     * Gets tag name
     *
     * @return tag
     */
    public String getTag() {
        return tag;
    }


    /**
     * Sets the tag name of the hashtag
     *
     * @param tag The tag name of the HashtagResult
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}
