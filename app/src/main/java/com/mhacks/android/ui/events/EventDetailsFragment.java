package com.mhacks.android.ui.events;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.mhacks.android.R;

import com.mhacks.android.data.model.Event;
import com.mhacks.android.data.model.Location;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Omkar Moghe on 12/25/2014.
 *
 * Uses and Event object to create a detailed view with all the info about the event.
 * Called from the WeekView's onEventClicked listener.
 */
public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";

    //Decalre Views.
    private View mEventDetailsFragView;

    private TextView eventTitle, eventTime, eventLocation, eventDescription, eventHost;
    private View colorBlock; //Header color. Matches color of event in calendar.

    //Date arrays
    private final String[] dayOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                                        "Friday", "Saturday"};
    private final String[] monthOfYear = {"January", "February", "March", "April", "May", "June",
                                          "July", "August", "September", "October", "November",
                                          "December"};

    /**
     * Creates a new instance of the EventDetailsFragment.
     * @param event Event to display in detailed view.
     * @param color Color of the event.
     * @return An EventDetailsFragment with the passed Event and color.
     */
    public static EventDetailsFragment newInstance(Event event, int color) {
        EventDetailsFragment f = new EventDetailsFragment();

        Bundle args = new Bundle();
        args.putInt("color", color);
        f.setArguments(args);

        return f;
    }

    /**
     * Returns the Event for this EventDetailsView.
     * @return Event that was passed in when newInstance() was called.
     */
    public Event getEvent () {
        return getArguments().getParcelable("event");
    }

    /**
     * Returns the color used to draw this event.
     * @return Color of the event.
     */
    public int getColor () {
        return getArguments().getInt("color");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mEventDetailsFragView = inflater.inflate(R.layout.fragment_event_details, container, false);

        //Instantiate TextViews
        eventTitle = (TextView) mEventDetailsFragView.findViewById(R.id.event_title);
        eventTime = (TextView) mEventDetailsFragView.findViewById(R.id.details_time);
        eventLocation = (TextView) mEventDetailsFragView.findViewById(R.id.details_location);
        eventDescription = (TextView) mEventDetailsFragView.findViewById(R.id.details_description);
        eventHost = (TextView) mEventDetailsFragView.findViewById(R.id.details_host);

        //Instantiate color header block
        colorBlock = mEventDetailsFragView.findViewById(R.id.header_color_block);
        colorBlock.setBackgroundColor(getColor());

        //Add correct details to the details view.
        setEventDetails();

        //Hide toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return mEventDetailsFragView;
    }

    /**
     * Method to use the Event object to populate the view using the appropriate info.
     */
    public void setEventDetails() {
        Event event = getEvent();
        ArrayList<Location> locations = getLocations(event);
        String locationString = "";

        eventTitle.setText(event.getName());
        eventTime.setText(formatDate(event.getStartTime(), 1));
        eventDescription.setText(event.getInfo());

        if (locations.size() > 0) {
            for (Location loc : locations) {
                locationString += loc.getName() + " & ";
            }
            eventLocation.setText(locationString.substring(0, locationString.length()-2));
        } else
            eventLocation.setText("Unable to fetch location.");

        //Null pointer check for Sponsor. Null Sponsor == The MHacks Team is hosting the event.
        if (event.getUserId() != null)
            eventHost.setText(event.getUserId());
        else
            eventHost.setText("The MHacks Team <3");
    }

    /**
     * Takes the Date object and duration and formats them into a more common format.
     * After:   Sunday, Jan 18
     *          5:00-6:00
     * @param eventDate Date to format into a more readable date.
     * @param duration Duration of event in seconds.
     * @return Readable string from Date.toString().
     */
    public String formatDate (Date eventDate, int duration) {
        String finalDate, day, month;
        int date, year, startHour, startMinute, endHour, endMinute;

        //Create Calendar object.
        Calendar start = Calendar.getInstance();
        start.setTime(eventDate);

        //Create end event.
        Calendar end = (Calendar) start.clone();
        int hourDuration = duration / 3600;      //getDuration returns seconds as an int. Need to convert to hours.
        int minuteDuration = (duration % 3600) / 60;    //Converting remainder of minutes to int minutes.
        end.add(Calendar.HOUR, hourDuration);
        end.add(Calendar.MINUTE, minuteDuration);

        day = dayOfWeek[start.get(Calendar.DAY_OF_WEEK) - 1]; //Get day of week as s string.
        month = monthOfYear[start.get(Calendar.MONTH)]; //Get month as a string.
        date = start.get(Calendar.DATE); //Get the date.
        year = start.get(Calendar.YEAR); //Get year.
        if (start.get(Calendar.HOUR) == 0) //Starting hour.
            startHour = 12;
        else
            startHour = start.get(Calendar.HOUR);
        startMinute = start.get(Calendar.MINUTE); //Starting minutes.
        if (end.get(Calendar.HOUR) == 0) //Ending hour.
            endHour = 12;
        else
            endHour = end.get(Calendar.HOUR);
        endMinute = end.get(Calendar.MINUTE); //Ending minute.



        //Build string to be displayed.
        finalDate = day + ", " + month + " " + date + "\n" + startHour + ":" +
                    String.format("%02d", startMinute) + " - " + endHour + ":" +
                    String.format("%02d", endMinute);

        return finalDate;
    }

    /**
     * Gets all the locationIds for a given Event based on the objectId's of the location.
     * @param event Event object for which locationIds are to be queried.
     * @return ArrayList of Location objects for the given Event.
     */
    public ArrayList<Location> getLocations(Event event) {
        return new ArrayList<Location>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ActionBarActivity) getActivity()).getSupportActionBar().show();
    }
}
