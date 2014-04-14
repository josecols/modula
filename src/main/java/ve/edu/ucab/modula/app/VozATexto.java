package ve.edu.ucab.modula.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

/**
 * Created by JOSE on 11/04/14.
 */
public class VozATexto {
    public static final int CONVERSION_ID = 1;
    private Activity actividad;

    public VozATexto(Activity actividad){
        this.actividad = actividad;
    }

    public void traducir(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es");
        try {

            actividad.startActivityForResult(intent, CONVERSION_ID);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(actividad.getApplicationContext(), "Lo siento, tu dispositivo no soporta reconocimiento de voz", Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
