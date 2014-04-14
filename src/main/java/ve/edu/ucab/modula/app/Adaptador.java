package ve.edu.ucab.modula.app;


import java.util.ArrayList;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Created by JOSE on 08/04/14.
 */
public class Adaptador extends BaseAdapter{
    private Context contexto;
    private ArrayList<Mensaje> mensajes;

    public Adaptador(Context contexto, ArrayList<Mensaje> mensajes){
        super();
        this.contexto = contexto;
        this.mensajes = mensajes;
    }

    public int getCount(){
        return mensajes.size();
    }

    public Object getItem(int position){
        return mensajes.get(position);
    }

    public long getItemId(int i) {
        return 0;
    }

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
        holder.setText(msj.getTexto());
        LayoutParams parametros = (LayoutParams) holder.getLayoutParams();
        if(msj.enviado()){
            parametros.gravity = Gravity.RIGHT;
            holder.setBackgroundResource(R.drawable.burbuja_verde);
        }else{
            parametros.gravity = Gravity.LEFT;
            holder.setBackgroundResource(R.drawable.burbuja_gris);
        }
        holder.setLayoutParams(parametros);
        return convertView;
    }
}