package robotics.hutu.joycontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import robotics.hutu.joycontrol.App;
import robotics.hutu.joycontrol.BluetoothConnectedThread;
import robotics.hutu.joycontrol.Controller;
import robotics.hutu.joycontrol.Profile;

/**
 * Поток отрисовки экрана управления с контроллерами
 */
public class DrawThread extends Thread{
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private BluetoothConnectedThread thread;
    private float joyBigRadius;
    private float joySmallRadius;
    private float joyOX;
    private float joyOY;
    private float newJoyOX;
    private float newJoyOY;
    private float stickOX;
    private float stickOY;
    private float newStickOY;
    private float stickHeight;
    private float stickWidth;
    private float stickSmallHeight;

    //TODO Приём данных из БД
    List<Controller> controllers;

    Profile profile;
    long profileID;

    private ControllerDao controllerDao;
    private Query<Controller> controllerQuery;
    /*
    public DrawThread(Context context, SurfaceHolder surfaceHolder, float joyBigRadius, float joySmallRadius, float joyOX, float joyOY, float newJoyOX, float newJoyOY,
                      float stickOX, float stickOY, float newStickOY, float stickHeight, float stickWidth, float stickSmallHeight){
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.joyBigRadius = joyBigRadius;
        this.joySmallRadius = joySmallRadius;
        this.joyOX = joyOX;
        this.joyOY = joyOY;
        this.newJoyOX = newJoyOX;
        this.newJoyOY = newJoyOY;
        this.stickOX = stickOX;
        this.stickOY = stickOY;
        this.newStickOY = newStickOY;
        this.stickHeight = stickHeight;
        this.stickWidth = stickWidth;
        this.stickSmallHeight = stickSmallHeight;
    }*/
    public DrawThread(List<Controller> controllers, Context context, SurfaceHolder surfaceHolder, long profileID){
        this.controllers = controllers;
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.profileID = profileID;
        setDatabase();
    }
    public void editSettings(float joyBigRadius, float joySmallRadius, float joyOX, float joyOY, float newJoyOX, float newJoyOY,
                             float stickOX, float stickOY, float newStickOY, float stickHeight, float stickWidth, float stickSmallHeight){
        this.joyBigRadius = joyBigRadius;
        this.joySmallRadius = joySmallRadius;
        this.joyOX = joyOX;
        this.joyOY = joyOY;
        this.newJoyOX = newJoyOX;
        this.newJoyOY = newJoyOY;
        this.stickOX = stickOX;
        this.stickOY = stickOY;
        this.newStickOY = newStickOY;
        this.stickHeight = stickHeight;
        this.stickWidth = stickWidth;
        this.stickSmallHeight = stickSmallHeight;
    }
    public void setBluetoothConnectedThread(BluetoothConnectedThread thread){
        this.thread = thread;
        Log.d("Info", "Set");
    }
    public float getJoyOX(){
        return joyOX;
    }
    public float getJoyOY(){
        return joyOY;
    }
    public void setRunFlag(boolean run){
        runFlag = run;
    }

