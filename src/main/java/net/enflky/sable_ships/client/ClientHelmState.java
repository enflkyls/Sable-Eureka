package net.enflky.sable_ships.client;

import net.enflky.sable_ships.network.HelmStatePacket;


 //Thread-safe (i hope so) holder for the latest client helm state.
 //Render once per frame for a not shit view aka consistent view.

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
