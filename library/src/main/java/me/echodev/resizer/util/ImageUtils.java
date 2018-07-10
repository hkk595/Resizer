package me.echodev.resizer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by K.K. Ho on 3/9/2017.
 */

public class ImageUtils {
    public static File getScaledImage(int targetLength, int quality, Bitmap.CompressFormat compressFormat,
                                      String outputDirPath, String outputFilename, File sourceImage) throws IOException {
        File directory = new File(outputDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Prepare the new file name and path
        String outputFilePath = FileUtils.getOutputFilePath(compressFormat, outputDirPath, outputFilename, sourceImage);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(targetLength, sourceImage);
        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);

        return new File(outputFilePath);
    }

    public static Bitmap getScaledBitmap(int targetLength, File sourceImage) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);

        // Get the dimensions of the original bitmap
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        float aspectRatio = (float) originalWidth / originalHeight;

        // Calculate the target dimensions
        int targetWidth, targetHeight;

        if (originalWidth > originalHeight) {
            targetWidth = targetLength;
            targetHeight = Math.round(targetWidth / aspectRatio);
        } else {
            aspectRatio = 1 / aspectRatio;
            targetHeight = targetLength;
            targetWidth = Math.round(targetHeight / aspectRatio);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

        //check the rotation of the image and display it properly
        ExifInterface exif;
        exif = new ExifInterface(sourceImage.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }
}
