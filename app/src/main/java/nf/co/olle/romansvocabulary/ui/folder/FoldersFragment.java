package nf.co.olle.romansvocabulary.ui.folder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;
import nf.co.olle.romansvocabulary.ui.BlankFragment;
import nf.co.olle.romansvocabulary.ui.dontknow.DoNotKnowFragment;
import nf.co.olle.romansvocabulary.utils.FragmentUtils;

import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_NAME;

public class FoldersFragment extends Fragment {
    public static String[] array=new String[1];
    private AppCompatActivity activity;


    /**
     * Konstanta pro definici polozky menu - mazani=0
     */
    private static final int MENU_DELETE_ID = 0;

    /**
     * Konstanta pro definici pridani slovicka - mazani=1
     */
    private static final int MENU_ADD_WORD_ID = 1;
    private OnAddNewWordListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_folders, container, false);

        //nastaveni notKnowWords nazvem na uvodni strance
        //naplneni pole jednou hodnotou z resouces hodnot
        ListView notKnownItemsListView=v.findViewById(R.id.not_know_item_in_list);
        array[0]= activity.getBaseContext().getString(R.string.i_dont_know);
        if(array.length>0) {
            notKnownItemsListView.setVisibility(ListView.VISIBLE);
            notKnownItemsListView.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, array));
        }
        //nastaveni posluchace na klik na hodnotu listu = na klik na hodnotu "Slovicka ktere neznam"
        notKnownItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //vyvola se udalost pro zpracovani s parametrem ID hodnoty
                onIDontKnowListItemClick(id);
            }
        });







        ListView listOfFolders=v.findViewById(R.id.list_of_folders);
        listOfFolders.setVisibility(ListView.VISIBLE);

        Context ctx=getActivity();
        BasicOperation<Folder> folderRepository = new DefaultFolderRepository(ctx);

        String[] columns={FOLDER_COLUMN_NAME};
        int[] ids={android.R.id.text1};
//		Cursor curs = folderRepository.getAllAsCursor();
//
//		Snackbar.make(getView(), "Replace with your own action " + curs.getCount(), Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        ListAdapter adapter=new SimpleCursorAdapter(ctx, android.R.layout.simple_list_item_1, folderRepository.getAllAsCursor(), columns, ids, 0);

        listOfFolders.setAdapter(adapter);

        registerForContextMenu(listOfFolders);

        listOfFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long index) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_content, FolderFragment.newInstance(index));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //zkontroluje, jestli aktivita implementuje rozhrani OnFolderClickedListener
            listener=(OnAddNewWordListener)context;
            activity = (AppCompatActivity) context;
        }
        catch(Exception e){
            throw new ClassCastException("Activity"+context.toString()+"has not implemented"+ FoldersFragment.OnAddNewWordListener.class.getName()+"interface\n"+e.getMessage());
        }
    }

    private void onIDontKnowListItemClick(long id) {
        if(id==0) {
            FragmentUtils.changeFragmentContent(new DoNotKnowFragment(), getActivity());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_DELETE_ID, 0, R.string.delete);
        menu.add(0, MENU_ADD_WORD_ID, 1, R.string.add_word);
        //TODO edit folder
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId())
        {
            //vyber z menu

            //smazani slozky, preda se metode id slozky
            case MENU_DELETE_ID:
                deleteFolder(info.id);
                return true;
            //smazani slozky, preda se posluchaci udalost na vlozeni noveho slovicka s id slozky
            case MENU_ADD_WORD_ID:
                listener.onAddNewWordButtonClicked(info.id);
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Metoda na smazani slozky
     * @param id ID slozky
     */
    private void deleteFolder(long id) {
        Context context=getActivity();
        BasicOperation<Folder> folderRepository = new DefaultFolderRepository(context);

        //smazani slozky - TRUE=smazano, FALSE=nesmazano
        if(folderRepository.delete(id))
        {
            //pri smazani znovu nactu data z databaze do fragment listu (uz bez smazane slozky)
            Toast.makeText(context, R.string.folder_deleted, Toast.LENGTH_LONG).show();
            updateList();
        }
        else
        {
            Toast.makeText(context, R.string.folder_not_deleted, Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Metoda, ktera plni list fragment hodnotami
     */
    public void updateList() {
        Context ctx=getActivity();
        BasicOperation<Folder> folderRepository = new DefaultFolderRepository(ctx);

        String[] columns={FOLDER_COLUMN_NAME};
        int[] ids={android.R.id.text1};

        ListAdapter adapter=new SimpleCursorAdapter(ctx, android.R.layout.simple_list_item_1, folderRepository.getAllAsCursor(), columns, ids, 0);
        //setListAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, getList()));


        ListView lv = getActivity().findViewById(R.id.list_of_folders);

        lv.setAdapter(adapter);
    }


    public interface OnAddNewWordListener {
        void onAddNewWordButtonClicked(long index);
    }

}
