package ve.edu.ucab.modula.app;

import android.content.Context;
import android.speech.tts.TextToSpeech;


/**
 * Created by JOSE on 11/04/14.
 */
public class TextoAVoz implements TextToSpeech.OnInitListener{
    private TextToSpeech pronunciador;
    public TextoAVoz(Context contexto){
        pronunciador = new TextToSpeech(contexto,this);
    }
    public int pronunciar(String str){
        return pronunciador.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void finalizar(){
        pronunciador.stop();
        pronunciador.shutdown();
    }
    public void onInit(int status){

    }

}

