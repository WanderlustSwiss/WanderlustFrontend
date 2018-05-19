package com.squareup.picasso;

/**
 * clears the cache of the given context
 *
 *
 */
public class PicassoCache {
    //https://stackoverflow.com/questions/22016382/invalidate-cache-in-picasso
    public static void clearCache (Picasso p) {
        p.cache.clear();
    }
}