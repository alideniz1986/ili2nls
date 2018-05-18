package ch.ehi.ili2nls;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={
"scopedName",
"elementType",
"name_de",
"name_fr",
"name_it",
"name_rm",
"name_en",
"documentation_de",
"documentation_fr",
"documentation_it",
"documentation_rm",
"documentation_en"
})
public class TranslationElement {
	private String scopedName;
	private String elementType;
	private String name_de;
	private String name_fr;
	private String name_it;
	private String name_rm;
	private String name_en;
	private String documentation_de;
	private String documentation_fr;
	private String documentation_it;
	private String documentation_rm;
	private String documentation_en;

	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public String getScopedName() {
		return scopedName;
	}
	public void setScopedName(String scopedName) {
		this.scopedName = scopedName;
	}
	public String getName_de() {
		return name_de;
	}
	public void setName_de(String name_de) {
		this.name_de = name_de;
	}
	public String getName_fr() {
		return name_fr;
	}
	public void setName_fr(String name_fr) {
		this.name_fr = name_fr;
	}
	public String getName_it() {
		return name_it;
	}
	public void setName_it(String name_it) {
		this.name_it = name_it;
	}
	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	public String getDocumentation_de() {
		return documentation_de;
	}
	public void setDocumentation_de(String documentation_de) {
		this.documentation_de = documentation_de;
	}
	public String getDocumentation_fr() {
		return documentation_fr;
	}
	public void setDocumentation_fr(String documentation_fr) {
		this.documentation_fr = documentation_fr;
	}
	public String getDocumentation_it() {
		return documentation_it;
	}
	public void setDocumentation_it(String documentation_it) {
		this.documentation_it = documentation_it;
	}
	public String getDocumentation_en() {
		return documentation_en;
	}
	public void setDocumentation_en(String documentation_en) {
		this.documentation_en = documentation_en;
	}
	public String getName_rm() {
		return name_rm;
	}
	public void setName_rm(String name_rm) {
		this.name_rm = name_rm;
	}
	public String getDocumentation_rm() {
		return documentation_rm;
	}
	public void setDocumentation_rm(String documentation_rm) {
		this.documentation_rm = documentation_rm;
	}	
	
}
