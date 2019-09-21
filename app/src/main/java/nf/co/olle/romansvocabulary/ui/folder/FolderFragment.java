package nf.co.olle.romansvocabulary.ui.folder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;
import nf.co.olle.romansvocabulary.ui.word.ShowWordsInFolderFragment;
import nf.co.olle.romansvocabulary.ui.word.TrainingFragment;
import nf.co.olle.romansvocabulary.utils.FragmentUtils;

/**
 * Fragment s layout s rozcestnikem pro slovicka ve vybrane slozce
 * Obsahuje akce pro zobrazeni seznamu techto slovicek nebo jejich trenovani, pro oboji tu je moznost zvoleni smer prekladu
 * Dale moznost pridani dalsiho slovicka do teto slozky
 * @author Roman Olle
 *
 */
public class FolderFragment extends Fragment {

	public static final String EXTRA_ID = "id";
	
	/**
	 * Staticka metoda pro vytvoreni instance tohoto fragmentu a to kvuli predani parametru
	 * @param id ID slozky
	 * @return Instance tohoto fragmentu
	 */
	public static FolderFragment newInstance(long id) {
		Bundle bundle = new Bundle();
	    bundle.putLong(EXTRA_ID, id);

	    FolderFragment f = new FolderFragment();
	    f.setArguments(bundle);

	    return f;
	}

	/**
	 * Vraci ID slozky
	 * @return ID slozky
	 */
	private long getIndex() {
	    return getArguments().getLong(EXTRA_ID);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root=inflater.inflate(R.layout.fragment_folder, container, false);


		//naplneni listu s hodnotami matersky->cizi a naopak
		//a vyplneni nadpisu nazvem slozky
		TextView header=(TextView)root.findViewById(R.id.header);
		Spinner choiceWay=(Spinner)root.findViewById(R.id.choice_way);
		
		BasicOperation<Folder> folderRepository = new DefaultFolderRepository(getActivity());
		Folder folder = folderRepository.getOne(getIndex());
		
//		
		if(folder == null) {
			header.setText(R.string.error);
			header.setError("");
		}
		else {
			List<String> list = new ArrayList<String>();
			header.setText(folder.getName());
			list.add(folder.getLang1()+"->"+folder.getLang2());
			list.add(folder.getLang2()+"->"+folder.getLang1());

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					this.getActivity(),
					android.R.layout.simple_spinner_item,
					list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			choiceWay.setAdapter(dataAdapter);
		}


		Button addNewWordButton = root.findViewById(R.id.addNewWordButton);
		addNewWordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(getActivity() instanceof  OnAddNewWordListener){
					((OnAddNewWordListener)getActivity()).onAddNewWordButtonClicked(getIndex());
				}
			}
		});

		Button showAllWordsButton = root.findViewById(R.id.showAllWordsButton);
		showAllWordsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onShowWordsAllButtonClicked(view);
			}
		});

		Button trainingButton = root.findViewById(R.id.trainingButton);
		trainingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onTrainingClicked(view);
			}
		});

		return root;
	}

	public void onShowWordsAllButtonClicked(View v) {

		Spinner choiceWay=(Spinner)getActivity().findViewById(R.id.choice_way);

		FragmentUtils.changeFragmentContent(ShowWordsInFolderFragment.newInstance(getIndex(), (long)choiceWay.getSelectedItemPosition()), getActivity());
	}


	public void onTrainingClicked(View v) {
		FragmentUtils.changeFragmentContent(TrainingFragment.newInstance(getIndex(),((Spinner)getActivity().findViewById(R.id.choice_way)).getSelectedItemPosition()), getActivity());
	}

	public interface OnAddNewWordListener {
		void onAddNewWordButtonClicked(long index);
	}
}
