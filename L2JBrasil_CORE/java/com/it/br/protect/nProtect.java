/*
 * nProtect create one in order to build support some protection!
 * Enclosing it kj2a specifically to support the protection of Free-Core.
 * The author of this product is unknown to me.
 * Everything I wrote importantly good luck to all.
 */
package com.it.br.protect;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.serverpackets.GameGuardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;

public class nProtect {
    private final static Logger _log = LoggerFactory.getLogger(nProtect.class);

    public static enum RestrictionType {
        RESTRICT_ENTER, RESTRICT_EVENT, RESTRICT_OLYMPIAD, RESTRICT_SIEGE
    }

    public class nProtectAccessor {
        public nProtectAccessor() {
        }

        public void setCheckGameGuardQuery(Method m) {
            nProtect.this._checkGameGuardQuery = m;
        }

        public void setStartTask(Method m) {
            nProtect.this._startTask = m;
        }

        public void setCheckRestriction(Method m) {
            nProtect.this._checkRestriction = m;
        }

        public void setSendRequest(Method m) {
            nProtect.this._sendRequest = m;
        }

        public void setCloseSession(Method m) {
            nProtect.this._closeSession = m;
        }

        public void setSendGGQuery(Method m) {
            nProtect.this._sendGGQuery = m;
        }

    }

    protected Method _checkGameGuardQuery = null;
    protected Method _startTask = null;
    protected Method _checkRestriction = null;
    protected Method _sendRequest = null;
    protected Method _closeSession = null;
    protected Method _sendGGQuery = null;
    private static nProtect _instance = null;

    public static nProtect getInstance() {
        if (_instance == null)
            _instance = new nProtect();
        return _instance;
    }

    private nProtect() {
        Class<?> clazz = null;
        try {
            try {
                clazz = Class.forName("com.fc.protection.main");
            } catch (ClassNotFoundException e) {
            }
            if (clazz != null) {
                Method m = clazz.getMethod("init", nProtectAccessor.class);
                if (m != null)
                    m.invoke(null, new nProtectAccessor());
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.warn("Error" + e.getMessage());
        }

    }

    public void sendGameGuardQuery(GameGuardQuery pkt) {
        try {
            if (_sendGGQuery != null)
                _sendGGQuery.invoke(pkt);

        } catch (Exception e) {
            //e.printStackTrace();
            //_log.warn("Error"+e.getMessage());
        }
    }

    public boolean checkGameGuardReply(L2GameClient cl, int[] reply) {
        try {
            if (_checkGameGuardQuery != null)
                return (Boolean) _checkGameGuardQuery.invoke(null, cl, reply);
        } catch (Exception e) {
            e.printStackTrace();
            _log.warn("Error" + e.getMessage());
        }
        return true;
    }

    public ScheduledFuture<?> startTask(L2GameClient client) {
        try {
            if (_startTask != null)
                return (ScheduledFuture<?>) _startTask.invoke(null, client);
        } catch (Exception e) {
            e.printStackTrace();
            _log.warn("Error" + e.getMessage());
        }
        return null;
    }

    public void sendRequest(L2GameClient cl) {
        if (_sendRequest != null)
            try {
                _sendRequest.invoke(null, cl);
            } catch (Exception e) {
                e.printStackTrace();
                _log.warn("Error" + e.getMessage());
            }
    }

    public void closeSession(L2GameClient cl) {
        if (_closeSession != null)
            try {
                _closeSession.invoke(null, cl);

            } catch (Exception e) {
                _log.warn("Error" + e.getMessage());
            }
    }

    public boolean checkRestriction(L2PcInstance player, RestrictionType type, Object... params) {
        try {
            if (_checkRestriction != null)
                return (Boolean) _checkRestriction.invoke(null, player, type, params);
        } catch (Exception e) {
            e.printStackTrace();
            _log.warn("Error" + e.getMessage());
        }
        return true;
    }
}
