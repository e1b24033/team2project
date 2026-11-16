package oit.is.team2.team2project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GameRoomTest {

  private GameRoom roomWith(int count) {
    GameRoom room = new GameRoom();
    for (int i = 1; i <= count; i++) {
      room.addPlayer(new Player("player" + i));
    }
    return room;
  }

  @ParameterizedTest(name = "{0}人のとき人数は{0}で開始可能は{1}")
  @CsvSource({
      "0, false",
      "1, false",
      "2, true",
      "4, true",
      "5, false"
  })
  void 参加者一覧から人数と開始可能状態を取得できる(int count, boolean expectedCanStart) {
    GameRoom room = roomWith(count);

    assertEquals(count, room.getPlayerCount());
    assertEquals(expectedCanStart, room.canStart());
  }

  @Test
  void 開始済みのルームは人数が条件を満たしても開始可能ではない() {
    GameRoom room = roomWith(2);
    assertTrue(room.canStart());

    room.setStarted(true);

    assertFalse(room.canStart());
  }
}
