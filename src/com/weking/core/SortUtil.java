package com.weking.core;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/5/9.
 * 排序Util
 */
public class SortUtil {

    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;
        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }


    public static TreeMap<String,Integer> sortMap(Map<String , Integer> map) {
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        sorted_map.putAll(map);
        return sorted_map;
    }

}
