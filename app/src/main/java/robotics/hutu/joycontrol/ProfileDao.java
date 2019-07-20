package robotics.hutu.joycontrol;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PROFILE".
*/
public class ProfileDao extends AbstractDao<Profile, Long> {

    public static final String TABLENAME = "PROFILE";

    /**
     * Properties of entity Profile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Name = new Property(0, String.class, "name", false, "NAME");
        public final static Property ID = new Property(1, Long.class, "ID", true, "_id");
        public final static Property MACaddress = new Property(2, String.class, "MACaddress", false, "MACADDRESS");
    }

    private DaoSession daoSession;


    public ProfileDao(DaoConfig config) {
        super(config);
    }
    
    public ProfileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PROFILE\" (" + //
                "\"NAME\" TEXT," + // 0: name
                "\"_id\" INTEGER PRIMARY KEY ," + // 1: ID
                "\"MACADDRESS\" TEXT);"); // 2: MACaddress
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PROFILE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Profile entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(2, ID);
        }
 
        String MACaddress = entity.getMACaddress();
        if (MACaddress != null) {
            stmt.bindString(3, MACaddress);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Profile entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(2, ID);
        }
 
        String MACaddress = entity.getMACaddress();
        if (MACaddress != null) {
            stmt.bindString(3, MACaddress);
        }
    }

    @Override
    protected final void attachEntity(Profile entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }    

    @Override
    public Profile readEntity(Cursor cursor, int offset) {
        Profile entity = new Profile( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // name
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // ID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // MACaddress
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Profile entity, int offset) {
        entity.setName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setMACaddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Profile entity, long rowId) {
        entity.setID(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Profile entity) {
        if(entity != null) {
            return entity.getID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Profile entity) {
        return entity.getID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}