package edu.brown.cs.actions;

import java.util.Map;

import edu.brown.cs.board.Intersection;
import edu.brown.cs.board.IntersectionCoordinate;
import edu.brown.cs.board.Path;
import edu.brown.cs.board.PathCoordinate;
import edu.brown.cs.catan.Player;
import edu.brown.cs.catan.Referee;

public class BuildRoad implements Action {

  private Referee _ref;
  private Player _player;
  private Path _path;
  private boolean _mustPay;

  public BuildRoad(Referee ref, int playerID, IntersectionCoordinate start,
      IntersectionCoordinate end, boolean mustPay) {
    assert ref != null && start != null && end != null;
    _mustPay = mustPay;
    _ref = ref;
    _player = ref.getPlayerByID(playerID);
    Intersection startPoint = ref.getBoard().getIntersections().get(start);
    Intersection endPoint = ref.getBoard().getIntersections().get(end);
    if (startPoint == null) {
      throw new IllegalArgumentException(
          "The starting coordinate does not exist.");
    }
    if (endPoint == null) {
      throw new IllegalArgumentException(
          "The ending coordinate does not exist.");
    }
    _path = ref.getBoard().getPaths().get(new PathCoordinate(start, end));
    if (_path == null) {
      throw new IllegalArgumentException(
          "There is no path from the start coordinate to the end coordinate.");
    }
    if (_player == null) {
      String err = String.format("No player exists with the id: %d", playerID);
      throw new IllegalArgumentException(err);
    }
  }

  @Override
  public Map<Integer, ActionResponse> execute() {
    // TODO: add validation & costs!!
    _player.buildRoad();
    _player.useRoad();
    _path.placeRoad(_player);
    return null;
  }
}
