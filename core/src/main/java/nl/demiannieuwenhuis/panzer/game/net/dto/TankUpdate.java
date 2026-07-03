package nl.demiannieuwenhuis.panzer.game.net.dto;


import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;

import java.nio.ByteBuffer;

public record TankUpdate(
    int UID,
    float x,
    float y,
    float currentHealth,
    Direction8 moveDirection,
    Direction8 visualDirection,
    float cannonAngle,
    boolean shooting,
    boolean stationary,
    boolean disabled
) {


    public static final int BYTES =
        Integer.BYTES   // UID
            + Float.BYTES   // x
            + Float.BYTES   // y
            + Float.BYTES   // currentHealth
            + Byte.BYTES    // moveDirection
            + Byte.BYTES    // visualDirection
            + Float.BYTES   // cannonAngle
            + Byte.BYTES    // shooting
            + Byte.BYTES    // stationary
            + Byte.BYTES;   // disabled

    private TankUpdate(ByteBuffer buffer) {
        this(
            buffer.getInt(),
            buffer.getFloat(),
            buffer.getFloat(),
            buffer.getFloat(),
            Direction8.values()[buffer.get()],
            Direction8.values()[buffer.get()],
            buffer.getFloat(),
            buffer.get() > 0,
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
        buffer.putFloat(currentHealth);
        buffer.put((byte) moveDirection.ordinal());
        buffer.put((byte) visualDirection.ordinal());
        buffer.putFloat(cannonAngle);
        buffer.put((byte) (shooting ? 1 : 0));
        buffer.put((byte) (stationary ? 1 : 0));
        buffer.put((byte) (disabled ? 1 : 0));
        buffer.flip();

        return buffer;
    }

    @Override
    public String toString() {
        return "UID: " + UID + "\n" +
            "X: " + x + "\n" +
            "Y: " + y + "\n" +
            "currentHealth: " + currentHealth + "\n" +
            "moveDirection: " + moveDirection + "\n" +
            "visualDirection: " + visualDirection + "\n" +
            "cannon angle: " + cannonAngle + "\n" +
            "shooting: " + shooting + "\n" +
            "stationary: " + stationary + "\n" +
            "disabled: " + stationary + "\n";
    }
}
