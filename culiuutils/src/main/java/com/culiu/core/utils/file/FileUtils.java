package com.culiu.core.utils.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.culiu.core.utils.debug.DebugLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * @author adison
 * @describe 文件工具类.
 * @date: 2014-10-22 下午3:42:16 <br/>
 */
public class FileUtils {

    /**
     * createDirInSDCard:创建目录以及检查是否创建成功. <br/>
     *
     * @param dirName
     * @return
     * @author adison
     */
    public static File createDirInSDCard(String dirName) {
        File file = createDirInSDCarder(Environment.getExternalStorageDirectory(), dirName);
        return file;
    }

    public static File createDirInSDCarder(File parent, String dirName) {
        File file = new File(parent, dirName);
        if (!file.exists() || !file.isFile()) {
            boolean isCreated = file.mkdir();
            // if(isCreated || file.isDirectory()) {
            // LogUtil.i(TAG, dirName + " create successfully!!!");
            // }
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 创建的文件
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    /**
     * SD卡是否可用
     *
     * @return true 可用
     * 为什么添加try-catch
     * Crash平台异常 @ 2.7.3 XIAOMI-2013022
     * Fatal Exception: java.lang.NullPointerException
     * at android.os.Environment.getExternalStorageState(Environment.java:654)
     * at com.culiu.core.utils.file.FileUtils.isExternalStorageMounted(SourceFile:70)
     */
    public static boolean isExternalStorageMounted() {
        try {
            boolean canRead = Environment.getExternalStorageDirectory().canRead();
            boolean onlyRead = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
            boolean unMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED);
            return !(!canRead || onlyRead || unMounted);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return
     */
    public static long getAvailableStorage() {

        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();

        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }

    }

    /**
     * SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardPresent() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取内部存储区/data/剩余空间
     *
     * @return Byte字节数
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSize();
        long availableBlocks = (long) stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取/data/data/[packageName]/cache目录
     *
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 获取/data/data/[packageName]/files目录
     *
     * @param context
     * @return
     */
    public static File getFileDirectory(Context context) {
        File appCacheDir = null;
        if (appCacheDir == null) {
            appCacheDir = context.getFilesDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 获取外部SD卡的应用私有空间的文件目录
     *
     * @param context
     * @return BugFixd: 只能Try-Catch了
     * Fatal Exception: java.lang.NullPointerException
     * at android.os.Parcel.readException(Parcel.java:1478)
     * at android.os.Parcel.readException(Parcel.java:1426)
     * at android.os.storage.IMountService$Stub$Proxy.mkdirs(IMountService.java:750)
     * at android.app.ContextImpl.ensureDirsExistOrFilter(ContextImpl.java:2195)
     * at android.app.ContextImpl.getExternalFilesDirs(ContextImpl.java:898)
     * at android.app.ContextImpl.getExternalFilesDir(ContextImpl.java:881)
     * at android.content.ContextWrapper.getExternalFilesDir(ContextWrapper.java:210)
     * at com.culiu.core.utils.file.FileUtils.getExternalFileDirectory(SourceFile:164)
     * at com.culiu.core.utils.file.FileUtils.getExternalFilePath(SourceFile:179)
     */
    public static File getExternalFileDirectory(Context context) {
        try {
            if (FileUtils.isExternalStorageMounted() && null != context.getExternalFilesDir("")) {
                return context.getExternalFilesDir("");
            }
            return new File(new StringBuilder().append("/mnt/sdcard/Android/data/").append(context.getPackageName()).append("/files/").toString());
        } catch (Exception e) {
            // /storage/emulated/0, /storage/sdcard0/
            return new File(new StringBuilder().append("/mnt/sdcard/Android/data/").append(context.getPackageName()).append("/files/").toString());
//        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName(); // 自定义SD卡路径
        }
    }

    /**
     * 获取外部SD卡的应用私有空间文件目录的绝对路径
     *
     * @param context
     * @return
     */
    public static String getExternalFilePath(Context context) {
        return getExternalFileDirectory(context).getAbsoluteFile() + File.separator;
    }

    /**
     * 获取外部SD卡的应用私有空间的文件目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDirectory(Context context) {
        if (FileUtils.isExternalStorageMounted()
                && context != null
                && null != context.getExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // /storage/emulated/0, /storage/sdcard0/
        return new File(new StringBuilder().append("/mnt/sdcard/Android/data/").append(context.getPackageName()).append("/cache/").toString());
    }

    /**
     * 判断文件是否存在. <br/>
     *
     * @param context
     * @param filename 可以是外部SD卡上的绝对路径文件, 也可以是内存空间的应用私有空间(files)下的相对路径文件
     * @return true: 文件存在; false: 文件不存在
     */
    public static boolean isFileExists(Context context, String filename) {
        if (FileUtils.isAbsolutePathFile(filename)) {
            return new File(filename).exists();
        } else {
            return new File(context.getFilesDir(), filename).exists();
        }
    }

    public static boolean isFileExists(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists())
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean hasFiles(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                String[] fileList = file.list();
                if (fileList != null && fileList.length > 0)
                    return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAssetsExists(Context context, String foldPath, String fileNath) {
        if (context == null || TextUtils.isEmpty(foldPath) || TextUtils.isEmpty(fileNath))
            return false;
        AssetManager am = context.getAssets();
        try {
            String[] names = am.list(foldPath);
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(fileNath.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 把源文件复制到目标文件中。
     *
     * @param source 源文件
     * @param dest   目标文件
     * @throws IOException 如果源文件不存在或者目标文件不可写入，抛出IO异常。
     */
    public static void copy(File source, File dest) throws IOException {
        FileInputStream fileIS = null;
        FileOutputStream fileOS = null;
        try {
            fileIS = new FileInputStream(source);
            fileOS = new FileOutputStream(dest);
            FileChannel fic = fileIS.getChannel();
            MappedByteBuffer mbuf = fic.map(FileChannel.MapMode.READ_ONLY, 0, source.length());
            fic.close();
            fileIS.close();
            if (!dest.exists()) {
                String destPath = dest.getPath();
                String destDir = destPath.substring(0, destPath.lastIndexOf(File.separatorChar));
                File dir = new File(destDir);
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        DebugLog.i("Directory created");
                    } else {
                        DebugLog.e("Directory not created");
                    }
                }
            }
            FileChannel foc = fileOS.getChannel();
            foc.write(mbuf);
            foc.close();
            mbuf.clear();
        } catch (Exception ex) {
            DebugLog.e("Source File not exist !");
        } finally {
            if (fileOS != null) {
                fileOS.close();
            }
            if (fileIS != null) {
                fileIS.close();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件路径
     * @param dest   目标文件路径
     * @throws IOException 如果源文件不存在或者目标文件不可写入，抛出IO异常。
     */
    public static void copy(String source, String dest) throws IOException {
        copy(new File(source), new File(dest));
    }

    /**
     * 保存一个输入流到指定路径中，保存完成后输入流将被关闭。
     *
     * @param is
     * @param path
     * @throws IOException
     */
    public static void save(InputStream is, String path) throws IOException {
        save(is, path, true);
    }

    /**
     * 保存一个输入流到指定路径中
     *
     * @param is               输入流
     * @param path             路径
     * @param closeInputStream 是否关闭输入流
     * @throws IOException
     */
    public static void save(InputStream is, String path, boolean closeInputStream) throws IOException {
        FileOutputStream os = new FileOutputStream(createFile(path));
        byte[] cache = new byte[10 * 1024];
        for (int len = 0; (len = is.read(cache)) != -1; ) {
            os.write(cache, 0, len);
        }
        os.close();
        if (closeInputStream)
            is.close();
    }

    public static File createFile(String path) throws IOException {
        File distFile = new File(path);
        if (!distFile.exists()) {
            File dir = distFile.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            distFile.createNewFile();
        }
        return distFile;
    }

    /**
     * 保存一个字节数组流到指定路径中
     *
     * @param data
     * @param path
     */
    public static void save(byte[] data, String path) throws IOException {
        FileOutputStream os = new FileOutputStream(createFile(path));
        os.write(data, 0, data.length);
        os.flush();
        os.close();
    }

    /**
     * 读取小文件的全部内容
     *
     * @param path
     * @return
     */
    public static String readFileContent(String path) {
        // TODO: 2016/4/5  应该依据文件的实际长度；动态分配Buffer大小，以便一次性读取完成文件内容；
        String result = null;
        FileInputStream is = null;

        try {
            File file = new File(path);
            is = new FileInputStream(file);
            byte[] readBuffer;
            readBuffer = streamToBytes(is, (int) file.length());
            result = new String(readBuffer, 0, (int) file.length());
//            result = readBuffer.toString();   // 这种方案有问题！
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e) {

            }
        }

        return result;
    }

    /**
     * Reads the contents of an InputStream into a byte[]. 【更加安全的读取文件全部内容】
     * Copy form @[Vooley.DiskBasedCache]
     */
    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        int count;
        int pos = 0;
        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
            pos += count;
        }

        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        }
        return bytes;
    }

    /**
     * 移动文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void moveFile(String source, String dest) throws IOException {
        copy(source, dest);
        File src = new File(source);
        if (src.exists() && src.canRead()) {
            if (src.delete()) {
                DebugLog.i("Source file was deleted");
            } else {
                src.deleteOnExit();
            }
        } else {
            DebugLog.w("Source file could not be accessed for removal");
        }
    }

    /**
     * 删除文件夹及其下内容
     *
     * @param dirPath
     * @return
     * @throws IOException
     */
    public static boolean deleteDirectory(String dirPath) throws IOException {
        if (dirPath == null)
            return false;
        return deleteDirectory(new File(dirPath));
    }

    /**
     * 删除文件夹下的文件，但不删除当前文件夹
     *
     * @param @param  dirFile
     * @param @return
     * @return boolean
     * @throws
     * @Description:TODO
     */
    public static boolean deleteFilesInDirectory(File dirFile) {
        boolean result = false;
        if (dirFile != null && dirFile.isDirectory()) {
            if (dirFile != null) {
                for (File file : dirFile.listFiles()) {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * 删除文件夹及其下内容
     *
     * @param dirFile
     * @return
     */
    public static boolean deleteDirectory(File dirFile) {
        boolean result = false;
        if (dirFile != null && dirFile.isDirectory()) {
            if (dirFile != null && dirFile.listFiles() != null) {
                for (File file : dirFile.listFiles()) {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
                if (dirFile.delete()) {
                    result = true;
                } else {
                    dirFile.deleteOnExit();
                }
            }
        }
        return result;
    }

    /**
     * 删除文件夹及其下内容。如果文件夹被系统锁定或者文件夹不能被清空，将返回false。
     *
     * @param directory
     * @return 文件夹删除成功则返回true，文件夹不存在则返回false。
     * @throws IOException 如果文件夹不能被删除，则抛出异常。
     */
    public static boolean deleteDirectoryWithOSNative(String directory) throws IOException {
        boolean result = false;
        Process process = null;
        Thread std = null;
        try {
            Runtime runTime = Runtime.getRuntime();
            if (File.separatorChar == '\\') {
                process = runTime.exec("CMD /D /C \"RMDIR /Q /S " + directory.replace('/', '\\') + "\"");
            } else {
                process = runTime.exec("rm -rf " + directory.replace('\\', File.separatorChar));
            }
            std = stdOut(process);
            while (std.isAlive()) {
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                }
            }
            result = true;
        } catch (Exception e) {
            DebugLog.e("Error running delete script");
        } finally {
            if (null != process) {
                process.destroy();
                process = null;
            }
            std = null;
        }
        return result;
    }

    /**
     * 删除文件
     * <p>
     * 解决删除文件报 libcore.io.ErrnoException: open failed: EBUSY (Device or resource busy) 问题:
     * <p>
     * 参考:
     * http://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
     * http://haoxiqiang.github.io/blog/20141222-Android-EBUSY-Exception.html
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        // 重命名删除文件,防止此文件还存在引用
        File tempFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(tempFile);
        // 删除文件
        boolean result = tempFile.delete();
        if (!result) {
            tempFile.deleteOnExit();
        }
        return result;
    }

    public static boolean deleteFile(Context context, String filename) {
        if (FileUtils.isAbsolutePathFile(filename)) {
            return FileUtils.deleteFile(new File(filename));
        } else {
            return context.deleteFile(filename);
        }
    }

    /**
     * 使用本地系统命令mv一个文件。
     *
     * @param from 原文件名
     * @param to   新文件名
     */
    public static void move(String from, String to) {
        Process process = null;
        Thread std = null;
        try {
            Runtime runTime = Runtime.getRuntime();
            if (File.separatorChar == '\\') {
                process = runTime.exec("CMD /D /C \"REN " + from + ' ' + to + "\"");
            } else {
                process = runTime.exec("mv -f " + from + ' ' + to);
            }
            std = stdOut(process);
            while (std.isAlive()) {
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            DebugLog.e("Error running delete script");
        } finally {
            if (null != process) {
                process.destroy();
                process = null;
                std = null;
            }
        }
    }

    /**
     * rename 在同一个存储设备分区内，重命名文件 <br/>
     *
     * @param context
     * @param source  : 可以是外部SD卡文件，也可以是应用内部私有空间文件
     * @param dest    ： 与source保持在同一个存储空间
     * @return
     */
    public static boolean rename(Context context, String source, String dest) {
        File sourceFile;
        File destFile;
        if (isAbsolutePathFile(source)) {
            sourceFile = new File(source);
            destFile = new File(dest);
        } else { /* 非绝对路径下，使用应用内部私有空间文件 */
            sourceFile = new File(context.getFilesDir(), source);
            destFile = new File(context.getFilesDir(), dest);
        }

        return sourceFile.renameTo(destFile);
    }

    /**
     * 创建一个文件夹。
     *
     * @param directory
     * @return 创建成功则返回true，否则返回false。
     * @throws IOException
     */
    public static boolean makeDirectory(String directory) throws IOException {
        return makeDirectory(directory, false);
    }

    /**
     * 提取文件名
     *
     * @param path
     * @return
     */
    public static String extractName(String path) {
        if (path == null)
            return null;
        boolean hasFileName = path.substring(path.length() - 5, path.length()).contains(".");
        if (hasFileName) {
            return path.substring(path.lastIndexOf(File.separator) + 1);
        } else {
            return null;
        }
    }

    /**
     * 导入文件后缀名
     *
     * @param path
     * @return
     */
    public static String extractSuffix(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    /**
     * 创建一个文件夹 如果 被标记为true，则父级文件夹不存在将会被自动创建。
     *
     * @param directory     需要被创建的文件夹
     * @param createParents 是否创建父级文件夹标识
     * @return 如果文件夹创建成功，返回true。如果文件夹已经存在，返回false。
     * @throws IOException 如果文件夹不能被创建，则抛出异常
     */
    public static boolean makeDirectory(String directory, boolean createParents) throws IOException {
        boolean created = false;
        File dir = new File(directory);
        if (createParents) {
            created = dir.mkdirs();
        } else {
            created = dir.mkdir();
        }
        return created;
    }

    /**
     * 计算文件夹大小
     *
     * @param directory
     * @return
     * @throws IOException
     */
    public static long getSize(File directory) throws IOException {

        File[] files = directory.listFiles();
        long size = 0;
        for (File f : files) {
            if (f.isDirectory())
                size += getSize(f);
            else {
                FileInputStream fis = new FileInputStream(f);
                size += fis.available();
                fis.close();
            }
        }
        return size;
    }

    /**
     * Special method for capture of StdOut.
     *
     * @return
     */
    private final static Thread stdOut(final Process p) {
        final byte[] empty = new byte[128];
        for (int b = 0; b < empty.length; b++) {
            empty[b] = (byte) 0;
        }
        Thread std = new Thread() {

            public void run() {
                StringBuilder sb = new StringBuilder(1024);
                byte[] buf = new byte[128];
                BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
                try {
                    while (bis.read(buf) != -1) {
                        sb.append(new String(buf).trim());
                        System.arraycopy(empty, 0, buf, 0, buf.length);
                    }
                    bis.close();
                } catch (Exception e) {
                    DebugLog.e(String.format("%1$s", e));
                }
            }
        };
        std.setDaemon(true);
        std.start();
        return std;
    }

    /**
     * 判断sdcard是否可写
     *
     * @return
     */
    public static boolean isSDCardWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是外部SD卡路径
     * @author yedr
     * @param filename
     * @return
     */
/*    public static boolean isExternalSdcardFile(String filename) {
        return filename.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
    }*/

    /**
     * 是否是绝对路径文件. <br/>
     *
     * @param filename
     * @return
     * @author yedr
     */
    public static boolean isAbsolutePathFile(String filename) {
        return filename.startsWith(File.separator);
    }

    public static String getRealPathFromUri(String fileUri) {
        // 去除协议头
        if (fileUri.startsWith("file://")) {
            return fileUri.replace("file://", "");
        } else if (fileUri.startsWith("content://")) {
            return fileUri.replace("content://", "");
        }

        return "";
    }

    public static File createParentPath(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        return createParentPath(new File(path));
    }

    public static File createParentPath(File file) {
        if (file == null)
            return null;
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            createParentPath(file.getParentFile());
            file.mkdir();
        }
        return file;
    }

    /**
     * 将文件内容读取到String中
     *
     * @param fileName
     * @return
     */
    public static String loadFileAsString(String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            String text = loadReaderAsString(reader);
            reader.close();
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将Reader内容读取到String
     *
     * @param reader
     * @return
     */
    public static String loadReaderAsString(Reader reader) {
        try {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[4096];
            int readLength = reader.read(buffer);
            while (readLength >= 0) {
                builder.append(buffer, 0, readLength);
                readLength = reader.read(buffer);
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 复制assets下的文件到sdcard中，需要异步操作可以自己实现
     *
     * @param context
     * @param srcPath
     * @param dstPath
     * @return
     */
    public static boolean copyAssetsToDst(Context context, String srcPath, String dstPath) {
        if (context == null || TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath))
            return false;
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames != null && fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean coryAssetsToSDCard(Context context, String fileName, String dstPath, String dstName) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(dstPath) || TextUtils.isEmpty(dstName))
            return false;
        try {
            File dir = new File(dstPath);
            if (!dir.exists())
                dir.mkdirs();
            String dstFilePath = dstPath + File.separator + dstName;

            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(dstFilePath);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝整个目录到另一个目录，异步操作自己实现
     *
     * @param context
     * @param srcPath
     * @param dstPath
     * @return
     */
    public static boolean copyDirectToDircet(Context context, String srcPath, String dstPath) {
        if (context == null || TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath))
            return false;
        try {
            File srcFile = new File(srcPath);
            String fileNames[] = srcFile.list();
            if (fileNames != null && fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) {
                        copyDirectToDircet(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else {
                        copyDirectToDircet(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(dstPath);
                InputStream is = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
