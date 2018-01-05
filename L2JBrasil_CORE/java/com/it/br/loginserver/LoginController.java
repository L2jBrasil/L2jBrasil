package com.it.br.loginserver;

import com.it.br.Config;
import com.it.br.configuration.settings.LoginSettings;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.lib.Log;
import com.it.br.gameserver.network.gameserverpackets.ServerStatus;
import com.it.br.loginserver.GameServerTable.GameServerInfo;
import com.it.br.loginserver.crypt.ScrambledKeyPair;
import com.it.br.util.Rnd;
import com.it.br.util.Util;

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * This class ...
 *
 * @version $Revision: 1.7.4.3 $ $Date: 2005/03/27 15:30:09 $
 */
public class LoginController
{
	protected static final Logger _log = Logger.getLogger(LoginController.class.getName());

	private static LoginController _instance;

	/** Clients that are on the LS but arent assocated with a account yet*/
	protected Set<L2LoginClient> _clients = new HashSet<>();

	/** Authed Clients on LoginServer*/
	protected Map<String, L2LoginClient> _loginServerClients = new ConcurrentHashMap<>();

	private Map<InetAddress, BanInfo> _bannedIps = new ConcurrentHashMap<>();

	private Map<InetAddress, FailedLoginAttempt> _hackProtection;

	protected ScrambledKeyPair[] _keyPairs;

	protected byte[][] _blowfishKeys;
	private static final int BLOWFISH_KEYS = 20;

	public static void load() throws GeneralSecurityException
	{
		if (_instance == null)
		{
			_instance = new LoginController();
		}
		else
		{
			throw new IllegalStateException("LoginController can only be loaded a single time.");
		}
	}

	public static LoginController getInstance()
	{
		return _instance;
	}

	private LoginController() throws GeneralSecurityException
	{
		_log.info("Loading LoginContoller...");

		_hackProtection = new HashMap<>();

		_keyPairs = new ScrambledKeyPair[10];

		KeyPairGenerator keygen = null;

		keygen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		keygen.initialize(spec);

		//generate the initial set of keys
		for (int i = 0; i < 10; i++)
		{
			_keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
		}
		_log.info("Cached 10 KeyPairs for RSA communication");

		testCipher((RSAPrivateKey) _keyPairs[0]._pair.getPrivate());

		// Store keys for blowfish communication
		generateBlowFishKeys();
	}

	/**
	 * This is mostly to force the initialization of the Crypto Implementation, avoiding it being done on runtime when its first needed.<BR>
	 * In short it avoids the worst-case execution time on runtime by doing it on loading.
	 * @param key Any private RSA Key just for testing purposes.
	 * @throws GeneralSecurityException if a underlying exception was thrown by the Cipher
	 */
	private static void testCipher(RSAPrivateKey key) throws GeneralSecurityException
	{
		// avoid worst-case execution, KenM
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
		rsaCipher.init(Cipher.DECRYPT_MODE, key);
	}

	private void generateBlowFishKeys()
	{
		_blowfishKeys = new byte[BLOWFISH_KEYS][16];

		for (int i = 0; i < BLOWFISH_KEYS; i++)
		{
			for (int j = 0; j < _blowfishKeys[i].length; j++)
			{
				_blowfishKeys[i][j] = (byte) (Rnd.nextInt(255)+1);
			}
		}
		_log.info("Stored "+_blowfishKeys.length+" keys for Blowfish communication");
	}

	/**
	 * @return Returns a random key
	 */
	public byte[] getBlowfishKey()
	{
		return _blowfishKeys[(int) (Math.random()*BLOWFISH_KEYS)];
	}

	public void addLoginClient(L2LoginClient client)
	{
		synchronized (_clients)
		{
			_clients.add(client);
		}
	}

	public void removeLoginClient(L2LoginClient client)
	{
		synchronized (_clients)
		{
			_clients.remove(client);
		}
	}

	public SessionKey assignSessionKeyToClient(String account, L2LoginClient client)
	{
		SessionKey key;

		key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
		_loginServerClients.put(account, client);
		return key;
	}

