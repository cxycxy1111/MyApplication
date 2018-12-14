package com.alfred.alfredtools;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {

    /**
     * 获取long类型的preferenceShare
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static long getLongFromPref(Context context, String fileName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,context.MODE_PRIVATE);
        return preferences.getLong(key,0);
    }

    /**
     * 获取String类型的preferenceShare
     * @param context
     * @param fileName
     * @param key
     * @return
     */
    public static String getStringFromPref(Context context,String fileName,String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static ArrayList<String> getKeysFromListMap(Map<String,String> map) {
        ArrayList<String> list = new ArrayList<>();
        Set<String> set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().toString());
        }
        return list;
    }

    public static ArrayList<String> getValuesFromListMapGet(Map<String,String> map,ArrayList keys) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0;i < keys.size();i++) {
            String value = map.get(keys.get(i));
            list.add(value);
        }
        return list;
    }

    /**
     * 针对ListMap进行升序排序
     * @param list
     * @param key
     */
    public static void sortListMapAsc(List<Map<String,String>> list, final String key) {
        if (null != list&& list.size()>0) {
            Collections.sort(list,new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    Collator collator = Collator.getInstance();
                    CollationKey collationKey_1 = collator.getCollationKey(o1.get(key).toString());
                    CollationKey collationKey_2 = collator.getCollationKey(o2.get(key).toString());
                    int result = collationKey_1.compareTo(collationKey_2);
                    return result;
                }
            });
        }
    }

    /**
     * 针对ListMap进行降序排序
     * @param list
     * @param key
     */
    public static void sortListMapDesc(List<Map<String,String>> list, final String key) {
        if (null != list&& list.size()>0) {
            Collections.sort(list,new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    Collator collator = Collator.getInstance();
                    CollationKey collationKey_1 = collator.getCollationKey(o1.get(key).toString());
                    CollationKey collationKey_2 = collator.getCollationKey(o2.get(key).toString());
                    int result = collationKey_1.compareTo(collationKey_2);
                    return -result;
                }
            });
        }
    }

    /**
     * 对List进行升序
     * @param list
     */
    public static void sortListAsc(List<String> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Collator collator = Collator.getInstance();
                    return collator.getCollationKey(o1.toString()).compareTo(collator.getCollationKey(o2.toString()));
                }
            });
        }
    }

    /**
     * 对List进行降序
     * @param list
     */
    public static void sortListDesc(List<String> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Collator collator = Collator.getInstance();
                    CollationKey key1 = collator.getCollationKey(o1.toString());
                    CollationKey key2 = collator.getCollationKey(o2.toString());
                    int result = key1.compareTo(key2);
                    return -result;
                }
            });
        }
    }

    public static int getPositionFromList(List<String> list,String object) {
        int j = 0;
        for (int i = 0;i < list.size();i++) {
            if (list.get(i).contains(object)) {
                j = i;
                return j;
            }
        }
        return j;
    }



    /**
     * 深拷贝
     * @param src
     * @param <T>
     * @return
     */
    public static <T> List<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.writeObject(src);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
        List<T> dest = null;
        try {
            dest = (List<T>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static boolean parseStringToBool(String string) {
        return !string.contains("0");
    }

    /**
     * 获取Text
     * @param context
     * @param resId
     * @return
     */
    public static CharSequence getText(Context context,int resId) {
        return context.getResources().getText(resId);
    }

    public static String transformUnicodeToStr(String unicodeStr) {
        String regExp = "&#\\d*;";
        Matcher m = Pattern.compile(regExp).matcher(unicodeStr);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String s = m.group(0);
            s = s.replaceAll("(&#)|;", "");
            char c = (char) Integer.parseInt(s);
            m.appendReplacement(sb, Character.toString(c));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
