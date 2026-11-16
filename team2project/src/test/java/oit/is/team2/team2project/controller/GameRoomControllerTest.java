package oit.is.team2.team2project.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import oit.is.team2.team2project.model.GameRoom;

// GameRoom はアプリ全体で共有されるため、テストごとにコンテキストを作り直して参加者を空にする
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameRoomControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  GameRoom gameRoom;

  @Test
  void topPageShowsNameFormWithoutLogin() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index.html"))
        .andExpect(content().string(containsString("name=\"name\"")))
        .andExpect(content().string(containsString("参加")));
  }

  // AC1
  @Test
  void joinShowsPlayerNameOnRoomPage() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("room.html"))
        .andExpect(content().string(containsString("alice さんが参加しました。")));

    assertEquals(1, gameRoom.getPlayerCount());
  }

  // AC2
  @Test
  void roomPageShowsCurrentPlayers() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "bob").with(csrf()));

    mockMvc.perform(get("/room"))
        .andExpect(status().isOk())
        .andExpect(view().name("room.html"))
        .andExpect(content().string(containsString("<li>alice</li>")))
        .andExpect(content().string(containsString("<li>bob</li>")))
        .andExpect(content().string(containsString("参加者一覧（2人）")));
  }

  @Test
  void joinWithBlankNameShowsErrorOnTopPage() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "   ").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("index.html"))
        .andExpect(content().string(containsString("名前を入力してください。")));

    assertEquals(0, gameRoom.getPlayerCount());
  }

  @Test
  void joinWhenRoomIsFullShowsErrorOnTopPage() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "bob").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "carol").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "dave").with(csrf()));

    mockMvc.perform(post("/room/join").param("name", "erin").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("index.html"))
        .andExpect(content().string(containsString("満員です。これ以上参加できません。")));

    assertEquals(4, gameRoom.getPlayerCount());
  }

  @Test
  void joinWithoutCsrfTokenIsForbidden() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "mallory"))
        .andExpect(status().isForbidden())
        .andExpect(content().string(not(containsString("mallory"))));

    assertEquals(0, gameRoom.getPlayerCount());
  }

  // 開始AC1: 2人で開始すると、別のリクエストからも開始済みと分かる
  @Test
  void startWithTwoPlayersMarksRoomAsStarted() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "bob").with(csrf()));

    mockMvc.perform(post("/room/start").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/room"));

    assertTrue(gameRoom.isStarted());

    mockMvc.perform(get("/room"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ゲームが開始されました。")));
  }

  // 開始AC1: 上限の4人でも開始できる
  @Test
  void startWithFourPlayersIsAllowed() throws Exception {
    for (String n : new String[] { "a", "b", "c", "d" }) {
      mockMvc.perform(post("/room/join").param("name", n).with(csrf()));
    }

    mockMvc.perform(post("/room/start").with(csrf()))
        .andExpect(status().is3xxRedirection());

    assertTrue(gameRoom.isStarted());
  }

  // 開始AC2: 1人では開始されず、理由が表示される
  @Test
  void startWithOnePlayerShowsReasonAndDoesNotStart() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));

    mockMvc.perform(post("/room/start").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("room.html"))
        .andExpect(content().string(containsString("ゲームを開始するには2人以上の参加が必要です。")))
        .andExpect(content().string(not(containsString("ゲームが開始されました。"))));

    assertFalse(gameRoom.isStarted());
  }

  // 開始AC2: 0人でも開始されない
  @Test
  void startWithNoPlayersShowsReasonAndDoesNotStart() throws Exception {
    mockMvc.perform(post("/room/start").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ゲームを開始するには2人以上の参加が必要です。")));

    assertFalse(gameRoom.isStarted());
  }

  // すでに開始済みなら二重に開始しない
  @Test
  void startTwiceShowsAlreadyStartedMessage() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "bob").with(csrf()));
    mockMvc.perform(post("/room/start").with(csrf()));

    mockMvc.perform(post("/room/start").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("すでにゲームは開始されています。")));
  }

  @Test
  void startWithoutCsrfTokenIsForbidden() throws Exception {
    mockMvc.perform(post("/room/join").param("name", "alice").with(csrf()));
    mockMvc.perform(post("/room/join").param("name", "bob").with(csrf()));

    mockMvc.perform(post("/room/start"))
        .andExpect(status().isForbidden());

    assertFalse(gameRoom.isStarted());
  }
}
