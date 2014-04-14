package ve.edu.ucab.modula.app;


import android.app.ListActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import java.util.ArrayList;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.ListView;
import android.widget.Toast;
import android.database.Cursor;


public class Chat extends ActionBarActivity {
    private ListView lista;
    private ImageButton boton_hablar;
    private ImageButton boton_escuchar;
    private ArrayList<Mensaje> mensajes;
    private EditText texto;
    private Adaptador adaptador;
    private TextoAVoz locutor;
    private VozATexto traductor;
    private DataBaseManager bd;
    private long id_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        locutor = new TextoAVoz(this);
        traductor = new VozATexto(this);
        boton_hablar = (ImageButton) findViewById(R.id.hablar);
        boton_escuchar = (ImageButton) findViewById(R.id.escuchar);
        texto = (EditText) findViewById(R.id.edit);
        mensajes = new ArrayList<Mensaje>();
        adaptador = new Adaptador(this,mensajes);
        bd = new DataBaseManager(getApplicationContext());
        id_chat = getIntent().getExtras().getLong("id_chat");
        lista = (ListView) findViewById(R.id.lista);
        lista.setAdapter(adaptador);
        boton_hablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = texto.getText().toString().trim();
                texto.setText("");
                if (str.length() > 0) {
                    Mensaje msj = new Mensaje(str, true);
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat, msj);
                    locutor.pronunciar(str);
                }
            }
        });
        boton_escuchar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                traductor.traducir();
            }
        }); 
        cargarMensajes();
    }


    private void cargarMensajes(){
        Cursor cur = bd.leerMensajes(id_chat);
        if(cur.moveToFirst()){
            int col_texto = cur.getColumnIndex(DataBaseContract.MensajesTabla.COLUMN_NAME_TEXTO);
            int col_enviado = cur.getColumnIndex(DataBaseContract.MensajesTabla.COLUMN_NAME_ENVIADO);
            do{
                mensajes.add(new Mensaje(cur.getString(col_texto),cur.getString(col_enviado).equals("1")));
            }while(cur.moveToNext());
            adaptador.notifyDataSetChanged();
            lista.setSelection(mensajes.size()-1);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VozATexto.CONVERSION_ID: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> texto = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Mensaje msj = new Mensaje(texto.get(0),false);
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat,msj);
                }
                break;
            }
            case 2: /*Valor del activity Frases*/{
                if (resultCode == RESULT_OK && null != data) {
                    String str = data.getStringExtra("frase");
                    Mensaje msj = new Mensaje(str,true);
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat,msj);
                    locutor.pronunciar(str);
                }
                break;
            }
            /*case Perfil.PERFIL_ID:{
                if (resultCode == RESULT_OK && null != data) {
                    //Actualizar titulo e imagen desde los datos de la BD
                    getActionBar().setLogo(Drawable.createFromPath((String) data.getExtras().get("ruta")));
                }
            }*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        getActionBar().setHomeButtonEnabled(true);
        Cursor cur = bd.leerChat(id_chat);
        String foto="";
        String titulo="";
        if(cur.moveToFirst()){
            foto = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO));
            titulo = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO));
        }
        if(foto.equals(""))
            getActionBar().setLogo(R.drawable.user);
        else {
            getActionBar().setLogo(Drawable.createFromPath(foto));
        }
        getActionBar().setTitle(titulo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toast t;
        switch (id){
            case R.id.action_frases:
                //llamar activity frases
                return true;
            default:
                /*Intent frases = new Intent(this,Perfil.class);
                startActivityForResult(frases,Perfil.PERFIL_ID);*/
                return true;
        }
        //return super.onOptionsItemSelected(item);
    }


    public void onDestroy() {
        if (locutor != null) {
            locutor.finalizar();
        }
        super.onDestroy();
    }

    void addNewMessage(Mensaje m)
    {
        mensajes.add(m);
        adaptador.notifyDataSetChanged();
        lista.setSelection(mensajes.size()-1);
    }
}
