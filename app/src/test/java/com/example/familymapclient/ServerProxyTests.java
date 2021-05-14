package com.example.familymapclient;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;
import Result.RegisterResult;

import static org.junit.Assert.*;

public class ServerProxyTests {

    public String RegisterRandUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("rand");
        request.setPassword("rand");
        request.setFirstName("fname");
        request.setLastName("lname");
        request.setEmail("fname@lname.com");
        request.setGender("m");

        String urlString = "http://localhost:8080/user/register";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            RegisterResult result = new RegisterResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        RegisterResult result = proxy.register(url, request);

        return result.getAuthToken();
    }

    public String RegisterRandUser2() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("rand2");
        request.setPassword("rand2");
        request.setFirstName("fname2");
        request.setLastName("lname2");
        request.setEmail("fname2@lname2.com");
        request.setGender("f");

        String urlString = "http://localhost:8080/user/register";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            RegisterResult result = new RegisterResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        RegisterResult result = proxy.register(url, request);

        return result.getAuthToken();
    }

    @Test
    public void RegisterPositive() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("connerw");
        request.setPassword("byu123");
        request.setFirstName("Conner");
        request.setLastName("Wattles");
        request.setEmail("conner@wattles.com");
        request.setGender("m");

        String urlString = "http://localhost:8080/user/register";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            RegisterResult result = new RegisterResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        RegisterResult result = proxy.register(url, request);

        assertEquals(true, result.getSuccess());
        assertEquals("connerw", result.getUsername());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void RegisterNegative() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("connerw");
        request.setPassword("byu123");
        request.setFirstName("Conner");
        request.setLastName("Wattles");
        request.setEmail("conner@wattles.com");
        request.setGender("m");

        String urlString = "http://localhost:8080/user/register";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            RegisterResult result = new RegisterResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        RegisterResult result = proxy.register(url, request);

        assertEquals(false, result.getSuccess());
    }

    @Test
    public void LoginPositive() {
        LoginRequest request = new LoginRequest();
        request.setUsername("connerw");
        request.setPassword("byu123");

        String urlString = "http://localhost:8080/user/login";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LoginResult result = new LoginResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        LoginResult result = proxy.login(url, request);

        assertEquals(true, result.getSuccess());
        assertEquals("connerw", result.getUsername());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void LoginNegative() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistant");
        request.setPassword("byu123");

        String urlString = "http://localhost:8080/user/login";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LoginResult result = new LoginResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        LoginResult result = proxy.login(url, request);

        assertEquals(false, result.getSuccess());
    }

    @Test
    public void EventPositive() {
        String authToken = RegisterRandUser();

        String urlStringEvents = "http://localhost:8080/event";
        URL url = null;
        try {
            url = new URL(urlStringEvents);
        } catch (MalformedURLException e) {
            EventsResult result = new EventsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        EventsResult result = proxy.events(url, authToken);

        assertEquals(true, result.getSuccess());
    }

    @Test
    public void EventNegative() {
        String authToken = "nonexistant";

        String urlStringEvents = "http://localhost:8080/event";
        URL url = null;
        try {
            url = new URL(urlStringEvents);
        } catch (MalformedURLException e) {
            EventsResult result = new EventsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        EventsResult result = proxy.events(url, authToken);

        assertEquals(false, result.getSuccess());
    }

    @Test
    public void PersonPositive() {
        String authToken = RegisterRandUser2();

        String urlStringPersons = "http://localhost:8080/person";
        URL url1 = null;
        try {
            url1 = new URL(urlStringPersons);
        } catch (MalformedURLException e) {
            PersonsResult result = new PersonsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        PersonsResult result = proxy.persons(url1, authToken);

        assertEquals(true, result.getSuccess());
    }

    @Test
    public void PersonNegative() {
        String authToken = "nonexistant";

        String urlStringPersons = "http://localhost:8080/person";
        URL url1 = null;
        try {
            url1 = new URL(urlStringPersons);
        } catch (MalformedURLException e) {
            PersonsResult result = new PersonsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
        }

        ServerProxy proxy = new ServerProxy();

        PersonsResult result = proxy.persons(url1, authToken);

        assertEquals(false, result.getSuccess());
    }
}
