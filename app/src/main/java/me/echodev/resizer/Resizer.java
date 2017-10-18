package me.echodev.resizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import me.echodev.resizer.util.ImageUtils;

/**
 * Created by K.K. Ho & HassanUsman on 1/9/2017.
 */

public class Resizer {
    private int targetLength, quality;
    private Bitmap.CompressFormat compressFormat;
    private String outputDirPath;
    private File sourceImage;

    public Resizer(Context context) {
        targetLength = 1080;
        quality = 80;
        compressFormat = Bitmap.CompressFormat.JPEG;
        outputDirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
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

    public Resizer setOutputDirPath(String outputDirPath) {
        this.outputDirPath = outputDirPath;
        return this;
    }

    public Resizer setSourceImage(File sourceImage) {
        this.sourceImage = sourceImage;
        return this;
    }

    public File getResizedFile() throws IOException {
        return ImageUtils.getScaledImage(targetLength, quality, compressFormat, outputDirPath, sourceImage);
    }

    public Bitmap getResizedBitmap() throws IOException {
        return ImageUtils.getScaledBitmap(targetLength, sourceImage);
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
