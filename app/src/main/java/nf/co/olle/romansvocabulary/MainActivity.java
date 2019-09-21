package nf.co.olle.romansvocabulary;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import nf.co.olle.romansvocabulary.ui.io.ExportFragment;
import nf.co.olle.romansvocabulary.ui.folder.AddFolderFragment;
import nf.co.olle.romansvocabulary.ui.folder.FolderFragment;
import nf.co.olle.romansvocabulary.ui.folder.FoldersFragment;
import nf.co.olle.romansvocabulary.ui.io.ImportFragment;
import nf.co.olle.romansvocabulary.ui.word.AddWordFragment;
import nf.co.olle.romansvocabulary.utils.FragmentUtils;

public class MainActivity extends AppCompatActivity implements FoldersFragment.OnAddNewWordListener, FolderFragment.OnAddNewWordListener {

    /**
     * Slouzi pro naplenni listu hodnotou pro zobrazeni slovicek, ktere uzivatel nezna
     */
    public static String[] array=new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeFragmentContent(new FoldersFragment());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export) {
            FragmentUtils.changeFragmentContent(new ExportFragment(),this);
            return true;
        }
        if (id == R.id.action_import) {
            FragmentUtils.changeFragmentContent(new ImportFragment(),this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddFolderClicked(View v) {
        FragmentUtils.changeFragmentContent(new AddFolderFragment(), this);
    }

    @Override
    public void onAddNewWordButtonClicked(long index) {
        FragmentUtils.changeFragmentContent(AddWordFragment.newInstance(index), this);
    }

    private void changeFragmentContent(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }


}
