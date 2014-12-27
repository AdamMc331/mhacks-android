package com.mhacks.android.ui.nav;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.mhacks.android.data.model.Event;
import com.mhacks.android.ui.weekview.WeekViewModified;
import com.mhacks.iv.android.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Omkar Moghe on 10/25/2014.
 *
 * Builds schedule with events pulled from the Parse database. Uses the EventDetailsFragment to
 * create event details.
 */
public class ScheduleFragment extends Fragment implements WeekViewModified.EventClickListener,
                                                          WeekViewModified.EventLongPressListener,
                                                          WeekViewModified.MonthChangeListener {

    public static final String TAG = "ScheduleFragment";

    //Declaring Views
    private View             mScheduleFragView;
    private WeekViewModified mWeekView;
    private LinearLayout mScheduleContainer;

    //Parse user
    private ParseUser mUser;

    /*Calendar view uses WeekViewEvent objects to build the calendar. WeekViewEvent objects built
    using Event (ParseObject) pulled from the Parse database.*/
    private List<WeekViewEvent> finalWeekViewEvents;
    private List<Event> finalEvents;

    private boolean firstRun = true; //Sets up the WeekView on the initial start.
    private boolean eventDetailsOpen = false; //Prevents multiple EventDetailFragments from opening.

    //Declares the EventDetailsFragment
    private EventDetailsFragment eventDetailsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mScheduleFragView = inflater.inflate(R.layout.fragment_schedule, container, false);

        mUser = ParseUser.getCurrentUser();

        //Called initially to build the schedule view and query events. 1 == January.
        getEvents(1);

        return mScheduleFragView;
    }

    /**
     * Builds the calendar using the WeekViewModified class. Adds the view to the LinearLayout
     * mScheduleContainer. Done programmatically to prevent the WeekView from being created before
     * the Event query is finished. (Causes NullPointerException).
     */
    private void setUpWeekView() {
        //Instantiate LinearLayout
        mScheduleContainer = (LinearLayout) mScheduleFragView.findViewById(R.id.schedule_container);
        mWeekView = new WeekViewModified(getActivity());
        //Set listeners
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener((WeekViewModified.MonthChangeListener) this);
        mWeekView.setEventLongPressListener(this);
        //Set Jan 16th as "today"
        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, 2015);
        today.set(Calendar.MONTH, Calendar.JANUARY);
        today.set(Calendar.DATE, 16);
        mWeekView.setToday(today);
        //Set up visuals of the calendar
        mWeekView.setBackgroundColor(Color.WHITE);
        mWeekView.setEventTextColor(Color.BLACK);
        mWeekView.setNumberOfVisibleDays(3);
        mWeekView.setTextSize(22);
        mWeekView.setHourHeight(120);
        mWeekView.setHeaderColumnPadding(8);
        mWeekView.setHeaderColumnTextColor(getResources().getColor(R.color.header_column_text_color));
        mWeekView.setHeaderRowPadding(16);
        mWeekView.setColumnGap(8);
        mWeekView.setHourSeparatorColor(Color.WHITE);
        mWeekView.setHeaderColumnBackgroundColor(Color.WHITE);
        mWeekView.setHeaderRowBackgroundColor(getResources().getColor(R.color.header_row_bg_color));
        mWeekView.setDayBackgroundColor(getResources().getColor(R.color.day_bg_color));
        mWeekView.setTodayBackgroundColor(getResources().getColor(R.color.today_bg_color));
        mWeekView.setHeaderColumnBackgroundColor(Color.BLACK);
        //Prevent scrolling.
        mWeekView.setHorizontalScrollEnabled(false);
        //Add the view to the LinearLayout
        mScheduleContainer.addView(mWeekView);
    }

    /**
     * Queries Event objects from Parse and assigns the list to the global List<Event> events.
     * @param newMonth Month for which to get events.
     */
    public void getEvents(final int newMonth) {
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.include("category"); //Pulls EventType object.
        query.include("host"); //Pulls Sponsor object.
        query.include("location"); //Pulls Location JSON array.
        query.findInBackground(new FindCallback<Event>() {
            public void done(List<Event> eventList, ParseException e) {
                if (e == null) {
                    //Calls create events to build WeekViewEvent objects from Event objects.
                    createEvents(eventList, newMonth);
                } else {
                    Toast.makeText(getActivity(), "No events found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Uses a list of Event objects to build WeekViewEvent objects that the WeekView uses to draw
     * the calendar.
     * @param eventList List of Event objects
     * @param newMonth Month for which to get events.
     */
    public void createEvents(List<Event> eventList, int newMonth) {
        finalWeekViewEvents = new ArrayList<WeekViewEvent>();
        finalEvents = new ArrayList<Event>(eventList);

        //Id for the events. Doubles as the position of the event in both lists.
        long id = 0;

        //For each loop that builds WeekViewEvent objects from each Event object.
        for(Event event : eventList) {
            //Create start event.
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(event.getStartTime());

            //Create end event.
            Calendar endTime = (Calendar) startTime.clone();
            int hourDuration = event.getDuration() / 3600;      //getDuration returns seconds as an int. Need to convert to hours.
            int minuteDuration = event.getDuration() % 3600;    //Converting remainder of minutes to int minutes.
            endTime.add(Calendar.HOUR, hourDuration);
            endTime.add(Calendar.MINUTE, minuteDuration);

            //Set color based on EventType (Category).
            int color;
            switch (event.getCategory().getColor()) {
                case 0: //Tech Talk
                    color = getResources().getColor(R.color.dev_orange);
                    break;
                case 1: //Special Event
                    color = getResources().getColor(R.color.dev_blue);
                    break;
                case 2: //Food
                    color = getResources().getColor(R.color.dev_green);
                    break;
                case 3:
                    color = getResources().getColor(R.color.dev_red);
                    break;
                default:
                    color = getResources().getColor(R.color.dev_purple);
            }

            //Create a WeekViewEvent
            WeekViewEvent weekViewEvent = new WeekViewEvent(id, event.getTitle(), startTime, endTime);
            weekViewEvent.setColor(color);

            //Add the WeekViewEvent to the list.
            finalWeekViewEvents.add(weekViewEvent);

            //Increment the id
            id++;
        }
        //Sets boolean to true when all WeekViewEvent objects have been created.
        if (firstRun) {
            setUpWeekView();
            firstRun = false;
        }
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if (!eventDetailsOpen) {
            eventDetailsFragment =
                    EventDetailsFragment.newInstance(finalEvents.get((int) event.getId()), event.getColor());
            getActivity().getFragmentManager()
                         .beginTransaction()
                         .add(R.id.drawer_layout, eventDetailsFragment)
                         .addToBackStack(null) //IMPORTANT. Allows the EventDetailsFragment to be closed.
                         .commit();
            //Prevents other events from being clicked while one event's details are being shown.
            eventDetailsOpen = true;
        }
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        /*Checks to see if the month being called is January (1). Not the best way to handle it but
        it works for this case since we only care about 3 days (Jan 16-18, 2015).*/
        if (newMonth == 1) {
            getEvents(newMonth);
            return finalWeekViewEvents;
        } else {
            /*onMonthChange is called 3 times by the view when it is created. This results in
            duplicate events being created. Thus, I return an empty ArrayList of WeekViewEvent
             objects if the month is not 1 (January).*/
            return new ArrayList<WeekViewEvent>();
        }
    }

    /**
     * Gets clicked view in the ScheduleFragment and the EventDetailsFragment and handles them
     * appropriately.
     * @param v View that was clicked
     */
    public void scheduleFragmentClick(View v) {
        //Swtich the id of the clicked view.
        switch (v.getId()) {
            case R.id.event_close_button:
                //Close the EventDetailsFragment
                getActivity().getFragmentManager().beginTransaction().remove(eventDetailsFragment).commit();
                eventDetailsOpen = false;
                break;
        }
    }
}
