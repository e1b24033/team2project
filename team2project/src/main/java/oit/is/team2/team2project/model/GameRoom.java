package oit.is.team2.team2project.model;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

// 単一ルームを前提とするため、アプリ全体で1つのインスタンスを共有する
@Component
public class GameRoom {
  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 4;

  ArrayList<Player> players = new ArrayList<>();
  boolean started = false;

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public int getPlayerCount() {
    return players.size();
  }

  public boolean canStart() {
    int count = getPlayerCount();
    return !started && count >= MIN_PLAYERS && count <= MAX_PLAYERS;
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

  public void setPlayers(ArrayList<Player> players) {
    this.players = players;
  }

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }
}
