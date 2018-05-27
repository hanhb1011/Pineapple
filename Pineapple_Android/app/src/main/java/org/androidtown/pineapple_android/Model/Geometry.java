package org.androidtown.pineapple_android.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MSI on 2018-04-24.
 */

public class Geometry {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<Object> coordinates = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Object> coordinates) {
        this.coordinates = coordinates;
    }
}
