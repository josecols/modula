package ve.edu.ucab.modula.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Perfil extends ActionBarActivity {
    public static final int PERFIL_ID = 3;
    private ImageView foto;
    private ImageButton eliminar;
    private ImageButton camara;
    private EditText titulo;
    private Button aceptar;
    private DataBaseManager bd;
    private Bitmap img;
    private File dir;
    private long id_chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        dir = getDir("Fotos",this.MODE_PRIVATE);
        if(!dir.exists())
            dir.mkdir();
        foto = (ImageView) findViewById(R.id.foto);
        eliminar = (ImageButton) findViewById(R.id.eliminar);
        camara = (ImageButton) findViewById(R.id.camara);
        titulo = (EditText) findViewById(R.id.titulo);
        aceptar = (Button) findViewById(R.id.aceptar);
        bd = new DataBaseManager(getApplicationContext());
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img = null;
                foto.setImageResource(R.drawable.user);
            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toma = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(toma,0);
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = titulo.getText().toString().trim();
                if(!str.equals("")){
                    if(img!=null){
                        File file = new File(dir,String.valueOf(id_chat)+".png");
                        FileOutputStream out;
                        if(file.exists())
                            file.delete();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out = new FileOutputStream(file);
                            img.compress(Bitmap.CompressFormat.PNG, 0, out);
                            out.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bd.actualizarChat(id_chat,str,file.getPath());
                    }
                    else
                        bd.actualizarChat(id_chat,str,"");
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast t = Toast.makeText(getApplicationContext(), "Agregue un Titulo", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        id_chat = getIntent().getExtras().getLong("id_chat");
        cargarDatosChat();
    }

    public void cargarDatosChat(){
        Cursor cur = bd.leerChat(id_chat);
        String aux_foto = "";
        String aux_titulo = "";
        if (cur.moveToFirst()) {
            aux_foto = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO));
            aux_titulo = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO));
        }
        if (aux_foto.equals("")){
            foto.setImageResource(R.drawable.user);
            img = null;
        }
        else {
            foto.setImageDrawable(Drawable.createFromPath(aux_foto));
            img = BitmapFactory.decodeFile(aux_foto);
        }
        titulo.setText(aux_titulo);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == RESULT_OK){
            img = (Bitmap) (data.getExtras().get("data"));
            foto.setImageBitmap(img);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
