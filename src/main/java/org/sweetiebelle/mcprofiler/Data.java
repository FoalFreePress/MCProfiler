package org.sweetiebelle.mcprofiler;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.connection.ConnectionManager;
import org.sweetiebelle.lib.exceptions.NoDataException;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.ConsoleAccount;
import org.sweetiebelle.mcprofiler.api.account.Note;
import org.sweetiebelle.mcprofiler.api.account.alternate.AltAccount;
import org.sweetiebelle.mcprofiler.api.account.alternate.BaseAccount;
import org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt;
import org.sweetiebelle.mcprofiler.api.response.NameResponse;

/**
 * This class handles data transfer to and from the SQL server.
 *
 */
class Data {

    private ConnectionManager connection;
    private Logger logger;
    private Settings s;

    Data(MCProfiler p, ConnectionManager connection, Settings s) {
        logger = p.getLogger();
        this.s = s;
        this.connection = connection;
        createTables();
    }

    /**
     * Adds the note to the player
     *
     * @param pUUID
     *            the user
     * @param pStaffName
     *            the staff who added it
     * @param pNote
     *            the note
     */
    void addNoteToUser(UUID pPlayer, UUID pStaff, String pNote) {
        try {
            PreparedStatement statement = connection.getStatement(String.format("INSERT INTO %snotes (uuid, time, staffuuid, note) VALUES (?, NOW(), ?, ?);", s.dbPrefix));
            statement.setString(1, pPlayer.toString());
            statement.setString(2, pStaff.toString());
            statement.setString(3, pNote);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            error(e);
        }
    }

