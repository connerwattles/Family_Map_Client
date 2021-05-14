package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.net.MalformedURLException;
import java.net.URL;

import Request.LoginRequest;
import Result.LoginResult;

public class LoginTask implements Runnable {
    private static final String SUCCESS_KEY = "success key";
    private static final String TOKEN_KEY = "token key";

    private LoginRequest loginRequest;
    private Handler messageHandler;
    private String serverHost;
    private String serverPort;

    public LoginTask(Handler message, LoginRequest request, String host, String port) {
        this.messageHandler = message;
        this.loginRequest = request;
        this.serverHost = host;
        this.serverPort = port;
    }

    @Override
    public void run() {
        //URL example http://host:port/user/login
        //URL example http://10.0.2.2:8080/user/login
        String urlString = "http://" + serverHost + ":" + serverPort + "/user/login";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LoginResult result = new LoginResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
            sendMessage(result.getSuccess(), null);
        }

        ServerProxy proxy = new ServerProxy();

        LoginResult result = proxy.login(url, this.loginRequest);

        DataCache dataCache = DataCache.getInstance();

        dataCache.setCurrUsername(result.getUsername());
        dataCache.setCurrPersonID(result.getPersonID());
        dataCache.setCurrAuthToken(result.getAuthToken());

        sendMessage(result.getSuccess(), result.getAuthToken());
    }

    private void sendMessage(boolean success, String token) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(SUCCESS_KEY, success);
        messageBundle.putString(TOKEN_KEY, token);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
