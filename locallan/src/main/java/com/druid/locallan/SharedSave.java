package com.druid.locallan;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

public class SharedSave {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharedSave(Context context, String name){
        this.context=context;
        sp=context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public SharedSave(Context context){
        this.context=context;
        sp=context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * 保存数据
     * @param key
     * @param obj
     */
    public void put(String key,Object obj){
        //获取到SharedPreferences对象后，只保存String，Integer，Boolean，Float，Long类型，其余的类型都当作是String类型来处理
        editor = sp.edit();
        if(obj instanceof String){
            editor.putString(key, (String)obj);
        }
        else if(obj instanceof Integer){
            editor.putInt(key, (Integer)obj);
        }
        else if(obj instanceof Boolean){
            editor.putBoolean(key, (Boolean)obj);
        }
        else if(obj instanceof Float){
            editor.putFloat(key, (Float)obj);
        }
        else if(obj instanceof Long){
            editor.putLong(key, (Long)obj);
        }
        else{
            editor.putString(key, (String)obj);
        }
        //提交
        editor.commit();
        //SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取数据
     * @param key
     * @param defValue
     * @return
     */
    public  Object get(String key,Object defValue){

        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        }
        else if (defValue instanceof Integer)  {
            return sp.getInt(key, (Integer) defValue);
        }
        else if (defValue instanceof Boolean)  {
            return sp.getBoolean(key, (Boolean) defValue);
        }
        else if (defValue instanceof Float)  {
            return sp.getFloat(key, (Float) defValue);
        }
        else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }


    /**
     * 移除某个key值已经对应的值
     * @param key
     */
    public boolean remove( String key)  {
        editor = sp.edit();
        editor.remove(key);
        return editor.commit();
        //SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public  void clear()  {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        //SharedPreferencesCompat.apply(editor);
    }

    public boolean hasKey(String key){
        return sp.contains(key);
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    public  boolean contains(String key)  {
        return sp.contains(key);
    }

    /**
     * 先反射获取系统appay方法.并尽可能调用appay方法
     * 如果没有通过反射获取到.那么就直接调用commit方法提交editor
     * 两者都不需要返回值
     * 因为commit方法是同步的，并且我们很多时候的commit操作都是UI线程中，毕竟是IO操作，尽可能异步；
     * 所以我们使用apply进行替代，apply异步的进行写入；但是apply相当于commit来说是new API呢，为了更好的兼容，我们做了适配；
     */
    private static class SharedPreferencesCompat{
        private static final Method APPAY_METHOD = findApplyMethod();

        //先通过反射获取一遍Method.如果有直接赋值给成员变量
        @SuppressWarnings({ "unchecked", "rawtypes" })//压制警告
        private static Method findApplyMethod(){
            try{
                Class clazz = SharedPreferences.Editor.class;
                return clazz.getMethod("apply");
            }
            catch (Exception e){
            }
            return null;
        }

        public static void apply(SharedPreferences.Editor editor){
            try {
                //先通过反射获取并调用系统的apply函数.没有在调用commit
                if(APPAY_METHOD != null){
                    APPAY_METHOD.invoke(editor);
                    return;
                }
            }
            catch (Exception e) {
            }
            editor.commit();
        }
    }
}
