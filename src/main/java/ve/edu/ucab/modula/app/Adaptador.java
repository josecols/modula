package ve.edu.ucab.modula.app;


import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Clase que funciona como adaptador del ListView empleado
 * en el chat
 */
public class Adaptador extends BaseAdapter{

    /**
     * contexto de la actividad que contiene el ListView que usa el adaptador
     */
    private Context contexto;

    /**
     * Arreglo de Mensajes que se visualizaran en el ListView
     */
    private ArrayList<Mensaje> mensajes;

    /**
     * Tamaño de Letra de los mensajes del chat
     */
    private int tamanoLetra;

    /**
     * Tipo de Letra de los mensajes del Chat
     */
    private int tipoLetra;

    /**
     *
     * @param contexto
     *      contexto de la actividad que contiene el ListView que usa el adaptador
     * @param mensajes
     *      Arreglo de Mensajes que se visualizaran en el ListView
     */
    public Adaptador(Context contexto, ArrayList<Mensaje> mensajes){
        super();
        this.contexto = contexto;
        this.mensajes = mensajes;
        SharedPreferences preferencia = contexto.getSharedPreferences("Preferencias", contexto.MODE_PRIVATE);
        tamanoLetra = preferencia.getInt("tamano",0)*2+18;
        tipoLetra = preferencia.getInt("fuente",0);
    }

    /**
     *
     * @return
     *      Numero de Mensajes en el Gestionados
     */
    public int getCount(){
        return mensajes.size();
    }

    /**
     *
     * @param position
     *      Posicion de un mensaje del Arreglo
     * @return
     *      Mensaje ubicado en la posicion dada
     */
    public Object getItem(int position){
        return mensajes.get(position);
    }

    /**
     * @param i
     *      Posicion de un mensaje en el arreglo
     * @return
     *      Id del mensaje No necesario en esta aplicacion
     */
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Funcion empleada para determinar el View final que se visualizara en el ListView
     * Esta funcion es usada internamente por el adaptador y es la encargada de determianar
     * de acuerdo al arreglo de mensajes como ha de visualizarse el chat
     */
    public View getView(int position, View convertView, ViewGroup parent){
        Mensaje msj = (Mensaje) this.getItem(position);
        TextView holder;

        if(convertView == null){
            convertView = LayoutInflater.from(contexto).inflate(R.layout.sms,parent,false);
            holder = (TextView) convertView.findViewById(R.id.globo);
            convertView.setTag(holder);
        }else{
            holder = (TextView) convertView.getTag();
        }
        holder.setTextSize(tamanoLetra);
        holder.setTypeface((tipoLetra==0)? Typeface.SANS_SERIF:(tipoLetra==1)? Typeface.MONOSPACE:Typeface.SERIF);
        holder.setText(msj.getTexto());
        LayoutParams parametros = (LayoutParams) holder.getLayoutParams();
        if(msj.enviado()){
            parametros.gravity = Gravity.RIGHT;
            if(!msj.listo())
                holder.setBackgroundResource(R.drawable.burbuja_verde1);
            else
                holder.setBackgroundResource(R.drawable.burbuja_verde2);

        }else{
            parametros.gravity = Gravity.LEFT;
            holder.setBackgroundResource(R.drawable.burbuja_gris);
        }
        holder.setLayoutParams(parametros);
        return convertView;
    }
}
