package edu.brown.cs.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.actions.ActionResponse;
import edu.brown.cs.actions.FollowUpAction;
import edu.brown.cs.board.Board;
import edu.brown.cs.board.BoardTile;
import edu.brown.cs.board.Building;
import edu.brown.cs.board.HexCoordinate;
import edu.brown.cs.board.Intersection;
import edu.brown.cs.board.IntersectionCoordinate;
import edu.brown.cs.board.Path;
import edu.brown.cs.board.Port;
import edu.brown.cs.board.Road;
import edu.brown.cs.board.Tile;
import edu.brown.cs.board.TileType;
import edu.brown.cs.catan.DevelopmentCard;
import edu.brown.cs.catan.GameSettings;
import edu.brown.cs.catan.Player;
import edu.brown.cs.catan.Referee;
import edu.brown.cs.catan.Referee.GameStatus;
import edu.brown.cs.catan.Resource;

import com.google.errorprone.annotations.Keep;

public class CatanConverter {

  private Gson _gson;

  public CatanSettings getSettings(String settings) {
    try {
      return _gson.fromJson(settings, CatanSettings.class);
    } catch (Exception e) { // TODO: change to something better?
      throw new IllegalArgumentException("Could not parse settings JSON.");
    }

  }

  public CatanConverter() {
    _gson = new Gson();
  }

  public JsonObject getGameState(Referee ref, int playerID) {
    return _gson.toJsonTree(new GameState(ref, playerID)).getAsJsonObject();
  }

  public Map<Integer, JsonObject> responseToJSON(
      Map<Integer, ActionResponse> response) {
    Map<Integer, JsonObject> toReturn = new HashMap<>();
    for (Map.Entry<Integer, ActionResponse> entry : response.entrySet()) {
      toReturn.put(entry.getKey(), _gson.toJsonTree(entry.getValue())
          .getAsJsonObject());
    }
    return toReturn;
  }

  private static class GameState {
    @Keep private int playerID;
    @Keep private List<Integer> turnOrder;
    @Keep private Integer winner;
    @Keep private Hand hand;
    @Keep private BoardRaw board;
    @Keep private int currentTurn;
    @Keep private FollowUpActionRaw followUp;
    @Keep private Collection<PublicPlayerRaw> players;
    @Keep private GameSettings settings;
    @Keep private GameStatsRaw stats;

    public GameState(Referee ref, int playerID) {
      this.playerID = playerID;
      this.currentTurn = ref.currentPlayer() != null ? ref.currentPlayer()
          .getID() : -1;
      this.hand = new Hand(ref.getPlayerByID(playerID));
      this.board = new BoardRaw(ref.getReadOnlyReferee(), ref.getBoard(),
          playerID);
      this.turnOrder = (ref.getGameStatus() != GameStatus.WAITING) ? ref
          .getTurnOrder() : null;
      this.winner = ref.getWinner() != null ? ref.getWinner().getID() : null;
      this.followUp = ref.getNextFollowUp(playerID) != null ? new FollowUpActionRaw(
          ref.getNextFollowUp(playerID)) : null;
      this.players = new ArrayList<>();
      this.settings = ref.getGameSettings();
      this.stats = new GameStatsRaw(ref);
      for (Player p : ref.getPlayers()) {
        players.add(new PublicPlayerRaw(p, ref.getReadOnlyReferee()));
      }
    }
  }

  public static class CatanSettings {
    private final int numPlayers;
    private final boolean isDecimal;

    public CatanSettings(int numPlayers, boolean decimal) {
      this.numPlayers = numPlayers;
      this.isDecimal = decimal;
    }

    public int getNumPlayers() {
      return numPlayers;
    }

    public boolean isDecimal() {
      return isDecimal;
    }

  }

  private static class Hand {
    @Keep private final Map<Resource, Double> resources;
    @Keep private final Map<DevelopmentCard, Integer> devCards;
    @Keep private boolean canBuildRoad;
    @Keep private boolean canBuildSettlement;
    @Keep private boolean canBuildCity;
    @Keep private boolean canBuyDevCard;

    public Hand(Player player) {
      resources = player.getResources();
      devCards = player.getDevCards();
      canBuildRoad = player.canBuildRoad();
      canBuildSettlement = player.canBuildSettlement();
      canBuildCity = player.canBuildCity();
      canBuyDevCard = player.canBuyDevelopmentCard();
    }
  }

  private static class BoardRaw {
    private final Collection<TileRaw> tiles;
    private final Collection<IntersectionRaw> intersections;
    private final Collection<PathRaw> paths;

