package nf.co.olle.romansvocabulary.ui.io;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.WordRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;
import nf.co.olle.romansvocabulary.ui.folder.Folder;
import nf.co.olle.romansvocabulary.ui.word.Word;

import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_NAME;

import static nf.co.olle.romansvocabulary.ui.io.FileConstants.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Map<Folder, List<Word>> foldersWords = new HashMap<>();
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        // Get listview checkbox.
        ListView folders = view.findViewById(R.id.list_of_folders);

        // Initiate listview data.
        final List<ListViewItemDTO> initItemList = new ArrayList<>();

        // Create a custom list view adapter with checkbox control.
        final ListViewItemCheckboxBaseAdapter listViewDataAdapter = new ListViewItemCheckboxBaseAdapter(view.getContext().getApplicationContext(), initItemList);
        listViewDataAdapter.notifyDataSetChanged();


        // Set data adapter to list view.
        folders.setAdapter(listViewDataAdapter);

        // When list view item is clicked.
        folders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                // Get user selected item.
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);

                // Translate the selected item to DTO object.
                ListViewItemDTO itemDto = (ListViewItemDTO)itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);

                // Reverse the checkbox and clicked item check state.
                if(itemDto.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                }else
                {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }

            }
        });

        Button selectAllButton = (Button)view.findViewById(R.id.select_all_button);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = initItemList.size();
                for(int i=0;i<size;i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);
                    dto.setChecked(true);
                }

                listViewDataAdapter.notifyDataSetChanged();
            }
        });

        // Click this button to disselect all listview items with checkbox unchecked.
        Button selectNoneButton = (Button)view.findViewById(R.id.select_none_button);
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = initItemList.size();
                for(int i=0;i<size;i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);
                    dto.setChecked(false);
                }

                listViewDataAdapter.notifyDataSetChanged();
            }
        });

        Button open_import_file_button = view.findViewById(R.id.open_import_file_button);
        open_import_file_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!isExternalStorageReadable()) {
                            Toast.makeText(getActivity(), R.string.read_file_need_permission, Toast.LENGTH_LONG).show();
                        } else {
                            EditText filename_tv = getView().findViewById(R.id.import_filename);



                            if(ROOT_FILE.exists()) {
                                File file = new File(ROOT_FILE, filename_tv.getText().toString());

                                FileOutputStream os = null;
                                StringBuilder text = new StringBuilder();
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(file));
                                    String line;
                                    initItemList.clear();
                                    while ((line = br.readLine()) != null) {
                                        String[] parts = line.split("\\|");
                                        Folder folder = parseToFolder(parts[0]);
                                        List<Word> words = new ArrayList<>();

                                        for(int index = 1 ; index < parts.length ; index++) {
                                            words.add(parseToWord(parts[index]));
                                        }


                                        ListViewItemDTO dto = new ListViewItemDTO();
                                        dto.setChecked(true);
                                        dto.setItemText(folder.asString());
                                        dto.setId(folder.getId());
                                        initItemList.add(dto);
                                        foldersWords.put(folder, words);
                                    }
                                    br.close();

                                    listViewDataAdapter.notifyDataSetChanged();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.fileNotFound), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.ioException), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.fileNotFound), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );








        Button importButton = view.findViewById(R.id.import_button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(ListViewItemDTO item : initItemList) {
                    if(item.isChecked()) {
                        Map.Entry<Folder, List<Word>> entry = findEntry(item.getId(), foldersWords);
                        if(entry != null) {
                            WordRepository<Word> wordRepository = DefaultWordRepository.getInstance(getContext());
                            BasicOperation<Folder> folderRepository = new DefaultFolderRepository(getContext());
                            //ulozim a dostanu id slozky
                            long id=folderRepository.insert(entry.getKey());

                            if(id>0)
                            {
                                for(Word word : entry.getValue())
                                {
                                    word.setFolderId(id);

                                    //pokusim se ulozit udaj a dostanu id
                                    long wordId=wordRepository.insert(word);

                                    if(word.getIsKnown() == 1) {
                                        wordRepository.addToDontKnow(wordId);
                                    }
                                }
                            }
                        }
                    }
                }
                Toast.makeText(getActivity(), getString(R.string.importSuccessful), Toast.LENGTH_LONG).show();
            }
        });







        return view;
    }

    private Map.Entry<Folder, List<Word>> findEntry(int id, Map<Folder, List<Word>> foldersWords) {
        for(Map.Entry<Folder, List<Word>> entry : foldersWords.entrySet()) {
            if(entry.getKey().getId() == id) {
                return entry;
            }
        }
        return null;
    }

    private Folder parseToFolder(String folderInString) {
        String[] par = folderInString.split(String.valueOf(PARAMETER_SEPARATOR));
        return new Folder(Integer.valueOf(par[0]), par[1], par[2], par[3]);
    }

    private Word parseToWord(String wordInString) {
        String[] par = wordInString.split(String.valueOf(PARAMETER_SEPARATOR));
        return new Word(par[0], par[1], par[2], par[3], Integer.valueOf(par[4]), 0);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
