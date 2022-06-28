package net.getquicker.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.*
import java.util.*

/**
 * https://gist.github.com/Mariovc/f06e70ebe8ca52fbbbe2
 * 选择或拍照
 */
object ImagePicker {
    private const val TAG = "ImagePicker"
    private const val TEMP_IMAGE_NAME = "tempImage"
    fun getPickImageIntent(context: Context): Intent? {
        var chooserIntent: Intent? = null
        var intentList: ArrayList<Intent?> = ArrayList()
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra("return-data", true)
        val uri =
            FileProvider.getUriForFile(context, "net.getquicker.fileprovider", getTempFile(context))
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intentList = addIntentsToList(context, intentList, pickIntent)
        intentList = addIntentsToList(context, intentList, takePhotoIntent)
        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(
                intentList.removeAt(intentList.size - 1),
                "选择照片"
            )
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toArray(arrayOf<Parcelable>())
            )
        }
        return chooserIntent
    }

    private fun addIntentsToList(
        context: Context,
        list: ArrayList<Intent?>,
        intent: Intent
    ): ArrayList<Intent?> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
            Log.d(TAG, "Intent: " + intent.action + " package: " + packageName)
        }
        return list
    }
//    fun getFileBytes(context: Context, resultCode: Int,imageReturnedIntent: Intent?): ByteArray? {
//        val imageFile = getTempFile(context)
//        if (resultCode == Activity.RESULT_OK) {
//            val isCamera = imageReturnedIntent == null || imageReturnedIntent.data == null ||
//                    imageReturnedIntent.data.toString().contains(imageFile.toString())
//            val file=if (isCamera) getTempFile(context) else File(imageReturnedIntent!!.data!!.)
//            val size = file.length().toInt()
//            val bytes = ByteArray(size)
//            try {
//                val buf = BufferedInputStream(FileInputStream(file))
//                buf.read(bytes, 0, bytes.size)
//                buf.close()
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            return bytes
//        }else{
//            return null
//        }
//
//    }
    fun getImageFromResult(
        context: Context, resultCode: Int,
        imageReturnedIntent: Intent?
    ): Bitmap? {
        var bm: Bitmap? = null
        val imageFile = getTempFile(context)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri?
            val isCamera = imageReturnedIntent == null || imageReturnedIntent.data == null ||
                    imageReturnedIntent.data.toString().contains(imageFile.toString())
            selectedImage = if (isCamera) Uri.fromFile(imageFile) else imageReturnedIntent!!.data
            Log.d(TAG, "selectedImage: $selectedImage")
            bm = decodeBitmap(context, selectedImage, 1)
//            val rotation = getRotation(context, selectedImage, isCamera)
//            bm = rotate(bm, rotation)
        }
        return bm
    }

    private fun getTempFile(context: Context): File {
        val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME)
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    /**
     * 当inSampleSize=1，即采样后的图片大小为图片的原始大小。小于1，也按照1来计算。
     * 当inSampleSize>1，即采样后的图片将会缩小，缩放比例为1/(inSampleSize的二次方)。
     */
    private fun decodeBitmap(context: Context, theUri: Uri?, sampleSize: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openAssetFileDescriptor(theUri!!, "r")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
            fileDescriptor!!.fileDescriptor, null, options
        )
        Log.d(
            TAG, options.inSampleSize.toString() + " sample method bitmap ... " +
                    actuallyUsableBitmap.width + " " + actuallyUsableBitmap.height
        )
        return actuallyUsableBitmap
    }

    fun calculateInSampleSize(
        context: Context,
        selectedImage: Uri?,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openAssetFileDescriptor(selectedImage!!, "r")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        BitmapFactory.decodeFileDescriptor(
            fileDescriptor!!.fileDescriptor, null, options
        )
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeght = height / 2
            val halfWidth = width / 2
            while (halfHeght / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getRotation(context: Context, imageUri: Uri?, isCamera: Boolean): Int {
        val rotation: Int
        rotation = if (isCamera) {
            getRotationFromCamera(context, imageUri)
        } else {
            getRotationFromGallery(context, imageUri)
        }
        Log.d(TAG, "Image rotation: $rotation")
        return rotation
    }

    private fun getRotationFromCamera(context: Context, imageFile: Uri?): Int {
        var rotate = 0
        try {

            //context.getContentResolver().notifyChange(imageFile, null);
            val exif = ExifInterface(imageFile!!.path!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    fun getRotationFromGallery(context: Context, imageUri: Uri?): Int {
        var result = 0
        val columns = arrayOf(MediaStore.Images.Media.ORIENTATION)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(imageUri!!, columns, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val orientationColumnIndex = cursor.getColumnIndex(columns[0])
                result = cursor.getInt(orientationColumnIndex)
            }
        } catch (e: Exception) {
            //Do nothing
        } finally {
            cursor?.close()
        } //End of try-catch block
        return result
    }

    private fun rotate(bm: Bitmap?, rotation: Int): Bitmap? {
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            return Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, matrix, true)
        }
        return bm
    }
}