package robotics.hutu.joycontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.List;

/**
 * Активность редактирования профиля, добавления контроллеров, редактирования контроллеров
 */
public class ProfileEditActivity extends AppCompatActivity  {
    long id;
    private DaoSession daoSession;
    private ControllerDao controllerDao;
    private ProfileDao profileDao;
    private Profile thisProfile;
    private List<Controller> thisControllers;
    private Button btnAdd;
    private Button btnEdit;
    private EditText editName;
    private SeekBar seekBar;
    private Spinner spinner;
    private Controller thisController;
    private SupportView supportView;
    private int indexControllerOnTouch;
    private int choosenIndex = -1;
    private boolean inTouch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Полноэкранный режим */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_edit);
        Intent intent = getIntent();
        id = Long.parseLong(intent.getStringExtra("id"));
        LinearLayout supportSurface = (LinearLayout) findViewById(R.id.supportSurface);
        supportView = new SupportView(this, id);
        supportSurface.addView(supportView);
        btnAdd = (Button) findViewById(R.id.addController);
        btnEdit = (Button) findViewById(R.id.editController);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addController();
                Log.d("btn", "add");
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editController();
                Log.d("btn", "edit");
            }
        });
        setDatabase();
    }
/*
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        float newOX = e.getX();
        float newOY = e.getY();
        if (!inTouch) {
            for (Controller controller : thisControllers) {
                if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius())) {
                    inTouch = true;
                    indexControllerOnTouch = thisControllers.indexOf(controller);
                    break;
                }
            }
        } else {
            if (checkPosition(newOX, newOY)) {
                thisControllers.get(indexControllerOnTouch).setCentreOX(newOX);
                thisControllers.get(indexControllerOnTouch).setCentreOY(newOY);
            } else {
                Toast.makeText(this, "Расположите дальше от других элементов", Toast.LENGTH_SHORT).show();
            }

        }
        return true;
    }
*/

    /**
     * Настройка БД
     */
    public void setDatabase() {
        daoSession = ((App) getApplication()).getDaoSession();
        /**
         * Для теста
         */
        //DaoMaster.dropAllTables(daoSession.getDatabase(), true);
        //DaoMaster.createAllTables(daoSession.getDatabase(), true);
        controllerDao = daoSession.getControllerDao();
        profileDao = daoSession.getProfileDao();
        thisProfile = profileDao.load(id);
        thisControllers = thisProfile.getControllers();
    }

    /**
     * Функция добавления контроллера
     */
    public void addController() {
        //TODO Сделать добавление элемента
        thisController = new Controller();
        choosenIndex = 1;
        thisController.setProfileID(thisProfile.getID());
        thisController.setRadius(0);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileEditActivity.this);
        LayoutInflater inflater = ProfileEditActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_controller, null);
        alertDialog.setView(dialogView);
        String pos = getResources().getString(R.string.Add);
        String neg = getResources().getString(R.string.Cancel);
        String[] data = getResources().getStringArray(R.array.JoyType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editName = (EditText) dialogView.findViewById(R.id.addControllerName);
        seekBar = (SeekBar) dialogView.findViewById(R.id.sizeSeek);
        spinner = (Spinner) dialogView.findViewById(R.id.listOfType);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getResources().getString(R.string.Type));
        alertDialog.setPositiveButton(pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = editName.getText().toString();
                int a = seekBar.getProgress();
                if(a < 50){
                    a = 50;
                }
                setParam(s, a);
                Log.d("Ik", thisController.getRadius()+"");
                thisController.setType(choosenIndex);
                Log.d("Rad ", thisController.getRadius()+"");
                if (thisController.getRadius() != 0) {
                    Log.d("Infp", "Insert");
                    daoSession.insert(thisController);
                    thisControllers.add(thisController);
                }
            }
        });
        Log.d("Ik", thisController.getRadius()+"TWO");
        alertDialog.setNegativeButton(neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                choosenIndex = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        alertDialog.show();

    }

    /**
     * Функция изменения контроллера
     */
    public void editController() {
        //TODO Сделать изменение контроллера
        try{
            final long idTemp = supportView.getIdControllerOnTouch();
            thisController = controllerDao.load(idTemp);
            choosenIndex = 1;
            thisController.setProfileID(thisProfile.getID());
            thisController.setRadius(0);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileEditActivity.this);
            LayoutInflater inflater = ProfileEditActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_controller, null);
            alertDialog.setView(dialogView);
            String pos = getResources().getString(R.string.Add);
            String neg = getResources().getString(R.string.Cancel);
            String[] data = getResources().getStringArray(R.array.JoyType);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editName = (EditText) dialogView.findViewById(R.id.addControllerName);
            editName.setText(thisController.getId());
            seekBar = (SeekBar) dialogView.findViewById(R.id.sizeSeek);
            seekBar.setProgress((int)thisController.getRadius());
            spinner = (Spinner) dialogView.findViewById(R.id.listOfType);
            spinner.setAdapter(adapter);
            spinner.setPrompt(getResources().getString(R.string.Type));
            alertDialog.setPositiveButton(pos, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String s = editName.getText().toString();
                    int a = seekBar.getProgress();
                    if(a < 50){
                        a = 50;
                    }
                    setParam(s, a);
                    Log.d("Ik", thisController.getRadius()+"");
                    thisController.setType(choosenIndex);
                    Log.d("Rad ", thisController.getRadius()+"");
                    if (thisController.getRadius() != 0) {
                        Log.d("Infp", "Insert");
                        thisControllers.remove(controllerDao.load(idTemp));
                        controllerDao.delete(controllerDao.load(idTemp));
                        daoSession.insert(thisController);
                        thisControllers.add(thisController);
                    }
                }
            });
            Log.d("Ik", thisController.getRadius()+"TWO");
            alertDialog.setNegativeButton(neg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    choosenIndex = position + 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            alertDialog.show();
        } catch (NullPointerException e){

        }
    }

    /**
     * Установка паарметров для контроллера в БД
     * @param s
     * @param a
     */
    public void setParam(String s, int a){
        Log.d("InfoS", a+"");
        thisController.setRadius(a);
        thisController.setId(s);
    }

    /**
     * Сохранение в БД перед выходом
     */
    @Override
    public void onBackPressed(){
        try{
            int size = thisControllers.size();
            for(int i = 0; i< size; i++){
                long idTemp = thisControllers.get(i).getID();
                Controller tempCtrl = controllerDao.load(idTemp);
                thisControllers.remove(controllerDao.load(idTemp));
                controllerDao.delete(controllerDao.load(idTemp));
                daoSession.insert(tempCtrl);
                thisControllers.add(tempCtrl);
            }
        } catch (NullPointerException e){}

        super.onBackPressed();
    }

    public boolean checkPosition(float x, float y) {
        for (Controller controller : thisControllers) {
            if (thisControllers.indexOf(controller) != indexControllerOnTouch) {
                if ((Math.sqrt(Math.pow((x - controller.getCentreOX()), 2) + Math.pow((x - controller.getCentreOY()), 2)) <= controller.getRadius() + thisControllers.get(indexControllerOnTouch).getRadius())) {
                    return false;
                }
            }
        }
        return true;
    }

