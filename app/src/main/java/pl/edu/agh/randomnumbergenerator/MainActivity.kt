package pl.edu.agh.randomnumbergenerator

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private lateinit var lightSensor: Sensor
    private lateinit var accelerationSensor: Sensor
    private lateinit var gyroscopeSensor: Sensor

    private var lightSensorReading: Float = 0.0F
    private var accelerationSensorReading: Float = 0.0F
    private var gyroscopeSensorReading: Float = 0.0F
    private var seed: Int = 0

    private val MULTIPLE_PERMISSIONS = 10
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var directory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lightSensorValue.inputType = 0
        accelerationSensorValue.inputType = 0
        gyroscopeSensorValue.inputType = 0
        seedValue.inputType = 0

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)

        if (checkPermissions()) {
            createDirectory()
        }

        genButton.setOnClickListener {
            BinFileGenerator(seed).generateRandomFiles(directory)
            Toast.makeText(this, "Files generated in $directory", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                lightSensorReading = event.values[0]
                lightSensorValue.setText(lightSensorReading.toString(), TextView.BufferType.NORMAL)
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                accelerationSensorReading = Math.abs(event.values[0]) * 100_000
                accelerationSensorValue.setText(accelerationSensorReading.toString(), TextView.BufferType.NORMAL)
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroscopeSensorReading = Math.abs(event.values[0]) * 100_000
                gyroscopeSensorValue.setText(gyroscopeSensorReading.toString(), TextView.BufferType.NORMAL)
            }
        }
        seed = Random.aggregateSeeds(lightSensorReading, accelerationSensorReading, gyroscopeSensorReading)
        seedValue.setText(seed.toString(), TextView.BufferType.NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    private fun checkPermissions(): Boolean {
        val permissionsNeeded = mutableListOf<String>()

        permissions.forEach {
            val result = ContextCompat.checkSelfPermission(this, it)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(it)
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                createDirectory()
            }
        }
    }

    private fun createDirectory() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "RandomNumberGenerator")
        if (!file.exists()) {
            file.mkdirs()
        }
        directory = file.absolutePath
    }
}
