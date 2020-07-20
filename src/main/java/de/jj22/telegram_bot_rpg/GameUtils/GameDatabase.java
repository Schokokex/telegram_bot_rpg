package de.jj22.telegram_bot_rpg.GameUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameDatabase {
	final private Connection connection;

	public GameDatabase(String filename) throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
		initDatabase();
	}

	public ResultSet getFirstEntity() throws SQLException {
		final var statement = preparedStatement("SELECT entity_id FROM FirstEntity");
		return statement.executeQuery();
	}

	private void initDatabase() throws SQLException {
		var asd = connection.createStatement();
		asd.execute("CREATE TABLE IF NOT EXISTS FirstEntity ( entity_id INTEGER PRIMARY KEY)");
		asd.execute(
				"CREATE TABLE IF NOT EXISTS Entity ( entity_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, linked_entities_list_id INTEGER )");
		asd.execute(
				"CREATE TABLE IF NOT EXISTS EntityLists ( ll_id INTEGER PRIMARY KEY AUTOINCREMENT, entity_id INTEGER, next_ll_id INTEGER )");
		asd.execute("CREATE TABLE IF NOT EXISTS Player ( player_id INTEGER PRIMARY KEY, entity_id INTEGER )");
		asd.close();
	}

	public ResultSet insertEntity(String name, String description, Integer list_id) throws SQLException {
		final var statement = connection.prepareStatement(
				"INSERT INTO Entity (name, description, linked_entities_list_id) VALUES (?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, name);
		statement.setString(2, description);
		statement.setObject(3, list_id);
		statement.executeUpdate();
		return statement.getGeneratedKeys();
	}

	/**
	 * 
	 * @param entity_id
	 * @param next_ll_id
	 * @return (ll_id, entity_id, next_ll_id)
	 * @throws SQLException
	 */
	public ResultSet insertEntityistItem(int entity_id, Integer next_ll_id) throws SQLException {
		final var statement = preparedStatement("INSERT INTO EntityLists (entity_id, next_ll_id) VALUES (?, ?)",
				entity_id, next_ll_id);
		statement.executeUpdate();
		return statement.getGeneratedKeys();
	}

	public void insertPlayer(int player_id, int entity_id) throws SQLException {
		preparedStatement("INSERT INTO Player VALUES (?, ?)", player_id, entity_id).executeUpdate();
	}

	private PreparedStatement preparedStatement(String pre, Object... vals) throws SQLException {
		final var statement = connection.prepareStatement(pre, Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < vals.length; i++) {
			statement.setObject(i + 1, vals[i]);
		}

		return statement;
	}

	public ResultSet selectEntity(int entity_id) throws SQLException {
		final var statement = preparedStatement("SELECT * FROM Entity WHERE entity_id=?", entity_id);
		return statement.executeQuery();
	}

	/**
	 * 
	 * @param entity_list_id
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public ResultSet selectEntityListItem(int entity_list_id, int index) throws SQLException {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		final var statement = preparedStatement("SELECT entity_id, next_ll_id FROM EntityLists WHERE ll_id=?",
				entity_list_id);
		final var res = statement.executeQuery();
		if (index > 0 && res.next()) {
			final var next_ll_id = res.getInt(2);
			return selectEntityListItem(next_ll_id, index - 1);
		} else {
			return res;
		}
	}

	/**
	 * 
	 * @param player_id
	 * @return player_id, entity_id
	 * @throws SQLException
	 */
	public ResultSet selectPlayer(int player_id) throws SQLException {
		final var statement = preparedStatement("SELECT player_id, entity_id FROM Player WHERE player_id=?",
				player_id);
		return statement.executeQuery();
	}

	public void setFirstEntity(int entity_id) throws SQLException {
		connection.createStatement().execute("DELETE FROM FirstEntity");
		preparedStatement("INSERT INTO FirstEntity VALUES (?)", entity_id).execute();
	}

	public void updateEntityName(int entity_id, String name) throws SQLException {
		preparedStatement("UPDATE Entity SET name=? WHERE entity_id=?", name, entity_id).execute();
	}

}