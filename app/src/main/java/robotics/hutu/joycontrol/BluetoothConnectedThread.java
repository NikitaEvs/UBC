package robotics.hutu.joycontrol;


import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Поток для приёма/передачи данных с Bluetooth нлиентом при наличии подключения
 * Запускается в BluetoothThread
 */

public class BluetoothConnectedThread extends Thread {

    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothSocket socket;
    private StringBuilder sb;
    private float newJoyOX;
    private float newJoyOY;
    private float joyOX;
    private float joyOY;
    private float newStickOY;
    private float stickOY;
    private float joyBigRadius;
    private boolean flgRun = false;

    private Context context;
    //Приём листа из БД
    List<Controller> controllers;
    Profile profile;
    long profileID;

    private ControllerDao controllerDao;
    private Query<Controller> controllerQuery;


    public BluetoothConnectedThread(BluetoothSocket socket, Context context, long profileID){
        this.context = context;
        this.socket = socket;
        this.profileID = profileID;
        setDatabase();
        flgRun = true;
        try{
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void editValues(float newJoyOX, float newJoyOY, float newStickOY, float joyOX, float joyOY, float stickOY, float joyBigRadius){
        this.newJoyOX = newJoyOX;
        this.newJoyOY = newJoyOY;
        this.newStickOY = newStickOY;
        this.joyOX = joyOX;
        this.joyOY = joyOY;
        this.stickOY = stickOY;
        this.joyBigRadius = joyBigRadius;
    }

    /**
     * Постоянный приём данных и отправка ответа при получении смивола H
     */
    @Override
    public void run(){
        while (flgRun){
            try {
                byte[] buffer = new byte[256];
                int bytes;
                bytes = inputStream.read(buffer);
                String inMsg = new String(buffer, 0, bytes);
                Log.d("InfoGive", inMsg);
                if(inMsg.equals("H")){
                    sendData();
                }
            } catch (IOException e) {

            } catch (NullPointerException e){

            }
        }
    }

    /**
     * Функция отправки данных на Bluetooth клиент
     * Получает значения из БД со всех контроллеров запущенного профиля и преобразует их в необходимый формат
     */
    public void sendData(){
        try{
            for(Controller controller: controllers){
                byte[] out = new byte[20];
                float gasValue;
                float rotateValue;
                float coff = 500/controller.getRadius();
                if(controller.getNewOY()>controller.getCentreOY()){
                    gasValue = -(float)Math.sqrt(Math.pow((controller.getNewOX() - controller.getCentreOX()), 2) + Math.pow((controller.getNewOY() - controller.getCentreOY()), 2));
                } else {
                    gasValue = (float)Math.sqrt(Math.pow((controller.getNewOX() - controller.getCentreOX()), 2) + Math.pow((controller.getNewOY() - controller.getCentreOY()), 2));
                }
                if(controller.getNewOX()>controller.getCentreOX()){
                    rotateValue = -(float)Math.sqrt(Math.pow(gasValue,2)-Math.pow((controller.getNewOY() - controller.getCentreOY()), 2));
                } else {
                    rotateValue = (float)Math.sqrt(Math.pow(gasValue,2)-Math.pow((controller.getNewOY() - controller.getCentreOY()), 2));
                }
                gasValue *= coff;
                rotateValue *= coff;
                gasValue += 500;
                rotateValue += 500;
                if(gasValue > 999){
                    gasValue = 999;
                }
                if(rotateValue > 999){
                    rotateValue = 999;
                }
                if(gasValue < 0){
                    gasValue = 0;
                }
                if(rotateValue < 0){
                    rotateValue = 0;
                }
                String gasStr = "";
                String rotStr = "";
                String stickStr = "";
                if(gasValue < 10){
                    gasStr = "00" + (int)gasValue;
                } else if(gasValue < 100){
                    gasStr = "0" + (int)gasValue;
                } else {
                    gasStr = (int)gasValue+"";
                }
                if(rotateValue < 10){
                    rotStr = "00" + (int)rotateValue;
                } else if(rotateValue < 100){
                    rotStr = "0" + (int)rotateValue;
                } else {
                    rotStr = (int)rotateValue+"";
                }
                String outStr = "";
                if(controller.getType() == 1){
                    outStr = controller.getId()+"G"+gasStr+"R"+rotStr+"F";
                } else if((controller.getType() == 2) || (controller.getType() == 3)){
                    outStr = controller.getId()+"G"+gasStr;
                } else if(controller.getType() == 4){
                    int status = controller.isOnTouch()? 1:0;
                    outStr = controller.getId()+"S"+status+"F";
                }
                Log.d("InfoGet", outStr);
                outputStream.write(outStr.getBytes());
            }
            /*
            //TODO Сделать конвертацию данных в удобную для козы форму
            byte[] out = new byte[20];
            float gasValue;
            float rotateValue;
            float headValue;
            float coff = 500/joyBigRadius;
            if(newJoyOY>joyOY){
                gasValue = -(float)Math.sqrt(Math.pow((newJoyOX - joyOX), 2) + Math.pow((newJoyOY - joyOY), 2));
            } else {
                gasValue = (float)Math.sqrt(Math.pow((newJoyOX - joyOX), 2) + Math.pow((newJoyOY - joyOY), 2));
            }
            if(newJoyOX>joyOX){
                rotateValue = -(float)Math.sqrt(Math.pow(gasValue,2)-Math.pow((newJoyOY - joyOY), 2));
            } else {
                rotateValue = (float)Math.sqrt(Math.pow(gasValue,2)-Math.pow((newJoyOY - joyOY), 2));
            }
            headValue = -(newStickOY - stickOY);
            gasValue *= coff;
            rotateValue *= coff;
            headValue *= coff;
            gasValue += 500;
            rotateValue += 500;
            headValue += 500;
            if(gasValue > 999){
                gasValue = 999;
            }
            if(rotateValue > 999){
                rotateValue = 999;
            }
            if(headValue > 999){
                headValue = 999;
            }
            String outStr = "G"+(int)gasValue+"R"+(int)rotateValue+"S"+(int)headValue+"F";
            String s = "kek";
            outputStream.write(outStr.getBytes());*/
        } catch (IOException e){
            Log.d("Error", "Trouble with sending data");
        }

    }
    public void kill(){
        flgRun = false;
    }

    /**
     * Функция настройки базы данных
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
