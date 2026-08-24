package nl.demiannieuwenhuis.panzer.game.net.data.udp;

import java.nio.ByteBuffer;
import java.util.List;

public record WorldUpdate(long sequenceNumber, List<TankUpdate> tankUpdates) {

    public ByteBuffer toBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(
            Long.BYTES + tankUpdates.size() * TankUpdate.BYTES
        );

        buffer.putLong(sequenceNumber);

        for (TankUpdate tankUpdate : tankUpdates) {
            buffer.put(tankUpdate.toBuffer());
        }
        buffer.flip();
        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sequence number: ").append(sequenceNumber).append("\n");

        for (TankUpdate tankUpdate : tankUpdates) {
            sb.append(tankUpdate).append("\n");
        }
        return sb.toString();
    }
}
