package oit.is.team2.team2project.model;

public class Player {
  String name;
  boolean joined;

  // 参加者モデルは参加時に生成するため、生成時点で参加状態とする
  public Player(String name) {
    this.name = name;
    this.joined = true;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isJoined() {
    return joined;
  }

  public void setJoined(boolean joined) {
    this.joined = joined;
  }
}
