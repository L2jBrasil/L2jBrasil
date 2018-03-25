/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.util;

import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.L2GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Flood protector implementation.
 * 
 * @author fordfrog
 */
public final class FloodProtectorAction
{
        
        /**
         * Logger
         */
        private static final Logger _log = LoggerFactory.getLogger(FloodProtectorAction.class);
        /**
         * Client for this instance of flood protector.
         */
        private final L2GameClient _client;
        /**
         * Configuration of this instance of flood protector.
         */
        private final FloodProtectorConfig _config;
        /**
         * Next game tick when new request is allowed.
         */
        private volatile int _nextGameTick = GameTimeController.getGameTicks();
        /**
         * Request counter.
         */
        private AtomicInteger _count = new AtomicInteger(0);
        /**
         * Flag determining whether exceeding request has been logged.
         */
        private boolean _logged;
        /**
         * Flag determining whether punishment application is in progress so that we do not apply
         * punisment multiple times (flooding).
         */
        private volatile boolean _punishmentInProgress;
        
        /**
         * Creates new instance of FloodProtectorAction.
         * 
         * @param player
         *            player for which flood protection is being created
         * @param config
         *            flood protector configuration
         */
        public FloodProtectorAction(final L2GameClient client, final FloodProtectorConfig config)
        {
                super();
                _client = client;
                _config = config;
        }

        /**
         * Checks whether the request is flood protected or not.
         * 
         * @param command
         *            command issued or short command description
         * 
         * @return true if action is allowed, otherwise false
         */
        public boolean tryPerformAction(final String command)
        {
                final int curTick = GameTimeController.getGameTicks();
                
                if (curTick < _nextGameTick || _punishmentInProgress)
                {
                        if (_config.LOG_FLOODING && !_logged && _log.isWarnEnabled())
                        {
                                log(" called command ", command, " ~", String.valueOf((_config.FLOOD_PROTECTION_INTERVAL - (_nextGameTick - curTick)) * GameTimeController.MILLIS_IN_TICK), " ms after previous command");
                                _logged = true;
                        }
                        
                        _count.incrementAndGet();
                        
                        if (!_punishmentInProgress && _config.PUNISHMENT_LIMIT > 0 && _count.get() >= _config.PUNISHMENT_LIMIT && _config.PUNISHMENT_TYPE != null)
                        {
                                _punishmentInProgress = true;
                                
                                if ("kick".equals(_config.PUNISHMENT_TYPE))
                                {
                                        kickPlayer();
                                }
                                else if ("ban".equals(_config.PUNISHMENT_TYPE))
                                {
                                        banAccount();
                                }
                                else if ("jail".equals(_config.PUNISHMENT_TYPE))
                                {
                                        jailChar();
                                }
                                
                                _punishmentInProgress = false;
                        }
                        
                        return false;
                }
                
                if (_count.get() > 0)
                {
                        if (_config.LOG_FLOODING && _log.isWarnEnabled())
                        {
                                log(" issued ", String.valueOf(_count), " extra requests within ~", String.valueOf(_config.FLOOD_PROTECTION_INTERVAL * GameTimeController.MILLIS_IN_TICK), " ms");
                        }
                }
                
                _nextGameTick = curTick + _config.FLOOD_PROTECTION_INTERVAL;
                _logged = false;
                _count.set(0);
                
                return true;
        }

        /**
         * Kick player from game (close network connection).
         */
        private void kickPlayer()
        {
                if (_client.getActiveChar() != null)
                        _client.getActiveChar().logout();
                else
                        _client.closeNow();

                if (_log.isWarnEnabled())
                {
                        log("kicked for flooding");
                }
        }

        /**
         * Bans char account and logs out the char.
         */
        private void banAccount()
        {
                if (_client.getActiveChar() != null)
                {
                        _client.getActiveChar().setPunishLevel(L2PcInstance.PunishLevel.ACC, _config.PUNISHMENT_TIME);

                        if (_log.isWarnEnabled())
                        {
                                log(" banned for flooding ", _config.PUNISHMENT_TIME <= 0 ? "forever" : "for " + _config.PUNISHMENT_TIME + " mins");
                        }

                        _client.getActiveChar().logout();
                }
                else
                        log(" unable to ban account: no active player");
        }
        
        /**
         * Jails char.
         */
        private void jailChar()
        {
                if (_client.getActiveChar() != null)
                {
                        _client.getActiveChar().setPunishLevel(L2PcInstance.PunishLevel.JAIL, _config.PUNISHMENT_TIME);
                        
                        if (_log.isWarnEnabled())
                        {
                                log(" jailed for flooding ", _config.PUNISHMENT_TIME <= 0 ? "forever" : "for " + _config.PUNISHMENT_TIME + " mins");
                        }
                }
                else
                        log(" unable to jail: no active player");
        }

        private void log(String... lines)
        {
                final StringBuilder output = StringUtil.startAppend(100, _config.FLOOD_PROTECTOR_TYPE, ": ");
                String address = null;
                try
                {
                        if (!_client.isDetached())
                                address = _client.getConnection().getInetAddress().getHostAddress();
                }
                catch (Exception e)
                {
                }

                switch (_client.getState())
                {
                        case IN_GAME:
                                if (_client.getActiveChar() != null)
                                {
                                        StringUtil.append(output, _client.getActiveChar().getName());
                                        StringUtil.append(output, "(", String.valueOf(_client.getActiveChar().getObjectId()),") ");
                                }
                        case AUTHED:
                                if (_client.getAccountName() != null)
                                        StringUtil.append(output, _client.getAccountName()," ");
                        case CONNECTED:
                                if (address != null)
                                        StringUtil.append(output, address);
                                break;
                        default:
                                throw new IllegalStateException("Missing state on switch");
                }

                StringUtil.append(output, lines);
                _log.warn(output.toString());
        }
}