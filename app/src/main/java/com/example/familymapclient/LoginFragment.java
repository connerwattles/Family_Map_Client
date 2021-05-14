package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Tasks.GetDataTask;
import Tasks.LoginTask;
import Tasks.RegisterTask;

public class LoginFragment extends Fragment {
    private static final String SUCCESS_KEY = "success key";
    private static final String TOKEN_KEY = "token key";

    private EditText serverHost;
    private EditText serverPort;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    private Button login;
    private Button register;
    private RadioButton male;
    private RadioButton female;
    private boolean taskSuccess;

    public LoginFragment() { }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);


        serverHost = (EditText) v.findViewById(R.id.serverHostField);
        serverPort = (EditText) v.findViewById(R.id.serverPortField);
        userName = (EditText) v.findViewById(R.id.userNameField);
        password = (EditText) v.findViewById(R.id.passwordField);
        firstName = (EditText) v.findViewById(R.id.firstNameField);
        lastName = (EditText) v.findViewById(R.id.lastNameField);
        email = (EditText) v.findViewById(R.id.emailField);
        gender = (RadioGroup) v.findViewById(R.id.genderGroup);
        male = (RadioButton) v.findViewById(R.id.maleRadio);
        female = (RadioButton) v.findViewById(R.id.femaleRadio);
        login = (Button) v.findViewById(R.id.signInButton);
        register = (Button) v.findViewById(R.id.registerButton);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                login.setEnabled(false);
                register.setEnabled(false);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set EditTexts
            }
            @Override
            public void afterTextChanged(Editable s) {
                login.setEnabled(setLogInEnabled());
                register.setEnabled(setRegisterEnabled());
            }
        };

        serverHost.addTextChangedListener(tw);
        serverPort.addTextChangedListener(tw);
        userName.addTextChangedListener(tw);
        password.addTextChangedListener(tw);
        firstName.addTextChangedListener(tw);
        lastName.addTextChangedListener(tw);
        email.addTextChangedListener(tw);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadio) {

                }
                if (checkedId == R.id.femaleRadio) {

                }
            }
        });


        //Set up a handler that will process messages from the GetDataTask and make updates on the UI thread
        Handler getDataUIThreadMessageHandler = new Handler(Looper.getMainLooper()) {
          @Override
          public void handleMessage(Message message) {
              Bundle bundle = message.getData();
              taskSuccess = bundle.getBoolean(SUCCESS_KEY, false);

              if(taskSuccess) {
                  String toastFirstName = null;
                  String toastLastName = null;
                  //    Create Toast with the users first and last name
                  //    Get instance of DataCache, get the array of persons, check each persons personID
                  //    and see if it matches the current users personID, when the correct person has
                  //    been found get the first and last name and create a toast.
                  DataCache dataCache = DataCache.getInstance();
                  String currPersonID = dataCache.getCurrPersonID();
                  Person[] persons = dataCache.getPersons();
                  for (Person p : persons) {
                      if (p.getPersonID().equals(currPersonID)) {
                          toastFirstName = p.getFirstName();
                          toastLastName = p.getLastName();
                      }
                  }
                  String toast = toastFirstName + " " + toastLastName;
                  Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();

                  dataCache.initiateFilter();

                  MapsFragment mapFragment = new MapsFragment();
                  FragmentManager fm = getFragmentManager();
                  fm.beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
              }
              else {
                  Toast.makeText(getActivity(), "Unsuccessful GetData Attempt", Toast.LENGTH_LONG).show();
              }
          }
        };


        //Set up a handler that will process messages from the Login/Register task and make updates on the UI thread
        Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                //Get success from bundle
                taskSuccess = bundle.getBoolean(SUCCESS_KEY, false);
                String token = bundle.getString(TOKEN_KEY, null);

                if(taskSuccess) {
                    //Call GetData Task
                    String authToken = token;
                    String host = serverHost.getText().toString();
                    String port = serverPort.getText().toString();
                    GetDataTask newGetDataTask = new GetDataTask(getDataUIThreadMessageHandler, authToken, host, port);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(newGetDataTask);
                }
                else {
                    Toast.makeText(getActivity(), "Unsuccessful Login/Register Attempt", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Perform Login Task or Register task on a thread based of button clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = serverHost.getText().toString();
                String port = serverPort.getText().toString();
                LoginRequest newLoginRequest = new LoginRequest();
                newLoginRequest.setUsername(userName.getText().toString());
                newLoginRequest.setPassword(password.getText().toString());


                LoginTask newLoginTask = new LoginTask(uiThreadMessageHandler, newLoginRequest, host, port);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(newLoginTask);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = serverHost.getText().toString();
                String port = serverPort.getText().toString();
                RegisterRequest newRegisterRequest = new RegisterRequest();
                newRegisterRequest.setUsername(userName.getText().toString());
                newRegisterRequest.setPassword(password.getText().toString());
                newRegisterRequest.setFirstName(firstName.getText().toString());
                newRegisterRequest.setLastName(lastName.getText().toString());
                newRegisterRequest.setEmail(email.getText().toString());
                if(male.isChecked()) { newRegisterRequest.setGender("m"); }
                else if (female.isChecked()) { newRegisterRequest.setGender("f"); }


                RegisterTask newRegisterTask = new RegisterTask(uiThreadMessageHandler, newRegisterRequest, host, port);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(newRegisterTask);
            }
        });

        return v;
    }

    private boolean setLogInEnabled() {
        boolean host = serverHost.getText().toString().isEmpty();
        boolean port = serverPort.getText().toString().isEmpty();
        boolean username = userName.getText().toString().isEmpty();
        boolean pword = password.getText().toString().isEmpty();

        if (host || port || username || pword)
            return false;
        else
            return true;
    }

    private boolean setRegisterEnabled() {
        boolean fname = firstName.getText().toString().isEmpty();
        boolean lname = lastName.getText().toString().isEmpty();
        boolean mail = email.getText().toString().isEmpty();

        if (fname || lname || mail || !setLogInEnabled())
            return false;
        else
            return true;
    }
}