/*
class DrawThreadEdit extends Thread {
    boolean flgRun = false;
    public DrawThreadEdit(){
        flgRun = true;
        run();
    }
    @Override
    public void run() {
        Canvas canvas;
        while (flgRun) {
            canvas = null;
            try {
                Paint paint = new Paint();
                canvas.drawColor(Color.WHITE);
                for (Controller controller : thisControllers) {
                    Log.d("Info", "Draw");
                    if (controller.getType() == 1) {
                        paint.setARGB(255, 50, 50, 50);
                        if (thisControllers.indexOf(controller) == indexControllerOnTouch && inTouch) {
                            paint.setARGB(255, 33, 150, 243);
                        }
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                        paint.setARGB(255, 255, 152, 0);
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius() / 3, paint);
                    } else if (controller.getType() == 2) {
                        paint.setARGB(255, 50, 50, 50);
                        if (thisControllers.indexOf(controller) == indexControllerOnTouch && inTouch) {
                            paint.setARGB(255, 33, 150, 243);
                        }
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                        paint.setARGB(255, 66, 66, 66);
                        RectF rectF = new RectF(controller.getCentreOX() - controller.getRadius() / 3, controller.getCentreOY() + controller.getRadius(), controller.getCentreOX() + controller.getRadius() / 3, controller.getCentreOY() - controller.getRadius());
                        Log.d("Rect", "left: " + (controller.getCentreOX() - controller.getRadius() / 3) + " top: " + (controller.getCentreOY() + controller.getRadius()) + " right: " + (controller.getCentreOX() - controller.getRadius() / 3) + " bottom: " + (controller.getCentreOY() - controller.getRadius()));
                        canvas.drawRoundRect(rectF, controller.getRadius() / 3, controller.getRadius() / 3, paint);
                        paint.setARGB(255, 255, 152, 0);
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius() / 3, paint);
                        controller.setNewOX(controller.getCentreOX());
                    } else if (controller.getType() == 3) {
                        paint.setARGB(255, 50, 50, 50);
                        if (thisControllers.indexOf(controller) == indexControllerOnTouch && inTouch) {
                            paint.setARGB(255, 33, 150, 243);
                        }
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                        paint.setARGB(255, 66, 66, 66);
                        RectF rectF = new RectF(controller.getCentreOX() - controller.getRadius(), controller.getCentreOY() + controller.getRadius() / 3, controller.getCentreOX() + controller.getRadius(), controller.getCentreOY() - controller.getRadius() / 3);
                        canvas.drawRoundRect(rectF, controller.getRadius() / 3, controller.getRadius() / 3, paint);
                        paint.setARGB(255, 255, 152, 0);
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius() / 3, paint);
                        controller.setNewOY(controller.getCentreOY());
                    } else if (controller.getType() == 4) {
                        paint.setARGB(255, 50, 50, 50);
                        if (thisControllers.indexOf(controller) == indexControllerOnTouch && inTouch) {
                            paint.setARGB(255, 33, 150, 243);
                        }
                        canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                    }
                }
            } catch (NullPointerException e) {

            }
        }
    }

    public boolean isFlgRun() {
        return flgRun;
    }

    public void setFlgRun(boolean flgRun) {
        this.flgRun = flgRun;
    }
}*/
}
