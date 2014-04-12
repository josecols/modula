package ve.edu.ucab.modula.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FrasesActivity extends ActionBarActivity {

    private ListView vistaFrases;
    private ActionMode mActionMode;
    private DataBaseManager mDbManager;
    private Cursor cursor;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.frases_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_nuevafrase:
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            vistaFrases.setItemChecked(-1, true);
            vistaFrases.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases);
        this.vistaFrases = (ListView) findViewById(R.id.vista_frases);
        this.mDbManager = new DataBaseManager(getApplicationContext());
        cargarLista();
    }

    @Override
    protected void onDestroy() {
        this.cursor.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_nuevafrase) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarLista() {
        this.cursor = this.mDbManager.leerFrases();
        String[] from = new String[]{DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, cursor, from, to, 0);
        this.vistaFrases.setAdapter(adapter);

        this.vistaFrases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vistaFrases.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                if (mActionMode != null)
                    return false;
                mActionMode = startActionMode(mActionModeCallback);
                vistaFrases.setItemChecked(position, true);
                return true;
            }
        });
    }
}