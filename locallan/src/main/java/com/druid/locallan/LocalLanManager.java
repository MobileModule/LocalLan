package com.druid.locallan;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocalLanManager {
    private static final String TAG = LocalLanManager.class.getName();
    public static final String SIMPLIFIED_CHINESE = "zh";
    public static final String ENGLISH = "en";

    public static void saveSP(Context context, String table, String key, Object value) {
        SharedSave sharedSave = new SharedSave(context, table);
        sharedSave.put(key, value);
    }

    public static Object getSP(Context context, String table, String key, Object value) {
        SharedSave sharedSave = new SharedSave(context, table);
        return sharedSave.get(key, value);
    }

    public static boolean hasKey(Context context, String table, String key) {
        SharedSave sharedSave = new SharedSave(context, table);
        return sharedSave.hasKey(key);
    }

    public static boolean removeKey(Context context, String table, String key) {
        SharedSave sharedSave = new SharedSave(context, table);
        return sharedSave.remove(key);
    }

    public static boolean firstEnterApp(Context context) {
        String table = SharedTable.GONDAR_SETTING;
        String key = SharedTable.GONDAR_SETTING_TB.FirstEnter;
        if (hasKey(context, table, key)) {
            return (boolean) getSP(context, table, key, false);
        } else {
            saveSP(context, table, key, false);
        }
        return true;
    }

    public static boolean isChinese(Context context) {
        int language = (int) getSP(context, SharedTable.GONDAR_SETTING, SharedTable.GONDAR_SETTING_TB.LANGUAGE, 0);
        if (language == 0)
            return true;
        return false;
    }

    public static void setSysLan(Context context, String lan) {
        saveSP(context, SharedTable.GONDAR_SETTING, SharedTable.GONDAR_SETTING_TB.LANGUAGE_SYS, lan);
    }

    public static String getSysLan(Context context) {
        String table = SharedTable.GONDAR_SETTING;
        String key = SharedTable.GONDAR_SETTING_TB.LANGUAGE_SYS;
        if (hasKey(context, table, key)) {
            return (String) getSP(context, table, key, "");
        } else {
            return "";
        }
    }

    public static void setDefaultLanguage(Context context, String lan) {
        int language = 1;
        if (firstEnterApp(context)) {
            if (lan.toUpperCase().equals(SIMPLIFIED_CHINESE.toUpperCase())) {
                language = 0;//中文
                setSpLanguage(context, language);
            } else {
                setSpLanguage(context, language);
            }
        }
    }

    public static Locale getLanguageLocale(Context context) {
        boolean isCN = isChinese(context);
        if (isCN) {
            return Locale.SIMPLIFIED_CHINESE;
        } else {
            return Locale.ENGLISH;
        }
    }

    public static String getLanguageStr(Context context) {
        boolean isCN = isChinese(context);
        if (isCN) {
            return SIMPLIFIED_CHINESE.toUpperCase();
        } else {
            return ENGLISH.toUpperCase();
        }
    }

    public static void setSpLanguage(Context context, int lan) {
        saveSP(context, SharedTable.GONDAR_SETTING, SharedTable.GONDAR_SETTING_TB.LANGUAGE, lan);
    }

    public static Context attachBaseContext(Context context) {
        String language = getLanguageStr(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = getLocaleByLanguage(language);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    public static void onChangeAppLanguage(Context context) {
        String newLanguage = getLanguageStr(context);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getLocaleByLanguage(newLanguage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    public static Locale getLocaleByLanguage(String language) {
        if (language.equals(SIMPLIFIED_CHINESE.toUpperCase())) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.ENGLISH;
    }
}
