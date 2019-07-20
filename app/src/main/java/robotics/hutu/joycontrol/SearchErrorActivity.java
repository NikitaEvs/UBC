package robotics.hutu.joycontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import robotics.hutu.joycontrol.MainActivity;
import robotics.hutu.joycontrol.R;

/**
 * Активность, которая вызывается при проблемах с поиском необходимого контроллера, предлагает поискать ещё или сменить имя
 */
public class SearchErrorActivity extends AppCompatActivity {
    private long profileID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_error);
        Button btnAgain = (Button) findViewById(R.id.btnSRCHAgain);
        Button btnAddress = (Button) findViewById(R.id.btnSRCHAddress);
        final EditText editAddress = (EditText) findViewById(R.id.editAddress);
        Intent intent = getIntent();
        profileID = Long.parseLong(intent.getStringExtra("ID"));
        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MACaddress = editAddress.getText().toString();
                toMain(MACaddress);
            }
        });
    }
    public void toMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ID", profileID+"");
        startActivity(intent);
    }
    public void toMain(String MACaddress){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ID", profileID+"");
        intent.putExtra("MAC_ADDRESS", MACaddress+"");
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){

    }
}
