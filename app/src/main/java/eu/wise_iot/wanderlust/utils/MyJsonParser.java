package eu.wise_iot.wanderlust.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabianschwander on 16.08.17.
 */

public class MyJsonParser<T> {
    private static final String TAG = "MyJsonParser";
    private Context context;
    private Class<T> clazz; // is not pretty, but so far unavoidable in order to pass the class generically

    public MyJsonParser(Class<T> clazz, Context context, int resourceId) {
        this.clazz = clazz;
        this.context = context;
        getTempListFromParsedJsonFile(resourceId);
    }

    public MyJsonParser(Class<T> clazz, Context context) {
        this.clazz = clazz;
        this.context = context;
    }

    public List<T> getListFromResourceFile(int resourceId) {
        List<T> tempList = getTempListFromParsedJsonFile(resourceId);
        return getPopulatedObjectList(tempList);
    }

    public String getJsonStringFromObject(T model) {
        Gson gson = new Gson();
        return gson.toJson(model);
    }

    private List<T> getTempListFromParsedJsonFile(int resourceId) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Gson gson = new Gson();
        Type listType = new TypeToken<List<T>>(){}.getType();
        return gson.fromJson(bufferedReader, listType);
    }

    private List<T> getPopulatedObjectList(List<T> tempList) {
        List<T> objectList = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < tempList.size(); i++) {
            String jsonAsString = gson.toJson(tempList.get(i));
            T model = gson.fromJson(jsonAsString, clazz);
            objectList.add(model);
        }
        return objectList;
    }

    public T getObjectFromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, clazz);
    }
}
