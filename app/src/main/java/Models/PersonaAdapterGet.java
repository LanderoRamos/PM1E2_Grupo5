package Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pm1e2_grupo5.DetallesActivity;
import com.example.pm1e2_grupo5.R;

import java.util.List;

public class PersonaAdapterGet extends BaseAdapter {

    private Context context;
    private List<Persona> personaList;

    public PersonaAdapterGet(Context context, List<Persona> personaList) {
        this.context = context;
        this.personaList = personaList;
    }

    @Override
    public int getCount() {
        return personaList.size();
    }

    @Override
    public Object getItem(int position) {
        return personaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_persona, parent, false);
        }

        ImageView imageViewFirma = convertView.findViewById(R.id.imageViewFirma);
        TextView textViewNombre = convertView.findViewById(R.id.textViewNombre);
        TextView textViewTelefono = convertView.findViewById(R.id.textViewTelefono);
        TextView textViewLatitud = convertView.findViewById(R.id.textViewLatitud);
        TextView textViewLongitud = convertView.findViewById(R.id.textViewLongitud);
        //Button buttonDetails = convertView.findViewById(R.id.buttonDetails);



        Persona persona = (Persona) getItem(position);

        textViewNombre.setText(""+persona.getId()+" "+persona.getNombre());
        textViewTelefono.setText(persona.getTelefono());
        textViewLatitud.setText(persona.getLatitud());
        textViewLongitud.setText(persona.getLongitud());

        // Decodificar la imagen base64 y cargarla en ImageView
        if (persona.getFirma() != null && !persona.getFirma().isEmpty()) {
            byte[] decodedString = Base64.decode(persona.getFirma(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageViewFirma.setImageBitmap(decodedByte);
        } else {
            // imageViewFirma.setImageResource(R.drawable.default_image); // Imagen por defecto
        }

        /*buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesActivity.class);
                intent.putExtra("latitud", Double.parseDouble(persona.getLatitud()));
                intent.putExtra("longitud", Double.parseDouble(persona.getLongitud()));
                context.startActivity(intent);
            }
        });*/


        return convertView;
    }





}
