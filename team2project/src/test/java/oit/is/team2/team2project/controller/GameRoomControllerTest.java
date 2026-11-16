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
}
