package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final int STORY_COLOR = 0xFF4EC73F;
    private static final int FAMILY_COLOR = 0xFFEC0606;
    private static final int SPOUSE_COLOR = 0xFF2909EE;
    private static final String[] COLORS = {"#ff0000", "#00ff00", "#0000ff", "#ffff00", "#00ffff",
                                            "#ff00ff", "#800000", "#008000", "#000080", "#800080",
                                            "#008080", "#808000", "#000000", "#808080", "#ffffff"};

    private Event[] events;
    private GoogleMap map;
    private ImageView image;
    private TextView personDetails;
    private RelativeLayout eventRL;
    private Event clickedEvent = null;
    private LatLng center = new LatLng(40, -112);
    private String centerEventID = null;
    private List<Polyline> polyLines = new ArrayList<Polyline>();
    private List<Marker> markers = new ArrayList<Marker>();
    private Map<String, String> coloredEvents = new HashMap<String, String>();
    private Boolean lifeStory = true;
    private Boolean familyTree = true;
    private Boolean spouse = true;
    private Boolean isEventActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        isEventActivity = false;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            centerEventID = bundle.getString("Event");
            setHasOptionsMenu(false);
            isEventActivity = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.searchMenuItem) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.settingsMenuItem) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        image = view.findViewById(R.id.detailsImage);
        personDetails = view.findViewById(R.id.detailsText);
        eventRL = view.findViewById(R.id.eventDetails);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (events != null) {
            DataCache dataCache = DataCache.getInstance();
            events = dataCache.getFilteredEvents();
            updateMarkers();

            boolean isPresent = false;
            for (Event e : events) {
                if (e.getEventID().equals(centerEventID))
                    isPresent = true;
            }
            if (!isPresent)
                removeLines();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        DataCache dataCache = DataCache.getInstance();
        events = dataCache.getEvents();
        map = googleMap;

        markEvents();


        if (centerEventID != null) {
            for (Event e : events) {
                if (e.getEventID().equals(centerEventID)) {
                    center = new LatLng(e.getLatitude(), e.getLongitude());
                    setDescriptionBox(e);
                    generateLines(e);
                }
            }
        }

        map.animateCamera(CameraUpdateFactory.newLatLng(center));

        //After a marker is clicked change the image and text based on the person it is attached to.
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event currEvent = (Event) marker.getTag();
                centerEventID = currEvent.getEventID();
                setDescriptionBox(currEvent);
                generateLines(currEvent);
                return false;
            }
        });

        eventRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedEvent != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    Bundle args = new Bundle();
                    args.putString("Person", clickedEvent.getPersonID());
                    intent.putExtras(args);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(),"Select an Event First!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void markEvents() {
        int i = 0;
        for (Event e : events) {
            if (i == COLORS.length) i = 0;

            String currColor;
            if (coloredEvents.containsKey(e.getEventType().toLowerCase())) {
                currColor = coloredEvents.get(e.getEventType().toLowerCase());
            }
            else {
                currColor = COLORS[i];
                coloredEvents.put(e.getEventType().toLowerCase(), currColor);
                i++;
            }

            LatLng currEvent = new LatLng(e.getLatitude(), e.getLongitude());
            Marker newMarker = map.addMarker(new MarkerOptions().position(currEvent).icon(getMarkerIcon(currColor)));
            newMarker.setTag(e);
            markers.add(newMarker);
        }
    }

    public void updateMarkers() {
        removeMarkers();
        markEvents();
    }

    public void removeMarkers() {
        for (Marker m : markers)
            m.remove();
        markers.clear();
    }

    public void setDescriptionBox(Event currEvent) {
        Person currPerson = null;
        clickedEvent = currEvent;
        String personID = currEvent.getPersonID();

        DataCache dataCache = DataCache.getInstance();
        Person[] persons = dataCache.getPersons();
        for (Person p : persons) {
            if (p.getPersonID().equals(personID)) currPerson = p;
        }

        if (currPerson.getGender().equals("m")) {
            Drawable male = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_boy);
            image.setImageDrawable(male);
        }
        else {
            Drawable female = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_girl);
            image.setImageDrawable(female);
        }

        String details = currPerson.getFirstName().toUpperCase() + " " + currPerson.getLastName().toUpperCase() +
                "\n" + currEvent.getEventType().toUpperCase() + ": " + currEvent.getCity() + ", " +
                currEvent.getCountry() + " (" + currEvent.getYear() + ")";
        personDetails.setText(details);
    }

    public void generateLines(Event currEvent) {
        removeLines();

        Person currPerson = null;
        String personID = currEvent.getPersonID();

        DataCache dataCache = DataCache.getInstance();
        Person[] persons = dataCache.getPersons();
        for (Person p : persons) {
            if (p.getPersonID().equals(personID)) currPerson = p;
        }

        lifeStory = dataCache.isLifeStoryLines();
        familyTree = dataCache.isFamilyTreeLines();
        spouse = dataCache.isSpouseLines();

        if (isEventActivity) { lifeStory = true; familyTree = true; spouse = true; }

        int generation = 0;
        if (lifeStory) generateLifeStoryLines(currPerson);
        if (familyTree) generateFamilyTreeLines(currEvent, currPerson, generation);
        if (spouse) generateSpouseLines(currEvent, currPerson);
    }

    public void generateLifeStoryLines(Person currPerson) {
        List<Event> personsEvents = new ArrayList<Event>();
        for (Event e : events) {
            if (e.getPersonID().equals(currPerson.getPersonID()))
                personsEvents.add(e);
        }

        DataCache dataCache = DataCache.getInstance();
        Event earliest = dataCache.getEarliestEvent(currPerson.getPersonID());

        personsEvents.remove(earliest);

        while (personsEvents.size() > 0) {
            Event next = null;
            int earliestYear = 99999;
            for (Event e : personsEvents) {
                if (e.getYear() < earliestYear) {
                    earliestYear = e.getYear();
                    next = e;
                }
            }
            polyLines.add(map.addPolyline(new PolylineOptions().add(new LatLng(earliest.getLatitude(), earliest.getLongitude()),
                    new LatLng(next.getLatitude(), next.getLongitude())).color(STORY_COLOR)));
            personsEvents.remove(next);
            earliest = next;
        }
    }

    public void generateFamilyTreeLines(Event currEvent, Person currPerson, int gen) {
        if (currPerson.getFatherID() == null || currPerson.getMotherID() == null) return;

        DataCache dataCache = DataCache.getInstance();
        Person father = dataCache.getPersonFromID(currPerson.getFatherID());
        Person mother = dataCache.getPersonFromID(currPerson.getMotherID());

        Event fatherEarliestEvent = dataCache.getEarliestEvent(currPerson.getFatherID());
        Event motherEarliestEvent = dataCache.getEarliestEvent(currPerson.getMotherID());


        for (Event e : events) {
            if (e.getEventID().equals(fatherEarliestEvent.getEventID())) {
                polyLines.add(map.addPolyline(new PolylineOptions().add(new LatLng(fatherEarliestEvent.getLatitude(), fatherEarliestEvent.getLongitude()),
                        new LatLng(currEvent.getLatitude(), currEvent.getLongitude())).width((gen + 2) * 5).color(FAMILY_COLOR)));
                generateFamilyTreeLines(fatherEarliestEvent, father, ++gen);
            }
            if (e.getEventID().equals(motherEarliestEvent.getEventID())) {
                polyLines.add(map.addPolyline(new PolylineOptions().add(new LatLng(motherEarliestEvent.getLatitude(), motherEarliestEvent.getLongitude()),
                        new LatLng(currEvent.getLatitude(), currEvent.getLongitude())).width((gen + 2) * 5).color(FAMILY_COLOR)));
                generateFamilyTreeLines(motherEarliestEvent, mother, ++gen);
            }
        }

    }

    public void generateSpouseLines(Event currEvent, Person currPerson) {
        if (currPerson.getSpouseID() == null) return;

        DataCache dataCache = DataCache.getInstance();
        Event spousesEarliestEvent = dataCache.getEarliestEvent(currPerson.getSpouseID());

        for (Event e : events) {
            if (e.getEventID().equals(spousesEarliestEvent.getEventID())) {
                polyLines.add(map.addPolyline(new PolylineOptions().add(new LatLng(spousesEarliestEvent.getLatitude(), spousesEarliestEvent.getLongitude()),
                        new LatLng(currEvent.getLatitude(), currEvent.getLongitude())).color(SPOUSE_COLOR)));
            }
        }
    }

    public void removeLines() {
        for(Polyline line : polyLines)
            line.remove();
        polyLines.clear();
    }
}