package ve.edu.ucab.modula.app;

import java.util.concurrent.Callable;

/**
 * TTSCallback permite encapsular los procedimientos que se deben ejecutar según
 * el estado alcanzado del servicio <TextToSpeech>.
 * <p/>
 * Son cuatro estados:
 * 1. Cuando el servicio realizó un vínculo con la aplicación y está a la espera de texto a pronunciar.
 * 2. Cuando el servicio está pronunciando texto.
 * 3. Cuando el servicio termina de pronunciar el texto.
 * 4. Cuando ocurre un error en la ejecución del servicio.
 */
public class TTSCallback {
    /**
     * Procedimiento a ejecutar cuando se cumpla el estado 1.
     */
    private Callable servicioEsperaCallback;
    /**
     * Procedimiento a ejecutar cuando se cumpla el estado 2.
     */
    private Callable servicioEjecucionCallback;
    /**
     * Procedimiento a ejecutar cuando se cumpla el estado 3.
     */
    private Callable servicioListoCallback;
    /**
     * Procedimiento a ejecutar cuando se cumpla el estado 4.
     */
    private Callable servicioErrorCallback;

    public TTSCallback(Callable servicioEspera, Callable servicioEjecucion, Callable servicioListo, Callable servicioError) {
        this.servicioEsperaCallback = servicioEspera;
        this.servicioEjecucionCallback = servicioEjecucion;
        this.servicioListoCallback = servicioListo;
        this.servicioErrorCallback = servicioError;
    }

    /**
     * Realiza la ejecución de <servicioEsperaCallback>. Si el método es null, se ignora.
     */
    public void servicioEspera() {
        try {
            this.servicioEsperaCallback.call();
        } catch (Exception e) {
        }
    }

    /**
     * Realiza la ejecución de <servicioEjecucionCallback>. Si el método es null, se ignora.
     */
    public void servicioEjecucion() {
        try {
            this.servicioEjecucionCallback.call();
        } catch (Exception e) {
        }
    }

    /**
     * Realiza la ejecución de <servicioListoCallback>. Si el método es null, se ignora.
     */
    public void servicioListo() {
        try {
            this.servicioListoCallback.call();
        } catch (Exception e) {
        }
    }

    /**
     * Realiza la ejecución de <servicioErrorCallback>. Si el método es null, se ignora.
     */
    public void servicioError() {
        try {
            this.servicioErrorCallback.call();
        } catch (Exception e) {
        }
    }
}
