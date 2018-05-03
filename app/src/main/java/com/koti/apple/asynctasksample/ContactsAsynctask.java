package com.koti.apple.asynctasksample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// chrome-extension://lhkmoheomjbkfloacpgllgjcamhihfaj/index.html
// https://jsoneditoronline.org/
public class ContactsAsynctask extends AsyncTask<String, Void, String> {

    private static final String TAG = "BlockAniAsync";
    private final Context mContext;
    private int responseCode;
    String displayMessage = null;

    public ContactsAsynctask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "onPreExecute()");
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = useHttpurl(strings);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.e(TAG, "response ======== " + response);

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            try {
                JSONObject object = new JSONObject(response);
                // new JSONObject(response).getJSONArray("contacts").get(0)
                JSONArray jsonArray=object.getJSONArray("contacts");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String address = jsonObject.getString("address");
                    String gender = jsonObject.getString("gender");
                    JSONObject phone=jsonObject.getJSONObject("phone");
                    String mobile = jsonObject.getString("mobile");
                    String home = jsonObject.getString("home");
                    String office = jsonObject.getString("office");
                    Log.i(TAG, " id   >>> "+id);
                    Log.i(TAG, " name   >>> "+name);
                    Log.i(TAG, " email   >>> "+email);
                    Log.i(TAG, " address   >>> "+address);
                    Log.i(TAG, " gender   >>> "+gender);
                    Log.i(TAG, " mobile   >>> "+mobile);
                    Log.i(TAG, " home   >>> "+home);
                    Log.i(TAG, " office   >>> "+office);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String useHttpurl(String[] inputs) {
        String responseString = "";
        try {
            // Get the contacts information
            URL url = new URL("https://api.androidhive.info/contacts");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write("");
            // Send require parameters to the Service call .
            // writer.write(getPostDataString(getParamsInHashMap(emailBody, fromEmail)));
            writer.flush();
            writer.close();
            outputStream.close();
            responseCode = connection.getResponseCode();
            Log.e(TAG, "responseCode ======= " + responseCode);
            InputStream inputStream = connection.getInputStream();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null) {
                    responseString += line;
                }
            } else {
                responseString = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    private HashMap<String, String> getParamsInHashMap(String emailBody, String fromEmail) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EMAILBODY", emailBody);
        params.put("FROM", fromEmail);
        Log.e(TAG, "fromEmail ===== "+fromEmail);

        return params;
    }
}

