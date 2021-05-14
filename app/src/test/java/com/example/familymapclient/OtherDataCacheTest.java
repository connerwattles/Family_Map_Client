package com.example.familymapclient;

import com.example.familymapclient.ExampleEventsAndPersons.EventsAndPersons;
import com.google.gson.Gson;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OtherDataCacheTest {
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
    public void RelationshipsPositive() {
        Person rootFather = dataCache.getPersonFromID(root.getFatherID());
        Person rootMother = dataCache.getPersonFromID(root.getMotherID());
        Person rootSpouse = dataCache.getPersonFromID(root.getSpouseID());

        assertEquals("father", dataCache.getRelationship(root, rootFather));
        assertEquals("mother", dataCache.getRelationship(root, rootMother));
        assertEquals("spouse", dataCache.getRelationship(root, rootSpouse));

        assertEquals("child", dataCache.getRelationship(rootFather, root));
    }

    @Test
    public void RelationshipNegative() {
        Person rootFather = dataCache.getPersonFromID(root.getFatherID());
        Person rootGrandfather = dataCache.getPersonFromID(rootFather.getFatherID());

        assertNotEquals("father", dataCache.getRelationship(root, rootGrandfather));
        assertNotEquals("mother", dataCache.getRelationship(root, rootGrandfather));
        assertNotEquals("spouse", dataCache.getRelationship(root, rootGrandfather));
        assertNotEquals("child", dataCache.getRelationship(root, rootGrandfather));
    }

    @Test
    public void ChronologicalEventsPositive() {
        Event[] sortedEvents = dataCache.sortPersonsEvents(root, dataCache.getEvents());

        assertEquals(1970, sortedEvents[0].getYear());
        assertEquals(2015, sortedEvents[4].getYear());
    }

    @Test
    public void ChronologicalEventsNegative() {
        Person rootFather = dataCache.getPersonFromID(root.getFatherID());
        Person rootGrandfather = dataCache.getPersonFromID(rootFather.getFatherID());

        Event[] kenEvents = dataCache.sortPersonsEvents(rootGrandfather, dataCache.getEvents());

        Event[] sortedEvents = dataCache.sortPersonsEvents(root, kenEvents);

        assertEquals(0, sortedEvents.length);
    }

    @Test
    public void SearchPositive() {
        Event[] usingEvents = dataCache.getEvents();
        Person[] usingPersons = dataCache.getPersons();

        Event[] matchingEvents = dataCache.getMatchingEvents("ur", usingEvents);
        Person[] matchingPersons = dataCache.getMatchingPersons("ur", usingPersons);

        assertEquals(3, matchingEvents.length);
        assertEquals(0, matchingPersons.length);


        Event[] matchingEvents1 = dataCache.getMatchingEvents("sheila", usingEvents);
        Person[] matchingPersons1 = dataCache.getMatchingPersons("sheila", usingPersons);

        assertEquals(0, matchingEvents1.length);
        assertEquals(1, matchingPersons1.length);
    }

    @Test
    public void SearchNegative() {
        String searchQuery = "should not exist";

        Event[] usingEvents = dataCache.getEvents();
        Person[] usingPersons = dataCache.getPersons();

        Event[] matchingEvents = dataCache.getMatchingEvents(searchQuery, usingEvents);
        Person[] matchingPersons = dataCache.getMatchingPersons(searchQuery, usingPersons);

        assertEquals(0, matchingEvents.length);
        assertEquals(0, matchingPersons.length);
    }
}
