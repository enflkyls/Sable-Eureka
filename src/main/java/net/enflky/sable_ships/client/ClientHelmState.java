package net.enflky.sable_ships.client;

import net.enflky.sable_ships.network.HelmStatePacket;

public final class ClientHelmState {

    private static volatile HelmSnapshot snapshot = HelmSnapshot.EMPTY;
    private static volatile long revision;

    private ClientHelmState() {}

    public static HelmSnapshot get() {
        return snapshot;
    }

    public static long revision() {
        return revision;
    }

    public static boolean shouldRenderHud() {
        return snapshot.active();
    }

    public static void update(HelmStatePacket packet) {
        HelmSnapshot next = HelmSnapshot.fromPacket(packet);
        if (snapshot.active() && !next.active() && !snapshot.helmPos().equals(next.helmPos())) {
            return;
        }
        if (next.equals(snapshot)) {
            return;
        }
        snapshot = next;
        revision++;
    }

    public static void clear() {
        if (snapshot == HelmSnapshot.EMPTY) {
            return;
        }
        snapshot = HelmSnapshot.EMPTY;
        revision++;
    }
}
