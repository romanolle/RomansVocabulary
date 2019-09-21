package nf.co.olle.romansvocabulary.ui.dontknow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.utils.FragmentUtils;

/**
 * Fragment s layout s rozcestnikem pro slovicka, ktera jsou oznacena jako "nevim"
 * Obsahuje akce pro zobrazeni seznamu techto slovicek nebo jejich trenovani, pro oboji tu je moznost zvoleni smer prekladu
 * @author Roman Olle
 *
 */
public class DoNotKnowFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root=inflater.inflate(R.layout.fragment_do_not_know, container, false);
		
		Spinner choiceWay=(Spinner)root.findViewById(R.id.choice_way);
		
		//naplneni listu s hodnotami matersky->cizi a naopak
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.lang1)+"->"+getString(R.string.lang2));
		list.add(getString(R.string.lang2)+"->"+getString(R.string.lang1));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				this.getActivity(),
				android.R.layout.simple_spinner_item,
				list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		choiceWay.setAdapter(dataAdapter);

		root.findViewById(R.id.trainingButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onTrainingClicked(view);
			}
		});

		root.findViewById(R.id.showAllWordsButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onShowAllClicked(view);
			}
		});
		
		return root;
	}


	/**
	 * Odchyceni udalosti po kliknuti na tlacitko pro zobrazeni vsech slovicek, ktere uzivatel nezna
	 * Vyvoval akci pro prechod na stranku s timto seznam slovicek
	 * @param v View
	 */
	public void onShowAllClicked(View v) {
		FragmentUtils.changeFragmentContent(ShowWordsWhatDoNotKnowFragment.newInstance(((Spinner)getActivity().findViewById(R.id.choice_way)).getSelectedItemPosition()), getActivity());
	}

	/**
	 * Odchyceni udalosti po kliknuti na tlacitko trenovani slovicek, ktere uzivatel nezna
	 * Vyvoval akci pro prechod na stranku s trenovanim techto slovicek
	 * @param v View
	 */
	public void onTrainingClicked(View v) {
		FragmentUtils.changeFragmentContent(TrainingDoNotKnowFragment.newInstance(((Spinner)getActivity().findViewById(R.id.choice_way)).getSelectedItemPosition()), getActivity());
	}

}
