package oit.is.team2.team2project.model;

public class Chip {
  String playerName;
  int balance;

  public Chip() {
  }

  public Chip(String playerName, int balance) {
    this.playerName = playerName;
    this.balance = balance;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }
}
