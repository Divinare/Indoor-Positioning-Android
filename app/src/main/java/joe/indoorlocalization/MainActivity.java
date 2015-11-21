package joe.indoorlocalization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "The app is running :)))");
        final Intent intent = new Intent(this, CalibrationActivity.class);


        Button btnSaveScan = (Button)(findViewById(R.id.btnMenuCalibrate));
        btnSaveScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                Log.i(TAG, "Button presseeed");
            }
        });

    }
}
