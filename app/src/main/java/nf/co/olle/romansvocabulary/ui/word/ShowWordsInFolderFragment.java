package nf.co.olle.romansvocabulary.ui.word;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.RepositoryIds;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;
import nf.co.olle.romansvocabulary.utils.FragmentUtils;

import static nf.co.olle.romansvocabulary.ui.Ids.FOLDER_ID;
import static nf.co.olle.romansvocabulary.ui.Ids.WAY;
import static nf.co.olle.romansvocabulary.ui.Ids.WAY_FROM_NATIVE;

/**
 * List fragment, ktery obsahuje seznam slovicek
 * Obsahuje menu na mazani a upravu slovicek
 * @author Roman Olle
 *
 */
public class ShowWordsInFolderFragment extends Fragment {

	/**
	 * Konstanta pro definici polozky menu - mazani=1
	 */
	private static final int MENU_DELETE_ID = 1;

	/**
	 * Konstanta pro definici polozky menu - edit=0
	 */
	private static final int MENU_EDIT_ID = 0;

	/**
	 * List, ktery drzi serazene ID slovicek
	 */
	private ArrayList<Long> l2;

	/**
	 * List, ktery drzi texty slovicek
	 */
	private ArrayList<String> l;

	/**
	 * List, ktery drzi modely slovicek
	 */
	private ArrayList<Word> listOfWords;

	/**
	 * aktivita, ktera vyvolala tento fragment musi implementovat rozhrani OnShowWordsListener
	 */
	private OnShowWordsListener listener;

	
	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param folder ID slozky
	 * @param way Smer cesty
	 * @return Instance tohoto fragmentu
	 */
	public static ShowWordsInFolderFragment newInstance(long folder, long way) {
		Bundle bundle = new Bundle();
		
		//zisk parametru
	    bundle.putLong(FOLDER_ID, folder);
	    bundle.putLong(WAY, way);

	    ShowWordsInFolderFragment f = new ShowWordsInFolderFragment();
	    f.setArguments(bundle);

	    return f;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root=inflater.inflate(R.layout.fragment_show_words_in_folder, container, false);


		ListView listOfWords=root.findViewById(R.id.list_of_words);

		//registrace kontextoveho menu
		registerForContextMenu(listOfWords);
		updateList(false, root);

		Button randomButton = root.findViewById(R.id.random_button);
		randomButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				randomize();
			}
		});
		return root;
	}

	//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try{
