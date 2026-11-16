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
    Player player = new Player(name);
    gameRoom.addPlayer(player);
    return player;
  }

  // 呼び出し側が一覧を直接書き換えないよう、コピーを返す
  public synchronized List<Player> getPlayers() {
    return List.copyOf(gameRoom.getPlayers());
  }
}
