package nf.co.olle.romansvocabulary.ui.dontknow;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.ui.word.TrainingFragment;
import nf.co.olle.romansvocabulary.ui.word.Word;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;

//import static nf.co.olle.romansvocabulary.android.Ids.

/**
 * Fragment, ktery postupne zobrazuje jedno slovicko po druhem ze seznamu slovicek, ktere uzivatel nezna
 * Zobrazuje se pouze v jednom jazyce pro uceni s moznosti zobrazit preklad
 * Uzivatel ma dale moznost slovicko odebrat z tohoto listu
 * @author Roman Olle
 *
 */
public class TrainingDoNotKnowFragment extends TrainingFragment {

	private static TrainingDoNotKnowFragment instance;

	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param way Smer cesty
	 * @return Instance tohoto fragmentu
	 */
	public static TrainingDoNotKnowFragment newInstance(long way) {
		//Singleton pattern
		if(instance==null)
			instance=new TrainingDoNotKnowFragment();
		
		Bundle b=new Bundle();
		b.putLong(EXTRA_WAY, way);
		instance.setArguments(b);
		return instance;
	}


	@Override
	protected List<Word> getWords(WordRepository<Word> wordRepository) {
		return wordRepository.getWordsWhatDoNotKnow(null);
	}
	
	
		
	@Override
	protected void setNamesOfElements(View root) {
		TextView doNotKnowButton=(TextView)root.findViewById(R.id.do_not_know);
		doNotKnowButton.setText(R.string.remove_from_dont_know_list);
	}

	/**
	 * Odebrani slovicka z listu slovicek, ktere uzivatel nezna
	 */
	@Override
	public void doNotKnowWord() {
		//pokud v tomto seznamu jiz neni, nebude se znovu odebirat, jen se oznami, ze tam jiz neni
		if(list.get(index).getIsKnown()==0) {
			Toast.makeText(getActivity(), R.string.already_is_not_in_dont_know_list, Toast.LENGTH_LONG).show();;
		}
		else {
			//jinak se z tama odebere (zmena atributu z 1 na 0)
			Toast.makeText(getActivity(), R.string.removed_from_dont_know_list, Toast.LENGTH_LONG).show();
			
			WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getActivity());

			//zmeni tento udaj v DB
			wordRepository.removeFromDontKnow(list.get(index).getId());
			
			//a to i pomocnem listu se slovicky
			list.get(index).setIsKnown(0);
		}
	}

}
