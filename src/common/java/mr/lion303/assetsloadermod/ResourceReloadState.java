package mr.lion303.assetsloadermod;

public final class ResourceReloadState {
    private static boolean gameReady;

    private ResourceReloadState() {
    }

    public static void markGameReady() {
        gameReady = true;
    }

    public static boolean isGameReady() {
        return gameReady;
    }
}
