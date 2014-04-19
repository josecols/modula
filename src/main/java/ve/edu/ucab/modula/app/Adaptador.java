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
 * Created by JOSE on 08/04/14.
 */
public class Adaptador extends BaseAdapter{
    private Context contexto;
    private ArrayList<Mensaje> mensajes;
    private int tamanoletra;
    private int tipoletra;


    public Adaptador(Context contexto, ArrayList<Mensaje> mensajes){
        super();
        this.contexto = contexto;
        this.mensajes = mensajes;
        SharedPreferences preferencia = contexto.getSharedPreferences("Preferencias", contexto.MODE_PRIVATE);
        tamanoletra = preferencia.getInt("tamano",0)*2+18;
        tipoletra = preferencia.getInt("fuente",0);
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
        holder.setTextSize(tamanoletra);
        holder.setTypeface((tipoletra==0)? Typeface.SANS_SERIF:(tipoletra==1)? Typeface.MONOSPACE:Typeface.SERIF);
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
