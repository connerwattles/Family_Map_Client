package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        String activityPersonID = getIntent().getCharSequenceExtra("Person").toString();
        Person activityPerson = null;

        DataCache dataCache = DataCache.getInstance();
        Person[] persons = dataCache.getPersons();
        for (Person p : persons) {
            if (p.getPersonID().equals(activityPersonID))
                activityPerson = p;
        }

        TextView personFirstName = findViewById(R.id.personFirstNameText);
        personFirstName.setText(activityPerson.getFirstName());

        TextView personLastName = findViewById(R.id.personLastNameText);
        personLastName.setText(activityPerson.getLastName());

        TextView gender = findViewById(R.id.genderText);
        if (activityPerson.getGender().equals("m")) gender.setText("Male");
        else gender.setText("Female");


        expandableListView.setAdapter(new ExpandableListAdapter(activityPerson));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void startPersonActivity(String personID) {
        Intent intent = new Intent(this, PersonActivity.class);
        Bundle args = new Bundle();
        args.putString("Person", personID);
        intent.putExtras(args);
        startActivity(intent);
    }

    public void startEventActivity(String eventID) {
        Intent intent = new Intent(this, EventActivity.class);
        Bundle args = new Bundle();
        args.putString("Event", eventID);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;
        private static final int NUM_GROUPS = 2;

        private List<Person> persons;
        private List<Event> events;

        private final Person thePerson;

        ExpandableListAdapter(Person person) {
            this.thePerson = person;

            persons = new ArrayList<Person>();
            events = new ArrayList<Event>();

            DataCache dataCache = DataCache.getInstance();
            Person[] allPersons = dataCache.getPersons();
            for (Person p : allPersons) {
                String fatherID = p.getFatherID();
                String motherID = p.getMotherID();
                String personID = p.getPersonID();

                if (fatherID == null) fatherID = "null";
                if (motherID == null) motherID = "null";

                if (fatherID.equals(thePerson.getPersonID()) || motherID.equals(thePerson.getPersonID()))
                    persons.add(p);
                if (personID.equals(thePerson.getMotherID()) || personID.equals(thePerson.getFatherID())
                        || personID.equals(thePerson.getSpouseID()))
                    persons.add(p);
            }

            Event[] allEvents = dataCache.getFilteredEvents();
            for (Event e : allEvents) {
                if (e.getPersonID().equals(thePerson.getPersonID()))
                    events.add(e);
            }
        }

        @Override
        public int getGroupCount() { return NUM_GROUPS;}

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case PERSON_GROUP_POSITION:
                    return persons.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return "LIFE EVENTS";
                case PERSON_GROUP_POSITION:
                    return "FAMILY";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                case PERSON_GROUP_POSITION:
                    return persons.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText("LIFE EVENTS");
                    return convertView;
                case PERSON_GROUP_POSITION:
                    titleView.setText("FAMILY");
                    return convertView;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = null;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return false; }

        private void initializePersonView(View personItemView, final int childPosition) {
            Person currPerson = persons.get(childPosition);

            ImageView personImage = personItemView.findViewById(R.id.personImage);
            if (currPerson.getGender().equals("m")) {
                Drawable male = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_boy);
                personImage.setImageDrawable(male);
            }
            else {
                Drawable female = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_girl);
                personImage.setImageDrawable(female);
            }

            TextView nameView = personItemView.findViewById(R.id.personNameText);
            nameView.setText(currPerson.getFirstName() + " " + currPerson.getLastName());

            TextView relationView = personItemView.findViewById(R.id.personRelationText);
            String currID = currPerson.getPersonID();
            if (currID.equals(thePerson.getFatherID())) {
                relationView.setText("Father");
            }
            else if (currID.equals(thePerson.getMotherID())) {
                relationView.setText("Mother");
            }
            else if (currID.equals(thePerson.getSpouseID())) {
                relationView.setText("Spouse");
            }
            else {
                relationView.setText("Child");
            }

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPersonActivity(currID);
                }
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            Event currEvent = events.get(childPosition);

            TextView eventView = eventItemView.findViewById(R.id.eventDetailsText);
            String eventDetails = currEvent.getEventType().toUpperCase() + ": " + currEvent.getCity() + ", " +
                                    currEvent.getCountry() + " (" + currEvent.getYear() + ")";
            eventView.setText(eventDetails);

            TextView nameView = eventItemView.findViewById(R.id.eventsPersonText);
            nameView.setText(thePerson.getFirstName() + " " + thePerson.getLastName());

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEventActivity(currEvent.getEventID());
                }
            });
        }
    }
}