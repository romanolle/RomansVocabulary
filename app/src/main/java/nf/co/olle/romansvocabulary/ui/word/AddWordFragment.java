package nf.co.olle.romansvocabulary.ui.word;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;
import nf.co.olle.romansvocabulary.ui.folder.Folder;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;

import static nf.co.olle.romansvocabulary.ui.Ids.FOLDER_ID;

/**
 * Fragment s layout s formularem pro pridani noveho slovicka
 * @author Roman Olle
 *
 */
public class AddWordFragment extends Fragment {
	
	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param id ID slozky, kde se ma slovicko ulozit
	 * @return Instance tohoto fragmentu
	 */
	public static Fragment newInstance(long id) {
		Bundle bundle=new Bundle();
		bundle.putLong(FOLDER_ID,id);
		AddWordFragment f=new AddWordFragment();
		f.setArguments(bundle);
		return f;
	}

	/**
	 * Vraci ID slozky, do ktere se ma slovicko ulozit
	 * @return ID slozky
	 */
	private long getIndex() {
	    return getArguments().getLong(FOLDER_ID);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root=inflater.inflate(R.layout.fragment_add_word, container, false);
		
		//nastaveni nazvu slozky , ktery se musi vytahnout z DB
		EditText folderName=(EditText)root.findViewById(R.id.folder);
		
		BasicOperation<Folder> folderRepository = new DefaultFolderRepository(getActivity());
		Folder folder=folderRepository.getOne(getIndex());
		
		if(folder == null) {
			folderName.setText(R.string.error);
			folderName.setError("");
		}
		else {
			folderName.setText(folder.getName());
		}
		

		//nastaveni posluchace pro tlacitko ulozeni
		Button submitButton=(Button)root.findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vyvolani akce ulozeni
				onSubmitClicked();
			}
		});
		
		return root;
	}

	/**
	 * Overeni odeslanych hodnot, ulozeni a po ulozeni vyprazdneni formulare
	 */
	public void onSubmitClicked()
	{
		View root=getView();

		String pronunciation=((EditText)root.findViewById(R.id.pronunciation)).getText().toString();
		String lang1=((EditText)root.findViewById(R.id.lang1)).getText().toString();
		String lang2=((EditText)root.findViewById(R.id.lang2)).getText().toString();
		String info=((EditText)root.findViewById(R.id.others)).getText().toString();
		long folderId=getIndex();

		try {//overeni, jestli jsou vyplnene vsechny povinne policka
			if(lang1.length()==0)
				throw new NullPointerException(getString(R.string.lang1));
			if(lang2.length()==0)
				throw new NullPointerException(getString(R.string.lang2));
			
			//zkontroluje se, jestli bylo slovicko ulozeno
			boolean save=storeWord(new Word(
					lang1, 
					lang2, 
					pronunciation, 
					info, 
					1, 
					folderId));
			
			//kdyz bylo, tak vyprazdnim bunky
			if(save) {
				((EditText)root.findViewById(R.id.lang1)).setText("");
				((EditText)root.findViewById(R.id.lang2)).setText("");
				((EditText)root.findViewById(R.id.others)).setText("");
				((EditText)root.findViewById(R.id.pronunciation)).setText("");
				

				((EditText)root.findViewById(R.id.lang1)).requestFocus();
			}
		} catch(NullPointerException e){
			//pokud ne, zobrazi se chyba, co je prazdne
			Toast.makeText(getActivity(), getString(R.string.field)+" "+e.getMessage()+" "+getString(R.string.cannot_be_empty), Toast.LENGTH_LONG).show();
		}
	}

	private boolean storeWord(Word word) {
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getContext());

		//pokusim se ulozit udaj a dostanu id
		long id=wordRepository.insert(word);

		//neuspesne ulozeni = id=-1
		if(id>-1) {
			Toast.makeText(getContext(), R.string.word_saved , Toast.LENGTH_LONG).show();
			return true;
		}
		Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
		return false;
	}

}
