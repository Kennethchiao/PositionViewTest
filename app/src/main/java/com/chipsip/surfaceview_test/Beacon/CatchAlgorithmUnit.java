package com.chipsip.surfaceview_test.Beacon; /**
 * Created by kenneth on 2016/3/16.
 */

import android.util.Log;

import com.chipsip.surfaceview_test.Setting.ConstantUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class CatchAlgorithmUnit {
    private static final String TAG = "BeaconMainActivity";
    private float AverageRssi = 0;
    private int OnlyOneBeacon = 1;
    private static boolean mEnableBeaconInterrupt = true; // beacon之間會互相中斷
    private List<Integer> mGroup = new ArrayList<Integer>(    // Minor ID!!!!!!非常重要
            Arrays.asList(
                    ConstantUnit.HereBeacon1,
                    ConstantUnit.HereBeacon2,
                    ConstantUnit.HereBeacon3,
                    ConstantUnit.HereBeacon4
            ));


    TreeMap<Integer, List<Integer>> mRssiListMap = new TreeMap<Integer, List<Integer>>();
    //實作一個  mRssiListMap 來儲存   List<Integer>>  ex: A[] ,B[],C[]
    TreeMap<Integer, Float> mRssiAverageMap = new TreeMap<Integer, Float>();
    //實作一個  mRssiListMap 來儲存   mRssiAverage

    /**
     * 利用 TreeMap 來處理資料，但卻不希望依照鍵值排序而是依照內容值來排序
     * 實做一個 Comparator 的介面 做為 TreeSet 的初始參數，這樣一來就可以隨心所欲的設定排序對象
     * 若是改為初始 TreeMap 的初始化參數的話，就只能比較鍵值而沒辦法比較內容值
     */
    static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        /**
         * 比較的對象是 Map.Entry 與 Map.Entry
         * 原本比較直覺的做法是－return e1.getValue().compareTo(e2.getValue()); 
         * 但是這樣子會讓 Value 值無法重複
         */
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        // / 所以將內容值相等的改為固定值1，這樣一來內容值相等的就不會被捨棄
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());// 最後再把 Map 的 Entry Set 取出來加入這個我們所特別設計 comparator 的物件中
        return sortedEntries;
    }

    public void addBeaconValue(int minor, int rssi) {
        if (mRssiListMap.containsKey(minor)) {
            List<Integer> rssiList = mRssiListMap.get(minor);  //把同一個minor的linst取出來

            if (rssiList.size() < ConstantUnit.RSSI_ANALASYS_COUNTER) { //如果小於 count數

                rssiList.add(rssi);
                mRssiListMap.put(minor, rssiList);
            } else if (rssiList.size() == ConstantUnit.RSSI_ANALASYS_COUNTER) {
                rssiList.remove(0); //刪除第INDEX = 0個

                rssiList.add(rssi);
                mRssiListMap.put(minor, rssiList);
            } else if (rssiList.size() > ConstantUnit.RSSI_ANALASYS_COUNTER) {
                Log.d(TAG, "RSSI List in Catcher More than " + ConstantUnit.RSSI_ANALASYS_COUNTER + "values!!!");
                throw new UnsupportedOperationException();
            }
        } else {
            ArrayList<Integer> rssiArray = new ArrayList<Integer>(ConstantUnit.RSSI_ANALASYS_COUNTER);
            for (int i = 0; i < ConstantUnit.RSSI_ANALASYS_COUNTER; ++i) {
                if (i == ConstantUnit.RSSI_ANALASYS_COUNTER - 1)
                    rssiArray.add(rssi);
                else
                    rssiArray.add(-100);//起始average值
            }
            mRssiListMap.put(minor, rssiArray);
        }
        caculateAverageRSSI(minor);
    }

    private void caculateAverageRSSI(int minor) {
        List<Integer> rssiList = mRssiListMap.get(minor);
        if (rssiList.size() == ConstantUnit.RSSI_ANALASYS_COUNTER) {
            Float average = 0.0f;
            for (Integer rssi :
                    rssiList) {
                average += rssi;
            }
            average /= ConstantUnit.RSSI_ANALASYS_COUNTER;

            mRssiAverageMap.put(minor, average);
            // Log.d(TAG, "caculateAverageRSSI : minorID: [" + minor + "] Average rssi : [" + average + "]");
        }
    }

    private void resetMap() {
        mRssiListMap.clear();
        mRssiAverageMap.clear();
    }

    public int getCatchedFromGroup() {

        SortedSet<Map.Entry<Integer, Float>> sortedEntries = entriesSortedByValues(mRssiAverageMap);
        //使用SortedSet<自然遞增排序> 去抓 entriesSortedByValues(mRssiAverageMap)
        Float max = sortedEntries.first().getValue();//取得最大值（也就是Sorted的第一個）
        Iterator<Map.Entry<Integer, Float>> it = sortedEntries.iterator(); // 設定走訪器
        int index = 0;
        Map.Entry<Integer, Float> entry = null;
        /**
         *取得SortedSet次一個的位置
         */
        while (it.hasNext() && index < 4) {//
            entry = it.next();
            index++;
        }
        Float second = entry.getValue();//取得次一個的值並賦予給second
        //  Log.d(TAG, "getCatchedFromGroup: MAX : " + max + ". // second : " + second);

        /**
         *如果最大的RSSI > PassageRangeRSSIThreshold 且 第一個Beacon的RSSI最大值 跟 第二個BeaconRSSI最大值 兩者相差為RSSIDifference時
         * 簡單來說RSSIDifference愈大，兩個beacon需要相差的rssi就要愈大(訊號強弱要夠明顯)
         * If Beacon = 1 RSSIDifference must = 0.
         */
        if (mGroup.size() == OnlyOneBeacon) {
            Log.d(TAG, "mGroup.size() == OnlyOneBeacon");
            if (max > ConstantUnit.PassageRangeRSSIThreshold) {
                resetMap();
                return sortedEntries.first().getKey();
            }
        } else {
            if (max > ConstantUnit.PassageRangeRSSIThreshold && Math.abs(max - second) >= ConstantUnit.RSSIDifference) {
                //  Log.d(TAG, "Beacon Catched :" + String.valueOf(sortedEntries.first().getKey()) + " , " + sortedEntries.first().getValue());
                AverageRssi = max;
                Log.d(TAG, "max : " + max);
                resetMap();
                return sortedEntries.first().getKey();//回傳最大值的minorID
            }
        }
        return ConstantUnit.NOT_CATCHED;
    }

    public int getCatched(int minor, int rssi) {
        if (mGroup.contains(minor)) {
            //      Log.d(TAG, "mGroup : " + mGroup + " // " + "MrID : " + minor + " // Rssi : " + rssi);
            addBeaconValue(minor, rssi);
            return getCatchedFromGroup();
        } else {
            if (rssi > ConstantUnit.OtherRangeRSSIThreshold) {
                Log.d(TAG, "getCatched : OtherRangeRSSIThreshold: " + "MrID : " + minor + " // Rssi : " + rssi);
                return minor;
            }
            return ConstantUnit.NOT_CATCHED;
        }
    }

    public float getRssi() {
        return AverageRssi;
    }

    public static Float getPassageRangeRSSIThreshold() {
        return ConstantUnit.PassageRangeRSSIThreshold;
    }

    public static void setPassageRangeRSSIThreshold(Float passageRangeRSSIThreshold) {
        ConstantUnit.PassageRangeRSSIThreshold = passageRangeRSSIThreshold;
    }

    public static Float getOtherRangeRSSIThreshold() {
        return ConstantUnit.OtherRangeRSSIThreshold;
    }

    public static void setOtherRangeRSSIThreshold(Float otherRangeRSSIThreshold) {
        ConstantUnit.OtherRangeRSSIThreshold = otherRangeRSSIThreshold;
    }

    public static Float getRSSIDifference() {
        return ConstantUnit.RSSIDifference;
    }

    public static void setRSSIDifference(Float RSSIDifference) {
        ConstantUnit.RSSIDifference = RSSIDifference;
    }

    public static boolean isEnableBeaconInterrupt() {
        return mEnableBeaconInterrupt;
    }

    public static void setEnableBeaconInterrupt(boolean mEnableBeaconInterrupt) {
        CatchAlgorithmUnit.mEnableBeaconInterrupt = mEnableBeaconInterrupt;
    }


}

