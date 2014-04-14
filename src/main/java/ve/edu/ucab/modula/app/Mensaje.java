package ve.edu.ucab.modula.app;

/**
 * Created by JOSE on 08/04/14.
 */
public class Mensaje {

    private String texto;
    private boolean enviado;

    public Mensaje(String texto, boolean enviado){
        this.texto = texto;
        this.enviado = enviado;
    }

    public String getTexto(){
        return texto;
    }

    public boolean enviado(){
        return enviado;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }

    public void setEnviado(boolean enviado){
        this.enviado = enviado;
    }
}