	public void removeAuthedLoginClient(String account)
	{
		_loginServerClients.remove(account);
	}

	public boolean isAccountInLoginServer(String account)
	{
		return _loginServerClients.containsKey(account);
	}

	public L2LoginClient getAuthedClient(String account)
	{
		return _loginServerClients.get(account);
	}

	public static enum AuthLoginResult { INVALID_PASSWORD, ACCOUNT_BANNED, ALREADY_ON_LS, ALREADY_ON_GS, AUTH_SUCCESS };

	public AuthLoginResult tryAuthLogin(String account, String password, L2LoginClient client) throws HackingException
	{
		AuthLoginResult ret = AuthLoginResult.INVALID_PASSWORD;
		// check auth
		if (loginValid(account, password, client))
		{
			// login was successful, verify presence on Gameservers
			ret = AuthLoginResult.ALREADY_ON_GS;
			if (!isAccountInAnyGameServer(account))
			{
				// account isnt on any GS verify LS itself
				ret = AuthLoginResult.ALREADY_ON_LS;

				// dont allow 2 simultaneous login
				synchronized (_loginServerClients)
				{
					if (!_loginServerClients.containsKey(account))
					{
						_loginServerClients.put(account, client);
						ret = AuthLoginResult.AUTH_SUCCESS;

						// remove him from the non-authed list
						removeLoginClient(client);
					}
				}
			}
		}
		else
		{
			if (client.getAccessLevel() < 0)
			{
				ret = AuthLoginResult.ACCOUNT_BANNED;
			}
		}
		return ret;
	}

	/**
	 * Adds the address to the ban list of the login server, with the given duration.
	 *
	 * @param address The Address to be banned.
	 * @param expiration Timestamp in miliseconds when this ban expires
	 * @throws UnknownHostException if the address is invalid.
	 */
	public void addBanForAddress(String address, long expiration) throws UnknownHostException
	{
		InetAddress netAddress = InetAddress.getByName(address);
		_bannedIps.put(netAddress, new BanInfo(netAddress,  expiration));
	}

	/**
	 * Adds the address to the ban list of the login server, with the given duration.
	 *
	 * @param address The Address to be banned.
	 * @param duration is miliseconds
	 */
	public void addBanForAddress(InetAddress address, long duration)
	{
		_bannedIps.put(address, new BanInfo(address,  System.currentTimeMillis() + duration));
	}

	public boolean isBannedAddress(InetAddress address)
	{
		BanInfo bi = _bannedIps.get(address);
		if (bi != null)
		{
			if (bi.hasExpired())
			{
				_bannedIps.remove(address);
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public Map<InetAddress, BanInfo> getBannedIps()
	{
		return _bannedIps;
	}


	/**
	 * Remove the specified address from the ban list
	 * @param address The address to be removed from the ban list
	 * @return true if the ban was removed, false if there was no ban for this ip
	 */
	public boolean removeBanForAddress(InetAddress address)
	{
		return _bannedIps.remove(address) != null;
	}

	/**
	 * Remove the specified address from the ban list
	 * @param address The address to be removed from the ban list
	 * @return true if the ban was removed, false if there was no ban for this ip or the address was invalid.
	 */
	public boolean removeBanForAddress(String address)
	{
		try
		{
			return this.removeBanForAddress(InetAddress.getByName(address));
		}
		catch (UnknownHostException e)
		{
			return false;
		}
	}

	public SessionKey getKeyForAccount(String account)
	{
		L2LoginClient client = _loginServerClients.get(account);
		if (client != null)
		{
			return client.getSessionKey();
		}
		return null;
	}

	public int getOnlinePlayerCount(int serverId)
	{
		GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(serverId);
		if (gsi != null && gsi.isAuthed())
		{
			return gsi.getCurrentPlayerCount();
		}
		return 0;
	}

	public boolean isAccountInAnyGameServer(String account)
	{
		Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
			{
				return true;
			}
		}
		return false;
	}

	public GameServerInfo getAccountOnGameServer(String account)
	{
		Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
			{
				return gsi;
			}
		}
		return null;
	}

	public int getTotalOnlinePlayerCount()
	{
		int total = 0;
		Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			if (gsi.isAuthed())
			{
				total += gsi.getCurrentPlayerCount();
			}
		}
		return total;
	}

