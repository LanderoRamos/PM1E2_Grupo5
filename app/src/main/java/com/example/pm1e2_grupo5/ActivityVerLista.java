package com.example.pm1e2_grupo5;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.Persona;
import Models.PersonaAdapterGet;

public class ActivityVerLista extends AppCompatActivity {

    private ListView listView;
    private PersonaAdapterGet personaAdapter;
    private List<Persona> personaList;
    private static final String TAG = "ActivityVerLista";
    Button btneliminar, btnactualizar,buttonDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_lista);

        //inicio de constructor

        listView = findViewById(R.id.listView);
        personaList = new ArrayList<>();
        personaAdapter = new PersonaAdapterGet(this, personaList);
        listView.setAdapter(personaAdapter);
        btneliminar = (Button) findViewById(R.id.button2);
        btnactualizar = (Button) findViewById(R.id.button3);
        buttonDetails = (Button) findViewById(R.id.buttonDetails);

        fetchPersonas();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Persona persona = personaList.get(position);
                int personaId = persona.getId();
                String lat = persona.getLatitud();
                Toast.makeText(ActivityVerLista.this, "ID de la persona: " + personaId+" "+lat, Toast.LENGTH_SHORT).show();
            }
        });

        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedPersona();
            }
        });

        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();
                if (position != ListView.INVALID_POSITION) {
                    Persona persona = personaList.get(position);
                    Intent intent = new Intent(ActivityVerLista.this, EditPersonaActivity.class);
                    intent.putExtra("persona", persona);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityVerLista.this, "Selecciona una persona para editar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = listView.getCheckedItemPosition();
                if (position != ListView.INVALID_POSITION) {
                    Persona persona = personaList.get(position);
                    Intent intent = new Intent(ActivityVerLista.this, DetallesActivity.class);
                    intent.putExtra("latitud", Double.parseDouble(persona.getLatitud()));
                    intent.putExtra("longitud", Double.parseDouble(persona.getLongitud()));
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityVerLista.this, "Selecciona una persona para editar", Toast.LENGTH_SHORT).show();
                }

                /**/
            }
        });

        //modifcar el list cuando ingrese texto
        EditText editText = findViewById(R.id.editTextText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Muestra el Toast cada vez que el texto cambia
                //Toast.makeText(ActivityVerLista.this, "Texto cambiado: " + s.toString(), Toast.LENGTH_SHORT).show();

                searchPersonas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita hacer nada aquí
            }
        });
        //fin de modificar list

        //fin contructor

    }




    private void fetchPersonas() {
        String url = "http://192.168.100.105/crud-examen/getperson.php"; // Cambia esta URL por la de tu API
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject personaObject = response.getJSONObject(i);
                                int id = Integer.parseInt(personaObject.getString("id"));
                                String nombre = personaObject.getString("nombre");
                                String telefono = personaObject.getString("telefono");
                                String latitud = personaObject.getString("latitud");
                                String longitud = personaObject.getString("longitud");
                                String firma = personaObject.getString("firma"); // Obtener la imagen base64

                                /*Log.d(TAG, "nombre: " + nombre);
                                Log.d(TAG, "telefono: " + telefono);
                                Log.d(TAG, "latitud: " + latitud);
                                Log.d(TAG, "longitud: " + longitud);
                                Log.d(TAG, "firma: " + firma);*/

                                // Crear una instancia de Persona con los datos recibidos
                                Persona persona = new Persona(nombre, telefono, latitud, longitud, firma, id);
                                personaList.add(persona); // Agregar persona a la lista
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // Notificar al adaptador que los datos han cambiado
                        personaAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void deleteSelectedPersona() {
        int position = listView.getCheckedItemPosition();
        if (position != ListView.INVALID_POSITION) {
            Persona persona = personaList.get(position);
            int personaId = persona.getId();
            String url = "http://192.168.100.105/crud-examen/deleteperson2.php?id=" + personaId;

            StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Intentar convertir la respuesta a JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.d(TAG, "Persona eliminada: " + jsonResponse.toString());
                            personaList.remove(position);
                            personaAdapter.notifyDataSetChanged();
                            Toast.makeText(ActivityVerLista.this, "Persona eliminada exitosamente", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e(TAG, "Respuesta no es un JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error al eliminar persona: " + responseBody);
                        } else {
                            Log.e(TAG, "Error al eliminar persona: " + error.toString());
                        }
                        Toast.makeText(ActivityVerLista.this, "Error al eliminar persona", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    } else {
        Toast.makeText(this, "Selecciona una persona para eliminar", Toast.LENGTH_SHORT).show();
    }
    }

    private void searchPersonas(String query) {
        String url = "http://192.168.100.105/crud-examen/bgetperson.php?query=" + Uri.encode(query);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        personaList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject personaObject = response.getJSONObject(i);
                                int id = personaObject.getInt("id");
                                String nombre = personaObject.getString("nombre");
                                String telefono = personaObject.getString("telefono");
                                String latitud = personaObject.getString("latitud");
                                String longitud = personaObject.getString("longitud");
                                String firma = personaObject.getString("firma");

                                Persona persona = new Persona(nombre, telefono, latitud, longitud, firma, id);
                                personaList.add(persona);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        personaAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error al buscar persona: " + responseBody);
                        } else {
                            Log.e(TAG, "Error al buscar persona: " + error.toString());
                        }
                        Toast.makeText(ActivityVerLista.this, "Error al buscar persona", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }





}