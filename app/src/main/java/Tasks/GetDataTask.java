package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.net.MalformedURLException;
import java.net.URL;

import Model.Event;
import Model.Person;
import Request.EventsRequest;
import Request.PersonsRequest;
import Result.EventsResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class GetDataTask implements  Runnable {
    private static final String SUCCESS_KEY = "success key";

    private Handler messageHandler;
    private String authToken;
    private String serverHost;
    private String serverPort;


    public GetDataTask(Handler message, String token, String host, String port) {
        this.messageHandler = message;
        this.authToken = token;
        this.serverHost = host;
        this.serverPort = port;
    }

    @Override
    public void run() {
        String urlStringEvents = "http://" + serverHost + ":" + serverPort + "/event";
        URL url = null;
        try {
            url = new URL(urlStringEvents);
        } catch (MalformedURLException e) {
            EventsResult result = new EventsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
            sendMessage(result.getSuccess());
        }
        //Call serverProxy for events with URL and populate dataCache with array of events
        ServerProxy proxy = new ServerProxy();

        EventsResult eventsResult = proxy.events(url, authToken);
        Event[] eventsList = eventsResult.getData();


        String urlStringPersons = "http://" + serverHost + ":" + serverPort + "/person";
        URL url1 = null;
        try {
            url1 = new URL(urlStringPersons);
        } catch (MalformedURLException e) {
            PersonsResult result = new PersonsResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
            sendMessage(result.getSuccess());
        }
        //Call serverProxy for persons with URL and populate dataCache with array of persons
        PersonsResult personsResult = proxy.persons(url1, authToken);
        Person[] personsList = personsResult.getData();


        DataCache dataCache = DataCache.getInstance();

        dataCache.setEvents(eventsList);
        dataCache.setPersons(personsList);


        sendMessage(eventsResult.getSuccess() && personsResult.getSuccess());
    }

    private void sendMessage(boolean success) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(SUCCESS_KEY, success);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
