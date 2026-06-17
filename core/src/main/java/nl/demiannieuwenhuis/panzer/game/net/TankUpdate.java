package nl.demiannieuwenhuis.panzer.game.net;


import java.nio.ByteBuffer;

public class TankUpdate {
    long sequenceNumber;
    int UID;
    float x;
    float y;
    float rotation ;
    float cannonAngle;
    boolean shooting;

    public TankUpdate(long sequenceNumber, int UID, double x, double y, double rotation, float cannonAngle, boolean shooting) {
        this.sequenceNumber = sequenceNumber;
        this.UID = UID;
        this.x = (float) x;
        this.y = (float) y;
        this.rotation = (float) rotation;
        this.cannonAngle = cannonAngle;
        this.shooting = shooting;
    }

    public TankUpdate(ByteBuffer buffer) {
        sequenceNumber = buffer.getLong();
        UID = buffer.getInt();
        x = (float) buffer.getDouble();
        y = (float) buffer.getDouble();
        rotation = (float) buffer.getDouble();
        cannonAngle = buffer.getFloat();
        shooting = (buffer.get() > 0);
    }

    public ByteBuffer toBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.putLong(sequenceNumber);
        buffer.putInt(UID);
        buffer.putDouble(x);
        buffer.putDouble(y);
        buffer.putDouble(rotation);
        buffer.putFloat(cannonAngle);
        buffer.put(shooting ? (byte) 1 : 0);

        return buffer;
    }

    @Override
    public String toString() {
        return "sequenceNumber: " + sequenceNumber + "\n" +
                "UID: " + UID + "\n" +
                "X: " + x + "\n" +
                "Y: " + y + "\n" +
                "rotation: " + rotation + "\n" +
                "cannon angle: " + cannonAngle + "\n" +
                "shooting: " + shooting + "\n";
    }

}
