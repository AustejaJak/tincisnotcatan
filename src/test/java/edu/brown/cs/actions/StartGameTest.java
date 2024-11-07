package edu.brown.cs.actions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.brown.cs.catan.MasterReferee;
import edu.brown.cs.catan.Referee;

public class StartGameTest {

    @Test
    public void testBadStatus() {
        Referee ref = new MasterReferee();

        new StartGame(ref).execute();

        try {
            new StartGame(ref);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testPlayerOrder() {
        Referee ref = new MasterReferee();
        ref.addPlayer("Alice");
        ref.addPlayer("Bob");
        ref.addPlayer("Charlie");
        ref.addPlayer("Dave");
        new StartGame(ref).execute();
    }
}
