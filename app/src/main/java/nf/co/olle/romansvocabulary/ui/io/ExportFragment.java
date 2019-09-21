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
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nf.co.olle.romansvocabulary.R;
import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.impl.DefaultFolderRepository;
import nf.co.olle.romansvocabulary.repository.impl.DefaultWordRepository;
import nf.co.olle.romansvocabulary.ui.folder.Folder;
import nf.co.olle.romansvocabulary.ui.word.Word;

import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_NAME;
import static nf.co.olle.romansvocabulary.ui.io.FileConstants.*;
public class ExportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        // Get listview checkbox.
        ListView folders = view.findViewById(R.id.list_of_folders);

        // Initiate listview data.
        final List<ListViewItemDTO> initItemList = this.getInitViewItemDtoList(view.getContext());

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


        Button exportButton = view.findViewById(R.id.export_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isExternalStorageWritable()) {
                    BasicOperation<Folder> folderRepository = new DefaultFolderRepository(view.getContext());
                    DefaultWordRepository wordRepository = DefaultWordRepository.getInstance(view.getContext());
                    try {
                        File folderFile =  FOLDER;
                        folderFile.mkdirs();
                        File file = new File(folderFile, "export-" + new Date().toString() + ".dat");

                        FileOutputStream os = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(os);



                        int size = initItemList.size();
                        for(int i=0;i<size;i++)
                        {
                            ListViewItemDTO dto = initItemList.get(i);

                            if(dto.isChecked())
                            {
                                StringBuilder builder = new StringBuilder();
                                Folder folder = folderRepository.getOne(dto.getId());
                                builder.append(folder.getId());
                                builder.append(PARAMETER_SEPARATOR);
                                builder.append(folder.getName());
                                builder.append(PARAMETER_SEPARATOR);
                                builder.append(folder.getLang1());
                                builder.append(PARAMETER_SEPARATOR);
                                builder.append(folder.getLang2());
                                builder.append(WORD_SEPARATOR);

                                List<Word> words = wordRepository.getWordsInFolder(folder.getId(), null);
                                for(Word word : words) {
                                    builder.append(word.getLang1());
                                    builder.append(PARAMETER_SEPARATOR);
                                    builder.append(word.getLang2());
                                    builder.append(PARAMETER_SEPARATOR);
                                    builder.append(word.getPronunciation());
                                    builder.append(PARAMETER_SEPARATOR);
                                    builder.append(word.getInfo());
                                    builder.append(PARAMETER_SEPARATOR);
                                    builder.append(word.getIsKnown());
                                    builder.append(WORD_SEPARATOR);
                                }

                                writer.write(builder.toString().substring(0,builder.toString().length() - 1));
                                writer.write(NEW_LINE);
                            }
                        }


                        writer.close();
                        os.close();

                        Toast.makeText(getActivity(), getString(R.string.exported) + file.getPath(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), getString(R.string.notWritable), Toast.LENGTH_LONG).show();
                }
                listViewDataAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }




    // Return an initialize list of ListViewItemDTO.
    private List<ListViewItemDTO> getInitViewItemDtoList(Context ctx)
    {

        BasicOperation<Folder> folderRepository = new DefaultFolderRepository(ctx);

        String[] columns={FOLDER_COLUMN_NAME};
        int[] ids={android.R.id.text1};





//        String itemTextArr[] = {"Android", "iOS", "Java", "JavaScript", "JDBC", "JSP", "Linux", "Python", "Servlet", "Windows"};
        List<Folder> folders = folderRepository.getAll();

        List<ListViewItemDTO> ret = new ArrayList<ListViewItemDTO>();

//        int length = itemTextArr.length;


        for(Folder folder : folders)
        {
            ListViewItemDTO dto = new ListViewItemDTO();
            dto.setChecked(false);
            dto.setItemText(folder.asString());
            dto.setId(folder.getId());

            ret.add(dto);
        }

        return ret;
    }

}
