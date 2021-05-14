package com.example.familymapclient;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataCache {
    private static DataCache instance;

    private String currUsername;
    private String currPersonID;
    private String currAuthToken;
    private Event[] events;
    private Person[] persons;

    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherFilter = true;
    private boolean motherFilter = true;
    private boolean maleFilter = true;
    private boolean femaleFilter = true;

    private final List<Event> rootAndSpouse = new ArrayList<>();
    private final List<Event> fatherEvents = new ArrayList<>();
    private final List<Event> motherEvents = new ArrayList<>();
    private final List<Event> maleEvents = new ArrayList<>();
    private final List<Event> femaleEvents = new ArrayList<>();

    private Map<String, Person> allIDsToPeople = new HashMap<>();
    private final Map<String, Event> allIDsToEvent = new HashMap<>();
    private final Map<String, Event> personsEarliestEvent = new HashMap<>();

    public synchronized static DataCache getInstance() {
        if (instance == null)
            instance = new DataCache();
        return instance;
    }

    private DataCache() { }

    public void initiateFilter() {
        createPersonMap();
        createEventMap();
        createEarliestEventsMap();

        Person root = allIDsToPeople.get(currPersonID);

        for (Event e : events) {
            if (e.getPersonID().equals(root.getPersonID())) {
                rootAndSpouse.add(e);
            }
            if (e.getPersonID().equals(root.getSpouseID())) {
                rootAndSpouse.add(e);
            }
        }

        filterFatherEvents(root.getFatherID());
        filterMotherEvents(root.getMotherID());
        filterMaleEvents();
        filterFemaleEvents();
    }

    public void filterFatherEvents(String personID) {
        Person currPerson = allIDsToPeople.get(personID);

        for (Event e : events) {
            if (e.getPersonID().equals(personID))
                fatherEvents.add(e);
        }
        if (currPerson.getFatherID() != null)
            filterFatherEvents(currPerson.getFatherID());
        if (currPerson.getMotherID() != null)
            filterFatherEvents(currPerson.getMotherID());
        if ((currPerson.getFatherID() == null) && (currPerson.getMotherID() == null))
            return;
    }

    public void filterMotherEvents(String personID) {

        Person currPerson = allIDsToPeople.get(personID);

        for (Event e : events) {
            if (e.getPersonID().equals(personID))
                motherEvents.add(e);
        }
        if (currPerson.getFatherID() != null)
            filterMotherEvents(currPerson.getFatherID());
        if (currPerson.getMotherID() != null)
            filterMotherEvents(currPerson.getMotherID());
        if ((currPerson.getFatherID() == null) && (currPerson.getMotherID() == null))
            return;
    }

    public void filterMaleEvents() {
        for (Person p : persons) {
            if (p.getGender().equals("m")) {
                for (Event e : events) {
                    if (e.getPersonID().equals(p.getPersonID()))
                        maleEvents.add(e);
                }
            }
        }
    }

    public void filterFemaleEvents() {
        for (Person p : persons) {
            if (p.getGender().equals("f")) {
                for (Event e : events) {
                    if (e.getPersonID().equals(p.getPersonID()))
                        femaleEvents.add(e);
                }
            }
        }
    }

    public void createPersonMap() {
        for (Person p : persons) {
            allIDsToPeople.put(p.getPersonID(), p);
        }
    }

    public void createEventMap() {
        for (Event e : events) {
            allIDsToEvent.put(e.getEventID(), e);
        }
    }

    public void createEarliestEventsMap() {
        for (Person p : persons) {
            personsEarliestEvent.put(p.getPersonID(), getEarliestEvent(p.getPersonID()));
        }
    }

    public Event getEarliestEvent(String personID) {
        List<Event> personsEvents = new ArrayList<Event>();
        for (Event e : events) {
            if (e.getPersonID().equals(personID))
                personsEvents.add(e);
        }

        int earliestYear = 99999;
        Event earliestEvent = null;
        for (Event e : personsEvents) {
            if (e.getYear() < earliestYear) {
                earliestYear = e.getYear();
                earliestEvent = e;
            }
        }

        return earliestEvent;
    }

    public List<Event> getMalesFromList(Set<Event> set) {
        List<Event> neededMaleEvents = new ArrayList<Event>();
        for (Event e : set) {
            Person attachedPerson = getPersonFromID(e.getPersonID());
            if (attachedPerson.getGender().equals("m"))
                neededMaleEvents.add(e);
        }
        return neededMaleEvents;
    }

    public List<Event> getFemalesFromList(Set<Event> set) {
        List<Event> neededMaleEvents = new ArrayList<Event>();
        for (Event e : set) {
            Person attachedPerson = getPersonFromID(e.getPersonID());
            if (attachedPerson.getGender().equals("f"))
                neededMaleEvents.add(e);
        }
        return neededMaleEvents;
    }

    public Event[] getFilteredEvents() {
        Set<Event> filteredEvents = new LinkedHashSet<>();
        Set<Event> temp = new LinkedHashSet<>();

        filteredEvents.addAll(rootAndSpouse);

        if (fatherFilter)
            filteredEvents.addAll(fatherEvents);
        if (motherFilter)
            filteredEvents.addAll(motherEvents);
        if (maleFilter && (!femaleFilter)) {
            temp.addAll(filteredEvents);
            filteredEvents.clear();
            filteredEvents.addAll(getMalesFromList(temp));
            temp.clear();
        }
        if (femaleFilter && (!maleFilter)) {
            temp.addAll(filteredEvents);
            filteredEvents.clear();
            filteredEvents.addAll(getFemalesFromList(temp));
            temp.clear();
        }
        if ((!maleFilter) && (!femaleFilter))
            filteredEvents.clear();

        Event[] result = new Event[filteredEvents.size()];
        result = filteredEvents.toArray(result);
        return result;
    }

    public Event getPersonsEarliestEvent(String personID) { return personsEarliestEvent.get(personID); }

    public Event getEventFromID(String eventID) { return allIDsToEvent.get(eventID); }

    public Person getPersonFromID(String personID) { return allIDsToPeople.get(personID); }

    public String getCurrUsername() { return currUsername; }

    public void setCurrUsername(String currUsername) { this.currUsername = currUsername; }

    public String getCurrPersonID() { return currPersonID; }

    public void setCurrPersonID(String currPersonID) { this.currPersonID = currPersonID; }

    public String getCurrAuthToken() { return currAuthToken; }

    public void setCurrAuthToken(String currAuthToken) { this.currAuthToken = currAuthToken; }

    public Event[] getEvents() { return events; }

    public void setEvents(Event[] events) { this.events = events; }

    public Person[] getPersons() { return persons; }

    public void setPersons(Person[] persons) { this.persons = persons; }

    public boolean isLifeStoryLines() { return lifeStoryLines; }

    public void setLifeStoryLines(boolean lifeStoryLines) { this.lifeStoryLines = lifeStoryLines; }

    public boolean isFamilyTreeLines() { return familyTreeLines; }

    public void setFamilyTreeLines(boolean familyTreeLines) { this.familyTreeLines = familyTreeLines; }

    public boolean isSpouseLines() { return spouseLines; }

    public void setSpouseLines(boolean spouseLines) { this.spouseLines = spouseLines; }

    public boolean isFatherFilter() { return fatherFilter; }

    public void setFatherFilter(boolean fatherFilter) { this.fatherFilter = fatherFilter; }

    public boolean isMotherFilter() { return motherFilter; }

    public void setMotherFilter(boolean motherFilter) { this.motherFilter = motherFilter; }

    public boolean isMaleFilter() { return maleFilter; }

    public void setMaleFilter(boolean maleFilter) { this.maleFilter = maleFilter; }

    public boolean isFemaleFilter() { return femaleFilter; }

    public void setFemaleFilter(boolean femaleFilter) { this.femaleFilter = femaleFilter; }

    public Map<String, Person> getAllIDsToPeople() { return allIDsToPeople; }

    public void setAllIDsToPeople(Map<String, Person> allIDsToPeople) { this.allIDsToPeople = allIDsToPeople; }

    public List<Event> getFatherEvents() { return fatherEvents; }

    public List<Event> getMotherEvents() { return motherEvents; }

    public List<Event> getMaleEvents() { return maleEvents; }

    public List<Event> getFemaleEvents() { return femaleEvents; }

    public void clearFilteredEvents() {
        fatherEvents.clear();
        motherEvents.clear();
        maleEvents.clear();
        femaleEvents.clear();
    }

    public void destroyCurrentSession() { instance = null; }

    public String getRelationship(Person currPerson, Person related) {
        if (related.getPersonID().equals(currPerson.getFatherID())) return "father";
        if (related.getPersonID().equals(currPerson.getMotherID())) return "mother";
        if (related.getPersonID().equals(currPerson.getSpouseID())) return "spouse";

        if (!(related.getFatherID() == null))
            if (related.getFatherID().equals(currPerson.getPersonID())) return "child";
        if (!(related.getMotherID() == null))
            if (related.getMotherID().equals(currPerson.getPersonID())) return "child";

        return "No direct relationship";
    }

    public Event[] sortPersonsEvents(Person currPerson, Event[] possibleEvents) {
        List<Event> personsEvents = new ArrayList<>();
        List<Event> sortedEvents = new ArrayList<>();
        for(Event e : possibleEvents) {
            if (e.getPersonID().equals(currPerson.getPersonID()))
                personsEvents.add(e);
        }

        Event earliest = getEarliestEvent(currPerson.getPersonID());
        while (personsEvents.size() > 0) {
            Event next = null;
            int earliestYear = 9999;
            for (Event e : personsEvents) {
                if (e.getYear() < earliestYear) {
                    earliestYear = e.getYear();
                    earliest = e;
                }
            }
            sortedEvents.add(earliest);
            personsEvents.remove(earliest);
        }

        Event[] result = new Event[sortedEvents.size()];
        result = sortedEvents.toArray(result);
        return result;
    }

    public Event[] getMatchingEvents(String query, Event[] possibleEvents) {
        List<Event> matches = new ArrayList<>();

        for (Event e : possibleEvents) {
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

    public Person[] getMatchingPersons(String query, Person[] possiblePersons) {
        List<Person> matches = new ArrayList<>();

        for (Person p : possiblePersons) {
            String name = (p.getFirstName() + " " + p.getLastName()).toLowerCase();
            if (name.contains(query.toLowerCase()))
                matches.add(p);
        }

        Person[] result = new Person[matches.size()];
        result = matches.toArray(result);
        return result;
    }
}