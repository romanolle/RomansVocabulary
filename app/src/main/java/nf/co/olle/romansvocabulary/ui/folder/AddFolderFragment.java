package nf.co.olle.romansvocabulary.ui.folder;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;

public class AddFolderFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_add_folder, container,false);

        //nastaveni posluchace pro tlacitko ulozeni
        Button submitButton=(Button)view.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //vyvolani akce ulozeni
                onSubmitClicked(v.getContext());
            }
        });


//        Toolbar toolbar = a.findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentActivity activity = getActivity();
//                activity.getSupportFragmentManager().popBackStack();
//            }
//        });
        return view;
    }

    public void onSubmitClicked(Context context)
    {
        View root=getView();

        try {
            String name=((EditText)root.findViewById(R.id.name)).getText().toString();
            String lang1=((EditText)root.findViewById(R.id.lang1)).getText().toString();
            String lang2=((EditText)root.findViewById(R.id.lang2)).getText().toString();

            //overeni, jestli jsou vyplnene vsechny povinne policka
            if(name.length()==0)
                throw new NullPointerException(getString(R.string.folder_name));
            if(lang1.length()==0)
                throw new NullPointerException(getString(R.string.lang1));
            if(lang2.length()==0)
                throw new NullPointerException(getString(R.string.lang2));

            //vyvolani udalosti pro zpracovani dat pro ulozeni slozky aktivitou
            //a.onAddFolder(new Folder(name, lang1, lang2));

            BasicOperation<Folder> folderRepository = new DefaultFolderRepository(getContext());
            //ulozim a dostanu id slozky
            long id=folderRepository.insert(new Folder(name, lang1, lang2));

            //id=-1 znamena, ze slozka nebyla ulozena
            if(id>-1)
            {

                Toast.makeText(context, R.string.folder_saved , Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
            }

        } catch(NullPointerException e){
            //pokud ne, zobrazi se chyba, co je prazdne
            Toast.makeText(getActivity(), getString(R.string.field)+" "+e.getMessage()+" "+getString(R.string.cannot_be_empty), Toast.LENGTH_LONG).show();
        }
    }
}
