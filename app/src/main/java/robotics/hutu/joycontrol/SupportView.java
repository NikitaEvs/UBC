package robotics.hutu.joycontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;

import robotics.hutu.joycontrol.App;
import robotics.hutu.joycontrol.Controller;
import robotics.hutu.joycontrol.Profile;
import robotics.hutu.joycontrol.SupportThread;

/**
 * Обработка нажатий для режима редактирования
 */
public class SupportView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    long id;
    private DaoSession daoSession;
    private ControllerDao controllerDao;
    private ProfileDao profileDao;
    private Profile thisProfile;
    private List<Controller> thisControllers;
    private boolean inTouch = false;
    private int indexControllerOnTouch;
    private long idControllerOnTouch;
    SupportThread thread;
    private Context context;

    public SupportView(Context context, long id) {
        super(context);
        this.context = context;
        this.id = id;
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    public SupportView(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    public SupportView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        getHolder().addCallback(this);
        setOnTouchListener(this);

    }

    public boolean onTouch(View v, MotionEvent e) {
        if (v.equals(this)) {

            float newOX = e.getX();
            float newOY = e.getY();
            if(e.getAction() == e.ACTION_DOWN){
                if (!inTouch) {
                    for (Controller controller : thisControllers) {
                        if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius())) {
                            inTouch = true;
                            indexControllerOnTouch = thisControllers.indexOf(controller);
                            idControllerOnTouch = thisControllers.get(thisControllers.indexOf(controller)).getID();
                            thread.editSettings(inTouch, indexControllerOnTouch);
                            break;
                        }
                    }
                } else {
                    for (Controller controller : thisControllers) {
                        if ((Math.sqrt(Math.pow((newOX - controller.getCentreOX()), 2) + Math.pow((newOY - controller.getCentreOY()), 2)) <= controller.getRadius())) {
                            inTouch = false;
                            indexControllerOnTouch = thisControllers.indexOf(controller);
                            thread.editSettings(inTouch, indexControllerOnTouch);
                            break;
                        }
                    }
                }
            } else if(e.getAction() == e.ACTION_MOVE){
                if(inTouch){
                    //if (checkPosition(newOX, newOY)) {
                        thisControllers.get(indexControllerOnTouch).setCentreOX(newOX);
                        thisControllers.get(indexControllerOnTouch).setCentreOY(newOY);
                        thisControllers.get(indexControllerOnTouch).setNewOX(newOX);
                        thisControllers.get(indexControllerOnTouch).setNewOY(newOY);
                        //thisProfile.getControllers().get(thisProfile.getControllers().indexOf(thisControllers.get(indexControllerOnTouch))).setCentreOX(newOX);
                        //thisProfile.getControllers().get(thisProfile.getControllers().indexOf(thisControllers.get(indexControllerOnTouch))).setCentreOX(newOY);
                    //}
                }
            }
            //TODO сделать тост


        }
        return true;
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setDatabase();
        thread = new SupportThread(getHolder(), context, id);
        thread.setFlgRun(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Info", "Destroy");
        int i = 0;

        for(Controller controller: thisControllers){
            try {
                i++;
                Log.d("Info", i + "");
                Log.d("Lay", "OX: " + controller.getCentreOX() + " OY: " + controller.getCentreOY());
                Controller controllerTemp = profileDao.load(id).getControllers().get(profileDao.load(id).getControllers().indexOf(controller));
                controllerTemp.setCentreOX(controller.getCentreOX());
                controllerTemp.setCentreOY(controller.getCentreOY());
                controllerTemp.setNewOX(controller.getCentreOX());
                controllerTemp.setNewOY(controller.getCentreOY());
                controllerDao.delete(profileDao.load(id).getControllers().get(profileDao.load(id).getControllers().indexOf(controller)));
               /* profileDao.load(id).getControllers().get(thisProfile.getControllers().indexOf(controller)).setCentreOX(controller.getCentreOX());
                profileDao.load(id).getControllers().get(thisProfile.getControllers().indexOf(controller)).setCentreOY(controller.getCentreOY());
                controllerDao.update(profileDao.load(id).getControllers().get(thisProfile.getControllers().indexOf(controller)));*/
                controllerDao.insert(controllerTemp);
                daoSession.insert(controllerTemp);
            } catch (NullPointerException e){}
        }
        boolean retry = true;
        thread.setFlgRun(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

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
        /**
         * TODO не забыть убрать
         */
        thisProfile = daoSession.getProfileDao().load(id);
        thisControllers = thisProfile.getControllers();
    }

    public long getIdControllerOnTouch() {
        return idControllerOnTouch;
    }

    public void setIdControllerOnTouch(long idControllerOnTouch) {
        this.idControllerOnTouch = idControllerOnTouch;
    }
}
