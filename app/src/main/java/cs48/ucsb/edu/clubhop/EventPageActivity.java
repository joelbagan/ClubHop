package cs48.ucsb.edu.clubhop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;


/**
 *
 * @author Joel B.
 * @author Bryanna P.
 */
public class EventPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        new DownloadImageTask((ImageView) findViewById(R.id.banner_img_view)).execute(getIntent().getExtras().getString("PictureURL"));

        //TextView monthView = (TextView) findViewById(R.id.month_view);
        //monthView.setText(getIntent().getExtras().getString("Month"));
        //TextView dateView = (TextView) findViewById(R.id.date_view);
//      dateView.setText(getIntent().getExtras().getInt("Date"));
        TextView titleView = (TextView) findViewById(R.id.title_view);
        titleView.setText(getIntent().getExtras().getString("Title"));
        TextView timeView = (TextView) findViewById(R.id.time_view);
        timeView.setText(getIntent().getExtras().getString("Time"));
        TextView statusView = (TextView) findViewById(R.id.status_view);
        statusView.setText(getIntent().getExtras().getString("EventType"));
        TextView locationView = (TextView) findViewById(R.id.location_view);
        locationView.setText(getIntent().getExtras().getString("Location"));
        TextView descView = (TextView) findViewById(R.id.desc_view);
        descView.setText(getIntent().getExtras().getString("Description"));
        descView.setMovementMethod(new ScrollingMovementMethod());
    }

    //inner class for Async image download
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
