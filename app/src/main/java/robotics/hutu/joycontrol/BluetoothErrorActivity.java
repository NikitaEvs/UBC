package robotics.hutu.joycontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Активность, которая вызывается при проблемах с включением Bluetooth
 */
public class BluetoothErrorActivity extends AppCompatActivity {
    private boolean flgBluetooth = false;
    BluetoothAdapter bluetoothAdapter;
    Intent intent;
    private final int BLUETOOTH_REQUEST = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_error);
        Button btnBTEnable = (Button)findViewById(R.id.btnBT);
        Button btnExit = (Button)findViewById(R.id.btnExit);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnBTEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }
    public void toMain(){
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void enableBluetooth(){
        Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BLUETOOTH_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case BLUETOOTH_REQUEST:
                if(resultCode == RESULT_OK){
                    Toast toast = Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
                    toast.show();
                    flgBluetooth = true;
                } else if(resultCode == RESULT_CANCELED) {
                    Toast toast = Toast.makeText(this, "Failed", Toast.LENGTH_SHORT);
                    toast.show();
                    flgBluetooth = false;
                }
                if (flgBluetooth){
                    toMain();
                }
                break;
        }
    }
    @Override
    public void onBackPressed(){

    }
}
