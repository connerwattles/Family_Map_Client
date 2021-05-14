package com.example.familymapclient;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.EventsRequest;
import Request.LoginRequest;
import Request.PersonsRequest;
import Request.RegisterRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class ServerProxy {

    public LoginResult login(URL url, LoginRequest request) {
        Gson gson = new Gson();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String json = gson.toJson(request);

            connection.connect();

            try(OutputStream requestBody = connection.getOutputStream();) {
                writeString(json, requestBody);
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String respData = readString(responseBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);
                result.setSuccess(true);
                responseBody.close();
                return result;
            }
            else {
                LoginResult result = new LoginResult();
                result.setSuccess(false);
                result.setMessage(connection.getResponseMessage());
                return result;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }
        return null;
    }

    public RegisterResult register(URL url, RegisterRequest request) {
        Gson gson = new Gson();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String json = gson.toJson(request);

            connection.connect();

            try(OutputStream requestBody = connection.getOutputStream();) {
                writeString(json, requestBody);
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String respData = readString(responseBody);
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                result.setSuccess(true);
                responseBody.close();
                return result;
            }
            else {
                RegisterResult result = new RegisterResult();
                result.setSuccess(false);
                result.setMessage(connection.getResponseMessage());
                return result;
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }
        return null;
    }

    public EventsResult events(URL url, String authToken) {
        Gson gson = new Gson();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");

            //Add authToken as request header
            connection.addRequestProperty("Authorization", authToken);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String respData = readString(responseBody);
                EventsResult result = gson.fromJson(respData, EventsResult.class);
                result.setSuccess(true);
                responseBody.close();
                return result;
            }
            else {
                EventsResult result = new EventsResult();
                result.setSuccess(false);
                result.setMessage(connection.getResponseMessage());
                return result;
            }
        } catch(Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }
        return null;
    }

    public PersonsResult persons(URL url, String authToken) {
        Gson gson = new Gson();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");

            //Add authToken as request header
            connection.addRequestProperty("Authorization", authToken);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String respData = readString(responseBody);
                PersonsResult result = gson.fromJson(respData, PersonsResult.class);
                result.setSuccess(true);
                responseBody.close();
                return result;
            }
            else {
                PersonsResult result = new PersonsResult();
                result.setSuccess(false);
                result.setMessage(connection.getResponseMessage());
                return result;
            }
        } catch(Exception e) {
            Log.e("HttpClient", e.getMessage(), e);
        }
        return null;
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
