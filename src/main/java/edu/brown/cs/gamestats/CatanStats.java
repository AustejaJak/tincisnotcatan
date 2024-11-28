package edu.brown.cs.gamestats;

import com.google.errorprone.annotations.Keep;

//Higher level class for storing GameStats in database
public class CatanStats {

  @Keep private static int startedGames = 0;
  @Keep private static int finishedGames = 0;

  public synchronized static GameStats getGameStatsObject(){
    startedGames++;
    return new GameStats();
  }

  public synchronized static void processGameStats(GameStats stats){
    finishedGames++;
    //TODO: process game stats, store to database...etc
  }



}
