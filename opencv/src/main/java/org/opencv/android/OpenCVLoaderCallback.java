package org.opencv.android;

public abstract class OpenCVLoaderCallback {
    public static final int SUCCESS = 0;
    public static final int INCOMPATIBLE_MANAGER_VERSION = 1;
    public static final int INIT_FAILED = 2;
    public static final int INSTALL_CANCELED = 3;
    public static final int MARKET_ERROR = 4;

    public abstract void onManagerConnected(int status);
    
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {
    }
}
