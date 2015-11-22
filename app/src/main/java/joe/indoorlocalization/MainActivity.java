package joe.indoorlocalization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static String TAG = "MainActivity";
   // final Intent intentSetPosition = new Intent(this, CalibrationActivity.class);
   // final Intent intentHelp = new Intent(this, CalibrationActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "The app is running :)))");


        final Intent intentCalibrate = new Intent(this, CalibrationActivity.class);

        Button btnSaveScan = (Button)(findViewById(R.id.btnMenuCalibrate));
        btnSaveScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentCalibrate);
                Log.i(TAG, "Button presseeed");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        final Intent intentLocate = new Intent(this, LocateActivity.class);
        final Intent intentCalibrate = new Intent(this, CalibrationActivity.class);


        int id = item.getItemId();
        if (id == R.id.menu_locate) {
            startActivity(intentLocate);
        } else if(id == R.id.menu_calibrate) {
            startActivity(intentCalibrate);
        } else if(id == R.id.menu_setPosition) {
            return true;
        } else if(id == R.id.menu_help) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
