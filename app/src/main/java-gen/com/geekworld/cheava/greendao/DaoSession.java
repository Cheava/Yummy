package com.geekworld.cheava.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.geekworld.cheava.greendao.ScreenContent;
import com.geekworld.cheava.greendao.ScreenImage;

import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig screenContentDaoConfig;
    private final DaoConfig screenImageDaoConfig;

    private final ScreenContentDao screenContentDao;
    private final ScreenImageDao screenImageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        screenContentDaoConfig = daoConfigMap.get(ScreenContentDao.class).clone();
        screenContentDaoConfig.initIdentityScope(type);

        screenImageDaoConfig = daoConfigMap.get(ScreenImageDao.class).clone();
        screenImageDaoConfig.initIdentityScope(type);

        screenContentDao = new ScreenContentDao(screenContentDaoConfig, this);
        screenImageDao = new ScreenImageDao(screenImageDaoConfig, this);

        registerDao(ScreenContent.class, screenContentDao);
        registerDao(ScreenImage.class, screenImageDao);
    }

    public void clear() {
        screenContentDaoConfig.getIdentityScope().clear();
        screenImageDaoConfig.getIdentityScope().clear();
    }

    public ScreenContentDao getScreenContentDao() {
        return screenContentDao;
    }

    public ScreenImageDao getScreenImageDao() {
        return screenImageDao;
    }

}
