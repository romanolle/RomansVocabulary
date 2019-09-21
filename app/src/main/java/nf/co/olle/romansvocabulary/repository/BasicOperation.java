package nf.co.olle.romansvocabulary.repository;

import android.database.Cursor;

import java.util.List;

public interface BasicOperation<MODEL> {
	/**
	 * Vraci max jeden zaznam z tabulky podle indexu
	 * @param index Id zaznamu
	 * @return Cursor s max jednim zaznamem 
	 */
	abstract MODEL getOne(long index);

	/**
	 * Vlozi jeden zaznam do tabulky
	 * @param model Hodnoty noveho radku, ktere se maji vlozit do tabulky
	 * @return Identifikator vlozeneho zaznamu | -1 pro neuspesne ulozeni
	 */
	abstract long insert(MODEL model);
	
	/**
	 * Smaze max jeden radek z tabulky podle ID
	 * @param index Id zaznamu
	 * @return True=radek byl uspesne smazan | False=nepodarilo se nic smazat
	 */
	abstract boolean delete(long index);
	
	/**
	 * Vraci vsechny zaznamy z tabulky
	 * @return Cursor se vsemi radky tabulky
	 */
	abstract List<MODEL> getAll();
	
	abstract Cursor getAllAsCursor();
}
