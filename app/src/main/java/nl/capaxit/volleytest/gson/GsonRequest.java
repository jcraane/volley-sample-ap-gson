package nl.capaxit.volleytest.gson;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final Gson GSON = new Gson();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private Map<String, String> headers = new HashMap<>();

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    private GsonRequest(
            final int method,
            final String url,
            final Class<T> clazz,
            final Response.Listener<T> successListener,
            final Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = successListener;
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
                    GSON.fromJson(json, clazz),
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

    public interface Builder<T> {
        GsonRequest<T> create(Class<T> clazz);
    }

    public static abstract class RequestBuilder<T> implements Builder<T> {
        protected final String url;
        protected Response.Listener<T> succesListener;
        protected Response.ErrorListener errorListener;

        public RequestBuilder(final String url) {
            this.url = url;
        }

        public RequestBuilder<T> successListener(final Response.Listener<T> successListener) {
            this.succesListener = successListener;
            return this;
        }

        public RequestBuilder<T> errorListener(final Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }
    }

    public static class Get<T> extends RequestBuilder<T> {
        public Get(final String url) {
            super(url);
        }

        @Override
        public GsonRequest<T> create(final Class<T> clazz) {
            return new GsonRequest<>(Method.GET, url, clazz, succesListener, errorListener);
        }
    }

    public static class Post<T, V> extends RequestBuilder<T> {
        private V body;
        final Class<V> requestClass;
        private GsonBuilder gsonBuilder;

        public Post(final String url, final Class<V> requestClass, final V body) {
            super(url);
            this.requestClass = requestClass;
            this.body = body;
            this.gsonBuilder = null;
        }

        public RequestBuilder<T> setGsonBuilder(final GsonBuilder gsonBuilder) {
            this.gsonBuilder = gsonBuilder;
            return this;
        }

        @Override
        public GsonRequest<T> create(final Class<T> responseClass) {
            return new GsonRequest<T>(Method.POST, url, responseClass, succesListener, errorListener) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Gson gson = gsonBuilder != null ?  gsonBuilder.create() : GSON;
                    return gson.toJson(body, requestClass).getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        }
    }
}
