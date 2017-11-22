package cs48.ucsb.edu.clubhop.Handlers;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import cs48.ucsb.edu.clubhop.User.FacebookEvent;
import cs48.ucsb.edu.clubhop.FilterCriteria.Criteria;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaAttending;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaCommunity;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaGroup;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaMaybe;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaNotReplied;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaPrivate;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaPublic;
import cs48.ucsb.edu.clubhop.FilterCriteria.CriteriaToday;
import cs48.ucsb.edu.clubhop.User.UserEventsModel;


/**
 *
 * @author Joel
 * @author Sean
 */
public class FilterHandler {

    public void setUp(Spinner filterMenu, final Context context, final GoogleMap map) {
        filterMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String filter = parentView.getItemAtPosition(position).toString();
                UserEventsModel model = UserEventsModel.getInstance();
                ArrayList<FacebookEvent> newEvents;
                switch (filter) {
                    case "All":
                        model.initializeMarkers(map);
                        Toast.makeText(context, "Showing all events", Toast.LENGTH_SHORT).show();
                        break;
                    case "Today":
                        Criteria todayEvents = new CriteriaToday();
                        newEvents = todayEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing events happening today", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Public":
                        Criteria publicEvents = new CriteriaPublic();
                        newEvents = publicEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing public events", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Private":
                        Criteria privateEvents = new CriteriaPrivate();
                        newEvents = privateEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing private events", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Community":
                        Criteria communityEvents = new CriteriaCommunity();
                        newEvents = communityEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing community events", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Group":
                        Criteria groupEvents = new CriteriaGroup();
                        newEvents = groupEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing group events", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Attending":
                        Criteria attendingEvents = new CriteriaAttending();
                        newEvents = attendingEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing events you are attending", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Maybe":
                        Criteria maybeEvents = new CriteriaMaybe();
                        newEvents = maybeEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing events you responded 'maybe' to", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case "Not Replied":
                        Criteria nRepEvents = new CriteriaNotReplied();
                        newEvents = nRepEvents.meetCriteria(model.getEvents());
                        if (newEvents.isEmpty()) {
                            Toast.makeText(context, "No events fit those criteria", Toast.LENGTH_SHORT).show();
                            break;
                        } else{
                            model.generateTheseMarkers(map, newEvents);
                            Toast.makeText(context, "Showing events you have not replied to", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    default:
                        Toast.makeText(context, "Invalid Filter", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }
}
