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

import com.it.br.Config;
import com.it.br.gameserver.network.L2GameClient;

/**
 * Collection of flood protectors for single player.
 * 
 * @author fordfrog
 */
public final class FloodProtectors
{
        /**
         * Use-item flood protector.
         */
        private final FloodProtectorAction _useItem;
        /**
         * Roll-dice flood protector.
         */
        private final FloodProtectorAction _rollDice;
        /**
         * Firework flood protector.
         */
        private final FloodProtectorAction _firework;
        /**
         * Item-pet-summon flood protector.
         */
        private final FloodProtectorAction _itemPetSummon;
        /**
         * Hero-voice flood protector.
         */
        private final FloodProtectorAction _heroVoice;
        /**
         * Global-chat flood protector.
         */
        private final FloodProtectorAction _globalChat;
        /**
         * Subclass flood protector.
         */
        private final FloodProtectorAction _subclass;
        /**
         * Drop-item flood protector.
         */
        private final FloodProtectorAction _dropItem;
        /**
         * Server-bypass flood protector.
         */
        private final FloodProtectorAction _serverBypass;
        /**
         * Multisell flood protector.
         */
        private final FloodProtectorAction _multiSell;
        /**
         * Transaction flood protector.
         */
        private final FloodProtectorAction _transaction;
        /**
        * packet flood protector.
        */
        private final FloodProtectorAction _packet;
        /**
         * Crystallize-item flood protector.
         */
         private final FloodProtectorAction _crystallizeItem;
         /**
          * Trade-Chat flood protector.
          */
          private final FloodProtectorAction _tradeChat;
          /**
           * Deposit-Item flood protector.
           */
           private final FloodProtectorAction _depositItem;
           /**
            * Withdraw-Item flood protector.
            */
            private final FloodProtectorAction _withdrawItem;
        /**
         * banking system flood protector.
         */
         private final FloodProtectorAction _banking;
         /**
     	 * Say Action protector
     	 */
     	private final FloodProtectorAction _sayAction;
     	
        /**
         * Creates new instance of FloodProtectors.
         * @param client player for which the collection of flood protectors is being created.
         */
        public FloodProtectors(final L2GameClient client)
        {
                super();
                _useItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_USE_ITEM);
                _rollDice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ROLL_DICE);
                _firework = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_FIREWORK);
                _itemPetSummon = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ITEM_PET_SUMMON);
                _heroVoice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_HERO_VOICE);
                _globalChat = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_GLOBAL_CHAT);
                _subclass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SUBCLASS);
                _dropItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_DROP_ITEM);
                _serverBypass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SERVER_BYPASS);
                _multiSell = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MULTISELL);
                _transaction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_TRANSACTION);
                _packet = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_PACKET);
                _crystallizeItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_CRYSTALLIZE_ITEM);
                _tradeChat = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_TRADE_CHAT);
                _depositItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_DEPOSIT_ITEM);
                _withdrawItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_WITHDRAW_ITEM);
                _banking = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_BANKING_SYSTEM);
                _sayAction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SAY_ACTION);
        }
        
        /**
         * Returns {@link #_useItem}.
         * 
         * @return {@link #_useItem}
         */
        public FloodProtectorAction getUseItem()
        {
                return _useItem;
        }
        
        /**
         * Returns {@link #_rollDice}.
         * 
         * @return {@link #_rollDice}
         */
        public FloodProtectorAction getRollDice()
        {
                return _rollDice;
        }
        
        /**
         * Returns {@link #_firework}.
         * 
         * @return {@link #_firework}
         */
        public FloodProtectorAction getFirework()
        {
                return _firework;
        }
        
        /**
         * Returns {@link #_itemPetSummon}.
         * 
         * @return {@link #_itemPetSummon}
         */
        public FloodProtectorAction getItemPetSummon()
        {
                return _itemPetSummon;
        }
        
        /**
         * Returns {@link #_heroVoice}.
         * 
         * @return {@link #_heroVoice}
         */
        public FloodProtectorAction getHeroVoice()
        {
                return _heroVoice;
        }
        
        /**
         * Returns {@link #_globalChat}.
         * 
         * @return {@link #_globalChat}
         */
        public FloodProtectorAction getGlobalChat()
        {
                return _globalChat;
        }
        
        /**
         * Returns {@link #_subclass}.
         * 
         * @return {@link #_subclass}
         */
        public FloodProtectorAction getSubclass()
        {
                return _subclass;
        }
        
        /**
         * Returns {@link #_dropItem}.
         * 
         * @return {@link #_dropItem}
         */
        public FloodProtectorAction getDropItem()
        {
                return _dropItem;
        }
        
        /**
         * Returns {@link #_serverBypass}.
         * 
         * @return {@link #_serverBypass}
         */
        public FloodProtectorAction getServerBypass()
        {
                return _serverBypass;
        }

        /**
         * 
         * @return _multisell
         */
        public FloodProtectorAction getMultiSell()
        {
                return _multiSell;
        }

        /**
         * Returns {@link #_transaction}.
         * 
         * @return {@link #_transaction}
         */
        public FloodProtectorAction getTransaction()
        {
                return _transaction;
        }

        /**
         * Returns {@link #_packet}.
         * 
         * @return {@link #_packet}
         */
        public FloodProtectorAction getPacket()
        {
                return _packet;
        }

        /**
         * Returns {@link #_crystallizeItem}.
         * 
         * @return {@link #_crystallizeItem}
         */        
        public FloodProtectorAction getCrystallizeItem()
        {
                return _crystallizeItem;
        }

        /**
         * Returns {@link #_tradeChat}.
         * 
         * @return {@link #_tradeChat}
         */        
        public FloodProtectorAction getTradeVoice()
        {
                return _tradeChat;
        }

        /**
         * Returns {@link #_depositItem}.
         * 
         * @return {@link #_depositItem}
         */        
        public FloodProtectorAction getDepositItem()
        {
                return _depositItem;
        }

        /**
         * Returns {@link #_withdrawItem}.
         * 
         * @return {@link #_withdrawItem}
         */        
        public FloodProtectorAction getWithdrawItem()
        {
                return _withdrawItem;
        }

        /**
         * Returns {@link #_banking}.
         * 
         * @return {@link #_banking}
         */
        public FloodProtectorAction getBankingSystem()
        {
                return _banking;
        }
    	/**
    	 * Returns {@link #_sayAction}.
    	 * 
    	 * @return {@link #_sayAction}
    	 */
    	public FloodProtectorAction getSayAction()
    	{
    		return _sayAction;
    	}
}