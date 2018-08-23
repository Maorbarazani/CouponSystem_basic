package b.connections;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import a.dbTables.DatabaseUtil;
import x.exceptions.ConnectionPoolException;
import z.utilities.ResourceFetcher;

public class ConnectionPool {

	private Set<Connection> connections = new HashSet<>();
	private static final int MAX_CON = 10;
	private static ConnectionPool instance;

	/**
	 * ConnectionPool CTOR, reads the url file and uses the driver manager to open
	 * connections to the DB.
	 * 
	 * @throws ConnectionPoolException
	 */
	private ConnectionPool() throws ConnectionPoolException {
		super();
		// get DB url:
		String url = null;
		try {
			url = ResourceFetcher.getUrl();
		} catch (IOException e1) {
			throw new ConnectionPoolException(
					"could not complete the ConnectionPool CTOR: could not find classpath resource file", e1);
		}

		// create DB if non-existent:
		Connection temp = null;
		try {
			System.out.println("## Trying to connect to existing DB... ##");
			temp = DriverManager.getConnection(url);
		} catch (SQLException e1) {
			System.out.println("## DB not found. Creating database: ##");
			DatabaseUtil.createAllTables();
		} finally {
			if (temp != null) {
				try {
					temp.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			}

		}

		// open connections to existing DB:
		while (connections.size() < MAX_CON) {
			try {
				DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
				Connection con = DriverManager.getConnection(url);
				connections.add(con);
			} catch (SQLException e) {
				throw new ConnectionPoolException(
						"could not complete the ConnectionPool CTOR: SQLException while trying to get connections", e);
			}

		}

	}

	/*
	 * this static initializer is used to create the singleton instance.
	 */
	static {
		try {
			// instance of singleton:
			instance = new ConnectionPool();
		} catch (ConnectionPoolException e) {
			System.err.println("ERROR: something went wrong. cannot initialize a ConnectionPool instance. cause:\n"
					+ e.getMessage());
		}

	}

	// Instance getter:
	/**
	 * used to return the 1 instance of the ConnectionPool singleton, to allow
	 * access to it's other methods on the DAO level.
	 * 
	 * @return {@link ConnectionPool}
	 * @throws ConnectionPoolException
	 */
	public synchronized static ConnectionPool getInstance() throws ConnectionPoolException {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	// pool methods:
	/**
	 * this method is used to get an active and available connection out of the
	 * connection pool's set. waits if there are no available connections, and get's
	 * notified by the returnConnection method to check for available connections
	 * again. removes the returned connection from the 'available' pool.
	 * 
	 * @return {@link Connection}
	 * @throws ConnectionPoolException
	 */
	public synchronized Connection getConnection() throws ConnectionPoolException {
		while (connections.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new ConnectionPoolException("'getConnection' wait was interrupted", e);
			}
		}
		Iterator<Connection> it = connections.iterator();
		Connection con = it.next();
		it.remove();
		return con;
	}

	/**
	 * get's an active connection and returns it to the available connection pool.
	 * once done, notifies.
	 * 
	 * @param Connection
	 */
	public synchronized void returnConnection(Connection con) {
		connections.add(con);
		notifyAll();
	}

	/**
	 * this method closes all active connections. should only be used on SYSTEM
	 * SHUTDOWN. closes all connections it can find on the pool, and waits to be
	 * notified if an active connection has returned to the pool, available for
	 * closure.
	 * 
	 * @throws ConnectionPoolException
	 * 
	 * @throws SQLException
	 */
	public synchronized void closeAllConnections() throws ConnectionPoolException {
		int counter = 0;
		while (counter < MAX_CON) {
			// waits for a connection to be available for closure
			while (connections.isEmpty()) {
				try {
					wait();
					// System.out.println("XX inside 1st small while");
				} catch (InterruptedException e) {
					throw new ConnectionPoolException("'closeAllConnections' wait was interrupted", e);

				}
			}
			// try and close all connections currently active in the pool, and increment the
			// counter
			while (!connections.isEmpty()) {
				// System.out.println("XX inside 2nd small while");

				Iterator<Connection> iterator = connections.iterator();
				Connection currentConnection = iterator.next();

				try {
					currentConnection.close();
					connections.remove(currentConnection);
					counter++;
				} catch (SQLException e) {
					throw new ConnectionPoolException("SQLException occured while trying to close all connections.", e);
				}

			}

		}
		System.out.println("\n## Succesfully closed all connections; ##\n");
	}

}
