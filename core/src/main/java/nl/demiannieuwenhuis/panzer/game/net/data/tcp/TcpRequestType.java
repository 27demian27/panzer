package nl.demiannieuwenhuis.panzer.game.net.data.tcp;

public enum TcpRequestType {
    INFO;

  public static TcpRequestType toType(byte b) {
      return switch (b) {
          case 0 -> INFO;
          default -> null;
      };
  }

    public byte toByte() {
        return switch (this) {
            case INFO -> 0;
        };
    }
}
