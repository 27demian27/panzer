package nl.demiannieuwenhuis.panzer.game.net.dto;


import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;

import java.nio.ByteBuffer;

public class TankUpdate {
    public final int UID;
    public final float x;
    public final float y;
    public final Direction8 moveDirection;
    public final Direction8 visualDirection;
    public final float cannonAngle;
    public final boolean shooting;

    public final boolean stationary;

    public static final int BYTES =
        Integer.BYTES   // UID
        + Float.BYTES    // x
        + Float.BYTES    // y
        + Byte.BYTES     // moveDirection
        + Byte.BYTES     // visualDirection
        + Float.BYTES    // cannonAngle
        + Byte.BYTES    // shooting
        + Byte.BYTES;   // stationary

    public TankUpdate(int UID, float x, float y, Direction8 moveDirection,
                      Direction8 visualDirection, float cannonAngle, boolean shooting, boolean stationary) {
        this.UID = UID;
        this.x = x;
        this.y = y;
        this.moveDirection = moveDirection;
        this.visualDirection = visualDirection;
        this.cannonAngle = cannonAngle;
        this.shooting = shooting;
        this.stationary = stationary;
    }

    private TankUpdate(ByteBuffer buffer) {
        this(
            buffer.getInt(),
            buffer.getFloat(),
            buffer.getFloat(),
            Direction8.values()[buffer.get()],
            Direction8.values()[buffer.get()],
            buffer.getFloat(),
            buffer.get() > 0,
            buffer.get() > 0
        );
    }

    public static TankUpdate fromBuffer(ByteBuffer buffer) {
        return new TankUpdate(buffer);
    }

    public ByteBuffer toBuffer() {
        byte[] data = new byte[BYTES];
        ByteBuffer buffer = ByteBuffer.wrap(data);

        buffer.putInt(UID);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.put((byte) moveDirection.ordinal());
        buffer.put((byte) visualDirection.ordinal());
        buffer.putFloat(cannonAngle);
        buffer.put((byte) (shooting ? 1 : 0));
        buffer.put((byte) (stationary ? 1 : 0));
        buffer.flip();

        return buffer;
    }

    @Override
    public String toString() {
        return "UID: " + UID + "\n" +
            "X: " + x + "\n" +
            "Y: " + y + "\n" +
            "moveDirection: " + moveDirection + "\n" +
            "visualDirection: " + visualDirection + "\n" +
            "cannon angle: " + cannonAngle + "\n" +
            "shooting: " + shooting + "\n" +
            "stationary: " + stationary + "\n";
    }
}
