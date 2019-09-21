package nf.co.olle.romansvocabulary.repository.configuration;


import android.database.sqlite.SQLiteOpenHelper;

/**
 * Abstraktni trida s povinnymi obecnymi udaji pro pripojeni pripojeni. 
 * Obsahuje uzavreni spojeni s DB. 
 * Vyzaduje po potomcich vytvoreni potrebnych trid.
 * @author Roman Olle
 *
 */
public abstract class DatabaseConnection {
	/**
	 * Nazev DB
	 */
	protected static final String DATABASE_NAME="romansVocabulary";
	
	/**
	 * Aktualni verze DB
	 */
	protected static final int DATABASE_VERSION=12;
	
	protected final SQLiteOpenHelper openHelper;

	public DatabaseConnection(SQLiteOpenHelper openHelper) {
		super();
		this.openHelper = openHelper;
	}

	/**
	 * Uzavre spojeni s DB
	 */
	public void close()
	{
		openHelper.close();
	}
}
