package nf.co.olle.romansvocabulary.repository;

import android.database.Cursor;

import java.util.List;

public interface WordRepository<MODEL> extends BasicOperation<MODEL> {
	
	abstract List<MODEL> getWordsInFolder(long folderId, String orderBy);
	
	abstract Cursor getWordsInFolderAsCursor(long folderId, String orderBy);

	abstract List<MODEL> getWordsWhatDoNotKnow(String orderBy);

	abstract Cursor getWordsWhatDoNotKnowAsCursor(String orderBy);

	abstract void addToDontKnow(Long wordId);

	abstract void removeFromDontKnow(Long wordId);

	abstract MODEL randomWord();
}
