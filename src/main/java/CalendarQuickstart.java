package main.java;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CalendarQuickstart {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR);

    com.google.api.services.calendar.Calendar service;
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                CalendarQuickstart.class.getResourceAsStream("../resources/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     *
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
    getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void addLectureUsingBatch(LinkedList<Lecture> lectures, Calendar calendar) throws IOException {
        View.header("Add Calendars using Batch");
        BatchRequest batch = service.batch();

        // Create the callback.
        JsonBatchCallback<Event> callback = new JsonBatchCallback<Event>() {

            @Override
            public void onSuccess(Event event, HttpHeaders responseHeaders) {
                View.display(event);
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                System.out.println("Error Message: " + e.getMessage());
            }
        };
        for (Lecture lecture: lectures){
            Event lectureEvent = eventFromLecture(lecture);
            service.events().insert(calendar.getId(),lectureEvent).queue(batch,callback);
        }

        // Create 2 Calendar Entries to insert.

        batch.execute();
    }

    private Event eventFromLecture(Lecture lecture) {
        Event event = new Event();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        // The following should give us the date of the lecture this week;
        gregorianCalendar.set(GregorianCalendar.DAY_OF_WEEK,(lecture.getDayOfWeek().getValue())%7 +1);
        gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, lecture.getStartTime().getHour());
        gregorianCalendar.set(GregorianCalendar.MINUTE, lecture.getStartTime().getMinute());
        gregorianCalendar.set(GregorianCalendar.SECOND, 0);
        Date startDate = gregorianCalendar.getTime();
        gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, lecture.getEndTime().getHour());
        gregorianCalendar.set(GregorianCalendar.MINUTE, lecture.getEndTime().getMinute());
        // There's no need to adjust the other variables for the end time
        Date endDate = gregorianCalendar.getTime();
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("GMT"));
        event.setStart(new EventDateTime().setDateTime(start).setTimeZone("GMT"));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("GMT"));
        event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("GMT"));
        event.setSummary(lecture.getTitle());
        event.setLocation(lecture.getLocation());
        // I set the recurrence from the current date to the 1st of july, you might need to change this
        event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20160701T170000Z"));
        return event;
    }

    public CalendarQuickstart(){
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        service =            getCalendarService();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        String pageToken = null;
        boolean flag_for_calendar_creation = true;
        do {

            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                if(calendarListEntry.getSummary().equals("Calendar for YoSchedule")){
                    flag_for_calendar_creation = false;
                    System.out.println(calendarListEntry.getSummary());
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null && flag_for_calendar_creation);

// creating calendar if there are no calendars with same description
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        calendar.setSummary("Calendar for YoSchedule");
        if (flag_for_calendar_creation){
            Calendar createdCalendar = service.calendars().insert(calendar).execute();

        }



        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }
    /*public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
                getCalendarService();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();

        String pageToken = null;
        boolean flag_for_calendar_creation = true;
        do {

            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                if(calendarListEntry.getSummary().equals("Calendar for YoSchedule")){
                    flag_for_calendar_creation = false;
                    System.out.println(calendarListEntry.getSummary());
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null && flag_for_calendar_creation);

// creating calendar if there are no calendars with same description
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        calendar.setSummary("Calendar for YoSchedule");
        if (flag_for_calendar_creation){
            Calendar createdCalendar = service.calendars().insert(calendar).execute();

        }




        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.println(start); //Prints start date of the event
                checkDateTime(start.toString()); //Checks event with present time
            }
        }
    }

    public static void checkDateTime(String eventTime) {
        String currentDate = createDate();
        String currentTime = createTime();
        if (eventTime.contains(currentDate)) {
            int number1 = ((Character.getNumericValue(eventTime.charAt(11)) * 10 + Character.getNumericValue(eventTime.charAt(12))) * 60) +
                    ((Character.getNumericValue(eventTime.charAt(14)) * 10 + Character.getNumericValue(eventTime.charAt(15))));
            int number2 = ((Character.getNumericValue(currentTime.charAt(0)) * 10 + Character.getNumericValue(currentTime.charAt(1))) * 60) +
                    (Character.getNumericValue(currentTime.charAt(3)) * 10 + Character.getNumericValue(currentTime.charAt(4)));
            if (number1 - number2 <= 10 && number1 - number2 > 0) {
                System.out.println("Success");
                SendYo yothem = new SendYo();
                yothem.sendYo("FILIPEW");
            }
        }


    }*/

    public static  String createDate(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();
        String dateTime = df.format(dateobj);
        return dateTime;
    }

    public static String createTime(){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date();
        String timeTime = df.format(dateobj);
        return timeTime;
    }

}