package nf.co.olle.romansvocabulary.ui.word;

import static nf.co.olle.romansvocabulary.ui.Ids.WAY_FROM_NATIVE;

public class Word {
	public final Long id;
	public final String lang1;
	public final String lang2;
	public final String pronunciation;
	public final String info;
	public int isKnown;
	public long folderId;

	public Word(String text) {
		this(-1, text);
	}
	public Word(long id, String text) {
		this(id, text, null, null, null, 0, -1);
	}
	public Word(String lang1, String lang2, String pronunciation, String info, int isKnown, long folderId) {
		this(-1, lang1, lang2, pronunciation, info, isKnown, folderId);
	}	
	public Word(long id, String lang1, String lang2, String pronunciation, String info, int isKnown, long folderId) {
		this.id = id;
		this.lang1 = lang1;
		this.lang2 = lang2;
		this.pronunciation = pronunciation;
		this.info = info;
		this.isKnown = isKnown;
		this.folderId = folderId;
	}
	public int getIsKnown() {
		return isKnown;
	}
	public void setIsKnown(int isKnown) {
		this.isKnown = isKnown;
	}
	public Long getId() {
		return id;
	}
	public String getLang1() {
		return lang1;
	}
	public String getLang2() {
		return lang2;
	}
	public String getPronunciation() {
		return pronunciation;
	}
	public String getInfo() {
		return info;
	}
	public long getFolderId() {
		return folderId;
	}
	public String fromWay(long way) {
		if(way == WAY_FROM_NATIVE) {
			return getLang1() + " -> " + getLang2() + " [" + getPronunciation() + "]";
		}
		return getLang2() + " [" + getPronunciation() + "] -> " + getLang1();
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}

	@Override
	public String toString() {
		return "Word [id=" + id + ", lang1=" + lang1 + ", lang2=" + lang2 + ", pronunciation=" + pronunciation
				+ ", info=" + info + ", isKnown=" + isKnown + ", folderId=" + folderId + "]";
	}
	
	
	
}
