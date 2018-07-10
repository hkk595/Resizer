package me.echodev.resizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import me.echodev.resizer.util.ImageUtils;

/**
 * Created by K.K. Ho on 1/9/2017.
 */

/**
 * An image resizing library for Android, which allows you to scale an image file to a smaller or bigger one while keeping the aspect ratio.
 */
public class Resizer {
    private int targetLength, quality;
    private Bitmap.CompressFormat compressFormat;
    private String outputDirPath, outputFilename;
    private File sourceImage;

    /**
     * The constructor to initialize Resizer instance.
     * @param context The global application context. You can get it by getApplicationContext().
     */
    public Resizer(Context context) {
        targetLength = 1080;
        quality = 80;
        compressFormat = Bitmap.CompressFormat.JPEG;
        outputDirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        outputFilename = null;
    }

    /**
     * Set the target length of the image. You only need to specify the target length of the longer side (or either side if it's a square). Resizer will calculate the rest automatically.
     * @param targetLength The target image length in pixel. The default value is 1080.
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setTargetLength(int targetLength) {
        this.targetLength = (targetLength < 0) ? 0 : targetLength;
        return this;
    }

    /**
     * Set the image quality. The higher value, the better image quality but larger file size. PNG, which is a lossless format, will ignore the quality setting.
     * @param quality The image quality value, ranges from 0 to 100. The default value is 80.
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setQuality(int quality) {
        if (quality < 0) {
            this.quality = 0;
        } else if (quality > 100) {
            this.quality = 100;
        } else {
            this.quality = quality;
        }
        return this;
    }

    /**
     * Set the image compression format by String.
     * @param outputFormat The compression format. The default format is JPEG.
     * @return This Resizer instance, for chained settings.
     */
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
            default:
                break;
        }
        return this;
    }

    /**
     * Set the image compression format by Bitmap.CompressFormat.
     * @param compressFormat The compression format. The default format is JPEG.
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setOutputFormat(Bitmap.CompressFormat compressFormat) {
        if (compressFormat == null) {
            throw new NullPointerException("compressFormat null");
        }

        this.compressFormat = compressFormat;
        return this;
    }

    /**
     * Set the output file name. If you don't set it, the output file will have the same name as the source file.
     * @param filename The name of the output file, without file extension.
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setOutputFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename null");
        }

        if (filename.toLowerCase(Locale.US).endsWith(".jpg")
                || filename.toLowerCase(Locale.US).endsWith(".jpeg")
                || filename.toLowerCase(Locale.US).endsWith(".png")
                || filename.toLowerCase(Locale.US).endsWith(".webp")) {
            throw new IllegalStateException("Filename should be provided without extension. See setOutputFormat(String).");
        }

        this.outputFilename = filename;
        return this;
    }

    /**
     * Set the output directory path. If this is the same as where the source image locates, the source image can be overwritten.
     * @param outputDirPath The path of the output directory. The default path is the external files directory of your app
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setOutputDirPath(String outputDirPath) {
        this.outputDirPath = outputDirPath;
        return this;
    }

    /**
     * Set the source image file.
     * @param sourceImage The source image file to be resized.
     * @return This Resizer instance, for chained settings.
     */
    public Resizer setSourceImage(File sourceImage) {
        this.sourceImage = sourceImage;
        return this;
    }

    /**
     * Get the resized image file.
     * @return The resized image file.
     * @throws IOException
     */
    public File getResizedFile() throws IOException {
        return ImageUtils.getScaledImage(targetLength, quality, compressFormat, outputDirPath, outputFilename,
                sourceImage);
    }

    /**
     * Get the resized image bitmap.
     * @return The resized image bitmap.
     * @throws IOException
     */
    public Bitmap getResizedBitmap() throws IOException {
        return ImageUtils.getScaledBitmap(targetLength, sourceImage);
    }

    /**
     * Get the resized image file as RxJava Flowable.
     * @return A Flowable that emits the resized image file or error.
     */
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

    /**
     * Get the resized image bitmap as RxJava Flowable.
     * @return A Flowable that emits the resized image bitmap or error.
     */
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
