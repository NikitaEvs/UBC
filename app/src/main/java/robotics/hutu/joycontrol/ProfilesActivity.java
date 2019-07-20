package robotics.hutu.joycontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность с профилями, с вохможностью перехода на их запуск или изменения
 */
public class ProfilesActivity extends AppCompatActivity {
    private ListView listProfile;
    private Button btnAdd;
    private ArrayAdapter<String> arrayAdapterProfile;
    private List<Profile> profiles = new ArrayList<>();
    private List<String> profileName = new ArrayList<>();
    private EditText addProfile;
    private ProfileDao profileDao;
    private Query<Profile> profileQuery;
    private EditText editName;
    private String MACaddress = null;
    private DaoSession daoSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Полноэкранный режим */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profiles);
        listProfile = (ListView) findViewById(R.id.listProfiles);
        setDatabase();
        try{
            arrayAdapterProfile = new ArrayAdapter<>(this, R.layout.list_item_profiles, profileName);
            listProfile.setAdapter(arrayAdapterProfile);
        } catch (NullPointerException e){}
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO добавить добавление профиля
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilesActivity.this);
                LayoutInflater inflater = ProfilesActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_profile, null);
                alertDialog.setView(dialogView);
                String pos = getResources().getString(R.string.Input);
                addProfile = (EditText) dialogView.findViewById(R.id.addProfile);
                alertDialog.setPositiveButton(pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Profile profile = new Profile();
                        profile.setName(addProfile.getText().toString());
                        profileDao.insert(profile);
                        setDatabase();
                        arrayAdapterProfile.notifyDataSetChanged();
                    }
                });
                alertDialog.show();
            }
        });
        listProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //TODO добавить запуск/изменение
                String title = profiles.get(position).getName();
                String msg = getResources().getString(R.string.Choice);
                String PosString = getResources().getString(R.string.Launch);
                String NegString = getResources().getString(R.string.Change);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilesActivity.this);
                alertDialog.setTitle(title);
                alertDialog.setMessage(msg);
                alertDialog.setPositiveButton(PosString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO запуск профиля
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilesActivity.this);
                        LayoutInflater inflater = ProfilesActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_set_mode, null);
                        alertDialog.setView(dialogView);
                        String pos = getResources().getString(R.string.Input);
                        String neg = getResources().getString(R.string.useDefault);
                        editName = (EditText) dialogView.findViewById(R.id.setName);
                        alertDialog.setPositiveButton(pos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MACaddress = editName.getText().toString();
                                Intent intent = new Intent(ProfilesActivity.this, MainActivity.class);
                                profiles.get(position).setMACaddress(MACaddress);
                                intent.putExtra("ID", profiles.get(position).getID()+"");
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton(neg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ProfilesActivity.this, MainActivity.class);
                                MACaddress = "default";
                                profiles.get(position).setMACaddress(MACaddress);
                                intent.putExtra("ID", profiles.get(position).getID()+"");
                                startActivity(intent);
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                });
                alertDialog.setNegativeButton(NegString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO редактирование профиля
                        Intent intent = new Intent(ProfilesActivity.this, ProfileEditActivity.class);
                        intent.putExtra("id", profiles.get(position).getID()+"");
                        startActivity(intent);
                    }
                });
                if(profiles.get(position).getMACaddress() == null){
                    alertDialog.show();
                }
            }
        });
        listProfile.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //TODO добавить
                String title = profiles.get(position).getName();
                String msg = getResources().getString(R.string.Confirm);
                String PosString = getResources().getString(R.string.ConfirmPositive);
                String NegString = getResources().getString(R.string.ConfirmNegative);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilesActivity.this);
                alertDialog.setTitle(title);
                alertDialog.setMessage(msg);
                alertDialog.setPositiveButton(PosString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        profileDao.delete(profiles.get(position));
                        profileName.remove(profiles.get(position).getName());
                        List<Controller> list = profiles.get(position).getControllers();
                        ControllerDao controllerDao = daoSession.getControllerDao();
                        for(Controller controller:list){
                            controllerDao.delete(controller);
                        }
                        setDatabase();
                        arrayAdapterProfile.notifyDataSetChanged();
                    }
                });
                alertDialog.setNegativeButton(NegString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }
    public void setDatabase() {
        daoSession = ((App) getApplication()).getDaoSession();
        /**
         * Для теста
         */
        //DaoMaster.dropAllTables(daoSession.getDatabase(), true);
        //DaoMaster.createAllTables(daoSession.getDatabase(), true);
        /**
         * TODO не забыть убрать
         */
        profileDao = daoSession.getProfileDao();
        profileQuery = profileDao.queryBuilder().orderAsc(ProfileDao.Properties.ID).build();
        profiles = profileQuery.list();
        if(profiles.size()>0){
            getProfileName();
        }
    }
    public void getProfileName(){
        for(Profile profile:profiles){
            if(profileName.indexOf(profile.getName()) == -1){
                profileName.add(profile.getName());
            }
        }
    }
}
