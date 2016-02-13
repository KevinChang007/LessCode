package com.jayfeng.lesscode.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.InputStream;

public final class BitmapLess {

    /**
     * 根据reqWidth, reqHeight计算最合适的inSampleSize
     *
     * @param options
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static int $sampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        // raw height and width of image
        int rawWidth = options.outWidth;
        int rawHeight = options.outHeight;

        // calculate best sample size
        int inSampleSize = 0;
        if (rawHeight > maxHeight || rawWidth > maxWidth) {
            float ratioWidth = (float) rawWidth / maxWidth;
            float ratioHeight = (float) rawHeight / maxHeight;
            inSampleSize = (int) Math.min(ratioHeight, ratioWidth);
        }
        inSampleSize = Math.max(1, inSampleSize);

        return inSampleSize;
    }

    /**
     * 更节省内存的读取raw资源成Bitmap
     *
     * @param context
     * @param rawId
     * @return
     */
    public static Bitmap $raw(Context context, int rawId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(rawId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 旋转Bitmap(默认回收传进来的原始Bitmap)
     *
     * @param originBitmap
     * @param angle
     * @return
     */
    public static Bitmap $rotate(Bitmap originBitmap, int angle) {
        return $rotate(originBitmap, angle, true);
    }

    /**
     * 旋转Bitmap
     *
     * @param angle
     * @param originBitmap
     * @param recycleOriginBitmap 是否回收传进来的原始Bitmap
     * @return
     */
    public static Bitmap $rotate(Bitmap originBitmap, int angle, boolean recycleOriginBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originBitmap,
                0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
        if (recycleOriginBitmap && originBitmap != null && !originBitmap.isRecycled()) {
            originBitmap.recycle();
        }
        return rotatedBitmap;
    }

    /**
     * 缩放Bitmap(默认回收传进来的原始Bitmap)
     *
     * @param originBitmap
     * @param scaleX
     * @param scaleY
     * @return
     */
    public static Bitmap $scale(Bitmap originBitmap, float scaleX, float scaleY) {
        return $scale(originBitmap, scaleX, scaleY, true);
    }

    /**
     * 缩放Bitmap
     *
     * @param originBitmap
     * @param scaleX
     * @param scaleY
     * @param recycleOriginBitmap 是否回收传进来的原始Bitmap
     * @return
     */
    public static Bitmap $scale(Bitmap originBitmap, float scaleX, float scaleY, boolean recycleOriginBitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originBitmap,
                0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
        if (recycleOriginBitmap && originBitmap != null && !originBitmap.isRecycled()) {
            originBitmap.recycle();
        }
        return rotatedBitmap;
    }

    /**
     * 获取缩略图（默认关闭自动旋转）
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap $thumbnail(String path, int maxWidth, int maxHeight) {
        return $thumbnail(path, maxWidth, maxHeight, false);
    }

    /**
     * 获取缩略图
     * 1. 支持自动旋转
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @param autoRotate
     * @return
     */
    public static Bitmap $thumbnail(String path, int maxWidth, int maxHeight, boolean autoRotate) {

        int angle = 0;
        if (autoRotate) {
            angle = ImageLess.$exifRotateAngle(path);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高, 此时返回bm为空
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        // 计算缩放比
        int sampleSize = $sampleSize(options, maxWidth, maxHeight);
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = BitmapFactory.decodeFile(path, options);

        if (autoRotate && angle != 0) {
            bitmap = $rotate(bitmap, angle);
        }

        return bitmap;
    }
}
