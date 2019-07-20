package robotics.hutu.joycontrol;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Обработка нажатий для контроллеров
 */

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    int dWidth;
    int dHeight;
    float joyBigRadius;
    float joySmallRadius;
    float joyOX;
    float joyOY;
    float newJoyOX;
    float newJoyOY;
    float stickOX;
    float stickOY;
    float newStickOY;
    float stickHeight;
    float stickWidth;
    float stickSmallHeight;
    int joyFingerID;
    int stickFingerID;
    boolean flgJoy = false;
    boolean flgStick = false;
    int buttonTouchID = -1;


    private DrawThread drawThread;
    private BluetoothThread bluetoothThread;
    private Context context;

    //TODO Получение листа с контроллерами из БД
    List<Controller> controllers;

    Profile profile;
    long profileID;

    private ControllerDao controllerDao;
    private Query<Controller> controllerQuery;


    public JoystickView(Context context, BluetoothThread bluetoothThread, long profileID) {
        super(context);
        this.context = context;
        this.bluetoothThread = bluetoothThread;
        this.profileID = profileID;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        setDatabase();
    }

    public JoystickView(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        setDatabase();
    }

    public JoystickView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
        setDatabase();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setDisplaySettings();
        Log.d("Proc", "Make drawThread");
        drawThread = new DrawThread(controllers, context, getHolder(), profileID);
        /*drawThread = new DrawThread(context, getHolder(), joyBigRadius, joySmallRadius, joyOX, joyOY, newJoyOX,
                newJoyOY, stickOX, stickOY, newStickOY, stickHeight, stickWidth, stickSmallHeight);*/
        drawThread.setRunFlag(true);
        bluetoothThread.setDrawThread(drawThread);
        drawThread.start();
        Log.d("Proc", "Start drawThread");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunFlag(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }
    /*
    public void updateThread() {
        drawThread.editSettings(joyBigRadius, joySmallRadius, joyOX, joyOY, newJoyOX, newJoyOY,
                stickOX, stickOY, newStickOY, stickHeight, stickWidth, stickSmallHeight);
    }
    public void updateThread(){
        drawThread.editSettings(controllers);
    }*/

    public void setDisplaySettings() {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            dWidth = size.x;
            dHeight = size.y;
            //new Logger(context, "Info").log(dWidth + " hei - "+dHeight);
            joyOX = dWidth / 4;
            joyOY = dHeight / 2;
            newJoyOX = joyOX;
            newJoyOY = joyOY;
            joyBigRadius = dHeight * (float) 0.3;
            joySmallRadius = joyBigRadius / 3;
            stickOX = dWidth * 3 / 4;
            stickOY = dHeight / 2;
            newStickOY = stickOY;
            stickHeight = joyBigRadius * 2;
            stickWidth = joySmallRadius * 2;
            stickSmallHeight = stickHeight / 6;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Обработка нажатия
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        if (v.equals(this)) {
            //new Logger(context, "Info").log( "Touch!");
            int actionMask = e.getActionMasked();
            int id = e.getPointerId(e.getActionIndex());
            if (actionMask == e.ACTION_DOWN) {
                float newOX = e.getX();
                float newOY = e.getY();
                for (Controller controller : controllers) {
                    Log.d("Button", "Point Down " + controller.getType());
                    if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius()) && controller.getType() == 4) {
                        if (controller.isOnTouch()) {
                            Log.d("Button", "Off");
                            if((buttonTouchID == -1) || (buttonTouchID == controller.getTouchID())){
                                controller.setOnTouch(false);
                                controller.setTouchID(id);
                                buttonTouchID = controller.getTouchID();
                            }
                        } else {
                            Log.d("Button", "On");
                            if((buttonTouchID == -1) || (buttonTouchID == controller.getTouchID())){
                                controller.setOnTouch(true);
                                controller.setTouchID(id);
                                buttonTouchID = controller.getTouchID();
                            }
                        }
                    } else if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius()) && !controller.isOnTouch()) {
                        controller.setNewOX(newOX);
                        controller.setNewOY(newOY);
                        controller.setTouchID(id);
                        controller.setOnTouch(true);
                    }
                }
            }
            /*
            if (actionMask == e.ACTION_DOWN) {
                float newOX = e.getX();
                float newOY = e.getY();
                /*new Logger(context, "Info").log( "-------------------------");
                new Logger(context, "Info").log(newOX + " newOX");
                new Logger(context, "Info").log(newOY + " newOY");*/
                /*
                if ((Math.sqrt(Math.pow((newOX - joyOX), 2) + Math.pow((newOY - joyOY), 2)) <= joyBigRadius) && !flgJoy) {
                    newJoyOX = newOX;
                    newJoyOY = newOY;
                    joyFingerID = id;
                    flgJoy = true;
                } else if (((newOX < stickOX + stickWidth / 2) && (newOX > stickOX - stickWidth / 2) && (newOY < stickOY + stickHeight / 2) && (newOY > stickOY - stickHeight / 2)) && !flgStick) { // добавить проверку на стик
                    newStickOY = newOY;
                    stickFingerID = id;
                    flgStick = true;
                }
                /*
                new Logger(context, "Info").log(joyFingerID + " joyFingerID");
                new Logger(context, "Info").log(flgJoy + " flgJOY");
                new Logger(context, "Info").log(flgStick + " flgStick");
                new Logger(context, "Info").log(  newJoyOX + " newJoyOX");
                new Logger(context, "Info").log(newJoyOY + " newJoyOY");
                new Logger(context, "Info").log( newStickOY + " newStickOY");
                new Logger(context, "Info").log( "-------------------------");
            }*/
            if (actionMask == e.ACTION_POINTER_DOWN) {
                if (!check()) {
                    for (int i = 0; i < e.getPointerCount(); i++) {
                        float newOX = e.getX(i);
                        float newOY = e.getY(i);
                        for (Controller controller : controllers) {
                            if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius()) && controller.getType() == 4) {
                                if (controller.isOnTouch()) {
                                    Log.d("Button", "Off");
                                    if((buttonTouchID == -1) || (buttonTouchID == controller.getTouchID())){
                                        controller.setOnTouch(false);
                                        controller.setTouchID(id);
                                        buttonTouchID = controller.getTouchID();
                                    }
                                } else {
                                    Log.d("Button", "On");
                                    if((buttonTouchID == -1) || (buttonTouchID == controller.getTouchID())){
                                        controller.setOnTouch(true);
                                        controller.setTouchID(id);
                                        buttonTouchID = controller.getTouchID();
                                    }
                                }
                            } else if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius()) && !controller.isOnTouch()) {
                                controller.setNewOX(newOX);
                                controller.setNewOY(newOY);
                                controller.setTouchID(id);
                                controller.setOnTouch(true);
                            }
                        }
                    }
                }
            } else if (actionMask == e.ACTION_MOVE) {
                for (int i = 0; i < e.getPointerCount(); i++) {
                    float newOX = e.getX(i);
                    float newOY = e.getY(i);
                    id = e.getPointerId(i);
                    for (Controller controller : controllers) {
                        if ((id == controller.getTouchID()) && controller.isOnTouch()) {
                            controller.setNewOX(newOX);
                            controller.setNewOY(newOY);
                        }
                    }
                }
            }
            /*
            if (actionMask == e.ACTION_POINTER_DOWN) {
                if (!(flgJoy && flgStick)) {
                    for (int i = 0; i < e.getPointerCount(); i++) {
                        float newOX = e.getX(i);
                        float newOY = e.getY(i);
                        if ((Math.sqrt(Math.pow((newOX - joyOX), 2) + Math.pow((newOY - joyOY), 2)) <= joyBigRadius) && !flgJoy) {
                            newJoyOX = newOX;
                            newJoyOY = newOY;
                            joyFingerID = id;
                            flgJoy = true;
                        } else if (((newOX < stickOX + stickWidth / 2) && (newOX > stickOX - stickWidth / 2) && (newOY < stickOY + stickHeight / 2) && (newOY > stickOY - stickHeight / 2)) && !flgStick) { // добавить проверку на стик
                            newStickOY = newOY;
                            stickFingerID = id;
                            flgStick = true;
                        }
                    }
                }*/
            /*} else if (actionMask == e.ACTION_MOVE) {
                for (int i = 0; i < e.getPointerCount(); i++) {
                    float newOX = e.getX(i);
                    float newOY = e.getY(i);
                    id = e.getPointerId(i);
                    if ((id == joyFingerID)&&flgJoy) {
                        newJoyOX = newOX;
                        newJoyOY = newOY;
                    } else if ((id == stickFingerID)&&flgStick) {
                        newStickOY = newOY;
                    }
                }
            }*/
            if ((actionMask == e.ACTION_UP) || (actionMask == e.ACTION_POINTER_UP)) {
                int ID = e.getPointerId(e.getActionIndex());
                for (Controller controller : controllers) {
                    if (controller.getTouchID() == ID) {
                        if (controller.getType() != 4) {
                            controller.setNewOX(controller.getCentreOX());
                            controller.setNewOY(controller.getCentreOY());
                            controller.setOnTouch(false);
                            buttonTouchID = -1;
                        }
                    }
                }
            } /*
            if ((actionMask == e.ACTION_UP) || (actionMask == e.ACTION_POINTER_UP)) {
                flgStick = false;
                flgJoy = false;

                int stickFingerID_Temp = stickFingerID;
                int joyFingerID_Temp = joyFingerID;
                stickFingerID = -1;
                joyFingerID = -1;
                int ID = e.getPointerId(e.getActionIndex());
                if (ID != joyFingerID_Temp) {
                    if (actionMask != e.ACTION_UP) {
                        joyFingerID = joyFingerID_Temp;
                        flgJoy = true;
                    }
                } else if (ID != stickFingerID_Temp) {
                    if (actionMask != e.ACTION_UP) {
                        stickFingerID = stickFingerID_Temp;
                        flgStick = true;
                    }
                }
                if (!flgJoy) {
                    newJoyOX = joyOX;
                    newJoyOY = joyOY;
                }
                if (!flgStick) {
                    newStickOY = stickOY;
                }
                updateThread();
            }*/
            //updateThread();
        }
        return true;
    }

    /**
     * Проверка на то, все ли контроллеры нажаты
     * @return
     */
    public boolean check() {
        boolean out = true;
        for (Controller controller : controllers) {
            out &= controller.isOnTouch();
        }
        return out;
    }

    /**
     * Настройка БД
     */
    public void setDatabase() {
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        profile = daoSession.getProfileDao().load(profileID);
        controllers = profile.getControllers();
        /*controllerDao = daoSession.getControllerDao();
        controllerQuery = controllerDao.queryBuilder().orderAsc(ControllerDao.Properties.ID).build();
        controllers = controllerQuery.list();*/
    }
}
