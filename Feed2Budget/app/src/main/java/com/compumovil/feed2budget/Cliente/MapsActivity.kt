package com.compumovil.feed2budget.Cliente

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.core.content.ContextCompat
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.compumovil.feed2budget.Item
import com.compumovil.feed2budget.ItemAdapter
import com.compumovil.feed2budget.R
import com.compumovil.feed2budget.User.User
import com.compumovil.feed2budget.databinding.ActivityMapsBinding
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.api.IGeoPoint
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

import android.content.Context
import android.location.Address


class MapsActivity : AppCompatActivity(), SensorEventListener {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var lastKnownLocation: GeoPoint? = null
    private var jsonArray = JSONArray()

    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensor: Sensor
    private var isDarkMode = false
    private lateinit var binding: ActivityMapsBinding
    private var mGeocoder: Geocoder? = null

    private lateinit var databaseReference: DatabaseReference
    private var items = mutableListOf<Item>()


    //Rutas
    private var roadOverlay: Polyline? = null
    lateinit var roadManager: RoadManager

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGeocoder = Geocoder(this)

        // Sensor de luz
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!


        // Pedir permiso
        askPermiso()

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        val listaPuntos = mutableListOf<GeoPoint>()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)


        // Establecer el zoom inicial del mapa
        map.controller.setZoom(5)

        // Agregar overlay para mostrar la ubicación del usuario
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        centerOnUserLocation()
        locationOverlay.enableFollowLocation()

        var latitude = 0.0
        var longitude = 0.0
        listaPuntos.clear()

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.empresa) {
                        val userId = userSnapshot.key
                        Toast.makeText(this@MapsActivity, user.direccion, Toast.LENGTH_SHORT).show()
                        val latLng = getLatLngFromAddress(this@MapsActivity, user.direccion)

                        if (latLng != null) {
                            latitude = latLng.first
                            longitude = latLng.second
                            listaPuntos.add(GeoPoint(latitude, longitude))
                            Toast.makeText(this@MapsActivity, "${latitude} ${longitude}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MapsActivity, "Unable to get Latitude and Longitude for the given address.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("Restaurantes", "User is not a company: ${user?.firstName}")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Restaurantes", "Failed to read users", databaseError.toException())
            }
        })

        val parrillada = GeoPoint(4.628596, -74.066345)
        val giornatta = GeoPoint(4.697936, -74.054965)
        val chula = GeoPoint(4.699182, -74.050430)
        for (geoPoint in listaPuntos) {
            addMarkerWithAddress(geoPoint)
        }

        //buscar y longclick
        buscar()
        longClick()


        //rutas
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        roadManager = OSRMRoadManager(this, "ANDROID")



    }
    //Attribute
    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road = roadManager.getRoad(routePoints)
        Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
        Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
        if (binding.map != null) {
            roadOverlay?.let { binding.map.overlays.remove(it) }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay?.outlinePaint?.color = Color.RED
            roadOverlay?.outlinePaint?.strokeWidth = 10f
            binding.map.overlays.add(roadOverlay)
        }
    }
    private fun longClick() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                addMarkerWithAddress(p)
                lastKnownLocation?.let { drawRoute(it, p) }
                return false
            }
        })

        map.overlays.add(0, mapEventsOverlay)
    }

    private fun addMarkerWithAddress(geoPoint: IGeoPoint) {
        val addresses = try {
            mGeocoder?.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        val address = addresses?.getOrNull(0)
        val marker = Marker(map)
        marker.position = geoPoint as GeoPoint?
        marker.title = address?.getAddressLine(0) ?: "Ubicación"
        map.overlays.add(marker)
        map.invalidate()

        // Calcular la distancia entre la posición actual y el marcador
        val distance = lastKnownLocation?.distanceToAsDouble(geoPoint) ?: 0.0
        val distanceInMeters = String.format(Locale.getDefault(), "%.2f", distance)

        // Mostrar un Toast con la distancia
        Toast.makeText(
            this,
            "Distancia al marcador: $distanceInMeters metros",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun buscar() {
        binding.etLocation.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val addressString = binding.etLocation.text.toString()
                if (addressString.isNotEmpty()) {
                    try {
                        val addresses = Geocoder(this).getFromLocationName(addressString, 2)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val addressResult = addresses[0]
                            val position = GeoPoint(addressResult.latitude, addressResult.longitude)

                            // Create a new marker
                            val marker = Marker(map)
                            marker.position = position
                            marker.title = addressResult.getAddressLine(0)
                            map.overlays.add(marker)

                            // Move the map's view to the marker
                            map.controller.animateTo(position)

                            // Calcular la distancia entre la posición actual y el marcador
                            val distance = lastKnownLocation?.distanceToAsDouble(position) ?: 0.0
                            val distanceInMeters =
                                String.format(Locale.getDefault(), "%.2f", distance)

                            // Mostrar un Toast con la distancia
                            Toast.makeText(
                                this,
                                "Distancia al marcador: $distanceInMeters metros",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "La dirección está vacía", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        locationOverlay.enableMyLocation()
        centerOnUserLocation()

        // Registrar el sensor de luz
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        locationOverlay.disableMyLocation()

        // Detener la escucha del sensor de luz
        mSensorManager.unregisterListener(this)
    }

    private fun askPermiso() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permiso concedido, no hacer nada adicional
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }

            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido, no hacer nada adicional
                } else {
                    finish()
                    Toast.makeText(this, "Funcionalidades reducidas", Toast.LENGTH_LONG).show()
                }
                return
            }

            else -> {
                // Ignorar todas las demás solicitudes
            }
        }
    }

    private fun centerOnUserLocation() {
        val handler = Handler()
        handler.postDelayed({
            val lastLocation = locationOverlay.myLocation
            if (lastLocation != null) {
                if (lastKnownLocation != null) {
                    val distance = lastKnownLocation!!.distanceToAsDouble(lastLocation)
                    if (distance > 30) {
                        saveLocationToJson(lastLocation)
                        lastKnownLocation = lastLocation
                    }
                } else {
                    lastKnownLocation = lastLocation
                }

                map.controller.setZoom(18)
                map.controller.setCenter(lastLocation)
            } else {
                Toast.makeText(this@MapsActivity, "Buscando ubicación...", Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }

    private fun saveLocationToJson(location: GeoPoint) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = dateFormat.format(Date())

        val locationData = LocationData(location.latitude, location.longitude, currentDateAndTime)
        val jsonData = JSONObject()
        jsonData.put("latitude", locationData.latitude)
        jsonData.put("longitude", locationData.longitude)
        jsonData.put("timestamp", locationData.timestamp)

        jsonArray.put(jsonData)

        try {
            val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(directory, "location_data.json")
            val fileWriter = FileWriter(file)
            fileWriter.write(jsonArray.toString())
            fileWriter.close()
            Toast.makeText(this, "Ubicación guardada en $file", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()

            Toast.makeText(this, "Error al guardar la ubicación", Toast.LENGTH_LONG).show()
        }
    }

    data class LocationData(val latitude: Double, val longitude: Double, val timestamp: String)

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita implementar
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Detectar cambios en la luminosidad
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            // Cambiar entre los modos claro y oscuro según la luminosidad
            if (event.values[0] < 100 && !isDarkMode) { // Cambia el 10 a tu umbral deseado
                isDarkMode = true
                changeMapStyle("oscuro") // Cambia al estilo oscuro
            } else if (event.values[0] >= 100 && isDarkMode) {
                isDarkMode = false
                changeMapStyle("claro") // Cambia al estilo claro
            }
        }
    }

    fun changeMapStyle(string: String) {
        when (string) {
            "claro" -> {
                map.setTileSource(TileSourceFactory.MAPNIK)
            }

            "oscuro" -> {
                binding.map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.dispatcher.executorService.shutdown()
    }
    private fun getCoordinatesFromAddress(address: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://nominatim.openstreetmap.org/search?q=${address.replace(" ", "%20")}&format=json&addressdetails=1&limit=1"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val gson = Gson()
                        val type = object : TypeToken<List<NominatimResponseItem>>() {}.type
                        val result: List<NominatimResponseItem> = gson.fromJson(responseBody, type)
                        val coordinates = result.firstOrNull()

                        withContext(Dispatchers.Main) {
                            coordinates?.let {
                                Toast.makeText(this@MapsActivity, "Latitude: ${it.lat}, Longitude: ${it.lon}", Toast.LENGTH_LONG).show()
                            } ?: run {
                                Toast.makeText(this@MapsActivity, "No coordinates found for the address.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        showToastOnMainThread("Empty response body.")
                    }
                } else {
                    showToastOnMainThread("Request failed: ${response.message}")
                }
            } catch (e: Exception) {
                showToastOnMainThread("Failed to get coordinates: ${e.message}")
            }
        }
    }
    private suspend fun showToastOnMainThread(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MapsActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun getLatLngFromAddress(context: Context, address: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocationName(address, 1)
            if (addresses!!.isNotEmpty()) {
                val location = addresses[0]
                return Pair(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
