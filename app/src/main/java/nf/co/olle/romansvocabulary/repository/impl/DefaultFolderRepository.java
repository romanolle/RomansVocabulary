package nf.co.olle.romansvocabulary.repository.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nf.co.olle.romansvocabulary.ui.folder.Folder;

import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMNS;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_LANG_1;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_LANG_2;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_COLUMN_NAME;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_FOLDER_COLUMN_ID;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_ORDER_BY;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_TABLE_NAME;

public class DefaultFolderRepository extends AbstractRepository<Folder> {
	
	/**
	 * Slouzi pro pripojeni k DB a vytvoreni/upravu tabulky
	 * @author Roman
	 *
	 */
	static class DatabaseHelper extends SQLiteOpenHelper {

		Cursor backUp=null;
		
		DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table "+FOLDER_TABLE_NAME+"("
					+FOLDER_FOLDER_COLUMN_ID+" INTEGER PRIMARY KEY,"
					+FOLDER_COLUMN_NAME+" VARCHAR(80) NOT NULL,"
					+FOLDER_COLUMN_LANG_1+" VARCHAR(20) NOT NULL,"
					+FOLDER_COLUMN_LANG_2+" VARCHAR(20) NOT NULL"
					+");");
			if(backUp!=null){
				Log.e("not null", "");
			}
			db.execSQL(DefaultWordRepository.CREATE_TABLE);
			db.execSQL(DefaultWordRepository.CREATE_TRIGGER);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.e("ERROR3", "fff");

			db.execSQL("DROP TABLE IF EXISTS "+FOLDER_TABLE_NAME);
			db.execSQL(DefaultWordRepository.DROP_TABLE);
			db.execSQL(DefaultWordRepository.DROP_TRIGGER);
			onCreate(db);
		}
	}
	
	public DefaultFolderRepository(Context context) {
		super(new DatabaseHelper(context));
	}

	@Override
	public long insert(Folder model) {
		SQLiteDatabase db=openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(FOLDER_COLUMN_NAME, model.getName());
		values.put(FOLDER_COLUMN_LANG_1, model.getLang1());
		values.put(FOLDER_COLUMN_LANG_2, model.getLang2());
		long id=db.insert(FOLDER_TABLE_NAME, null, values);
		db.close();
		return id;
	}

	@Override 
	protected List<Folder> transformCursorToList(Cursor cursor) {
		List<Folder> folders = new ArrayList<Folder>();
		
		int nameIndex=cursor.getColumnIndex(FOLDER_COLUMN_NAME);
		int idIndex=cursor.getColumnIndex(FOLDER_FOLDER_COLUMN_ID);
		int lang1Index=cursor.getColumnIndex(FOLDER_COLUMN_LANG_1);
		int lang2Index=cursor.getColumnIndex(FOLDER_COLUMN_LANG_2);

		while(cursor.moveToNext()) {
			//konkretni plneni
			folders.add(new Folder(
					cursor.getInt(idIndex), 
					cursor.getString(nameIndex), 
					cursor.getString(lang1Index), 
					cursor.getString(lang2Index)
				)
			);
		}
		cursor.close();
		return folders;
	}

	@Override
	String getTableName() {
		return FOLDER_TABLE_NAME;
	}

	@Override
	String[] getColumns() {
		return FOLDER_COLUMNS;
	}

	@Override
	String getOrderBy() {
		return FOLDER_ORDER_BY;
	}

	@Override
	String getColumnId() {
		return FOLDER_FOLDER_COLUMN_ID;
	}

}
