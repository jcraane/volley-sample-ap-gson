package nl.capaxit.volleytest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import nl.capaxit.volleytest.gson.GsonRequest;

/**
 * Created by jamiecraane on 23/06/15.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATIONS_URL = "http://ews-rpx.ns.nl/private-ns-api/json/v1/stations";

    private TextView resultView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.stations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                retrieveStations();
            }
        });
        resultView = (TextView) findViewById(R.id.result);
        findViewById(R.id.postSomething).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                postSomething();
            }
        });
    }

    private void postSomething() {
        final GsonRequest<Void> postRequest = new GsonRequest<Void>(Request.Method.POST, "http://192.168.1.173:8085/api/v1/static", Void.class, new Response.Listener<Void>() {
            @Override
            public void onResponse(final Void aVoid) {
                Log.i(TAG, "success posting message");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                Log.e(TAG, "error posting message", volleyError);
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return "{\"name\":\"Jamie\"}".getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleyTestApplication.getInstance().addToRequestQueue(postRequest);
    }

    private void retrieveStations() {
        final GsonRequest<StationResponse> request = new GsonRequest<>(Request.Method.GET, STATIONS_URL, StationResponse.class, new Response.Listener<StationResponse>() {
            @Override
            public void onResponse(final StationResponse stationResponse) {
                resultView.setText("" + stationResponse.getItems().size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        VolleyTestApplication.getInstance().addToRequestQueue(request);
    }
}

