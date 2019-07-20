package robotics.hutu.joycontrol;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Класс контроллеров
 */
@Entity (active = true, nameInDb = "CONTROLLERS")
public class Controller {
    @Id
    private Long ID; //Уникальный ID для БД
    private long profileID; //ID профиля, к которому относится данный контроллер
    /**
     * 1 - двухосевой джойстик
     * 2 - одноосевой вертикальный
     * 3 - одноосевой горизонтальный
     * 4 - кнопка
     */
    private int type; //Тип контроллера
    private int touchID; //ID пальца, который нажал на данный контроллер, нужен для отслеживания нажатий
    @NotNull
    private String id; //Тег контроллера
    /**
     * Переменные, отвечающие за отрисовку
     */
    private float centreOX;
    private float centreOY;
    private float newOX;
    private float newOY;
    private float radius;
    private boolean onTouch;
    /**
     * Далее идут Getter'ы и Setter'ы, а также код, сгенерированный GreenDao для ОР БД
     */
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 570571578)
    private transient ControllerDao myDao;

    public Controller(int type, String id, float centreOX, float centreOY, float radius){
        this.type = type;
        this.id = id;
        this.centreOX = centreOX;
        this.centreOY = centreOY;
        this.radius = radius;
        this.newOX = centreOX;
        this.newOY = centreOY;
    }

    @Generated(hash = 1516637789)
    public Controller(Long ID, long profileID, int type, int touchID, @NotNull String id,
            float centreOX, float centreOY, float newOX, float newOY, float radius,
            boolean onTouch) {
        this.ID = ID;
        this.profileID = profileID;
        this.type = type;
        this.touchID = touchID;
        this.id = id;
        this.centreOX = centreOX;
        this.centreOY = centreOY;
        this.newOX = newOX;
        this.newOY = newOY;
        this.radius = radius;
        this.onTouch = onTouch;
    }

    @Generated(hash = 549905556)
    public Controller() {
    }

    public String getId() {
        return id;
    }

    public float getCentreOX() {
        return centreOX;
    }

    public float getCentreOY() {
        return centreOY;
    }

    public float getNewOX() {
        return newOX;
    }

    public float getNewOY() {
        return newOY;
    }

    public float getRadius() {
        return radius;
    }

    public int getType() {
        return type;
    }

    public void setCentreOX(float centreOX) {
        this.centreOX = centreOX;
    }

    public void setCentreOY(float centreOY) {
        this.centreOY = centreOY;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNewOX(float newOX) {
        this.newOX = newOX;
    }

    public void setNewOY(float newOY) {
        this.newOY = newOY;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOnTouch() {
        return onTouch;
    }

    public int getTouchID() {
        return touchID;
    }

    public void setOnTouch(boolean onTouch) {
        this.onTouch = onTouch;
    }

    public void setTouchID(int touchID) {
        this.touchID = touchID;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public boolean getOnTouch() {
        return this.onTouch;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public long getProfileID() {
        return this.profileID;
    }

    public void setProfileID(long profileID) {
        this.profileID = profileID;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1836696567)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getControllerDao() : null;
    }
}
