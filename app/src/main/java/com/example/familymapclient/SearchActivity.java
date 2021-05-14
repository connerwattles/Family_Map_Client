package com.example.familymapclient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {
    private static final int EVENT_ITEM_VIEW_TYPE = 0;
    private static final int PERSON_ITEM_VIEW_TYPE = 1;

    private Event[] matchingEvents = {};
    private Person[] matchingPersons = {};

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView search = findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                matchingEvents = getMatchingEvents(query);
                matchingPersons = getMatchingPersons(query);

                List<Event> allEvents = Arrays.asList(matchingEvents);
                List<Person> allPersons = Arrays.asList(matchingPersons);

                SearchAdapter adapter = new SearchAdapter(allEvents, allPersons);
                recyclerView.setAdapter(adapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                matchingEvents = getMatchingEvents(newText);
                matchingPersons = getMatchingPersons(newText);

                List<Event> allEvents = Arrays.asList(matchingEvents);
                List<Person> allPersons = Arrays.asList(matchingPersons);

                SearchAdapter adapter = new SearchAdapter(allEvents, allPersons);
                recyclerView.setAdapter(adapter);

                return false;
            }
        });

        recyclerView = findViewById(R.id.searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        List<Event> allEvents = Arrays.asList(matchingEvents);
        List<Person> allPersons = Arrays.asList(matchingPersons);

        SearchAdapter adapter = new SearchAdapter(allEvents, allPersons);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public Event[] getMatchingEvents(String query) {
        List<Event> matches = new ArrayList<>();

        DataCache dataCache = DataCache.getInstance();
        Event[] events = dataCache.getFilteredEvents();
        for (Event e : events) {
            String traits = (e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() +
                            " (" + e.getYear() + ")").toLowerCase();
            if (traits.contains(query.toLowerCase())) {
                matches.add(e);
            }
        }

        Event[] result = new Event[matches.size()];
        result = matches.toArray(result);
        return result;
    }

    public Person[] getMatchingPersons(String query) {
        List<Person> matches = new ArrayList<>();

        DataCache dataCache = DataCache.getInstance();
        Person[] allPersons = dataCache.getPersons();
        for (Person p : allPersons) {
            String name = (p.getFirstName() + " " + p.getLastName()).toLowerCase();
            if (name.contains(query.toLowerCase()))
                matches.add(p);
        }

        Person[] result = new Person[matches.size()];
        result = matches.toArray(result);
        return result;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Event> events;
        private final List<Person> persons;

        SearchAdapter(List<Event> events, List<Person> persons) {
            this.events = events;
            this.persons = persons;
        }

        @Override
        public int getItemViewType(int position) {
            return position < events.size() ? EVENT_ITEM_VIEW_TYPE : PERSON_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == EVENT_ITEM_VIEW_TYPE)
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            else
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < events.size())
                holder.bind(events.get(position));
            else
                holder.bind(persons.get(position - events.size()));
        }

        @Override
        public int getItemCount() { return events.size() + persons.size(); }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView top;
        private final TextView bottom;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder (View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENT_ITEM_VIEW_TYPE) {
                icon = itemView.findViewById(R.id.eventImage);
                top = itemView.findViewById(R.id.eventDetailsText);
                bottom = itemView.findViewById(R.id.eventsPersonText);
            }
            else {
                icon = itemView.findViewById(R.id.personImage);
                top = itemView.findViewById(R.id.personNameText);
                bottom = itemView.findViewById(R.id.personRelationText);
            }
        }

        private void bind(Event event) {
            this.event = event;

            String eventDetails = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " +
                    event.getCountry() + " (" + event.getYear() + ")";
            top.setText(eventDetails);

            DataCache dataCache = DataCache.getInstance();
            Person[] persons = dataCache.getPersons();
            Person currPerson = null;

            for (Person p : persons) {
                if (p.getPersonID().equals(event.getPersonID()))
                    currPerson = p;
            }
            String text = currPerson.getFirstName() + " " + currPerson.getLastName();
            bottom.setText(text);
        }

        private void bind(Person person) {
            this.person = person;

            if (person.getGender().equals("m")) {
                Drawable male = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_boy);
                icon.setImageDrawable(male);
            }
            else {
                Drawable female = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_girl);
                icon.setImageDrawable(female);
            }
            String text = person.getFirstName() + " " + person.getLastName();
            top.setText(text);

            bottom.setText(" ");
        }

        @Override
        public void onClick(View v) {
            if(viewType == EVENT_ITEM_VIEW_TYPE) {
                startEventActivity(event.getEventID());
            }
            else {
                startPersonActivity(person.getPersonID());
            }
        }
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
}