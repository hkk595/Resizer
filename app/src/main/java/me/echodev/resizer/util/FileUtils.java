package me.echodev.resizer.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by K.K. Ho on 3/9/2017.
 */

public class FileUtils {
    public static String getOutputFilePath(Bitmap.CompressFormat compressFormat, String outputDirPath, String outputFilename, File sourceImage) {
        String originalFileName = sourceImage.getName();
        String targetFileName;
        String targetFileExtension = "." + compressFormat.name().toLowerCase().replace("jpeg", "jpg");

        if (outputFilename == null) {
            int extensionIndex = originalFileName.lastIndexOf('.');
            if (extensionIndex == -1) {
                targetFileName = originalFileName + targetFileExtension;
            } else {
                targetFileName = originalFileName.substring(0, extensionIndex) + targetFileExtension;
            }
        } else {
            targetFileName = outputFilename + targetFileExtension;
        }

        return outputDirPath + File.separator + targetFileName;
    }

    public static void writeBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality, String filePath) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }
}