//			//zkontroluje, jestli aktivita implementuje rozhrani OnShowWordsListener
//			listener=(OnShowWordsListener)activity;
//		}
//		catch(Exception e){
//			throw new ClassCastException("Activity"+activity.toString()+"has not implemented"+OnShowWordsListener.class.getName()+"interface\n"+e.getMessage());
//		}
//	}
	
	/**
	 * Metoda, ktera plni list fragment hodnotami
	 * @param random Hodnota, ktera urcuje, jestli maji byt slovicka serazeny, nebo jestli maji byt nahodne serazeny
	 * @param view
	 */
	public void updateList(boolean random, View view) {
		Context ctx=getActivity();
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(ctx);
		ListView listOfWordsView=view.findViewById(R.id.list_of_words);
		
		//pomocne listy slovicek
		l=new ArrayList<String>();
		l2=new ArrayList<Long>();
		listOfWords=new ArrayList<Word>();

		List<Word> words=getWords(wordRepository);

		//pokud slozka nema slovicka, znepristupni se Random tlacitko, a do listu se nastavi null
		if(words.size() < 1) {
			listOfWordsView.setAdapter(null);
			disableRandomButton(view);
		}
		else {			
			enableRandomButton(view);

			for(Word word : words) {

				//podle smeru se ulozi text
				l.add(word.fromWay(getArguments().getLong(WAY)));
				
				//ulozeni indexu
				l2.add(word.getId());
				
				//ulozeni modelu slavicka
				listOfWords.add(word);
			}
			
			//vytvoreni adapteru pro list
			StableArrayAdapter a=new StableArrayAdapter(ctx, android.R.layout.simple_list_item_1, l,l2);
			listOfWordsView.setAdapter(a);
		}
	}

	private void enableRandomButton(View view) {
		Button b=(Button)view.findViewById(R.id.random_button);
		b.setEnabled(true);
	}

	private void disableRandomButton(View view) {
		Button b=(Button)view.findViewById(R.id.random_button);
		b.setEnabled(false);
	}

	/**
	 * Vraci ID slozky
	 * @return ID slozky
	 */
	private long getFolderId() {
	    return getArguments().getLong(FOLDER_ID);
	}
	
	/**
	 * Vraci Cursor slovicek ziskanych z DB
	 * @param wordRepository Model slovicek s pripojenim na DB
	 * @return
	 */
	protected List<Word> getWords(WordRepository<Word> wordRepository) {
		//podle cesty se zvoli serazeni
		if(getArguments().getLong(WAY)==WAY_FROM_NATIVE) {
			return wordRepository.getWordsInFolder(getFolderId(),RepositoryIds.WORD_ORDER_BY_LANG1);
		}
		return wordRepository.getWordsInFolder(getFolderId(),RepositoryIds.WORD_ORDER_BY_LANG2);
	}

	/**
	 * Zamichani slovicek
	 */
	public void randomize() {
		//zamichani modelu slov
		Context ctx=getActivity();
		Collections.shuffle(listOfWords);
		ListView listOfWordsView=getActivity().findViewById(R.id.list_of_words);

		//vyprazdneni listu
		l=new ArrayList<String>();
		l2=new ArrayList<Long>();
		//podle noveho zamichani, vlozim data do listu
		for(Word word:listOfWords){
			l.add(word.fromWay(getArguments().getLong(WAY)));
			l2.add(word.getId());
		}
		
		//ziskam adapter a vlozim ho do Fragment listu
		StableArrayAdapter a=new StableArrayAdapter(ctx, android.R.layout.simple_list_item_1, l,l2);
		listOfWordsView.setAdapter(a);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_EDIT_ID, 0, R.string.edit);
		menu.add(0, MENU_DELETE_ID, 1, R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId())
		{
		//vyber z menu
		
			//smazani slovicka, preda se metode id slovicka
			case MENU_DELETE_ID:
				deleteWord(info.id);
				return true;
			//uprava slovicka, opet se preda ID slovicka
			case MENU_EDIT_ID:
				editWord(info.id);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	/**
	 * Vyvola aktivitu pro upravu slovicka, predava ji ID slovicka
	 * @param id ID slovicka
	 */
	private void editWord(long id) {
		FragmentUtils.changeFragmentContent(EditWordFragment.newInstance(id),getActivity());
	}
	
	public long getWordsId(long position){
		return l2.get((int)position).longValue();
	}

	/**
	 * Smaze slovicko s danym ID z DB
	 * @param id ID slovicka
	 */
	private void deleteWord(long id) {
		Context context=getActivity();
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(context);
		
		//smaze slovicko
		if(wordRepository.delete(getWordsId(id))) {
			//pri uspechu se musi znovu nahrat list=bez smazaneho slovicka
			Toast.makeText(context, R.string.word_deleted, Toast.LENGTH_LONG).show();
			updateList(false, getView());
		} else {
			Toast.makeText(context, R.string.word_not_deleted, Toast.LENGTH_LONG).show();
		}
	}
	

	/**
	 * Rucne vytvoreny adapter na slovicka pro ListFragment
	 * @author Roman Olle
	 *
	 */
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    public StableArrayAdapter(Context context, int textViewResourceId,
	    		List<String> objects,List<Long> ids) {
	    	
	      super(context, textViewResourceId,objects);
	      
	    }

	  }
	
	/**
	 * Rozhrani, ktere nuti aktivitu implementovat metodu pro znepristupneni tlacitka nahodne
	 * @author Roman Olle
	 *
	 */
	public static interface OnShowWordsListener{
		
		/**
		 * Znepristupni tlacitko nahodne
		 */
		public void disableRandomButton();
		
		/**
		 * Zpristupni tlacitko nahodne
		 */
		public void enableRandomButton();
	}

}
