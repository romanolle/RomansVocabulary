package nf.co.olle.romansvocabulary.ui.word;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;

/**
 * Fragment, ktery postupne zobrazuje jedno slovicko po druhem a to pouze v jednom jazyce pro uceni s moznosti zobrazit preklad
 * Uzivatel ma dale moznost slovicko vlozit do listu slovicek, ktere nezna
 * @author Roman Olle
 *
 */
public class TrainingFragment extends Fragment {

	/**
	 * Konstanta s nazvem pro prenos parametru "id" = ID slozky, kde se ma slovicko vlozit
	 */
	public static final String EXTRA_FOLDER_ID = "folder";

	/**
	 * Konstanta s nazvem pro prenos parametru "way" = smer slovicek
	 */
	public static final String EXTRA_WAY = "way";

	/**
	 * List slovicek
	 */
	protected ArrayList<Word> list;
	
	/**
	 * Aktualni pozice listu slovicek
	 */
	protected int index=-1;

	private int countOfWords;
	
	private static TrainingFragment instance;

	
	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param folder ID slozky
	 * @param way Smer cesty
	 * @return Instance tohoto fragmentu
	 */
	public static TrainingFragment newInstance(long folder, long way) {
		//Singleton pattern
		if(instance==null)
			instance=new TrainingFragment();
		
		Bundle b=new Bundle();
		b.putLong(EXTRA_FOLDER_ID, folder);
		b.putLong(EXTRA_WAY, way);
		instance.setArguments(b);
		return instance;
	}

	/**
	 * Vraci ID slozky, ze ktere se vybiraji slovicka
	 * @return ID slozky
	 */	
	private long getFolderIndex() {
	    return getArguments().getLong(EXTRA_FOLDER_ID);
	}

