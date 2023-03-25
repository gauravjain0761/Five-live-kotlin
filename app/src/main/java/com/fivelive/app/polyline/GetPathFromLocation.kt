package com.fivelive.app.polyline

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.fivelive.app.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetPathFromLocation(
    context: Context,
    private val source: LatLng,
    private val destination: LatLng,
    private val onPath: (polyLine: PolylineOptions?) -> Unit
) : AsyncTask<String?, Void?, PolylineOptions?>() {
    private val TAG = "GetPathFromLocation"
    private var API_KEY = ""
    var context: Context? = null

    init {
        API_KEY = context.getString(R.string.google_map_or_places_id)
    }

    fun getUrl(origin: LatLng, dest: LatLng): String {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val parameters = "$str_origin&$str_dest&$sensor"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$API_KEY"
    }

    protected override fun doInBackground(vararg url: String?): PolylineOptions? {
        val data: String
        return try {
            var inputStream: InputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val directionUrl = URL(getUrl(source, destination))
                connection = directionUrl.openConnection() as HttpURLConnection
                connection!!.connect()
                inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuffer = StringBuffer()
                var line: String? = ""
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuffer.append(line)
                }
                data = stringBuffer.toString()
                bufferedReader.close()
            } catch (e: Exception) {
                Log.e(TAG, "Exception : $e")
                return null
            } finally {
                inputStream!!.close()
                connection!!.disconnect()
            }
            Log.e(TAG, "Background Task data : $data")
            val jsonObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jsonObject = JSONObject(data)
                // Starts parsing data
                val helper = DirectionHelper()
                routes = helper.parse(jsonObject)
                Log.e(TAG, "Executing Routes : " /*, routes.toString()*/)
                var points: ArrayList<LatLng?>
                var lineOptions: PolylineOptions? = null

                // Traversing through all the routes
                for (i in routes.indices) {
                    points = ArrayList()
                    lineOptions = PolylineOptions()

                    // Fetching i-th route
                    val path = routes[i]

                    // Fetching all the points in i-th route
                    for (j in path!!.indices) {
                        val point = path[j]
                        val lat = point!!["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)
                        points.add(position)
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points)
                    lineOptions.width(10f)
                    lineOptions.color(Color.BLUE)
                    Log.e(TAG, "PolylineOptions Decoded")
                }

                // Drawing polyline in the Google Map for the i-th route
                lineOptions
            } catch (e: Exception) {
                Log.e(TAG, "Exception in Executing Routes : $e")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Background Task Exception : $e")
            null
        }
    }

    override fun onPostExecute(polylineOptions: PolylineOptions?) {
        super.onPostExecute(polylineOptions)
        onPath(polylineOptions)
    }
}