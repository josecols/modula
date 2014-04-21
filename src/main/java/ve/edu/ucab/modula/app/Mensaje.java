package ve.edu.ucab.modula.app;

/**
 * Clase para el manejo de los mensajes en el chat
 */
public class Mensaje {

    /**
     * informacion que lleva el mensaje
     */
    private String texto;

    /**
     * sentido del mensaje
     * true = mensaje enviado desde el usuario hacia el oyente
     * false = mensaje recibido por el usuario desde el hablante
     */
    private boolean enviado;

    /**
     * atributo usado en caso de que el mensaje haya sido
     * enviado para determinar si culmino su reproduccion (Sonido)
     */
    private boolean listo;

    public Mensaje(String texto, boolean enviado){
        this.texto = texto;
        this.enviado = enviado;
        this.listo = false;
    }

    public String getTexto(){
        return texto;
    }

    public boolean enviado(){ return enviado; }

    public boolean listo(){ return listo; }

    public void setTexto(String texto){
        this.texto = texto;
    }

    public void setEnviado(boolean enviado){
        this.enviado = enviado;
    }

    public void setListo(boolean listo) {this.listo = listo; }
}