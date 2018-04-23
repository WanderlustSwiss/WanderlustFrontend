package com.squareup.picasso;

public class PicassoCache {

    public static void clearCache (Picasso p) {
        p.cache.clear();
    }
}