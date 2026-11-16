package oit.is.team2.team2project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import oit.is.team2.team2project.model.GameRoom;
import oit.is.team2.team2project.model.Player;

class GameRoomServiceTest {

  @Test
  void joinAddsJoinedPlayerToRoom() {
    GameRoom room = new GameRoom();
    GameRoomService service = new GameRoomService(room);

    Player player = service.join("alice");

    assertEquals("alice", player.getName());
    assertTrue(player.isJoined());
    assertEquals(1, room.getPlayerCount());
  }

  @Test
  void getPlayersReturnsPlayersInJoinOrder() {
    GameRoomService service = new GameRoomService(new GameRoom());
    service.join("alice");
    service.join("bob");

    List<String> names = service.getPlayers().stream().map(Player::getName).toList();

    assertEquals(List.of("alice", "bob"), names);
  }

  @Test
  void joinRejectsWhenRoomIsFull() {
    GameRoom room = new GameRoom();
    GameRoomService service = new GameRoomService(room);
    service.join("alice");
    service.join("bob");
    service.join("carol");
    service.join("dave");

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> service.join("erin"));

    assertEquals("満員です。これ以上参加できません。", exception.getMessage());
    assertEquals(4, room.getPlayerCount());
  }

  @Test
  void canStartReturnsFalseWhenBelowMinimumPlayers() {
    GameRoomService service = new GameRoomService(new GameRoom());
    service.join("alice");

    assertFalse(service.canStart());
  }

  @Test
  void getPlayersReturnsUnmodifiableList() {
    GameRoom room = new GameRoom();
    GameRoomService service = new GameRoomService(room);
    service.join("alice");

    assertThrows(UnsupportedOperationException.class,
        () -> service.getPlayers().add(new Player("mallory")));
    assertEquals(1, room.getPlayerCount());
  }
}
