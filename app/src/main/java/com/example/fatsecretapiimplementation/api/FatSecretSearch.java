package com.example.fatsecretapiimplementation.api;

import android.net.Uri;
import android.util.Log;

import com.fatsecret.platform.services.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FatSecretSearch {

    final static private String APP_METHOD = "GET";
    final static private String APP_KEY = "000"; //from FATSECRET API key when you registered as a developer
    final static private String APP_URL = "http://platform.fatsecret.com/rest/server.api";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1"; //The method used to generate the signature (only HMAC-SHA1 is supported)
    static private String APP_SECRET = "000"; //from FATSECRET

    private static String[] generateOauthParams(int i) {
        return new String[]{
                "oauth_consumer_key=" + APP_KEY,
                "oauth_signature_method=HMAC-SHA1",
                "oauth_timestamp=" +                    //The date and time, expressed in the number of seconds since January 1, 1970 00:00:00 GMT.
                        Long.valueOf(System.currentTimeMillis() * 2).toString(), // Should be  Long.valueOf(System.currentTimeMillis() / 1000).toString()
                "oauth_nonce=" + nonce(),               // A randomly generated string for a request that can be combined with the timestamp to produce a unique value
                "oauth_version=1.0",                    // MUST be "1.0"
                "format=json",                          // The desired response format. Valid response formats are "xml" or "json" (default value is "xml").
                "page_number=" + i,                     // The zero-based offset into the results for the query. Use this parameter with max_results to request successive pages of search results (default value is 0).
                "max_results=" + 50};                   // The maximum number of results to return (default value is 20). This number cannot be greater than 50.
    }

    private static String sign(String[] params) {
        String[] p = {com.example.fatsecretapiimplementation.api.FatSecretSearch.APP_METHOD, Uri.encode(FatSecretSearch.APP_URL), Uri.encode(paramify(params))};
        String s = join(p);
        APP_SECRET += "&";
        SecretKey sk = new SecretKeySpec(APP_SECRET.getBytes(), HMAC_SHA1_ALGORITHM);
        APP_SECRET = APP_SECRET.substring(0, APP_SECRET.length() - 1);
        try {
            Mac m = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            m.init(sk);
            return Uri.encode(new String(Base64.encode(m.doFinal(s.getBytes()), Base64.DEFAULT)).trim());

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Log.w("FatSecret_TEST FAIL", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    private static String paramify(String[] params) {
        String[] p = Arrays.copyOf(params, params.length);
        Arrays.sort(p);
        return join(p);
    }

    private static String join(String[] array) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                b.append("&");
            b.append(array[i]);
        }
        return b.toString();
    }

    private static String nonce() {
        Random r = new Random();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < r.nextInt(8) + 2; i++)
            n.append(r.nextInt(26) + 'a');
        return n.toString();
    }

    public JSONObject searchFood(String searchFood, int page) {
        List<String> params = new ArrayList<>(Arrays.asList(generateOauthParams(page)));
        String[] template = new String[1];
        params.add("method=foods.search");
        params.add("search_expression=" + Uri.encode(searchFood));
        params.add("oauth_signature=" + sign(params.toArray(template)));

        JSONObject foods = null;
        try {
            URL url = new URL(APP_URL + "?" + paramify(params.toArray(template)));
            URLConnection api = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));
            while ((line = reader.readLine()) != null) builder.append(line);
            JSONObject food = new JSONObject(builder.toString());   // { first
            foods = food.getJSONObject("foods");                    // { second
        } catch (Exception exception) {
            Log.println(Log.INFO, "SEARCHERROR", exception.toString());
            Log.e("FatSecret Error", exception.toString());
            exception.printStackTrace();
        }
        return foods;
    }

}
