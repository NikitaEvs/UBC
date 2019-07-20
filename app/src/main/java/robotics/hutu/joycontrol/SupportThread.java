package robotics.hutu.joycontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Вспомогательный поток отрисовки для режима редактирования контроллеров
 */

public class SupportThread extends Thread {
    SurfaceHolder surfaceHolder;
    long id;
    private DaoSession daoSession;
    private ControllerDao controllerDao;
    private ProfileDao profileDao;
    private Profile thisProfile;
    private List<Controller> thisControllers;
    private boolean flgRun;
    private Context context;
    private boolean inTouch;
    private int indexControllerOnTouch;
    public SupportThread(SurfaceHolder surfaceHolder, Context context, long id){
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.id = id;
        setDatabase();
        run();
    }
    public void editSettings(boolean inTouch, int indexControllerOnTouch){
        this.inTouch = inTouch;
        this.indexControllerOnTouch = indexControllerOnTouch;
    }
    @Override
    public void run() {
        Log.d("Info1", flgRun+"");
        Canvas canvas;
        while (flgRun) {
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    try{
                        Paint paint = new Paint();
                        canvas.drawColor(Color.WHITE);
                        Log.d("Info", "Size: "+thisControllers.size());
                        thisControllers = thisProfile.getControllers();
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
                    } catch (NullPointerException e){}
                }
            } catch (IllegalArgumentException e) {

            }
            finally {
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
    public void setDatabase() {
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
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

    public boolean isFlgRun() {
        return flgRun;
    }

    public void setFlgRun(boolean flgRun) {
        this.flgRun = flgRun;
    }
}
