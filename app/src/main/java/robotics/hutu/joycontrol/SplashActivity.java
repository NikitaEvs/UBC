package robotics.hutu.joycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import robotics.hutu.joycontrol.ProfilesActivity;

/**
 * Активность для заставки
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ProfilesActivity.class);
        startActivity(intent);
        finish();
    }
}
