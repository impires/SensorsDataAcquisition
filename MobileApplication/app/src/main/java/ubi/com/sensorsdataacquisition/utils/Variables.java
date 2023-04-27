package ubi.com.sensorsdataacquisition.utils;

/**
 * Created by Ivan on 18/12/15.
 */
public class Variables {

    /**
     * Variable to control the capture
     */
    public static boolean isCapturing = false;

    /**
     * Variable for each folder path
     */
    public static String folderPath;
    public static String rootPath;

    /**
     * Variable for the time before start the capture
     */
    public static int TIME_BEFORE = 10000;
    public static int TIME_AFTER = 30000;

    /**
     * Variables personal data
     */
    public static String id_study = "";
    public static String age_person = "";
    public static String height_person = "";
    public static String weight_person = "";
    public static String gender_person = "";
    public static String test = "";

    /**
     * Variables sensors enabled
     */
    public static boolean accEnabled = true;
    public static boolean gyroEnabled = true;
    public static boolean magEnabled = true;
    public static boolean gpsEnabled = true;
}
