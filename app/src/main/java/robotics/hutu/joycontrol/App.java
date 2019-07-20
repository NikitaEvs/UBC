package robotics.hutu.joycontrol;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

/**
 * Util класс для базы данных, служит для её создания
 */

public class App extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate(){
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "controllers-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
