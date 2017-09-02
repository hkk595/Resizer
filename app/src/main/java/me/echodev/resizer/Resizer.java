package me.echodev.resizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;

/**
 * Created by K.K. Ho on 1/9/2017.
 */

public class Resizer {
    private int targetLength, quality;
    private Bitmap.CompressFormat compressFormat;
    private String destinationDirPath;
    private File sourceImage;

    public Resizer(Context context) {
        targetLength = 1080;
        quality = 80;
        compressFormat = Bitmap.CompressFormat.JPEG;
        destinationDirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public Resizer setTargetLength(int targetLength) {
        this.targetLength = targetLength;
        return this;
    }

    public Resizer setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public Resizer setOutputFormat(String outputFormat) {
        switch (outputFormat) {
            case "JPEG":
                this.compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case "PNG":
                this.compressFormat = Bitmap.CompressFormat.PNG;
                break;
            case "WEBP":
                this.compressFormat = Bitmap.CompressFormat.WEBP;
                break;
        }
        return this;
    }

    public Resizer setDestinationDirPath(String destinationDirPath) {
        this.destinationDirPath = destinationDirPath;
        return this;
    }

    public Resizer setSourceImage(File sourceImage) {
        this.sourceImage = sourceImage;
        return this;
    }

    public File getResizedFile() throws IOException {
        File file = new File(destinationDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        // Prepare the new file name and path
        String originalFileName = sourceImage.getName();
        String targetFileName;
        String targetFileExtension = "." + compressFormat.name().toLowerCase().replace("jpeg", "jpg");

        int extensionIndex = originalFileName.lastIndexOf('.');
        if (extensionIndex == -1) {
            targetFileName = originalFileName + targetFileExtension;
        } else {
            targetFileName = originalFileName.substring(0, extensionIndex) + targetFileExtension;
        }

        String destinationFilePath = destinationDirPath + File.separator + targetFileName;

        // Write the resized image to the new file
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destinationFilePath);
            getResizedBitmap().compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationFilePath);
    }

    public Bitmap getResizedBitmap() throws IOException {
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

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    public Flowable<File> getResizedFileAsFlowable() {
        return Flowable.defer(new Callable<Flowable<File>>() {
            @Override
            public Flowable<File> call() {
                try {
                    return Flowable.just(getResizedFile());
                } catch (IOException e) {
                    return Flowable.error(e);
                }
            }
        });
    }

    public Flowable<Bitmap> getResizedBitmapAsFlowable() {
        return Flowable.defer(new Callable<Flowable<Bitmap>>() {
            @Override
            public Flowable<Bitmap> call() {
                try {
                    return Flowable.just(getResizedBitmap());
                } catch (IOException e) {
                    return Flowable.error(e);
                }
            }
        });
    }
}
