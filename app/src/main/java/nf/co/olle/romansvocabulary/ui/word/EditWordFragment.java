package nf.co.olle.romansvocabulary.ui.word;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;

public class EditWordFragment extends Fragment {

	public static final String EXTRA_ID = "id";

	
	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param id ID slovicka
	 * @return Instance tohoto fragmentu
	 */
	public static Fragment newInstance(long id) {
		Bundle bundle=new Bundle();
		bundle.putLong(EXTRA_ID,id);
		EditWordFragment f=new EditWordFragment();
		f.setArguments(bundle);
		return f;
	}

	/**
	 * Vraci ID slovicka
	 * @return ID slovicka
	 */
	private long getIndex() {
	    return getArguments().getLong(EXTRA_ID);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root=inflater.inflate(R.layout.fragment_add_word, container, false);
		
		TextView header=(TextView)root.findViewById(R.id.add_word);
		header.setText(R.string.edit_word);
		
		EditText folderName=(EditText)root.findViewById(R.id.folder);
		EditText lang1=(EditText)root.findViewById(R.id.lang1);
		EditText lang2=(EditText)root.findViewById(R.id.lang2);
		EditText pronunciation=(EditText)root.findViewById(R.id.pronunciation);
		EditText info=(EditText)root.findViewById(R.id.others);
		
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getActivity());

		//naplneni formulare hodnotami slovicka
		Word word=wordRepository.getOne(getIndex());
		
//		
		if(word == null) {
			folderName.setText(R.string.error);
			folderName.setError("");
		}
		else {
			//konkretni plneni
			lang1.setText(word.getLang1());
			lang2.setText(word.getLang2());
			pronunciation.setText(word.getPronunciation());
			info.setText(word.getInfo());
		}

		//nastaveni posluchace pro tlacitko upravy
		Button submitButton=(Button)root.findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vyvolani akce upravy
				onSubmitClicked();
			}
		});
		
		
		return root;
	}
	
	/**
	 * Overeni odeslanych hodnot, ulozeni hodnot do DB
	 */
	public void onSubmitClicked()
	{
		View root=getView();

		String pronunciation=((EditText)root.findViewById(R.id.pronunciation)).getText().toString();
		String lang1=((EditText)root.findViewById(R.id.lang1)).getText().toString();
		String lang2=((EditText)root.findViewById(R.id.lang2)).getText().toString();
		String info=((EditText)root.findViewById(R.id.others)).getText().toString();
		Long wordId=getIndex();
		
		try {
			//overeni, jestli jsou vyplnene vsechny povinne policka
			if(lang1.length()==0)
				throw new NullPointerException(getString(R.string.lang1));
			if(lang2.length()==0)
				throw new NullPointerException(getString(R.string.lang2));
			onEditWord(new Word(wordId, lang1, lang2, pronunciation, info, 1, -1));

			getActivity().onBackPressed();
		} catch(NullPointerException e){
			//pokud ne, zobrazi se chyba, co je prazdne
			Toast.makeText(getActivity(), getString(R.string.field)+" "+e.getMessage()+" "+getString(R.string.cannot_be_empty), Toast.LENGTH_LONG).show();
		}
	}

	private void onEditWord(Word word) {
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getContext());

		//ulozi zmeny slovicka, a dostanu pocet ovlivnenych radku
		long numberOfChanges=wordRepository.insert(word);
		if(numberOfChanges>0)
		{
			//pokud byla provedena zmena, tak zobrazim zpravu
			Toast.makeText(getContext(), R.string.word_saved , Toast.LENGTH_LONG).show();
			//prejdu na predchozi stranku (seznam slovicek v dane slozce)
		}
		else
		{
			//pokud nebyla provedena zadna zmena
			Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
		}
	}
}
