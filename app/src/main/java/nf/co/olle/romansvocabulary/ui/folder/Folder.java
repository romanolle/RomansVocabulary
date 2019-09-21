package nf.co.olle.romansvocabulary.ui.folder;

public class Folder {
	private final int id;
	private final String name;
	private final String lang1;
	private final String lang2;
	
	public Folder(String name, String lang1, String lang2) {
		this(-1, name, lang1, lang2);
	}
	public Folder(int id, String name, String lang1, String lang2) {
		this.id = id;
		this.name = name;
		this.lang1 = lang1;
		this.lang2 = lang2;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getLang1() {
		return lang1;
	}
	public String getLang2() {
		return lang2;
	}

	public String asString() {
		return name + "(" + lang1 + " -> " + lang2 + ")";
	}

	@Override
	public String toString() {
		return "Folder [id=" + id + ", name=" + name + ", lang1=" + lang1 + ", lang2=" + lang2 + "]";
	}
	
}