    /**
     * Функция отрисовки
     */
    @Override
    public void run(){
        Canvas canvas;
        while(runFlag){
            canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    try {
                        Paint paint = new Paint();
                        canvas.drawColor(Color.WHITE);
                        fixValuesTemp();
                        Log.d("Size", "Size: "+controllers.size());
                        for(Controller controller:controllers){
                            if(controller.getType() == 1){
                                paint.setARGB(255, 50, 50, 50);
                                canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                                paint.setARGB(255, 255, 152, 0);
                                canvas.drawCircle(controller.getNewOX(), controller.getNewOY(), controller.getRadius()/3, paint);
                            } else if(controller.getType() == 2){
                                paint.setARGB(255, 50, 50, 50);
                                canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                                paint.setARGB(255,66,66,66);
                                RectF rectF = new RectF(controller.getCentreOX() - controller.getRadius()/3, controller.getCentreOY() + controller.getRadius(), controller.getCentreOX() + controller.getRadius()/3, controller.getCentreOY() - controller.getRadius());
                                Log.d("Rect", "left: "+(controller.getCentreOX() - controller.getRadius()/3)+" top: "+(controller.getCentreOY() + controller.getRadius())+" right: "+(controller.getCentreOX() - controller.getRadius()/3)+ " bottom: "+ (controller.getCentreOY() - controller.getRadius()));
                                canvas.drawRoundRect(rectF, controller.getRadius()/3, controller.getRadius()/3, paint);
                                paint.setARGB(255, 255, 152, 0);
                                canvas.drawCircle(controller.getCentreOX(), controller.getNewOY(), controller.getRadius()/3, paint);
                                controller.setNewOX(controller.getCentreOX());
                            } else if(controller.getType() == 3){
                                paint.setARGB(255, 50, 50, 50);
                                canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                                paint.setARGB(255,66,66,66);
                                RectF rectF = new RectF(controller.getCentreOX() - controller.getRadius(), controller.getCentreOY() + controller.getRadius()/3, controller.getCentreOX() + controller.getRadius(), controller.getCentreOY() - controller.getRadius()/3);
                                canvas.drawRoundRect(rectF, controller.getRadius()/3, controller.getRadius()/3, paint);
                                paint.setARGB(255, 255, 152, 0);
                                canvas.drawCircle(controller.getNewOX(), controller.getCentreOY(), controller.getRadius()/3, paint);
                                controller.setNewOY(controller.getCentreOY());
                            } else if(controller.getType() == 4){
                                paint.setARGB(255, 50, 50, 50);
                                canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius(), paint);
                                Log.d("Button", controller.isOnTouch()+"");
                                if(controller.isOnTouch()){
                                    paint.setARGB(255, 255, 152, 0);
                                    canvas.drawCircle(controller.getCentreOX(), controller.getCentreOY(), controller.getRadius()*9/10, paint);
                                }
                            }

                        }
                        sendValue();
                        /*
                        Paint paint = new Paint();
                        canvas.drawColor(Color.WHITE);
                        paint.setARGB(255, 50, 50, 50);
                        fixValuesTemp();
                        canvas.drawCircle(joyOX, joyOY, joyBigRadius, paint);
                        canvas.drawCircle(stickOX, stickOY, joyBigRadius, paint);
                        paint.setARGB(255,66,66,66);
                        RectF rect = new RectF(stickOX - stickWidth / 2, stickOY + stickHeight / 2, stickOX + stickWidth / 2, stickOY - stickHeight / 2);
                        //canvas.drawRect(rect, paint);
                        canvas.drawRoundRect(rect, joyBigRadius/3, joyBigRadius/3,  paint);
                        paint.setARGB(255, 255, 152, 0);
                        fixValuesTemp();
                        //new Logger(context, "Info").log("Paint:   x: " + newJoyOX + " y: " + newJoyOY);
                        canvas.drawCircle(newJoyOX, newJoyOY, joySmallRadius, paint);
                        canvas.drawCircle(stickOX, newStickOY, stickWidth/2, paint);
                        //canvas.drawRect(stickOX - stickWidth / 2, newStickOY + stickSmallHeight / 2, stickOX + stickWidth / 2, newStickOY - stickSmallHeight / 2, paint);
                        sendValue();*/
                    } catch (NullPointerException e){
                    }
                }
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            finally {
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * Функция корректировки показаний из БД для того, чтобы шляпка джойстиков и стиков не вылезала за края контроллера
     */
    public void fixValuesTemp(){
        for(Controller controller:controllers){
            Log.d("Val", "NewOX: "+controller.getNewOX()+" NewOY"+controller.getNewOY());
            if(Math.sqrt(Math.pow((controller.getNewOX() - controller.getCentreOX()),2)+Math.pow((controller.getNewOY() - controller.getCentreOY()),2)) > controller.getRadius()){
                float k;
                if(controller.getNewOX() - controller.getCentreOX() == 0){
                    if(controller.getNewOY() > controller.getCentreOY()){
                        controller.setNewOY(controller.getRadius());
                    } else {
                        controller.setNewOY(-controller.getRadius());
                    }
                    controller.setNewOX(controller.getNewOX() - controller.getCentreOX());
                } else {
                    k = (controller.getNewOY() - controller.getCentreOY())/(controller.getNewOX() - controller.getCentreOX());
                    if(controller.getNewOX() > controller.getCentreOX()){
                        controller.setNewOX((controller.getRadius())/((float)Math.sqrt(k*k+1)));
                        controller.setNewOY((k*controller.getRadius())/((float)Math.sqrt(k*k+1)));
                    } else {
                        controller.setNewOX((-controller.getRadius()) / ((float)Math.sqrt(k*k+1)));
                        controller.setNewOY((-k*controller.getRadius())/((float)Math.sqrt(k*k+1)));
                    }
                }
                controller.setNewOX(controller.getNewOX() + controller.getCentreOX());
                controller.setNewOY(controller.getNewOY() + controller.getCentreOY());
                Log.d("Val", "NewOXNEW: "+controller.getNewOX()+" NewOYNEW"+controller.getNewOY());
            }
        }


        /*
        if(Math.sqrt(Math.pow((newJoyOX - joyOX),2)+Math.pow((newJoyOY - joyOY),2)) > joyBigRadius){
            float k;
            if(newJoyOX - joyOX == 0){
                if(newJoyOY > joyOY){
                    newJoyOY = joyBigRadius;
                } else {
                    newJoyOY = -joyBigRadius;
                }
                newJoyOX -= joyOX;
            } else {
                k = (newJoyOY - joyOY)/(newJoyOX - joyOX);
                if(newJoyOX > joyOX){
                    newJoyOX = (joyBigRadius) / ((float)Math.sqrt(k*k+1));
                    newJoyOY = (k*joyBigRadius)/((float)Math.sqrt(k*k+1));
                } else {
                    newJoyOX = (-joyBigRadius) / ((float)Math.sqrt(k*k+1));
                    newJoyOY = (-k*joyBigRadius)/((float)Math.sqrt(k*k+1));
                }
            }
            newJoyOX += joyOX;
            newJoyOY += joyOY;
            new Logger(context, "Info").log("x: " + newJoyOX+" y: "+newJoyOY);
        }
        if(newStickOY > stickOY + stickHeight/2){
            newStickOY = stickOY + stickHeight/2;
        } else if(newStickOY < stickOY - stickHeight/2){
            newStickOY = stickOY - stickHeight/2;
        }*/
    }

    public void sendValue(){
        thread.editValues(newJoyOX, newJoyOY, newStickOY, joyOX, joyOY, stickOY, joyBigRadius);
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
