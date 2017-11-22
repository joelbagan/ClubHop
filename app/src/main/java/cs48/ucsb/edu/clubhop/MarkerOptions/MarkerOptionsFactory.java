package cs48.ucsb.edu.clubhop.MarkerOptions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cs48.ucsb.edu.clubhop.User.FacebookEvent;

/**
 * Created by Joel on 2/17/2017.
 *
 * @author Joel
 */

public class MarkerOptionsFactory {

    public MarkerOptions getOptions(FacebookEvent event) {
        String eventType = event.getType();
        switch (eventType) {
            case "public":
                return new PublicMarkerOptions().generate(event);
            case "private":
                return new PrivateMarkerOptions().generate(event);
            case "community":
                return new CommunityMarkerOptions().generate(event);
            case "group":
                return new GroupMarkerOptions().generate(event);
            default:
                return new MarkerOptions().position(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
        }
    }
}
