package cs48.ucsb.edu.clubhop.MarkerOptions;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cs48.ucsb.edu.clubhop.User.FacebookEvent;

/**
 * Created by Joel on 2/17/2017.
 *
 * @author Joel
 */

public class PublicMarkerOptions extends AbstractMarkerOptions {

    @Override
    MarkerOptions generate(FacebookEvent event) {
        return new MarkerOptions()
                .position(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    }
}
