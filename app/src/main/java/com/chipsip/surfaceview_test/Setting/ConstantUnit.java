package com.chipsip.surfaceview_test.Setting;

/**
 * Created by kenneth on 2016/3/31.
 */
public class ConstantUnit {

    /**
     * @Beacon_Minor_ID :
     */
    public final static int
            HereBeacon1 = 2,
            HereBeacon2 = 3,
            HereBeacon3 = 6,
            HereBeacon4 = 7,
            EstBeacon1 = 43091,
            EstBeacon2 = 62610,
            EstBeacon3 = 5741;

    /**
     * @average_counter_number.
     */
    public static final int RSSI_ANALASYS_COUNTER = 5;

    /**
     * @Average_rssi get beacon threshold.
     * -69(~=3.12m.) average-real-test-distance <= 1m.
     * least rssi = -54 ~ -50
     * Suggest rssi = -69( ~= 3.12m. )
     */
    public static Float PassageRangeRSSIThreshold = -69.0f;

    /**
     * @Single-time_rssi get beacon threshold.
     * If Minor ID not in mGroup list will check this threshold to catch beacon.
     * least rssi = -54 ~ -50
     * Suggest rssi = -59( ~= 1m. )
     */
    public static Float OtherRangeRSSIThreshold = -59.0f;

    /**
     * If beacon > 1, getCatchedFromGroup_Func will check their largest average rssi.
     */
    public static Float RSSIDifference = 0.0f;//數字愈大條件愈苛刻 訊號強弱要夠明顯，若訊號強度不夠明顯就不讀Beacon。

    public static final int NOT_CATCHED = -1;


}
