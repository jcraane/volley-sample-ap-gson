package nl.capaxit.volleytest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jamiecraane on 24/06/15.
 */
public class StationResponse {
    @SerializedName("payload")
    private List<Station> items;

    public List<Station> getItems() {
        return items;
    }
}
