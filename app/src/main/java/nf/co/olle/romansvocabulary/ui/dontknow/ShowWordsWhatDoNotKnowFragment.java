package nf.co.olle.romansvocabulary.ui.dontknow;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import java.util.List;

import nf.co.olle.romansvocabulary.ui.word.ShowWordsInFolderFragment;
import nf.co.olle.romansvocabulary.ui.word.Word;
import nf.co.olle.romansvocabulary.repository.RepositoryIds;
import nf.co.olle.romansvocabulary.repository.WordRepository;

import static nf.co.olle.romansvocabulary.ui.Ids.WAY;
import static nf.co.olle.romansvocabulary.ui.Ids.WAY_FROM_NATIVE;

/**
 * Predstavuje list fragment, ktery obsahuje seznam slovicek, ktere uzivatel oznacil jakoze je nezna
 * Vsechny metody dedi od ShowWordsInFolderFragment
 * @author Roman Olle
 *
 */
public class ShowWordsWhatDoNotKnowFragment extends ShowWordsInFolderFragment {

	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param way Smer cesty
	 * @return Instance tohoto fragmentu
	 */
	public static ShowWordsInFolderFragment newInstance(long way) {
		Bundle bundle = new Bundle();
	    bundle.putLong(WAY, way);

	    ShowWordsWhatDoNotKnowFragment f = new ShowWordsWhatDoNotKnowFragment();
	    f.setArguments(bundle);

	    return f;
	}
	
	@Override
	protected List<Word> getWords(WordRepository<Word> wordRepository) {
		//podle cesty se zvoli serazeni
		if(getArguments().getLong(WAY)==WAY_FROM_NATIVE) {
			return wordRepository.getWordsWhatDoNotKnow(RepositoryIds.WORD_ORDER_BY_LANG1);
		}
		return wordRepository.getWordsWhatDoNotKnow(RepositoryIds.WORD_ORDER_BY_LANG2);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		//need to be empty because this class does not have context menu and extends class with context menu = need to be empty
	}

}
