package oit.is.team2.team2project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void プレイヤー名を与えて生成すると名前と参加状態を保持する() {
    Player player = new Player("alice");

    assertEquals("alice", player.getName());
    assertTrue(player.isJoined());
  }
}
