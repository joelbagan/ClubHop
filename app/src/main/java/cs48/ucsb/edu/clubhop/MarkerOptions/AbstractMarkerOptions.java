package cs48.ucsb.edu.clubhop.MarkerOptions;

import com.google.android.gms.maps.model.MarkerOptions;

import cs48.ucsb.edu.clubhop.User.FacebookEvent;

/**
 * This is an abstract class for making different MarkerOptions
 * for the different type of events. The generate() function takes
 * a LatLng to update the position field as well, since markers
 * are often created and added to the map at the same time
 *
 * @author Joel
 */
public abstract class AbstractMarkerOptions {
    abstract MarkerOptions generate(FacebookEvent event);
}
