package robotics.hutu.joycontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Главная активность с созданием необходимых потоков
 */
public class MainActivity extends AppCompatActivity {

    final int BLUETOOTH_REQUEST = 5;
    private boolean flgBluetooth = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothThread bluetoothThread;
    private List<String> profileName = new ArrayList<>();
    private Handler handler;
    private String MACaddress;
    private String nameCustom = null;
    private long profileID;
    private EditText editName;
    private ProfileDao profileDao;
    private Query<Profile> profileQuery;
    private List<Profile> profiles = new ArrayList<>();
    Profile profile;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Полноэкранный режим */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;

        /**
         * Для теста
         */
        DaoSession daoSession = ((App)getApplication()).getDaoSession();
        ControllerDao controllerDao = daoSession.getControllerDao();
        /*DaoMaster.dropAllTables(daoSession.getDatabase(), true);
        DaoMaster.createAllTables(daoSession.getDatabase(), true);
        Controller test1 = new Controller(2, "TWO", 600, 600, 300);
        controllerDao.insert(test1);
        Controller test = new Controller(1, "ONE", 300, 300, 200);
        controllerDao.insert(test);
        Controller test2 = new Controller(3, "Three", 900, 900, 200);
        controllerDao.insert(test2);
        Controller test3 = new Controller(4, "Four", 1300, 300, 200);
        controllerDao.insert(test3);*/
        Intent intent = getIntent();
        profileID = Integer.parseInt(intent.getStringExtra("ID"));
        setDatabase();
        try{
            nameCustom = profile.getMACaddress();
            Log.d("Name", nameCustom);
            if(nameCustom.equals("default")){
                MACaddress = null;
            }
        } catch (NullPointerException e){
            Log.d("Name","Error");
        }
        try{
            String name = getIntent().getStringExtra("MAC_ADDRESS");
            if(name != null){
                nameCustom = name;
            }
            Log.d("Give", nameCustom+"kek");
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Give", "Error");
        }
        Log.d("Name", nameCustom);
        /*
        Controller test1 = new Controller(1, "TWO", 600, 600, 200);
        controllerDao.insert(test1);
        Controller test2 = new Controller(1, "K", 900, 900, 150);
        controllerDao.insert(test2);*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                String toastOut = bundle.getString("Info");
                Toast.makeText(context, toastOut, Toast.LENGTH_SHORT).show();
            }
        };
        if(!bluetoothAdapter.isEnabled()){
            enableBluetooth();
        } else {
            flgBluetooth = true;
        }
        new Logger(context, "Info").log(flgBluetooth+"");
        if(flgBluetooth){
            BTStart();
        }
    }

    /**
     * Функция для включения и настройки потоков управления Bluetooth
     */
    public void enableBluetooth(){
        flgBluetooth = false;
        Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BLUETOOTH_REQUEST);
    }
    public void BTStart(){
        if(nameCustom != null){
            bluetoothThread = new BluetoothThread(context, this, nameCustom, handler, profileID, true);
            bluetoothThread.start();
        } else if((MACaddress == null) || (nameCustom.equals("default"))){
            bluetoothThread = new BluetoothThread(context, this, handler, profileID);
            bluetoothThread.start();
        } else {
            bluetoothThread = new BluetoothThread(context, this, MACaddress, handler, profileID);
            bluetoothThread.start();
        }
        JoystickView joystickView = new JoystickView(this, bluetoothThread, profileID);
        LinearLayout surface = (LinearLayout)findViewById(R.id.joySurface);
        surface.addView(joystickView);
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            MACaddress = getIntent().getStringExtra("MAC_ADDRESS");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case BLUETOOTH_REQUEST:
                if(resultCode == RESULT_OK){
                    Toast toast = Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
                    toast.show();
                    BTStart();
                    flgBluetooth = true;
                } else if(resultCode == RESULT_CANCELED) {
                    Toast toast = Toast.makeText(this, "Failed", Toast.LENGTH_SHORT);
                    toast.show();
                    flgBluetooth = false;
                    Intent intent = new Intent(this, BluetoothErrorActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
    public void destroy(){
        onDestroy();
    }
    public void toSRCHError(){
        Intent intent = new Intent(this, SearchErrorActivity.class);
        startActivity(intent);
    }

    /**
     * Настройка БД
     */
    public void setDatabase() {
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        profile = daoSession.getProfileDao().load(profileID);
    }
    public void getProfileName(){
        for(Profile profile:profiles){
            if(profileName.indexOf(profile.getName()) == -1){
                profileName.add(profile.getName());
            }
        }
    }
}