	/**
	 * Vraci smer zobrazeni slovice
	 * @return ID slozky
	 */
	private long getWay() {
	    return getArguments().getLong(EXTRA_WAY);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root=inflater.inflate(R.layout.fragment_training, container, false);
		index=-1;
		
		//nastaveni posluchace tlacitka next na kliknuti
		Button next=(Button)root.findViewById(R.id.next_word);
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//prechod na dalsi slovicko || zobrazeni prekladu
				nextWord();
			}
		});

		//nastaveni posluchace tlacitka pridani slovicka do listu, ktere uzivatel nezna
		Button notKnowButton=(Button)root.findViewById(R.id.do_not_know);
		notKnowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//akce
				doNotKnowWord();
			}
		});
		
		//prejmenovani ruznych elementu, hlavne pro potomky
		setNamesOfElements(root);
		
		//ziskani slovicek z DB
		WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getActivity());
		List<Word> words = getWords(wordRepository);
		
		//kdyz neexistuji slovicka, znepristupni se tlacitka
		if(words.size() < 1) {
			notKnowButton.setEnabled(false);
			next.setEnabled(false);
			TextView lang1=(TextView)root.findViewById(R.id.lang1);
			lang1.setText(R.string.no_words);
			lang1.setError("");
		}
		else {
			//jinak se naplni list slovickama
			countOfWords=words.size();
			list=new ArrayList<Word>();
			for(Word word : words) {
				list.add(new Word(
						word.getId(), 
						word.getLang1(), 
						word.getLang2(), 
						word.getPronunciation(), 
						word.getInfo(), 
						word.getIsKnown(),
						word.getFolderId()
						));
			}
			
			//zamicha se list
			Collections.shuffle(list);
			
			//necha se zobrazit prvni slovicko
			nextWord(root);
		}
		
		return root;
	}
	
	/**
	 * Vraci Cursor slovicek ziskanych z DB
	 * @param wordRepository Model slovicek s pripojenim na DB
	 * @return
	 */
	protected List<Word> getWords(WordRepository<Word> wordRepository) {
		//seznam slovicek v dane slozce bez serazeni
		return wordRepository.getWordsInFolder(getFolderIndex(),null);
	}

	/**
	 * Slouzi pro ruzna nastaveni a prejmenovani elementu u potomku teto tridy, tady je v podstate jako abstract method
	 * @param root View
	 */
	protected void setNamesOfElements(View root) {
	}

	/**
	 * Zobrazeni dalsiho slovicka nebo prekladu predchoziho bez view, ktere se tady ziska
	 */
	public void nextWord() {
		nextWord(getView());
	}
	
	/**
	 * Se ziskanym view se zobrazi dalsi slovicko nebo preklad predchoziho
	 * @param root view
	 */
	public void nextWord(View root) {
		//ziskani textfieldu
		TextView lang1=(TextView)root.findViewById(R.id.lang1);
		TextView lang2=(TextView)root.findViewById(R.id.lang2);
		TextView info=(TextView)root.findViewById(R.id.info);
		TextView position=(TextView)root.findViewById(R.id.position);
		
		//pokud je slovicko v prekladu (lang2) GONE=v danem snimku nebylo videt => provede se pouze preklad (GONE->VISIBLE)
		if(lang2.getVisibility()==View.GONE)
			showWord();
		else{
			//pokud byl uz preklad zobrazeny, tak necham zobrazit dalsi slovicko v listu a lang2 nastavim opet na GONE
			
			//v kazdem kroku prejdu na dalsi slovicko
			if(index==-1)
				index=0;
			else
				index++;
			
			//nastaveni pozice
			if(index>=countOfWords)
				position.setText("");
			else
				position.setText(String.valueOf(index+1)+"/"+String.valueOf(countOfWords));
			
			//tlacitko dostane nazev Zobraz preklad
			Button next=(Button)root.findViewById(R.id.next_word);
			next.setText(R.string.show_word);
			
			//lang2 a info budou GONE
			lang2.setVisibility(View.GONE);
			info.setVisibility(View.GONE);
			
			//pokud index je uz vetsi nez pocet slov
			if(index>=list.size()) {
				//tak zobrazim hlasku, ze neni vice slov
				lang1.setText(R.string.no_more_words);
				Button dontKnow=(Button)root.findViewById(R.id.do_not_know);
				
				//znepristupnim tlacitka
				next.setEnabled(false);
				dontKnow.setEnabled(false);
			}
			else {
				//jinak podle cesty naplnim fieldy
				Word word=list.get(index);
				if(getWay()==0){
					lang1.setText(word.getLang1());
					lang2.setText(word.getLang2()+" ["+word.getPronunciation()+"]");
					info.setText(word.getInfo());
				}
				else {
					lang2.setText(word.getLang1());
					lang1.setText(word.getLang2()+" ["+word.getPronunciation()+"]");
					info.setText(word.getInfo());
				}
			}
		}
	}
	
	/**
	 * Pridani slovicka do listu slovicek, ktere uzivatel nezna
	 */
	public void doNotKnowWord() {
		//pokud v tomto seznamu jiz je, nebude se tam pridavat, jen se oznami, ze tam jiz je
		if(list.get(index).getIsKnown()==1) {
			Toast.makeText(getActivity(), R.string.already_exists_in_dont_know_list, Toast.LENGTH_LONG).show();;
		}
		else {
			//jinak se tam prida (zmena atributu z 0 na 1)
			Toast.makeText(getActivity(), R.string.added_to_dont_know_list, Toast.LENGTH_LONG).show();
			
			WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getActivity());
			
			//zmeni tento udaj v DB
			wordRepository.addToDontKnow(list.get(index).getId());
			
			//a to i pomocnem listu se slovicky
			list.get(index).setIsKnown(1);
		}
	}
	
	/**
	 * Zobrazi preklad slovicka a jeho info
	 */
	public void showWord() {
		TextView lang2=(TextView)getView().findViewById(R.id.lang2);
		TextView info=(TextView)getView().findViewById(R.id.info);
		Button next=(Button)getView().findViewById(R.id.next_word);

		//zmeni text tlacitka na Dalsi
		next.setText(R.string.next_word);
		
		//a zobrazi preklad a info
		lang2.setVisibility(View.VISIBLE);
		info.setVisibility(View.VISIBLE);
	}
}