    public BoardRaw(Referee ref, Board board, int playerID) {
      intersections = new ArrayList<>();
      for (Intersection intersection : board.getIntersections().values()) {
        intersections.add(new IntersectionRaw(intersection, ref, playerID));
      }
      paths = new ArrayList<>();
      for (Path path : board.getPaths().values()) {
        paths.add(new PathRaw(ref.getReadOnlyReferee(), path, playerID));
      }

      tiles = new ArrayList<>();
      for (Tile tile : board.getTiles()) {
        tiles.add(new TileRaw(tile));
      }
    }
  }

  public static class PathRaw {
    @Keep private IntersectionCoordinate start;
    @Keep private IntersectionCoordinate end;
    @Keep private RoadRaw road;
    @Keep private boolean canBuildRoad;

    public PathRaw(Referee ref, Path path, int playerID) {
      start = path.getStart().getPosition();
      end = path.getEnd().getPosition();
      road = path.getRoad() != null ? new RoadRaw(path.getRoad()) : null;
      canBuildRoad = ref.getGameStatus() == GameStatus.SETUP ? path
          .canPlaceSetupRoad(ref.getSetup()) : path.canPlaceRoad(ref
          .getPlayerByID(playerID));
    }

  }

  private static class RoadRaw {
    @Keep private int player;

    public RoadRaw(Road road) {
      player = road.getPlayer().getID();
    }
  }

  private static class BuildingRaw implements Building {

    @Keep private int player;
    @Keep private final String type;

    BuildingRaw(Building building) {
      if (building.getPlayer() != null) {
        player = building.getPlayer().getID();
      }
      type = building.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public Map<Integer, Map<Resource, Integer>> collectResource(
        Resource resource) {
      assert false; // Should never be called!
      return null;
    }

    @Override
    public Player getPlayer() {
      assert false; // Should never be called!
      return null;
    }

  }

  private static class IntersectionRaw {

    @Keep private final BuildingRaw building;
    @Keep private final Port port;
    @Keep private final IntersectionCoordinate coordinate;
    @Keep private final boolean canBuildSettlement;

    IntersectionRaw(Intersection i, Referee ref, int playerID) {
      building = i.getBuilding() != null ? new BuildingRaw(i.getBuilding())
          : null;
      port = i.getPort();
      coordinate = i.getPosition();
      canBuildSettlement = i.canPlaceSettlement(ref, playerID);
    }

  }

  private static class TileRaw {
    @Keep private final HexCoordinate hexCoordinate;
    @Keep private final TileType type;
    @Keep private final boolean hasRobber;
    @Keep private final int number;
    @Keep private final List<IntersectionCoordinate> portLocations;
    @Keep private final Resource portType;

    public TileRaw(BoardTile tile) {
      hexCoordinate = tile.getCoordinate();
      type = tile.getType();
      hasRobber = tile.hasRobber();
      number = tile.getRollNumber();
      portLocations = tile.getPortLocations();
      portType = tile.getPortType();
    }
  }

  private static class PublicPlayerRaw {
    @Keep private String name;
    @Keep private int id;
    @Keep private String color;
    @Keep private int numSettlements;
    @Keep private int numCities;
    @Keep private int numPlayedKnights;
    @Keep private int numRoads;
    @Keep private boolean longestRoad;
    @Keep private boolean largestArmy;
    @Keep private int victoryPoints;
    @Keep private double numResourceCards;
    @Keep private int numDevelopmentCards;
    @Keep private Map<Resource, Double> rates;

    public PublicPlayerRaw(Player p, Referee r) {
      name = p.getName();
      id = p.getID();
      color = p.getColor();
      numSettlements = p.numSettlements();
      numCities = p.numCities();
      numPlayedKnights = p.numPlayedKnights();
      numRoads = p.numRoads();
      longestRoad = r.hasLongestRoad(p.getID());
      largestArmy = r.hasLargestArmy(p.getID());
      victoryPoints = r.getNumPublicPoints(p.getID());
      rates = r.getBankRates(p.getID());
      numResourceCards = p.getNumResourceCards();
      numDevelopmentCards = p.getNumDevelopmentCards();
    }

  }

  private static class GameStatsRaw {
    @Keep private int[] rolls;
    @Keep private int turn;

    GameStatsRaw(Referee ref){
      this.rolls = ref.getGameStats().getRollsArray();
      this.turn = ref.getTurn().getTurnNum();
    }

  }

  private static class FollowUpActionRaw {

    @Keep private String actionName;
    @Keep private Object actionData;

    public FollowUpActionRaw(FollowUpAction followUp) {
      actionName = followUp.getID();
      actionData = followUp.getData();
    }
  }
}
