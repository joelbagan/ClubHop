package cs48.ucsb.edu.clubhop.MapsActivity;

import com.google.android.gms.maps.model.Marker;

import cs48.ucsb.edu.clubhop.Facebook.FacebookEvent;

/**
 * Created by Joel on 2/17/2017.
 */

public class InfoWindowConfigurator {
    public void config(Marker marker) {
        FacebookEvent event = (FacebookEvent) marker.getTag();
        marker.setTitle(event.getTitle());
        marker.setSnippet(event.getDescription());
    }
}
