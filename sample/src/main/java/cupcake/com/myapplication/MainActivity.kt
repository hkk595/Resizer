package cupcake.com.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.echodev.resizer.Resizer
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EasyImage.configuration(this).setAllowMultiplePickInGallery(true)
        EasyImage.openCamera(this, 155)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : EasyImage.Callbacks {
            override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource, type: Int) {

            }

            override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                val resizedImage = Resizer(this@MainActivity)
                        .setTargetLength(512)
                        .setSourceImage(imageFiles[0])
                        .resizedBitmap

                image.setImageBitmap(resizedImage)
            }

            override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@MainActivity)
                    if (photoFile != null) {
                        val x = photoFile.delete()
                        Log.i("TAG", "onCanceled: pictureDeleted =$x")
                    }
                }
            }
        })
    }

}
