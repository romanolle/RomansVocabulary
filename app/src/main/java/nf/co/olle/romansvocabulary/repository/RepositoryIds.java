package nf.co.olle.romansvocabulary.repository;

public abstract class RepositoryIds {

	/**
	 * Nazev tabulky
	 */
	public static final String FOLDER_TABLE_NAME="folder";

	/**
	 * Nazev sloupce id
	 */
	public static final String FOLDER_FOLDER_COLUMN_ID="_id";

	/**
	 * Nazev sloupce name
	 * Nazev slozky
	 */
	public static final String FOLDER_COLUMN_NAME="name";

	/**
	 * Nazev sloupce lang1
	 * Slouzi pro vlozeni nazvu materskeho jazyka
	 */
	public static final String FOLDER_COLUMN_LANG_1="lang1";

	/**
	 * Nazev sloupce lang2
	 * Slouzi pro vlozeni nazvu ciziho jazyka
	 */
	public static final String FOLDER_COLUMN_LANG_2="lang2";
	
	/**
	 * Seznam vsech sloupcu pro vyber z DB
	 */
	public static final String[] FOLDER_COLUMNS = {FOLDER_FOLDER_COLUMN_ID, FOLDER_COLUMN_NAME, FOLDER_COLUMN_LANG_1, FOLDER_COLUMN_LANG_2};
	
	/**
	 * Defaultni serazeni  podle ID DESC
	 */
	public static final String FOLDER_ORDER_BY=FOLDER_FOLDER_COLUMN_ID+" DESC";
	

	/**
	 * Nazev tabulky
	 */
	public static final String WORD_TABLE_NAME="word";

	/**
	 * Nazev sloupce id
	 */
	public static final String WORD_COLUMN_ID="_id";

	/**
	 * Nazev sloupce pronunciation
	 * Vyslovnost
	 */
	public static final String WORD_COLUMN_PRONUNCIATION="pronunciation";

	/**
	 * Nazev sloupce lang1
	 * Slovicko v materskem jazyce
	 */
	public static final String WORD_COLUMN_LANG_1="lang1";

	/**
	 * Nazev sloupce lang2
	 * Slovicko v cizim jazyce
	 */
	public static final String WORD_COLUMN_LANG_2="lang2";

	/**
	 * Nazev sloupce info
	 * Doplnujici informace o slovicku
	 */
	public static final String WORD_COLUMN_INFO="info";

	/**
	 * Nazev sloupce folder
	 * Id slozky ve kterem je slovicko ulozeno
	 */
	public static final String WORD_COLUMN_FOLDER="folder";

	/**
	 * Nazev sloupce is_known
	 * Znazornuje, jestli uzivatel dane slovicko zna nebo nezna
	 */
	public static final String WORD_COLUMN_IS_KNOWN="is_known";

	/**
	 * Seznam vsech sloupcu pro vyber z DB
	 */
	public static final String[] WORD_COLUMNS = {WORD_COLUMN_ID, WORD_COLUMN_LANG_1, WORD_COLUMN_LANG_2, WORD_COLUMN_PRONUNCIATION, WORD_COLUMN_INFO, WORD_COLUMN_IS_KNOWN, WORD_COLUMN_FOLDER};
	
	/**
	 * Defaultni serazeni  podle ID DESC
	 */
	public static final String WORD_ORDER_BY=WORD_COLUMN_ID+" DESC";
	
	/**
	 * Serazeni  podle slovicek v materskem jazyce
	 */
	public static final String WORD_ORDER_BY_LANG1=WORD_COLUMN_LANG_1;
	
	/**
	 * Serazeni  podle slovicek v cizim jazyce
	 */
	public static final String WORD_ORDER_BY_LANG2=WORD_COLUMN_LANG_2;
}
