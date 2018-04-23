package com.squareup.picasso;

public class PicassoCache {
    //https://stackoverflow.com/questions/22016382/invalidate-cache-in-picasso
    public static void clearCache (Picasso p) {
        p.cache.clear();
    }
}