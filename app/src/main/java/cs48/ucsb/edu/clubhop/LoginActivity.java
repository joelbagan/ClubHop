package cs48.ucsb.edu.clubhop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs48.ucsb.edu.clubhop.MapsActivity.MapsActivity;
import cs48.ucsb.edu.clubhop.User.UserEventsModel;

/**
 * Activity that handles the user's login. First activity to occur when the app launches.
 * @author Sean S.
 * @author Patrick V.
 * @author Minnie Y.
 */
public class LoginActivity extends AppCompatActivity {

	/**
	 * Button that the user interacts with in order to log into Facebook.
	 */
    LoginButton loginButton;

	/**
	 * The JSONArray of Events that come in from the graph request.	
	 */
	// Maybe change into just a local variable in the onSuccess() method?
    JSONArray content;

	/**
	 * The TextView that welcomes the user. Also tells the user if there was a problem during login.
	 */
    TextView textView;


    CallbackManager callbackManager;

	final String READ_PERMISSIONS = "user_events";

	// For REQUESTED_FIELDS, always make sure that "events" is at the end so that
	// CLOSED_FIELDS will always pertain to "events" in TOTAL_FIELDS
	final String REQUESTED_FIELDS = "name,events";
	final String SPECIFIC_FIELDS = "id,name,description,type,picture,place,start_time,end_time,rsvp_status";
	final String CLOSED_FIELDS = "{" + SPECIFIC_FIELDS + "}";
	//final String CLOSED_FIELDS = "{" + CLOSED_FIELDS + "}";
	final String TOTAL_FIELDS = REQUESTED_FIELDS + CLOSED_FIELDS;

	//ArrayList<GraphRequest> allRequests = new ArrayList<>(); //what if i need to make multiple requests

    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

	/**
	 * Method that handles the creation of the entire LoginActivity. Within this method, we
	 * create the login button and handle the login, handle permissions, and make the graph request.
	 */
	// There seems to be a lot of responsibilities in this one function, might want to separate this
	// into many functions?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        final Intent intent = new Intent(this, MapsActivity.class);
        textView = (TextView) findViewById(R.id.textView);

		AccessToken currentToken = AccessToken.getCurrentAccessToken();
		
		// btw we need to handle the case of the user not giving us permission to see events,
		// but still being logged in

		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(READ_PERMISSIONS);
		callbackManager = CallbackManager.Factory.create();
		checkPermission();
		setupLoginButton(loginButton, callbackManager, READ_PERMISSIONS, TOTAL_FIELDS, intent);

		if (!(currentToken == null || currentToken.getPermissions() == null
				|| currentToken.isExpired() && savedInstanceState == null)) {

			// do request
			// TODO: 3/3/2017 abstract into method
			/*
			handleEventsRequest(currentToken, TOTAL_FIELDS);
			handleNotRepliedEventsRequest(currentToken, SPECIFIC_FIELDS);
			*/
			handleUserLoggedIn(intent, currentToken);

		}

    }

	private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

	public void setupLoginButton(LoginButton loginButton, CallbackManager callbackManager,
                                 String readPermissions, final String requestedFields, final Intent intent) {

				loginButton.setReadPermissions(readPermissions);
				loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {

						final AccessToken accessToken = AccessToken.getCurrentAccessToken();

						handleUserLoggedIn(intent, accessToken);
					}

					@Override
					public void onCancel() {
						textView.setText("Login Cancelled");
					}

					@Override
					public void onError(FacebookException error) {
						textView.setText("Login Error");
					}
				});

	}

	public GraphRequest handleEventsRequest(AccessToken accessToken, String desiredFields) {
		GraphRequest request = GraphRequest.newMeRequest(
				accessToken,
				new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(JSONObject object, GraphResponse response) {
						try {
							UserEventsModel.getInstance().setUser(response.getJSONObject());
							JSONArray eventsJSONArray = response.getJSONObject().getJSONObject("events").getJSONArray("data");
							UserEventsModel.getInstance().addEvents(eventsJSONArray);
							//handleJSONArray(eventsJSONArray);
						} catch (JSONException e) {
							e.printStackTrace();
							textView.setText("Failed to get replied events");
						}
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", desiredFields);
		request.setParameters(parameters);
		request.executeAsync();  // dont wanna hang the main thread now do we
		return request;
	}

	public GraphRequest handleNotRepliedEventsRequest(AccessToken accessToken, String desiredFields) {
		GraphRequest request = GraphRequest.newGraphPathRequest(
				accessToken,
				"/me/events/not_replied",
				new GraphRequest.Callback() {
					@Override
					public void onCompleted(GraphResponse response) {
						try {
							JSONArray eventsJSONArray = response.getJSONObject().getJSONArray("data");
							UserEventsModel.getInstance().addEvents(eventsJSONArray);
						} catch (JSONException e) {
							e.printStackTrace();
							textView.setText("Failed to get not replied events");
						}
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", desiredFields);
		request.setParameters(parameters);
		request.executeAsync();
		return request;
	}

	/*
	public void launchRequest(GraphRequest request, String desiredFields) {
			Bundle parameters = new Bundle();
			parameters.putString("fields", desiredFields);
			request.setParameters(parameters);
			request.executeAsync();
	}
	*/

	/*public void testButton(){
		handleUserLoggedIn(new Intent(this, MapsActivity.class), AccessToken.getCurrentAccessToken(), TOTAL_FIELDS);
	}*/

	private void handleUserLoggedIn(Intent intent, AccessToken currentToken) {
		handleEventsRequest(currentToken, TOTAL_FIELDS);
		handleNotRepliedEventsRequest(currentToken, SPECIFIC_FIELDS);
		/*
		allRequests.add( handleEventsRequest(currentToken) );
		allRequests.add( handleNotRepliedEventsRequest(currentToken) );
		*/
		/*
		for (int i=0; i < allRequests.size(); ++i)
			launchRequest( allRequests.get(i), total_fields );
		*/
		startActivity(intent);
	}

	/*
	public void addThisToModel(JSONArray events) {

		UserEventsModel.getInstance().addEvents(events);

	}
	*/

}
