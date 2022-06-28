package net.getquicker.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.blankj.utilcode.util.ScreenUtils
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

typealias LumaListener = CameraManager.(data: ByteArray, width: Int, height: Int) -> Unit

/**
 *  author : Clay
 *  date : 2021/12/27 17:05:01
 *  description : 相机管理
 */
class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewFinder: PreviewView,
    private val rootView: View, private val listener: LumaListener
) : DefaultLifecycleObserver {
    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    private var displayId: Int = -1

    //    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val displayManager by lazy {
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        rootView.keepScreenOn = true
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

        // Wait for the views to be properly laid out
        viewFinder.post {
            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId
            // Set up the camera and its use cases
            setUpCamera()
        }
    }

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {
        // Get screen metrics used to setup camera for full screen resolution
        val screenAspectRatio =
            aspectRatio(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())
        val rotation = viewFinder.display.rotation
        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")
        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer())
            }
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalyzer
            )
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
//            observeCameraState(camera?.cameraInfo!!)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = rootView.let { view ->
            if (displayId == this@CameraManager.displayId) {
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        }
    }

    inner class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        override fun analyze(image: ImageProxy) {
            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0
            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases
            lastAnalyzedTimestamp = frameTimestamps.first
            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val data = imageProxyToByteArray(image)
            listener(data, image.width, image.height)
            image.close()
        }
    }

    /**
     * 转换为图片的二进制
     */
    private fun imageProxyToByteArray(image: ImageProxy): ByteArray {
        val yuvBytes = ByteArray(image.width * (image.height + image.height / 2))
        val yPlane = image.planes[0].buffer
        val uPlane = image.planes[1].buffer
        val vPlane = image.planes[2].buffer

        yPlane.get(yuvBytes, 0, image.width * image.height)

        val chromaRowStride = image.planes[1].rowStride
        val chromaRowPadding = chromaRowStride - image.width / 2

        var offset = image.width * image.height
        if (chromaRowPadding == 0) {

            uPlane.get(yuvBytes, offset, image.width * image.height / 4)
            offset += image.width * image.height / 4
            vPlane.get(yuvBytes, offset, image.width * image.height / 4)
        } else {
            for (i in 0 until image.height / 2) {
                uPlane.get(yuvBytes, offset, image.width / 2)
                offset += image.width / 2
                if (i < image.height / 2 - 2) {
                    uPlane.position(uPlane.position() + chromaRowPadding)
                }
            }
            for (i in 0 until image.height / 2) {
                vPlane.get(yuvBytes, offset, image.width / 2)
                offset += image.width / 2
                if (i < image.height / 2 - 1) {
                    vPlane.position(vPlane.position() + chromaRowPadding)
                }
            }
        }

        return yuvBytes
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // Shut down our background executor
        cameraExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}