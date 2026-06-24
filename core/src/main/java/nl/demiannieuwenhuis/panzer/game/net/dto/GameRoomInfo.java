package nl.demiannieuwenhuis.panzer.game.net.dto;

public record GameRoomInfo(String code, int playerCount, int maxPlayers, String mapFileName) {}
