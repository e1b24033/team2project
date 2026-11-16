package oit.is.team2.team2project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import oit.is.team2.team2project.model.GameRoom;
import oit.is.team2.team2project.model.Player;

// 参加者状態はメモリ上の GameRoom（アプリ全体で1つ）で管理する。
// 複数の利用者が同時に参加しても一覧が壊れないよう、操作を synchronized にする。
@Service
public class GameRoomService {
  private final GameRoom gameRoom;

  public GameRoomService(GameRoom gameRoom) {
    this.gameRoom = gameRoom;
  }

  public synchronized Player join(String name) {
    if (gameRoom.getPlayerCount() >= GameRoom.MAX_PLAYERS) {
      throw new IllegalStateException("満員です。これ以上参加できません。");
    }

    Player player = new Player(name);
    gameRoom.addPlayer(player);
    return player;
  }

  public synchronized boolean canStart() {
    return gameRoom.canStart();
  }

  public synchronized StartResult startGame() {
    if (gameRoom.isStarted()) {
      return StartResult.failure("すでにゲームは開始されています。");
    }
    if (gameRoom.getPlayerCount() < GameRoom.MIN_PLAYERS) {
      return StartResult.failure("ゲームを開始するには2人以上の参加が必要です。");
    }
    gameRoom.setStarted(true);
    return StartResult.ok();
  }

  public synchronized boolean isStarted() {
    return gameRoom.isStarted();
  }

  public record StartResult(boolean success, String message) {
    public static StartResult ok() {
      return new StartResult(true, null);
    }

    public static StartResult failure(String message) {
      return new StartResult(false, message);
    }
  }

  // 呼び出し側が一覧を直接書き換えないよう、コピーを返す
  public synchronized List<Player> getPlayers() {
    return List.copyOf(gameRoom.getPlayers());
  }
}
