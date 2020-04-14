package top.i97.editadapterlib.util;

import java.util.List;

/**
 * name: ListUtils
 * desc: 集合工具类
 * date: 2020/4/14 4:31 PM
 * version: v1.0
 * author: Plain
 * blog: https://plain-dev.com
 * email: support@plain-dev.com
 */
public class ListUtils {

    private ListUtils() {

    }

    /**
     * List是否为空
     *
     * @param list List
     * @return true or false
     */
    public static boolean isEmpty(List list) {
        return null == list || 0 == list.size();
    }


}
