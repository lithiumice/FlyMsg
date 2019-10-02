package com.speedystone.greendaodemo.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import online.hualin.flymsg.db.FileHistory;
import online.hualin.flymsg.db.ChatHistory;
import online.hualin.flymsg.db.Poetry;

import com.speedystone.greendaodemo.db.FileHistoryDao;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.PoetryDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig fileHistoryDaoConfig;
    private final DaoConfig chatHistoryDaoConfig;
    private final DaoConfig poetryDaoConfig;

    private final FileHistoryDao fileHistoryDao;
    private final ChatHistoryDao chatHistoryDao;
    private final PoetryDao poetryDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        fileHistoryDaoConfig = daoConfigMap.get(FileHistoryDao.class).clone();
        fileHistoryDaoConfig.initIdentityScope(type);

        chatHistoryDaoConfig = daoConfigMap.get(ChatHistoryDao.class).clone();
        chatHistoryDaoConfig.initIdentityScope(type);

        poetryDaoConfig = daoConfigMap.get(PoetryDao.class).clone();
        poetryDaoConfig.initIdentityScope(type);

        fileHistoryDao = new FileHistoryDao(fileHistoryDaoConfig, this);
        chatHistoryDao = new ChatHistoryDao(chatHistoryDaoConfig, this);
        poetryDao = new PoetryDao(poetryDaoConfig, this);

        registerDao(FileHistory.class, fileHistoryDao);
        registerDao(ChatHistory.class, chatHistoryDao);
        registerDao(Poetry.class, poetryDao);
    }
    
    public void clear() {
        fileHistoryDaoConfig.clearIdentityScope();
        chatHistoryDaoConfig.clearIdentityScope();
        poetryDaoConfig.clearIdentityScope();
    }

    public FileHistoryDao getFileHistoryDao() {
        return fileHistoryDao;
    }

    public ChatHistoryDao getChatHistoryDao() {
        return chatHistoryDao;
    }

    public PoetryDao getPoetryDao() {
        return poetryDao;
    }

}