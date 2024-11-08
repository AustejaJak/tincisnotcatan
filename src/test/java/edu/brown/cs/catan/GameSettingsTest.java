package edu.brown.cs.catan;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameSettingsTest {
    private JsonObject mockSettings;

    // Setup the mock settings object
    @Before
    public void setup() {
        mockSettings = new JsonObject();
    }

    // Test for the default constructor
    @Test
    public void testDefaultConstructor() {
        GameSettings gameSettings = new GameSettings();

        assertEquals(Settings.DEFAULT_NUM_PLAYERS, gameSettings.numPlayers);
        assertEquals(Settings.WINNING_POINT_COUNT, gameSettings.winningPointCount);
        assertFalse(gameSettings.isDecimal);
        assertFalse(gameSettings.isDynamic);
        assertFalse(gameSettings.isStandard);
    }

    // Test for the constructor with valid JSON input
    @Test
    public void testConstructorWithValidJson() {
        // Set the mock JSON object values
        mockSettings.addProperty("numPlayers", 4);
        mockSettings.addProperty("victoryPoints", 10);
        mockSettings.addProperty("isDecimal", true);
        mockSettings.addProperty("isDynamic", true);
        mockSettings.addProperty("isStandard", true);

        // Instantiate GameSettings using the mock JSON object
        GameSettings gameSettings = new GameSettings(mockSettings);

        // Assert the values are correctly parsed from the JsonObject
        assertEquals(4, gameSettings.numPlayers);
        assertEquals(10, gameSettings.winningPointCount);
        assertTrue(gameSettings.isDecimal);
        assertTrue(gameSettings.isDynamic);
        assertTrue(gameSettings.isStandard);
    }

    // Test for the constructor with missing parameters (JsonObject doesn't contain certain keys)
    @Test
    public void testConstructorWithMissingParams() {
        // Set the mock JSON object values
        mockSettings.addProperty("numPlayers", 4);
        mockSettings.addProperty("victoryPoints", 10);
        mockSettings.addProperty("isDecimal", false);
        // Simulate missing "isDynamic"
        mockSettings.remove("isDynamic");
        mockSettings.addProperty("isStandard", true);

        // Instantiate GameSettings with the mock JSON object
        GameSettings gameSettings = new GameSettings(mockSettings);

        // Assert that missing or invalid fields use default values
        assertEquals(4, gameSettings.numPlayers);
        assertEquals(10, gameSettings.winningPointCount);
        assertFalse(gameSettings.isDecimal);  // Default value
        assertFalse(gameSettings.isDynamic);  // Default value
        assertTrue(gameSettings.isStandard);
    }

    // Test for when "numPlayers" and "victoryPoints" are missing from the JSON object
    @Test
    public void testConstructorWithMissingRequiredParams() {
        // Simulate missing "numPlayers" and "victoryPoints"
        mockSettings.remove("numPlayers");
        mockSettings.remove("victoryPoints");
        mockSettings.addProperty("isDecimal", false);

        // Instantiate GameSettings with the mock JSON object
        GameSettings gameSettings = new GameSettings(mockSettings);

        // Assert that defaults are used for missing required fields
        assertEquals(Settings.DEFAULT_NUM_PLAYERS, gameSettings.numPlayers);
        assertEquals(Settings.WINNING_POINT_COUNT, gameSettings.winningPointCount);
    }
}
