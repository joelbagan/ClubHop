package cs48.ucsb.edu.clubhop.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cs48.ucsb.edu.clubhop.R;

/**
 * @author Bryanna P.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView standardButton = (ImageView) findViewById(R.id.standardButton);
        standardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel.getInstance().setCurrentStyleID(R.raw.standard_style);
                Toast.makeText(SettingsActivity.this,
                        "Map style successfully changed to Standard",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ImageView nightButton = (ImageView) findViewById(R.id.nightButton);
        nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel.getInstance().setCurrentStyleID(R.raw.night_style);
                Toast.makeText(SettingsActivity.this,
                        "Map style successfully changed to Night",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ImageView retroButton = (ImageView) findViewById(R.id.retroButton);
        retroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel.getInstance().setCurrentStyleID(R.raw.retro_style);
                Toast.makeText(SettingsActivity.this,
                        "Map style successfully changed to Retro",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ImageView aubButton = (ImageView) findViewById(R.id.aubButton);
        aubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel.getInstance().setCurrentStyleID(R.raw.aubergine_style);
                Toast.makeText(SettingsActivity.this,
                        "Map style successfully changed to Aubergine",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*
        SettingsModel.getInstance().addListener(new SettingsModelListener() {
            @Override
            public void onStyleChange() {

            }
        });
        */
    }
}
