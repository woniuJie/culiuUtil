package com.culiu.core.utils.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.culiu.core.utils.debug.DebugLog;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * uri生成工具
 * Created by wangjing on 2015/12/15.
 */
public class UriUtil {

    private final static String CHARSET = "UTF-8";

    private static final String SCHEME_FILE = "file";

    private static final String SCHEME_CONTENT = "content";

    private static final String GOOGLE_GALLERY3D = "content://com.google.android.gallery3d";

    /**
     * 将android的content的uri转换为file类型的uri
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public static Uri getUriFromMediaUri(Context context, Uri uri) {
        if (SCHEME_FILE.equals(uri.getScheme())) {
            return uri;
        }
        File file = getFromMediaUri(context, uri);
        if (file == null)
            return null;
        return Uri.fromFile(file);
    }

    /**
     * 将androidcontent的uri转换为file
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public static File getFromMediaUri(Context context, Uri uri) {
        if (context == null)
            return null;
        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith(GOOGLE_GALLERY3D)) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                // google drive images
                return getFromMediaUriPfd(context, uri);
            } catch (SecurityException e) {
                // nothing we can do
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return null;
    }

    /**
     * 将google drive的uri转换为file
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public static File getFromMediaUriPfd(Context context, Uri uri) {
        if (uri == null) return null;
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(context);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return new File(tempFilename);
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    /**
     * 安全的关闭流
     *
     * @param c
     */
    public static void closeSilently(@Nullable Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    /**
     * 创建一个临时文件的路径
     *
     * @param context
     *
     * @return
     *
     * @throws IOException
     */
    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, CHARSET);
        } catch (Exception e) {
            DebugLog.i(e.getMessage());
        }

        return string;
    }

    public static String urlDecode(String string) {
        try {
            return URLDecoder.decode(string, CHARSET);
        } catch (Exception e) {
            DebugLog.i(e.getMessage());
        }

        return string;
    }

    public static String getFormString(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        Set<String> set = map.keySet();

        for (String key : set) {
            result.append(key).append("=").append(urlEncode(map.get(key))).append("&");
        }
        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    public static String uriToStr(Uri uri) {
        return uri.toString();
    }

    public static Uri strToUri(String uriString) {
        if (TextUtils.isEmpty(uriString))
            return null;
        return Uri.parse(uriString).buildUpon().build();
    }

    public static boolean isLocalUri(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        Uri uri = strToUri(url);
        if (uri == null)
            return false;
        if ("http".equals(uri.getScheme()))
            return false;
        if ("https".equals(uri.getScheme()))
            return false;
        return true;
    }

    public static String getPath(Context context, final Uri uri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    switch (type) {
                        case "image":
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            } else {
                return uri.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                String value = cursor.getString(column_index);
                if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith("file://")) {
                    return null;
                }
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
