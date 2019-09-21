package nf.co.olle.romansvocabulary.repository.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import nf.co.olle.romansvocabulary.repository.BasicOperation;
import nf.co.olle.romansvocabulary.repository.configuration.DatabaseConnection;

public abstract class AbstractRepository<MODEL> extends DatabaseConnection implements BasicOperation<MODEL> {

	public AbstractRepository(SQLiteOpenHelper openHelper) {
		super(openHelper);
	}

	@Override
	public MODEL getOne(long index) {
		SQLiteDatabase db=openHelper.getReadableDatabase();

		List<MODEL> folders = transformCursorToList(db.query(getTableName(), getColumns(), getColumnId()+"=?", new String[]{String.valueOf(index)}, null, null, null));
		db.close();
		return folders.size() < 1 ? null : folders.get(0);
	}

	@Override
	public boolean delete(long index) {
		SQLiteDatabase db=openHelper.getWritableDatabase();
		int deleteCount=db.delete(getTableName(), getColumnId()+"=?", new String[]{String.valueOf(index)});
		db.close();
		if(deleteCount>0)
			return true;
		return false;
	}

	@Override
	public List<MODEL> getAll() {
		return transformCursorToList(getAllAsCursor());
	}

	abstract List<MODEL> transformCursorToList(Cursor cursor);

	@Override
	public Cursor getAllAsCursor() {
		SQLiteDatabase db=openHelper.getReadableDatabase();
		Cursor cursor = db.query(getTableName(), getColumns(), null, null, null, null, getOrderBy());
		//db.close();
		return cursor;
	}

	abstract String getTableName();
	abstract String[] getColumns();
	abstract String getOrderBy();
	abstract String getColumnId();


}
