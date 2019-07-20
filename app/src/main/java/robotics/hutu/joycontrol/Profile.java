package robotics.hutu.joycontrol;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Класс профиля, имеет в составе массив контроллеров, которые к нему относятся
 * Также MAC адрес
 */

@Entity
public class Profile {
    private String name;
    @Id
    private Long ID;
    private String MACaddress;
    @ToMany(referencedJoinProperty = "profileID")
    private List<Controller> controllers;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 89320040)
    private transient ProfileDao myDao;


    @Generated(hash = 220803886)
    public Profile(String name, Long ID, String MACaddress) {
        this.name = name;
        this.ID = ID;
        this.MACaddress = MACaddress;
    }

    @Generated(hash = 782787822)
    public Profile() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1159184896)
    public List<Controller> getControllers() {
        if (controllers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ControllerDao targetDao = daoSession.getControllerDao();
            List<Controller> controllersNew = targetDao
                    ._queryProfile_Controllers(ID);
            synchronized (this) {
                if (controllers == null) {
                    controllers = controllersNew;
                }
            }
        }
        return controllers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1197633137)
    public synchronized void resetControllers() {
        controllers = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1351849779)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProfileDao() : null;
    }

    public String getMACaddress() {
        return this.MACaddress;
    }

    public void setMACaddress(String MACaddress) {
        this.MACaddress = MACaddress;
    }
    
}
