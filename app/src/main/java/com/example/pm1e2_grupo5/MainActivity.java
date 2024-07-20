package com.example.pm1e2_grupo5;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    double latitude ;
    double longitude;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;



    View view;

    Bitmap image;
    EditText txtLatitud, txtLongitud, nombre, telefono;
    Button tomarfoto, guardar, contactos;

    Boolean actualizacionActiva;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        actualizacionActiva = false;
        contactos = (Button) findViewById(R.id.btn_contacto);
        txtLatitud = ( EditText ) findViewById(R.id.txtLatitud);
        txtLongitud = ( EditText ) findViewById(R.id.txtLongitud);
        nombre = ( EditText ) findViewById(R.id.txtNombre);
        telefono = ( EditText ) findViewById(R.id.txtTelefono);
        guardar = (Button) findViewById(R.id.btn_guardar);
        view = (View) findViewById(R.id.viewfirma);

        // geolocalizacion
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.length()==0 || telefono.length()==0) {
                    Toast.makeText(MainActivity.this, "El campo está vacío o solo contiene espacios.", Toast.LENGTH_SHORT).show();
                } else {
                    enviarDatosAlServidor();
                    //ClearScreen();
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVerLista.class);
                startActivity(intent);
            }
        });




        //fin del constrctor
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            String message = "Lat: " + latitude + " Lon: " + longitude;
                            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            txtLatitud.setText(""+latitude);
                            txtLongitud.setText(""+longitude);

                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String Viewfirma(View view5) {
        view5.setDrawingCacheEnabled(true);
        Bitmap bitmap = view5.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void enviarDatosAlServidor() {
        String url = "http://192.168.100.105/crud-examen/postperson2.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", nombre.getText().toString());
            jsonObject.put("telefono", telefono.getText().toString());
            jsonObject.put("latitud", txtLatitud.getText().toString());
            jsonObject.put("longitud", txtLongitud.getText().toString());
            jsonObject.put("firma", Viewfirma(view)); // Se pasa la cadena base64
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        Toast.makeText(MainActivity.this, "Persona guardada exitosamente", Toast.LENGTH_SHORT).show();
                        finish(); // Finaliza la actividad después de guardar los datos

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Intentar obtener la respuesta en bruto
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error: " + responseBody);
                            //Toast.makeText(ActivityAgregarLista.this, "Error al guardar la persona: " + responseBody, Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "Exito: " + error.toString());
                            //Toast.makeText(ActivityAgregarLista.this, "Error al guardar la persona", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void ClearScreen() {
        nombre.setText("");
        telefono.setText("");
        view.setDrawingCacheEnabled(false);
    }


}