package oit.is.team2.team2project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team2.team2project.model.Player;
import oit.is.team2.team2project.service.GameRoomService;

@Controller
public class GameRoomController {
  private final GameRoomService gameRoomService;

  public GameRoomController(GameRoomService gameRoomService) {
    this.gameRoomService = gameRoomService;
  }

  @GetMapping("/")
  public String index() {
    return "index.html";
  }

  @PostMapping("/room/join")
  public String join(@RequestParam(defaultValue = "") String name, ModelMap model) {
    String playerName = name.strip();
    if (playerName.isEmpty()) {
      model.addAttribute("errorMessage", "名前を入力してください。");
      return "index.html";
    }

    try {
      Player player = gameRoomService.join(playerName);
      model.addAttribute("player", player);
      model.addAttribute("players", gameRoomService.getPlayers());
      model.addAttribute("started", gameRoomService.isStarted());
      return "room.html";
    } catch (IllegalStateException e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "index.html";
    }
  }

  @PostMapping("/room/start")
  public String start(ModelMap model) {
    GameRoomService.StartResult result = gameRoomService.startGame();
    if (result.success()) {
      return "redirect:/room";
    }
    model.addAttribute("errorMessage", result.message());
    model.addAttribute("players", gameRoomService.getPlayers());
    model.addAttribute("started", gameRoomService.isStarted());
    return "room.html";
  }

  @GetMapping("/room")
  public String room(ModelMap model) {
    model.addAttribute("players", gameRoomService.getPlayers());
    model.addAttribute("started", gameRoomService.isStarted());
    model.addAttribute("autoRefresh", true);
    return "room.html";
  }
}