	public int getMaxAllowedOnlinePlayers(int id)
	{
		GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(id);
		if (gsi != null)
		{
			return gsi.getMaxPlayers();
		}
		return 0;
	}

	public boolean isLoginPossible(L2LoginClient client, int serverId)
	{
		GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(serverId);
		int access = client.getAccessLevel();
		if (gsi != null && gsi.isAuthed())
		{
			boolean loginOk = (gsi.getCurrentPlayerCount() < gsi.getMaxPlayers() && gsi.getStatus() != ServerStatus.STATUS_GM_ONLY) || access >= 100;

			if (loginOk && client.getLastServer() != serverId)
			{
				Connection con = null;
				PreparedStatement statement = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();

					String stmt = "UPDATE accounts SET lastServer = ? WHERE login = ?";
					statement = con.prepareStatement(stmt);
					statement.setInt(1, serverId);
					statement.setString(2, client.getAccount());
					statement.executeUpdate();
					statement.close();
				}
				catch (Exception e)
				{
					_log.warning("Could not set lastServer: "+e);
				}
				finally
				{
					try { con.close(); } catch (Exception e) { }
					try { statement.close(); }
					catch (Exception e) { }
				}
			}
			return loginOk;
		}
		return false;
	}

	public void setAccountAccessLevel(String account, int banLevel)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			String stmt = "UPDATE accounts SET access_level=? WHERE login=?";
			statement = con.prepareStatement(stmt);
			statement.setInt(1, banLevel);
			statement.setString(2, account);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Could not set accessLevel: "+e);
		}
		finally
		{
			try
			{
				statement.close();
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public boolean isGM(String user)
	{
		boolean ok = false;
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT access_level FROM accounts WHERE login=?");
			statement.setString(1, user);
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				int accessLevel = rset.getInt(1);
				if (accessLevel >= 100)
					ok = true;
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("could not check gm state:" + e);
			ok = false;
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
			try { statement.close(); } catch (Exception e) {}
		}
		return ok;
	}

	/**
	 * <p>This method returns one of the cached {@link ScrambledKeyPair ScrambledKeyPairs} for communication with Login Clients.</p>
	 * @return a scrambled keypair
	 */
	public ScrambledKeyPair getScrambledRSAKeyPair()
	{
		return _keyPairs[Rnd.nextInt(10)];
	}

	/**
	 * user name is not case sensitive any more
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean loginValid(String user, String password, L2LoginClient client )// throws HackingException
	{
		boolean ok = false;
		InetAddress address = client.getConnection().getInetAddress();
		// log it anyway
		Log.add("'" + (user == null ? "null" : user) + "' " + (address == null ? "null" : address.getHostAddress()), "logins_ip");

		// player disconnected meanwhile
		if (address == null)
		{
			return false;
		}

		Connection con = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);

			byte[] expected = null;
			int access = 0;
			int lastServer = 1;

			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT password, access_level, lastServer FROM accounts WHERE login=?");
			statement.setString(1, user);
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				expected = Base64.getDecoder().decode(rset.getString("password"));
				access = rset.getInt("access_level");
				lastServer = rset.getInt("lastServer");
				if (lastServer <= 0) lastServer = 1; // minServerId is 1 in Interlude
				if (Config.DEBUG) _log.fine("account exists");
			}
			rset.close();
			statement.close();

			// if account doesnt exists
			if (expected == null)
			{
				LoginSettings loginSettings = getSettings(LoginSettings.class);
				if (loginSettings.isAutoCreateAccountEnabled())
				{
					if ((user.length() >= 2) && (user.length() <= 14))
					{
						statement = con.prepareStatement("INSERT INTO accounts (login,password,lastactive,access_level,lastIP) values(?,?,?,?,?)");
						statement.setString(1, user);
						statement.setString(2, Base64.getEncoder().encodeToString(hash));
						statement.setLong(3, System.currentTimeMillis());
						statement.setInt(4, 0);
						statement.setString(5, address.getHostAddress());
						statement.execute();
						statement.close();

						_log.info("created new account for " + user);
						return true;

					}
					_log.warning("Invalid username creation/use attempt: " + user);
					return false;
				}
				_log.warning("account missing for user " + user);
				return false;
			}
			else
			{
				// is this account banned?
				if (access < 0)
				{
					client.setAccessLevel(access);
					return false;
				}

				// check password hash
				ok = true;
				for (int i = 0; i < expected.length; i++)
				{
					if (hash[i] != expected[i])
					{
						ok = false;
						break;
					}
				}
			}

			if (ok)
			{
				client.setAccessLevel(access);
				client.setLastServer(lastServer);
				statement = con.prepareStatement("UPDATE accounts SET lastactive=?, lastIP=? WHERE login=?");
				statement.setLong(1, System.currentTimeMillis());
				statement.setString(2, address.getHostAddress());
				statement.setString(3, user);
				statement.execute();
				statement.close();
			}
		}
		catch (Exception e)
		{
			_log.warning("Could not check password:" + e);
			ok = false;
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}

		if (!ok)
		{
			Log.add("'" + user + "' " + address.getHostAddress(), "logins_ip_fails");

			FailedLoginAttempt failedAttempt = _hackProtection.get(address);
			int failedCount;
			if (failedAttempt == null)
			{
				_hackProtection.put(address, new FailedLoginAttempt(address, password));
				failedCount = 1;
			}
			else
			{
				failedAttempt.increaseCounter(password);
				failedCount = failedAttempt.getCount();
			}

			LoginSettings loginSettings = getSettings(LoginSettings.class);
			if (failedCount >= loginSettings.getTriesBeforeBan())
			{
				_log.info("Banning '"+address.getHostAddress()+"' for "+ loginSettings.getTimeBlockAfterBan()+" seconds due to "+failedCount+" invalid user/pass attempts");
				this.addBanForAddress(address, Util.secondsToMilliseconds(loginSettings.getTimeBlockAfterBan()));
			}
		}
		else
		{
			_hackProtection.remove(address);
			Log.add("'" + user + "' " + address.getHostAddress(), "logins_ip");
		}

		return ok;
	}

	public boolean loginBanned(String user)
	{
		boolean ok = false;

		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT access_level FROM accounts WHERE login=?");
			statement.setString(1, user);
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				int accessLevel = rset.getInt(1);
				if (accessLevel < 0) ok = true;
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			// digest algo not found ??
			// out of bounds should not be possible
			_log.warning("could not check ban state:" + e);
			ok = false;
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}

		return ok;
	}

	class FailedLoginAttempt
	{
		//private InetAddress _ipAddress;
		private int _count;
		private long _lastAttempTime;
		private String _lastPassword;

		public FailedLoginAttempt(InetAddress address, String lastPassword)
		{
			//_ipAddress = address;
			_count = 1;
			_lastAttempTime = System.currentTimeMillis();
			_lastPassword = lastPassword;
		}

		public void increaseCounter(String password)
		{
			if (!_lastPassword.equals(password))
			{
				// check if theres a long time since last wrong try
				if (System.currentTimeMillis() - _lastAttempTime < 300*1000)
				{
					_count++;
				}
				else
				{
					// restart the status
					_count = 1;

				}
				_lastPassword = password;
				_lastAttempTime = System.currentTimeMillis();
			}
			else //trying the same password is not brute force
			{
				_lastAttempTime = System.currentTimeMillis();
			}
		}

		public int getCount()
		{
			return _count;
		}
	}

	class BanInfo
	{
		private InetAddress _ipAddress;
		// Expiration
		private long _expiration;

		public BanInfo(InetAddress ipAddress, long expiration)
		{
			_ipAddress = ipAddress;
			_expiration = expiration;
		}

		public InetAddress getAddress()
		{
			return _ipAddress;
		}

		public boolean hasExpired()
		{
			return System.currentTimeMillis() > _expiration && _expiration > 0;
		}
	}
}