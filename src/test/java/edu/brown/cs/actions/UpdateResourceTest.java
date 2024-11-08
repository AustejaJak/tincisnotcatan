package edu.brown.cs.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.catan.MasterReferee;
import edu.brown.cs.catan.Player;
import edu.brown.cs.catan.Referee;
import edu.brown.cs.catan.Resource;

public class UpdateResourceTest {

    private Referee ref;
    private int playerID;

    @Before
    public void setUp() {
        ref = new MasterReferee();
        playerID = ref.addPlayer("TestPlayer");
    }

    @Test
    public void testConstructorWithValidPlayer() {
        try {
            UpdateResource updateResource = new UpdateResource(ref, playerID);
            assertNotNull(updateResource);
        } catch (Exception e) {
            fail("Constructor threw an exception for a valid player: " + e.getMessage());
        }
    }

    @Test
    public void testConstructorWithInvalidPlayer() {
        try {
            new UpdateResource(ref, -1); // Invalid player ID
            fail("Expected IllegalArgumentException for invalid player ID");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("No player exists with ID"));
        }
    }

    @Test
    public void testExecuteUpdatesResources() {
        UpdateResource updateResource = new UpdateResource(ref, playerID);
        Player player = ref.getPlayerByID(playerID);


        player.addResource(Resource.WOOD, 5.0, ref.getBank());
        player.addResource(Resource.BRICK, 5.0, ref.getBank());

        Map<Integer, ActionResponse> response = updateResource.execute();

        assertNotNull(response);
        assertTrue(response.containsKey(playerID));
        ActionResponse actionResponse = response.get(playerID);
        assertTrue(actionResponse.getSuccess());
        assertEquals("You unlocked the power!", actionResponse.getMessage());

        Map<Resource, Double> updatedResources = player.getResources();
        for (Resource res : updatedResources.keySet()) {
            if (res != Resource.WILDCARD) {
                assertEquals(99.0, updatedResources.get(res), 0.01);
            }
        }
    }
}