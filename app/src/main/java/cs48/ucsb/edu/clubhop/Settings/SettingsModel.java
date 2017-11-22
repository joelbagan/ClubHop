package cs48.ucsb.edu.clubhop.Settings;

import java.util.ArrayList;

import cs48.ucsb.edu.clubhop.R;

/**
 *  * Created by bryannaphan on 2/23/17.
 *  A class that holds all of the Settings chosen by a user.
 *
 *  This class is implemented as a singleton with a static instance, allowing
 *  any of the other classes in the project to access the Model's data.
 *
 *  @author Bryanna P.
 */

public class SettingsModel {
    private int currentStyleID;

    /**
     * A list of the listeners that are subscribed to the SettingsModel.
     */
    private ArrayList<SettingsModelListener> listeners;

    /**
     * The singleton instance of the model.
     */
    private static SettingsModel instance;

    /**
     * Private constructor that initializes listeners and events to be default ArrayLists.
     */
    private SettingsModel() {
        listeners = new ArrayList<>();
        currentStyleID = R.raw.standard_style;
        //styleIDs = new ArrayList<>();
        //styleType = "";
    }

    /**
     * @return If the instance is not created yet, it will be created.
     * Else, it returns the instance of the SettingsModel.
     */
    public static SettingsModel getInstance() {
        if (instance == null)
            instance = new SettingsModel();
        return instance;
    }

    /**
     * Subscribes a listener to this Model, which will be notified when the Model changes.
     *
     * @param listener A new listener that would like to subscribe to the model.
     */
    public void addListener(SettingsModelListener listener) {
        this.listeners.add(listener);
    }

    /*
    public void setMapsActivity(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }
    */

    public int getCurrentStyleID() {
        return currentStyleID;
    }

    // Preconditions: styleID pertains to a Google maps style ID
    // Postconditions: the current settings will change the map to the style passed in
    public void setCurrentStyleID(int styleID) {
        currentStyleID = styleID;
        notifyListeners();
    }

    /**
     * Tells all of the subscribed listeners that the model has changed.
     */
    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onStyleChange();
        }
    }

}
