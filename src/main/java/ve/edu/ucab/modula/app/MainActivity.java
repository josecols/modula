package ve.edu.ucab.modula.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity es la vista principal de la aplicación, la misma contiene el listado de coversaciones
 * y permite acceder a las frases favoritas y a los ajustes de la aplicación.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_ajustes:
                return true;
            case R.id.action_frases:
                Intent intent = new Intent(this, FrasesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_nuevochat:
                Intent chat = new Intent(this, ChatActivity.class);
                chat.putExtra("id_chat", new DataBaseManager(this).crearChat());
                startActivity(chat);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
