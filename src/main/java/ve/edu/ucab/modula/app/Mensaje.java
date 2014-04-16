package ve.edu.ucab.modula.app;

/**
 * Created by JOSE on 08/04/14.
 */
public class Mensaje {

    private String texto;
    private boolean enviado;
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