    void createProfile(Account account) {
        try {
            PreparedStatement statement = connection.getStatement("INSERT INTO " + s.dbPrefix + "profiles (uuid, lastKnownName, ip, laston, lastpos) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, account.getUUID().toString());
            statement.setString(2, account.getName());
            statement.setString(3, account.getIP());
            statement.setNull(4, Types.TIMESTAMP);
            statement.setString(5, account.getLocation());
        } catch (SQLException e) {
            error(e);
        }
    }

    /**
     * Gets an account from a player name.
     *
     * @param playername
     *            player's name
     * @return an {@link Account} if it exists, or null
     * @throws NoDataException
     *             if no such account exists
     *
     */
    Optional<Account> getAccount(String name, boolean needsLastNames) {
        UUID uuid = null;
        String laston = null;
        String location = null;
        String ip = null;
        Note[] notes = null;
        NameResponse[] names = null;
        ResultSet rs;
        String query = "SELECT * FROM " + s.dbPrefix + "profiles where lastKnownName = \"" + name + "\";";
        // Get Basic player information.
        try {
            rs = getResultSet(query);
            if (rs.next()) {
                String stringuuuid = rs.getString("uuid");
                if (stringuuuid == null || stringuuuid.equals("") || stringuuuid.equalsIgnoreCase("null"))
                    throw new NoDataException("Null or missing UUID in the table.");
                uuid = UUID.fromString(rs.getString("uuid"));
                name = rs.getString("lastKnownName");
                laston = rs.getString("laston");
                ip = rs.getString("ip");
                location = rs.getString("lastpos");
            } else
                throw new NoDataException("No Account found.");
            rs = getResultSet("SELECT * FROM " + s.dbPrefix + "notes where UUID = \"" + uuid.toString() + "\";");
            ArrayList<Note> nList = new ArrayList<Note>();
            while (rs.next()) {
                nList.add(new Note(rs.getString("staffuuid"), rs.getString("time"), rs.getString("note")));
            }
            notes = nList.toArray(new Note[nList.size()]);
            rs.close();
        } catch (SQLException | NoDataException e) {
            error(e);
            return Optional.<Account>empty();
        }
        // Get their associated names.
        try {
            if (needsLastNames)
                names = NamesFetcher.getPreviousNames(uuid);
        } catch (IOException e) {
            error(e);
        }
        return Optional.<Account>of(new Account(uuid, name, laston, location, ip, notes, names, true));
    }

    /**
     * Gets an account from a UUID
     *
     * @param uuid
     *            player's UUID
     * @return an {@link Account} if it exists, or null
     * @throws NoDataException
     *             if no such account exists
     */
    Optional<Account> getAccount(UUID uuid, boolean needsLastNames) {
        if (uuid.equals(SweetieLib.CONSOLE_UUID))
            return Optional.of(ConsoleAccount.getInstance());
        String name = null;
        String laston = null;
        String location = null;
        String ip = null;
        Note[] notes = null;
        NameResponse[] names = null;
        ResultSet rs;
        try {
            rs = getResultSet("SELECT * FROM " + s.dbPrefix + "profiles where UUID = \"" + uuid.toString() + "\";");
            if (rs.next()) {
                name = rs.getString("lastKnownName");
                laston = rs.getString("laston");
                ip = rs.getString("ip");
                location = rs.getString("lastpos");
            } else {
                rs.close();
                throw new NoDataException("No Account found.");
            }
            rs.close();
            rs = getResultSet("SELECT * FROM " + s.dbPrefix + "notes where UUID = \"" + uuid.toString() + "\";");
            ArrayList<Note> nList = new ArrayList<Note>();
            while (rs.next()) {
                nList.add(new Note(rs.getString("staffuuid"), rs.getString("time"), rs.getString("note")));
            }
            notes = nList.toArray(new Note[nList.size()]);
            rs.close();
        } catch (SQLException | NoDataException e) {
            error(e);
            return Optional.<Account>empty();
        }
        try {
            if (needsLastNames)
                names = NamesFetcher.getPreviousNames(uuid);
        } catch (IOException e) {
            error(e);
        }
        return Optional.<Account>of(new Account(uuid, name, laston, location, ip, notes, names, true));
    }

    /**
     * Returns a String of all the UUIDs, comma separated, of the player's UUID given.
     *
     * @param pUUID
     *            Player UUID
     * @param isRecursive
     *            Is the search recursive?
     * @return the alt accounts of the player, or null if an error occurs.
     */
    @SuppressWarnings({ "unchecked" })
    ArrayList<? extends BaseAccount> getAltsOfPlayer(UUID pUUID, boolean isRecursive) {
        ResultSet rs;
        // Who needs casts anyway?
        ArrayList<? extends BaseAccount> array = null;
        try {
            if (isRecursive) {
                array = new ArrayList<AltAccount>(0);
                rs = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE uuid = \"" + pUUID.toString() + "\";");
                while (rs.next()) {
                    ResultSet ipSet = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE ip = \"" + rs.getString("ip") + "\";");
                    while (ipSet.next())
                        ((ArrayList<AltAccount>) array).add(new AltAccount(UUID.fromString(ipSet.getString("uuid")), ipSet.getString("ip")));
                    ipSet.close();
                }
                return recursivePlayerSearch((ArrayList<AltAccount>) array);
            }
            array = new ArrayList<UUIDAlt>(0);
            rs = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE uuid = \"" + pUUID.toString() + "\";");
            while (rs.next()) {
                ResultSet ipSet = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE ip = \"" + rs.getString("ip") + "\";");
                while (ipSet.next())
                    ((ArrayList<UUIDAlt>) array).add(new UUIDAlt(UUID.fromString(ipSet.getString("uuid")), ipSet.getString("ip")));
                ipSet.close();
            }
            return array;
        } catch (SQLException e) {
            error(e);
            return array;
        }
    }

    /**
     * Get a String of IPs from an account
     *
     * @param a
     *            the account
     * @return the IP string, separated by commas, or null if an error occurs.
     */
    ArrayList<String> getIPsByPlayer(Account a) {
        ArrayList<String> list = new ArrayList<String>(0);
        try {
            ResultSet rs = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE uuid = \"" + a.getUUID().toString() + "\";");
            while (rs.next())
                list.add(rs.getString("ip"));
            rs.close();
            return list;
        } catch (SQLException e) {
            error(e);
            return list;
        }
    }

    /**
     * Gets an IP string of UUIDs separated by commands from one IP.
     *
     * @param pIP
     *            the IP
     * @return the string or null if an error occurs.
     */
    ArrayList<String> getUsersAssociatedWithIP(String pIP) {
        ArrayList<String> uuids = new ArrayList<String>(0);
        try {
            ResultSet rs = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE ip = \"" + pIP + "\";");
            // Find all the uuids linked to the specific ip
            while (rs.next())
                uuids.add(rs.getString("uuid"));
            rs.close();
            return uuids;
        } catch (SQLException e) {
            error(e);
        }
        return uuids;
    }

    /**
     * Sets the player's location
     *
     * @param pUUID
     *            the player's UUID
     * @param pLocation
     *            their location
     */
    void setPlayerLastPosition(UUID pUUID, String location) {
        // Make sure that the data for the player is valid, if the player has logged on before
        try {
            PreparedStatement statement = connection.getStatement(String.format("UPDATE %sprofiles SET lastpos = ? WHERE uuid = ?;", s.dbPrefix));
            statement.setString(1, location);
            statement.setString(2, pUUID.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            error(e);
        }
    }

    /**
     * Stores the player's IP
     *
     * @param pUUID
     *            the player's uuid
     * @param pIP
     *            their ip
     */
    void storePlayerIP(UUID pUUID, String pIP) {
        ResultSet rs;
        try {
            rs = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE ip = \"" + pIP + "\" AND uuid = \"" + pUUID.toString() + "\";");
            if (rs.next())
                return;
            // Having multiple UUIDs and IPs in an SQL table is THE WHOLE POINT in an sql table!
            PreparedStatement statement = connection.getStatement(String.format("INSERT INTO %siplog (uuid, ip) VALUES (?, ?);", s.dbPrefix));
            statement.setString(1, pUUID.toString());
            statement.setString(2, pIP);
            statement.executeUpdate();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            error(e);
        }
    }

    /**
     * Update general player information.
     *
     * @param pUUID
     *            Their UUID
     * @param newName
     *            their Name
     * @param newIP
     *            Their IP
     */
    void updatePlayerInformation(Account account) {
        try {
            PreparedStatement statement = connection.getStatement(String.format("UPDATE %sprofiles SET lastKnownName = ? WHERE uuid = ?;", s.dbPrefix));
            statement.setString(1, account.getName());
            statement.setString(2, account.getUUID().toString());
            statement.executeUpdate();
            statement.close();
            statement = connection.getStatement(String.format("UPDATE %sprofiles SET ip = ? WHERE uuid = ?;", s.dbPrefix));
            statement.setString(1, account.getIP());
            statement.setString(2, account.getUUID().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            error(e);
        }
    }

    /**
     * Creates a new table in the database
     *
     * @param pQuery
     *            the query
     * @return
     */
    private boolean createTable(String pQuery) {
        try {
            executeQuery(pQuery);
            return true;
        } catch (SQLException e) {
            error(e);
        }
        return false;
    }

    /**
     * Create tables if needed
     */
    private void createTables() {
        // Generate the information about the various tables
        String notes = "CREATE TABLE " + s.dbPrefix + "notes (noteid INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36) NOT NULL, time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, staffuuid VARCHAR(36) NOT NULL, note VARCHAR(255) NOT NULL)";
        String profiles = "CREATE TABLE " + s.dbPrefix + "profiles (profileid INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36) NOT NULL, lastKnownName VARCHAR(16) NOT NULL, ip VARCHAR(39), laston TIMESTAMP, lastpos VARCHAR(75))";
        String iplog = "CREATE TABLE " + s.dbPrefix + "iplog (ipid INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ip VARCHAR(36) NOT NULL, uuid VARCHAR(36) NOT NULL)";
        // Generate the database tables
        if (!tableExists(s.dbPrefix + "profiles"))
            createTable(profiles);
        if (!tableExists(s.dbPrefix + "iplog"))
            createTable(iplog);
        if (!tableExists(s.dbPrefix + "notes"))
            createTable(notes);
    }

    /**
     * Method used to handle errors
     *
     * @param e
     *            Exception
     */
    private void error(Throwable e) {
        if (s.printStackTraces) {
            e.printStackTrace();
            return;
        }
        if (e instanceof SQLException) {
            logger.severe("SQLException: " + e.getMessage());
            return;
        }
        if (e instanceof IllegalArgumentException)
            // It was probably someone not putting in a valid UUID, so we can ignore.
            // p.severe("IllegalArgumentException: " + e.getMessage());
            return;
        if (e instanceof NoDataException) {
            // If true, then it was caused by data in Account not being found.
            if (e.getMessage().equalsIgnoreCase("No Account found."))
                return;
            logger.severe("NoDataException: " + e.getMessage());
            return;
        }
        if (e instanceof NoClassDefFoundError)
            // Handle Plugins not found.
            // p.severe("NoClassDefFoundError: " + e.getMessage());
            return;
        if (e instanceof IOException) {
            logger.severe("IOException: " + e.getMessage());
            return;
        }
        // Or e.getCause();
        logger.severe("Unhandled Exception " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Executes an SQL query. Throws an exception to allow for other methods to handle it.
     *
     * @param query
     *            the query to execute
     * @return number of rows affected
     * @throws SQLException
     *             if an error occurred
     */
    private int executeQuery(String query) throws SQLException {
        return connection.executeUpdate(query);
    }

    /**
     * Private method for getting an SQL connection, then submitting a query. This method throws an SQL Exception to allow another method to handle it.
     *
     * @param query
     *            the query to get data from.
     * @return the data
     * @throws SQLException
     *             if an error occurs
     */
    private ResultSet getResultSet(String query) throws SQLException {
        return connection.executeQuery(query);
    }

    /**
     * Does a recursive player search with the given params
     *
     * @param uuidToIP
     *            A MultiMap associating UUIDs with IPs
     * @return A array of {@link AltAccounts} containing all the alts.
     * @throws SQLException
     *             if an error occurs, to allow the method that calls this function to catch it.
     */
    private ArrayList<AltAccount> recursivePlayerSearch(ArrayList<AltAccount> array) throws SQLException {
        int finalContinue = 0;
        ResultSet uuidSet;
        ResultSet ipSet;
        boolean ipSetComplete = true;
        boolean uuidSetComplete = true;
        // The size of the array is the number of times to iterate.
        for (int i = 0; i < array.size(); i++) {
            AltAccount a = new AltAccount(array.get(i).getUUID(), array.get(i).getIP());
            UUID uuid = a.getUUID();
            // Get the IPs where uuid is equal to the account's UUID
            uuidSet = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE uuid = \"" + uuid.toString() + "\";");
            while (uuidSet.next()) {
                UUID uuidUUID = UUID.fromString(uuidSet.getString("uuid"));
                String uuidIP = uuidSet.getString("ip");
                AltAccount uuidAlt = new AltAccount(uuidUUID, uuidIP);
                // If the map already contains the alt account, continue onto the next one.
                if (array.contains(uuidAlt))
                    continue;
                // If we got to this point, then there are ips with uuids we haven't found yet.
                uuidSetComplete = false;
                array.add(uuidAlt);
                ipSet = getResultSet("SELECT * FROM " + s.dbPrefix + "iplog WHERE ip = \"" + uuidIP + "\";");
                while (ipSet.next()) {
                    // Get the UUIDs associated with the IP.
                    AltAccount ipAlt = new AltAccount(UUID.fromString(ipSet.getString("uuid")), ipSet.getString("ip"));
                    // If we already have it, continue onto the next.
                    if (array.contains(ipAlt))
                        continue;
                    // If we reached this point, it means there were uuids with ips we haven't found yet.
                    ipSetComplete = false;
                    array.add(ipAlt);
                }
            }
            if (uuidSetComplete && ipSetComplete) {
                // Number of total times to iterate.
                finalContinue++;
                // If the number of times we've iterated equals the number of elements in the Array, it means we've found all the data. It's time to get out of here.
                if (finalContinue == array.size())
                    return array;
                continue;
            }
        }
        // There's still more stuff to find.
        // return ObjectArrays.concat(array, recursivePlayerSearch(array), AltAccount.class);
        array.addAll(recursivePlayerSearch(array));
        return array;
    }

    /**
     * checks if a table exists
     *
     * @param pTable
     *            the table name.
     * @return true if the table exists, or false if either the table does not exists, or another error occurs.
     */
    private boolean tableExists(String pTable) {
        try {
            return getResultSet("SELECT * FROM " + pTable) != null;
        } catch (SQLException e) {
            return false;
        }
    }
}
