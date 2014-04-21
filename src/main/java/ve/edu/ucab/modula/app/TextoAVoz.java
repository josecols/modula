package ve.edu.ucab.modula.app;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;

/**
 * TextoAVoz permite pronunciar texto audible a través del servicio TTS de Google.
 */
public class TextoAVoz {

    /**
     * Interfaz entre el servicio TTS de Google y la aplicación.
     */
    private TextToSpeech pronunciador;

    /**
     * Constructor de la clase.
     *
     * @param contexto Contexto de la aplicación. En un Activity se debe pasar this.
     * @param callback Encapsula los procedimientos a ejecutar dependiendo del estado del servicio de pronunciación.
     */
    public TextoAVoz(final Context contexto, final TTSCallback callback) {
        this.pronunciador = new TextToSpeech(contexto, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                callback.servicioEspera();
                pronunciador.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {
                        callback.servicioEjecucion();
                    }

                    @Override
                    public void onDone(String s) {
                        callback.servicioListo();
                    }

                    @Override
                    public void onError(String s) {
                        callback.servicioError();
                    }
                });
            }
        });
    }

    /**
     * Pronuncia el texto recibido.
     *
     * @param str Texto que se va a pronunciar.
     * @return TextToSpeech.ERROR o TextToSpeech.SUCCESS
     */
    public int pronunciar(String str) {
        HashMap<String, String> opciones = new HashMap<String, String>();
        opciones.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        opciones.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, str);
        return pronunciador.speak(str, TextToSpeech.QUEUE_FLUSH, opciones);
    }

    /**
     * Detiene la voz sintetica
     */
    public int parar(){
        return pronunciador.stop();
    }

    /**
     *
     * @return
     *      true si hay algun sonido reproduciendose
     */
    public boolean estaHablando(){
        return pronunciador.isSpeaking();
    }

    /**
     * Detiene el servicio, este método se debe llamar cuando se destruye un Activity que emplea la clase o
     * habrá fuga de memoria en el servicio.
     */
    public void finalizar() {
        pronunciador.stop();
        pronunciador.shutdown();
    }
}

