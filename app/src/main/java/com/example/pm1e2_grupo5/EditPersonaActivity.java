package com.example.pm1e2_grupo5;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Models.Persona;

public class EditPersonaActivity extends AppCompatActivity {

    EditText editTextNombre, editTextTelefono, editTextLatitud, editTextLongitud;
    Button buttonUpdate;
    private Persona persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_persona);

        // Obtener referencias a los elementos de la vista
        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        editTextTelefono = (EditText) findViewById(R.id.editTextTelefono);
        editTextLatitud = (EditText) findViewById(R.id.editTextLatitud);
        editTextLongitud = (EditText) findViewById(R.id.editTextLongitud);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

        // Obtener los datos de la persona desde el Intent
        Intent intent = getIntent();
        persona = (Persona) intent.getSerializableExtra("persona");

        // Rellenar los campos con los datos actuales
        editTextNombre.setText(persona.getNombre());
        editTextTelefono.setText(persona.getTelefono());
        editTextLatitud.setText(persona.getLatitud());
        editTextLongitud.setText(persona.getLongitud());

        // Configurar el botón de actualización
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos modificados
                String nombre = editTextNombre.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String latitud = editTextLatitud.getText().toString();
                String longitud = editTextLongitud.getText().toString();

                // Actualizar la persona
                persona.setNombre(nombre);
                persona.setTelefono(telefono);
                persona.setLatitud(latitud);
                persona.setLongitud(longitud);

                // Enviar los datos actualizados al servidor
                updatePersona(persona);
            }
        });
    }

    private void updatePersona(Persona persona) {
        String url = "http://192.168.100.105/crud-examen/updateperson.php";

        // Crear el objeto JSON con los datos a actualizar
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", persona.getId());
            jsonObject.put("nombre", persona.getNombre());
            jsonObject.put("telefono", persona.getTelefono());
            jsonObject.put("latitud", persona.getLatitud());
            jsonObject.put("longitud", persona.getLongitud());
            jsonObject.put("firma", persona.getFirma());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(EditPersonaActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish(); // Cierra la actividad
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error al actualizar persona: " + responseBody);
                        } else {
                            Log.e(TAG, "Error al actualizar persona: " + error.toString());
                        }
                        Toast.makeText(EditPersonaActivity.this, "Error al actualizar persona", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
