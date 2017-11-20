package cs48.ucsb.edu.clubhop.MapsActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import cs48.ucsb.edu.clubhop.Bundlers.EventPageBundler;
import cs48.ucsb.edu.clubhop.EventPageActivity;
import cs48.ucsb.edu.clubhop.Handlers.FilterHandler;
import cs48.ucsb.edu.clubhop.Handlers.MarkerHandler;
import cs48.ucsb.edu.clubhop.LoginActivity;
import cs48.ucsb.edu.clubhop.R;
import cs48.ucsb.edu.clubhop.Settings.SettingsActivity;
import cs48.ucsb.edu.clubhop.Settings.SettingsModel;
import cs48.ucsb.edu.clubhop.Settings.SettingsModelListener;
import cs48.ucsb.edu.clubhop.User.UserEventsModel;
import cs48.ucsb.edu.clubhop.User.UserEventsModelListener;

import static cs48.ucsb.edu.clubhop.R.id.map;

// Navigation

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    private boolean receivedEvents = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private MapsActivity mapsActivityInstance = this;

    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;

    protected Location mLastLocation;
    private boolean locRetreived = false;

    private FilterHandler filterHandler;
    private MarkerHandler markerHandler;

    // Navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Spinner(filter menu)
        filterHandler = new FilterHandler();


        markerHandler = new MarkerHandler();

        // Setting up the UserEventsModel listener
        UserEventsModel.getInstance().addListener(new UserEventsModelListener() {
            @Override
            public void onEventsCreated() {
                receivedEvents = true;
                if (mMap != null) {
                    UserEventsModel.getInstance().initializeMarkers(mMap);
                    //setUpMap();
                }
            }

            @Override
            public void onMarkersCreated() {
                Toast.makeText(MapsActivity.this, "Markers generated successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserChanged() {
                // TODO: get user name printed;
                // right now it gets the name, but won't put it onto the nav drawer layout for some reason

                // Do stuff to get user name for nav bar
                //NavigationView navHeaderView = (NavigationView) navigationView.inflateHeaderView(R.layout.navigation_drawer_header);
                NavigationView navHeaderView = (NavigationView) navigationView.findViewById(R.id.navigation_view);
                View headerView = navHeaderView.getHeaderView(0);
                TextView userNameView = (TextView) headerView.findViewById(R.id.user_name);
                String userName = UserEventsModel.getInstance().getUser().getName();
                userNameView.setText(userName);
            }

        });

        // Setting up the SettingsModel listener
        SettingsModel.getInstance().addListener(new SettingsModelListener() {
            @Override
            public void onStyleChange() {
                int settingStyleID = SettingsModel.getInstance().getCurrentStyleID();
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mapsActivityInstance, settingStyleID));
            }
        });

        // Navigation
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Create click listener for navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) { // get from drawer_menu.xml
                    case R.id.home_id:
                        handleNewLocation(mLastLocation);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.settings_id:
                        Intent settingsIntent = new Intent(MapsActivity.this, SettingsActivity.class);
                        MapsActivity.this.startActivity(settingsIntent);
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.logout_id:
                        // TODO: 3/3/2017 this
                        // will do something with onActivityResult in LoginActivity to keep
                        // Facebook stuff out of here111
                        if (AccessToken.getCurrentAccessToken() != null)
                            LoginManager.getInstance().logOut();
                        Intent logoutIntent = new Intent(MapsActivity.this, LoginActivity.class);
                        MapsActivity.this.startActivity(logoutIntent);
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        Toast.makeText(MapsActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });
    }

    private void setUpMap() {
        UserEventsModel model = UserEventsModel.getInstance();
        for (int i = 0; i < model.getSize(); ++i) {
            markerHandler.create(mMap, model.getEvent(i));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleApiClient.connect();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(MapsActivity.this, "location permission not granted", Toast.LENGTH_SHORT).show();
        }

        Spinner filterMenu = (Spinner) findViewById(R.id.spinner);
        filterHandler.setUp(filterMenu, getBaseContext(), mMap);
        //Example markers
        /*
        Marker privateEx = mMap.addMarker(new PrivateMarkerOptions()
                .generate(new LatLng(34.412723, -119.861915)));

        Marker publicEx = mMap.addMarker(new PublicMarkerOptions()
                .generate(new LatLng(34.411271, -119.856207)));

        Marker commEx = mMap.addMarker(new CommunityMarkerOptions()
                .generate(new LatLng(34.413122, -119.857826)));

        Marker groupEx = mMap.addMarker(new GroupMarkerOptions()
                .generate(new LatLng(34.413686, -119.859485)));
        */
        if (receivedEvents) {
            setUpMap();
            NavigationView navHeaderView = (NavigationView) navigationView.findViewById(R.id.navigation_view);
            View headerView = navHeaderView.getHeaderView(0);
            TextView userNameView = (TextView) headerView.findViewById(R.id.user_name);
            String userName = UserEventsModel.getInstance().getUser().getName();
            userNameView.setText(userName);
        }

    }

    @Override
    protected void onResume() {
        if (!locRetreived) mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStart() {
        if (!locRetreived) mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Location services suspended. Please reconnect.");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            LocationRequest mLocationRequest = new LocationRequest().create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(10 * 1000) //10 seconds in ms
                    .setFastestInterval(1 * 1000); //1 seconds, in ms
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            locRetreived = true;
            handleNewLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //mGoogleApiClient.disconnect();
        locRetreived = true;
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(10)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Bundle bundle = new EventPageBundler().makeBundle(marker); // calling makebundle on certain events crashes the app ( RECcess Event )

        Intent eventPageIntent = new Intent(this, EventPageActivity.class);
        eventPageIntent.putExtras(bundle);
        startActivity(eventPageIntent, bundle);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        new InfoWindowConfigurator().config(marker);

        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
