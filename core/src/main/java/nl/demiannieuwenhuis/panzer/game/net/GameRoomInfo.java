package nl.demiannieuwenhuis.panzer.game.net;

public record GameRoomInfo(String code, int playerCount, int maxPlayers, String mapFileName) {}
