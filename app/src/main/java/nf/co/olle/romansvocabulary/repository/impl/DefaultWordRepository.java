package nf.co.olle.romansvocabulary.repository.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nf.co.olle.romansvocabulary.ui.word.Word;
import nf.co.olle.romansvocabulary.repository.WordRepository;

import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_FOLDER_COLUMN_ID;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.FOLDER_TABLE_NAME;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMNS;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_FOLDER;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_ID;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_INFO;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_IS_KNOWN;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_LANG_1;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_LANG_2;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_COLUMN_PRONUNCIATION;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_ORDER_BY;
import static nf.co.olle.romansvocabulary.repository.RepositoryIds.WORD_TABLE_NAME;

public class DefaultWordRepository extends AbstractRepository<Word> implements WordRepository<Word> {
	
	public static final String CREATE_TABLE="create table "+WORD_TABLE_NAME+"("
			+WORD_COLUMN_ID+" INTEGER PRIMARY KEY,"
			+WORD_COLUMN_LANG_1+" VARCHAR(80) NOT NULL,"
			+WORD_COLUMN_LANG_2+" VARCHAR(80) NOT NULL,"
			+WORD_COLUMN_PRONUNCIATION+" VARCHAR(80),"
			+WORD_COLUMN_INFO+" TEXT,"
			+WORD_COLUMN_IS_KNOWN+" TINYINT(1) DEFAULT 0,"
			+WORD_COLUMN_FOLDER+" INTEGER NOT NULL,"
			+"FOREIGN KEY("+WORD_COLUMN_FOLDER+") REFERENCES "+FOLDER_TABLE_NAME+"("+FOLDER_FOLDER_COLUMN_ID+")"
			+");";
	
	public static final String CREATE_TRIGGER="CREATE TRIGGER delete_words_with_folder BEFORE DELETE ON folder "
		       +  "FOR EACH ROW BEGIN"
		       +         " DELETE FROM word WHERE folder = OLD._id;"
		       +  " END;";

	public static final String DROP_TRIGGER="DROP TRIGGER IF EXISTS delete_words_with_folder";
	public static final String DROP_TABLE="DROP TABLE IF EXISTS "+WORD_TABLE_NAME;
	
	private static DefaultWordRepository INSTANCE = null;
	
	public static DefaultWordRepository getInstance(Context context) {
		if(INSTANCE == null) {
			INSTANCE = new DefaultWordRepository(context);
		}
		return INSTANCE;
	}
	
	/**
	 * Slouzi pro pripojeni k DB a vytvoreni/upravu tabulky
	 * @author Roman
	 *
	 */
	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);

			db.execSQL(CREATE_TRIGGER);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.e("ERROR3", "aaa");
			db.execSQL(DROP_TABLE);
			db.execSQL(DROP_TRIGGER);
			onCreate(db);
		}
	}
	
	private DefaultWordRepository(Context context) {
		super(new DatabaseHelper(context));
	}

	@Override
	public long insert(Word model) {
		SQLiteDatabase db=openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(WORD_COLUMN_LANG_1, model.getLang1());
		values.put(WORD_COLUMN_LANG_2, model.getLang2());
		values.put(WORD_COLUMN_PRONUNCIATION, model.getPronunciation());
		values.put(WORD_COLUMN_INFO, model.getInfo());
		
		long id = 0;
		//insert = does not have id
		if(model.getId() == null || model.getId() < 0) {
			values.put(WORD_COLUMN_FOLDER, String.valueOf(model.getFolderId()));
			id=db.insert(WORD_TABLE_NAME, null, values);
		} else {
			id=db.update(WORD_TABLE_NAME, values, WORD_COLUMN_ID+"=?", new String[]{String.valueOf(model.getId())});
		}
		db.close();
		return id;
	}

	protected List<Word> transformCursorToList(Cursor cursor) {
		List<Word> words = new ArrayList<Word>();
		
		int idIndex=cursor.getColumnIndex(WORD_COLUMN_ID);
		int lang1Index=cursor.getColumnIndex(WORD_COLUMN_LANG_1);
		int lang2Index=cursor.getColumnIndex(WORD_COLUMN_LANG_2);
		int folderIdIndex=cursor.getColumnIndex(WORD_COLUMN_FOLDER);
		int pronunciationIndex=cursor.getColumnIndex(WORD_COLUMN_PRONUNCIATION);
		int isKnownIndex=cursor.getColumnIndex(WORD_COLUMN_IS_KNOWN);
		int infoIndex=cursor.getColumnIndex(WORD_COLUMN_INFO);

		while(cursor.moveToNext()) {
			//konkretni plneni
			words.add(new Word(
					cursor.getInt(idIndex),  
					cursor.getString(lang1Index), 
					cursor.getString(lang2Index),
					cursor.getString(pronunciationIndex), 
					cursor.getString(infoIndex),
					cursor.getInt(isKnownIndex),
					cursor.getInt(folderIdIndex)
				)
			);
		}
		cursor.close();
		return words;
	}

	@Override
	String getTableName() {
		return WORD_TABLE_NAME;
	}

	@Override
	String[] getColumns() {
		return WORD_COLUMNS;
	}

	@Override
	String getOrderBy() {
		return WORD_ORDER_BY;
	}

	@Override
	String getColumnId() {
		return WORD_COLUMN_ID;
	}

	@Override
	public List<Word> getWordsInFolder(long folderId, String orderBy) {
		return transformCursorToList(getWordsInFolderAsCursor(folderId, orderBy));
	}

	@Override
	public Cursor getWordsInFolderAsCursor(long folderId, String orderBy) {
		SQLiteDatabase db=openHelper.getReadableDatabase();
		
		List<Word> l = transformCursorToList(db.query(WORD_TABLE_NAME, WORD_COLUMNS, null, null, null, null, orderBy));
		
		return db.query(WORD_TABLE_NAME, WORD_COLUMNS, WORD_COLUMN_FOLDER+"=?", new String[]{String.valueOf(folderId)}, null, null, orderBy);
	}

	@Override
	public List<Word> getWordsWhatDoNotKnow(String orderBy) {
		return transformCursorToList(getWordsWhatDoNotKnowAsCursor(orderBy));
	}

	@Override
	public Cursor getWordsWhatDoNotKnowAsCursor(String orderBy) {
		SQLiteDatabase db=openHelper.getReadableDatabase();
		//is_known=1
		return db.query(getTableName(), getColumns(), WORD_COLUMN_IS_KNOWN+"=?", new String[]{"1"}, null, null, orderBy);
	}

	/**
	* Nastaveni daneho slovicka jakoze ho uzivatel nezna = is_known=1
	* @param wordId ID slovicka
	*/
	@Override
	public void addToDontKnow(Long wordId) {
		SQLiteDatabase db=openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(WORD_COLUMN_IS_KNOWN, 1);
		db.update(getTableName(), values, getColumnId()+"=?", new String[]{String.valueOf(wordId)});
		db.close();
	}

	@Override
	public void removeFromDontKnow(Long wordId) {
		SQLiteDatabase db=openHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(WORD_COLUMN_IS_KNOWN, 0);
		db.update(getTableName(), values, getColumnId()+"=?", new String[]{String.valueOf(wordId)});
		db.close();
	}

	@Override
	public Word randomWord() {
		SQLiteDatabase db=openHelper.getReadableDatabase();
		List<Word> list = transformCursorToList(db.query(getTableName(), getColumns(), null, null, null, null, "RANDOM()","1"));
		db.close();
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	


}
