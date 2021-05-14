package com.example.familymapclient;

import com.example.familymapclient.ExampleEventsAndPersons.EventsAndPersons;
import com.google.gson.Gson;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import Model.Event;
import Model.Person;

import static org.junit.Assert.*;

public class FilterTests {
    private static DataCache dataCache;
    private static Gson gson = new Gson();
    private static Person root;

    @BeforeClass
    public static void initializeDataCache() throws FileNotFoundException {
        dataCache = DataCache.getInstance();
        FileReader jsonFile = new FileReader("C:\\Users\\conne\\AndroidStudioProjects\\FamilyMapClient\\app\\src\\test\\java\\com\\example\\familymapclient\\ExampleEventsAndPersons\\TestEventsAndPersons");
        EventsAndPersons data = gson.fromJson(jsonFile, EventsAndPersons.class);

        dataCache.setCurrUsername("sheila");
        dataCache.setCurrPersonID("Sheila_Parker");
        dataCache.setEvents(data.getEvents());
        dataCache.setPersons(data.getPersons());

        root = new Person();
        root.setFirstName("Sheila");
        root.setLastName("Parker");
        root.setGender("f");
        root.setPersonID("Sheila_Parker");
        root.setSpouseID("Davis_Hyer");
        root.setFatherID("Blaine_McGary");
        root.setMotherID("Betty_White");
        root.setUsername("sheila");

        dataCache.createPersonMap();
        dataCache.createEventMap();
        dataCache.createEarliestEventsMap();
    }

    @Test
    public void fatherFilterPositive() {
        dataCache.clearFilteredEvents();

        dataCache.filterFatherEvents(root.getFatherID());

        List<Event> filteredEvents = dataCache.getFatherEvents();

        assertEquals(5, filteredEvents.size());

        for (Event e : filteredEvents) {
            assertNotEquals("Betty_White", e.getPersonID());
            assertNotEquals("Frank_Jones", e.getPersonID());
            assertNotEquals("Mrs_Jones", e.getPersonID());
        }
    }

    @Test
    public void fatherFilterAbnormal() {
        dataCache.clearFilteredEvents();

        Person rootFather = dataCache.getPersonFromID(root.getFatherID());
        dataCache.filterFatherEvents(rootFather.getFatherID());

        List<Event> filteredEvents = dataCache.getFatherEvents();

        assertEquals(2, filteredEvents.size());
        for (Event e : filteredEvents) {
            assertNotEquals("Davis_Hyer", e.getPersonID());
            assertNotEquals("Sheila_Parker", e.getPersonID());
            assertNotEquals("Blaine_McGary", e.getPersonID());
            assertNotEquals("Betty_White", e.getPersonID());
            assertNotEquals("Mrs_Jones", e.getPersonID());
            assertNotEquals("Frank_Jones", e.getPersonID());
        }
    }

    @Test
    public void motherFilterPositive() {
        dataCache.clearFilteredEvents();

        dataCache.filterMotherEvents(root.getMotherID());

        List<Event> filteredEvents = dataCache.getMotherEvents();

        assertEquals(5, filteredEvents.size());
        for (Event e : filteredEvents) {
            assertNotEquals("Blaine_McGary", e.getPersonID());
            assertNotEquals("Mrs_Rodham", e.getPersonID());
            assertNotEquals("Ken_Rodham", e.getPersonID());
        }
    }

    @Test
    public void motherFilterAbnormal() {
        dataCache.clearFilteredEvents();

        Person rootMother = dataCache.getPersonFromID(root.getMotherID());
        dataCache.filterFatherEvents(rootMother.getFatherID());

        List<Event> filteredEvents = dataCache.getFatherEvents();

        assertEquals(2, filteredEvents.size());
        for (Event e : filteredEvents) {
            assertNotEquals("Davis_Hyer", e.getPersonID());
            assertNotEquals("Sheila_Parker", e.getPersonID());
            assertNotEquals("Blaine_McGary", e.getPersonID());
            assertNotEquals("Betty_White", e.getPersonID());
            assertNotEquals("Mrs_Rodham", e.getPersonID());
            assertNotEquals("Ken_Rodham", e.getPersonID());
        }
    }

    @Test
    public void maleFilterPositive() {
        dataCache.clearFilteredEvents();

        dataCache.filterMaleEvents();

        List<Event> filteredEvents = dataCache.getMaleEvents();

        assertEquals(6, filteredEvents.size());
        for (Event e : filteredEvents) {
            assertNotEquals("Sheila_Parker", e.getPersonID());
            assertNotEquals("Betty_White", e.getPersonID());
            assertNotEquals("Mrs_Rodham", e.getPersonID());
            assertNotEquals("Mrs_Jones", e.getPersonID());
        }
    }

    @Test
    public void maleFilterAbnormal() throws FileNotFoundException {
        dataCache.clearFilteredEvents();

        Event temp = new Event();
        temp.setEventType("Championship");
        temp.setPersonID("Davis_Hyer");
        temp.setCountry("Unkited States");
        temp.setCity("New York");
        temp.setLatitude((float) 40.7128);
        temp.setLongitude((float) -74);
        temp.setYear(2015);
        temp.setEventID("123454");
        temp.setUsername("sheila");

        Event temp1 = new Event();
        temp1.setEventType("Championship2");
        temp1.setPersonID("Davis_Hyer");
        temp1.setCountry("Unkited States");
        temp1.setCity("Provo");
        temp1.setLatitude((float) 52.4833);
        temp1.setLongitude((float) -111.6585);
        temp1.setYear(2015);
        temp1.setEventID("09876");
        temp1.setUsername("sheila");

        Event[] abnormal = new Event[]{temp, temp1};

        dataCache.setEvents(abnormal);
        dataCache.filterMaleEvents();

        List<Event> filteredEvents = dataCache.getMaleEvents();

        assertEquals(2, filteredEvents.size());

        initializeDataCache();
    }

    @Test
    public void femaleFilterPositive() {
        dataCache.clearFilteredEvents();

        dataCache.filterFemaleEvents();

        List<Event> filteredEvents = dataCache.getFemaleEvents();

        assertEquals(10, filteredEvents.size());
        for (Event e : filteredEvents) {
            assertNotEquals("Davis_Hyer", e.getPersonID());
            assertNotEquals("Blaine_McGary", e.getPersonID());
            assertNotEquals("Ken_Rodham", e.getPersonID());
            assertNotEquals("Frank_Jones", e.getPersonID());
        }
    }

    @Test
    public void femaleFilterAbnormal() throws FileNotFoundException {
        dataCache.clearFilteredEvents();

        Event temp = new Event();
        temp.setEventType("Championship");
        temp.setPersonID("Betty_White");
        temp.setCountry("Unkited States");
        temp.setCity("New York");
        temp.setLatitude((float) 40.7128);
        temp.setLongitude((float) -74);
        temp.setYear(2015);
        temp.setEventID("123454");
        temp.setUsername("sheila");

        Event temp1 = new Event();
        temp1.setEventType("Championship2");
        temp1.setPersonID("Betty_White");
        temp1.setCountry("Unkited States");
        temp1.setCity("Provo");
        temp1.setLatitude((float) 52.4833);
        temp1.setLongitude((float) -111.6585);
        temp1.setYear(2015);
        temp1.setEventID("09876");
        temp1.setUsername("sheila");

        Event[] abnormal = new Event[]{temp, temp1};

        dataCache.setEvents(abnormal);
        dataCache.filterFemaleEvents();

        List<Event> filteredEvents = dataCache.getFemaleEvents();

        assertEquals(2, filteredEvents.size());

        initializeDataCache();
    }
}
