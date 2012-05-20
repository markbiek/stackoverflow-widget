package com.janustech.stackoverflowwidget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


public class RestJsonClient {

    public static JSONObject connect(String url)
    {

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url); 
        httpget.addHeader("Accept-Encoding", "gzip");

        // Execute the request
        HttpResponse response;

        JSONObject json = new JSONObject();

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
            	InputStream instream = response.getEntity().getContent();
            	Header contentEncoding = response.getFirstHeader("Content-Encoding");
            	if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            	    instream = new GZIPInputStream(instream);
            	}
            	
                String result= convertStreamToString(instream);

                Log.d("RestJsonClient", result);
                json=new JSONObject(result);            

                instream.close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
    /**
     *
     * @param is
     * @return String
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}

