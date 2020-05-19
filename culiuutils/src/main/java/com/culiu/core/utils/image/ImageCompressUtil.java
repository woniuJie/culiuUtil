package com.culiu.core.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片压缩处理工具类
 * <p/>
 * Created by wangjing on 2016/2/1.
 */
public class ImageCompressUtil {

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param filePath 待处理的图片路径
     * @param maxSize  压缩到多大 kb
     *
     * @return bytes of image
     */
    public static byte[] compress(String filePath, int maxSize) {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists())
            return new byte[0];
        return compress(BitmapFactory.decodeFile(filePath), maxSize);
    }

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param file    待处理的file
     * @param maxSize 压缩到多大 kb
     *
     * @return bytes of image
     */
    public static byte[] compress(File file, int maxSize) {
        if (file == null || !file.exists())
            return new byte[0];
        return compress(BitmapFactory.decodeFile(file.getAbsolutePath()), maxSize);
    }

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param bitmap  待处理的bitmap
     * @param maxSize 压缩到多大 kb
     *
     * @return bytes of iamge
     */
    public static byte[] compress(Bitmap bitmap, int maxSize) {
        if (bitmap == null)
            return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int factor = 0;
        int bitmapSize = getBitmapSize(bitmap);
        int kb = bitmapSize / 1024;
        if (kb <= 1024) { // 1M 以内
            factor = 20;
        } else {
            factor = 50;
        }
        int length = 0;
        int options = 100;
        do {
            options -= factor;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            length = baos.toByteArray().length;
        } while (length / 1024 > maxSize);
        return baos.toByteArray();
    }

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param filePath 图片路径
     * @param fileSize 压缩到多大 kb
     *
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmap(String filePath, int fileSize) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compress(filePath, fileSize));
        return BitmapFactory.decodeStream(bais, null, null);
    }

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param file     带压缩的图片file
     * @param fileSize 压缩到多大 kb
     *
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmap(File file, int fileSize) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compress(file, fileSize));
        return BitmapFactory.decodeStream(bais, null, null);
    }

    /**
     * 压缩Bitmap，从图片质量100开始压缩
     *
     * @param bitmap   待压缩的图片
     * @param fileSize 压缩到多大 kb
     *
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int fileSize) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compress(bitmap, fileSize));
        return BitmapFactory.decodeStream(bais, null, null);
    }

    /**
     * 获取图片方向
     *
     * @param filePath 图片路径
     *
     * @return 图片方向值
     */
    public static int getImageOrientation(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return ExifInterface.ORIENTATION_NORMAL;
        return getImageOrientation(new File(filePath));
    }

    /**
     * 获取图片方向
     *
     * @param file 图片
     *
     * @return 图片方向值
     */
    public static int getImageOrientation(File file) {
        if (file == null || !file.exists())
            return ExifInterface.ORIENTATION_NORMAL;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exifInterface == null)
            return ExifInterface.ORIENTATION_NORMAL;
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
    }

    /**
     * 旋转图片
     *
     * @param bitmap      待旋转的图片
     * @param orientation 旋转到那个方向
     *
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bitmapRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 压缩图片
     *
     * @param filePath
     * @param width
     * @param height
     *
     * @return
     */
    public static File compressImage(String filePath, float width, float height) {
        return compressBitmapWidthHeight(createTempImageFilePath(),
                new File(filePath), width, height);
    }

    /**
     * 创建临时文件
     *
     * @return
     */
    private static String createTempImageFilePath() {
        return null;
    }

    /**
     * 压缩图片文件,并生成压缩后的临时文件
     *
     * @param tempFilePath 输出临时文件路径
     * @param file         待压缩的file文件
     * @param width        输出宽度
     * @param height       输出高度
     *
     * @return 压缩后生成的临时文件
     */
    public static File compressBitmapWidthHeight(String tempFilePath, File file,
                                                 float width, float height) {
        if (file == null || !file.exists())
            return file;
        Bitmap bitmap = compressBitmapWidthHeight(BitmapFactory.decodeFile(file.getAbsolutePath()),
                width, height);
        File tempFile = new File(tempFilePath);
        if (!tempFile.exists()) {
            try {
                File dir = tempFile.getParentFile();
                if (dir != null && !dir.exists()) {
                    dir.mkdirs();
                }
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean compressSuccess = false;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            bitmap = rotateBitmap(bitmap, getImageOrientation(file));
            compressSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null)
                bitmap.recycle();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return compressSuccess ? tempFile : file;
    }

    /**
     * 获取bitmap的大小
     *
     * @param bitmap 计算的bitmap
     *
     * @return bimap的字节数
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 压缩bitmap,返回压缩后的bitmap
     *
     * @param bitmap 待压缩的bitmap
     * @param width  输出的宽度
     * @param height 输出的高度
     *
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmapWidthHeight(Bitmap bitmap, float width, float height) {
        // 压缩图片质量到1M以下, 读取图片options
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        ByteArrayInputStream bais = new ByteArrayInputStream(compress(bitmap, 1024));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bais, null, options);
        options.inJustDecodeBounds = false;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        // 计算缩放比例
        int inSampleSize = 1;
        if (outWidth > outHeight && outWidth > width) {
            inSampleSize = (int) (options.outWidth / width);
        } else if (outWidth < outHeight && outHeight > height) {
            inSampleSize = (int) (options.outHeight / height);
        }
        if (inSampleSize <= 0)
            inSampleSize = 1;
        options.inSampleSize = inSampleSize;
        bais.reset();
        // 使用新的option压缩图片
        bitmap = BitmapFactory.decodeStream(bais, null, options);
        return bitmap;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
//        return BitmapFactory.decodeStream(byteArrayInputStream, null, null);
    }

}
