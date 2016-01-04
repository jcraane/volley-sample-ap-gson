package nl.capaxit.volleytest.gson;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * todo hoe error handling generiek maken -> custom errorlistener maken om VolleyError te parsen.
 * todo custom caching, hoe kunnen we dit integreren -> hier kunnen we een custom Entry voor maken met een eigen time to live.
 * todo support voor custom type adapters toevoegen -> Request extendable maken.
 * <p/>
 * Created by jamiecraane on 24/06/15.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private Map<String, String> headers = new HashMap<>();

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(
            final int method,
            final String url,
            final Class<T> clazz,
            final Response.Listener<T> listener,
            final Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    /**
     * @param headers Map of request headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
