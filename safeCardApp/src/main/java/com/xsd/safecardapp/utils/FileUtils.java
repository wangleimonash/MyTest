package com.xsd.safecardapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 项目名称：SvnParent
 * 类描述：
 * 创建人：wangmin
 * 创建时间：2016/7/25 14:30
 * 备注：
 */
public class FileUtils {
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    /** 判断SD卡是否挂载 */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存到sd卡image目录下
     * @param bitmap
     * @param context
     * @param name
     * @return
     */
    public boolean saveBitmap(Bitmap bitmap, Context context,String name){
        if(bitmap == null){
            Log.d("WM","save filed, bitmap is null");
            return false;
        }
        if(isSDCardAvailable()){
            File file = new File(SD_CARD_PATH,context.getPackageName());
            if(!file.exists()){
                file.mkdirs();
            }
            File imgPath = new File(file,"images");
            if(!imgPath.exists()){
                imgPath.mkdirs();
            }
            File imageFile = new File(imgPath,name);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Log.d("WM","保存成功");
                return true;
            }catch (Exception e){
                Log.e("WM","保存失败"+e.getMessage());
                return false;
            }
        }else{
            Log.d("WM","save filed, SDCard is not avluable");
            return false;
        }
    }
}
