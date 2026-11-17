package oit.is.team2.team2project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChipTableTest {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  void schema読み込み後にプレイヤー名をキーとして残高を保存できる() {
    jdbcTemplate.update("INSERT INTO chip (playerName, balance) VALUES (?, ?)", "alice", 500);

    Integer balance = jdbcTemplate.queryForObject(
        "SELECT balance FROM chip WHERE playerName = ?", Integer.class, "alice");
    assertEquals(500, balance);

    assertThrows(DuplicateKeyException.class,
        () -> jdbcTemplate.update("INSERT INTO chip (playerName, balance) VALUES (?, ?)", "alice", 100));
  }

  @Test
  void 初期残高を指定して登録すると整数のチップ残高として保存される() {
    jdbcTemplate.update("INSERT INTO chip (playerName, balance) VALUES (?, ?)", "bob", 1000);

    Object balance = jdbcTemplate.queryForObject(
        "SELECT balance FROM chip WHERE playerName = ?", Object.class, "bob");
    assertEquals(Integer.valueOf(1000), balance);
  }
}
