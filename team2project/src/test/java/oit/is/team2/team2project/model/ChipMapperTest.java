package oit.is.team2.team2project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChipMapperTest {

  @Autowired
  ChipMapper chipMapper;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  void 初期残高をMapperで登録するとDBに保存される() {
    int insertedRows = chipMapper.insertChip(new Chip("mapper-insert", 750));

    Integer storedBalance = jdbcTemplate.queryForObject(
        "SELECT balance FROM chip WHERE playerName = ?", Integer.class, "mapper-insert");
    assertEquals(1, insertedRows);
    assertEquals(750, storedBalance);
  }

  @Test
  void プレイヤー名で取得すると保存済みの残高が返る() {
    jdbcTemplate.update("INSERT INTO chip (playerName, balance) VALUES (?, ?)", "mapper-select", 1250);

    Chip chip = chipMapper.selectByPlayerName("mapper-select");

    assertNotNull(chip);
    assertEquals("mapper-select", chip.getPlayerName());
    assertEquals(1250, chip.getBalance());
  }

  @Test
  void 残高を更新すると保存済みの残高が指定値に変わる() {
    jdbcTemplate.update("INSERT INTO chip (playerName, balance) VALUES (?, ?)", "mapper-update", 500);

    int updatedRows = chipMapper.updateBalance("mapper-update", 1200);

    assertEquals(1, updatedRows);
    Integer storedBalance = jdbcTemplate.queryForObject(
        "SELECT balance FROM chip WHERE playerName = ?", Integer.class, "mapper-update");
    assertEquals(1200, storedBalance);
  }

  @Test
  void 存在しないプレイヤーを更新すると更新件数は0になる() {
    int updatedRows = chipMapper.updateBalance("nonexistent-player", 999);

    assertEquals(0, updatedRows);
  }
}
