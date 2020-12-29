package com.example.ftpexample

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var filePath : Uri
    private lateinit var imageView : ImageView

    fun selectImage(view: View) {
        startFileChoose()
    }

    fun uploadFunction(view: View) {
        uploadFile()
    }

    fun downloadFunction(view: View) {

        var storage : FirebaseStorage = FirebaseStorage.getInstance()
        var imageRef : StorageReference = storage.getReferenceFromUrl("gs://ftp-example.appspot.com/images").child("pic.jpg")

        var pd = ProgressDialog(this)
        pd.setTitle("Downloading")
        pd.show()

        var file: File = File.createTempFile("image", "jpg")
        imageRef.getFile(file).addOnSuccessListener {p0 ->

            pd.dismiss()
            var bitmap : Bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
            }
                .addOnFailureListener {p0->
                    pd.dismiss()
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener {p0->
                    var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Downloaded ${progress.toInt()}%")

                    }
    }

    private fun startFileChoose() {

        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode 2== 111 && resultCode == RESULT_OK && data != null) {
            filePath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            Toast.makeText(this, "Image selected, now you can press upload", Toast.LENGTH_LONG).show()
            //imageView.setImageBitmap(bitmap)
        }
    }

    private fun uploadFile() {
        if (filePath!= null) {
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()

            var imageRef = FirebaseStorage.getInstance().reference.child("images/pic.jpg")
            imageRef.putFile(filePath)
                    .addOnSuccessListener {p0 ->

                        pd.dismiss()
                        Toast.makeText(this, "File Uploaded Successfully", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {p0 ->

                        pd.dismiss()
                        Toast.makeText(this, p0.message, Toast.LENGTH_LONG).show()
                    }
                    .addOnProgressListener {p0 ->
                        var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                        pd.setMessage("Uploaded ${progress.toInt()}%")
                    }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
    }
}