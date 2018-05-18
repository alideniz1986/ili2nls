package ch.ehi.ili2nls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;

public class Ili2TranslationXmlTest {

	private static final String ILIFILENAME = "src/test/data/ili2translationxml/EnumOk.ili";
	
	//Es ueberprueft, ob die ModelDocumentation korrekt in das ili file geschrieben wurde.
	@Test
	public void modelDocWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA")) {
					assertEquals("Das ist Dokumentation zum Modell in DE", ele.getDocumentation_de());
					return;
				}
			}
		}
		fail("Doc DE in the Model can not be found!");
	}
	@Test
	public void modelDocWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA")) {
					assertEquals("Das ist Dokumentation zum Modell in FR", ele.getDocumentation_fr());
					return;
				}
			}
		}
		fail("Doc FR in the Model can not be found!");
	}
	
	//Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	@Test
	public void modelWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA")) {
					assertEquals("EnumOkA", ele.getName_de());
					return;
				}
			}
		}
		fail("In Language DE in the MODEL can not be found!");
	}
	
	//Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	@Test
	public void modelWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA")) {
					assertEquals("EnumOkB", ele.getName_fr());
					return;
				}
			}
		}
		fail("In Language FR in the MODEL can not be found!");
	}
	
	//Es ueberprueft, ob die TopicA korrekt in das ili file geschrieben wurde.
	@Test
	public void topicWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA")) {
					assertEquals("TopicA", ele.getName_de());
					return;
				}
			}
		}
		fail("In Language DE the TOPIC can not be found!");
	}
	
	//Es ueberprueft, ob die TopicB korrekt in das ili file geschrieben wurde.
	@Test
	public void topicWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA")) {
					assertEquals("TopicB", ele.getName_fr());
					return;
				}
			}
		}
		fail("Language FR in the TOPIC can not be found!");
	}
	
	//Es ueberprueft, ob die ClassA korrekt in das ili file geschrieben wurde.
	@Test
	public void classWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA")) {
					assertEquals("ClassA", ele.getName_de());
					return;
				}
			}
		}
		fail("Language DE in the CLASS can not be found!");
	}
	
	//Es ueberprueft, ob die ClassB korrekt in das ili file geschrieben wurde.
	@Test
	public void classWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA")) {
					assertEquals("ClassB", ele.getName_fr());
					return;
				}
			}
		}
		fail("In Language FR the CLASS can not be found!");
	}
	
	//Es ueberprueft, ob die Attribute korrekt in das ili file geschrieben wurde.
	@Test
	public void attributeWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA")) {
					assertEquals("attrA", ele.getName_de());
					return;
				}
			}
		}
		fail("Language DE in the ATTRIBUTE can not be found!");
	}
	
	//Es ueberprueft, ob die Attribute korrekt in das ili file geschrieben wurde.
	@Test
	public void attributeWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA")) {
					assertEquals("attrB", ele.getName_fr());
					return;
				}
			}
		}
		fail("Language FR in the ATTRIBUTE can not be found!");
	}
	
	@Test
	public void attributeDocWithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA")) {
					assertEquals("Das ist Dokumentation in DE", ele.getDocumentation_de());
					return;
				}
			}
		}
		fail("Language DE in the ATTRIBUTE documentation can not be found!");
	}
	//Es ueberprueft, ob die Attribute Document korrekt in das ili file geschrieben wurde.
	@Test
	public void attributeDocWithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA")) {
					assertEquals("Das ist Dokumentation in FR", ele.getDocumentation_fr());
					return;
				}
			}
		}
		fail("Language FR in the ATTRIBUTE documentation can not be found!");
	}
	
	
	
	//Es ueberprueft, ob die EnumerationB2 korrekt in das ili file geschrieben wurde.
	@Test
	public void enumerationELementDocB2WithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2")) {
					assertEquals("enum docu zu a2", ele.getDocumentation_de());
					return;
				}
			}
		}
		fail("Language DE in the ELEMENT dokumentation can not be found!");
	}
	@Test
	public void enumerationELementDocB2WithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2")) {
					assertEquals("enum docu zu b2", ele.getDocumentation_fr());
					return;
				}
			}
		}
		fail("Language FR in the ELEMENT dokumentation can not be found!");
	}
	
	//Es ueberprueft, ob die SubEnumerationA21 korrekt in das ili file geschrieben wurde.
	@Test
	public void enumerationSubEnumB21WithLanguageDE() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2.a21")) {
					assertEquals("a21", ele.getName_de());
					return;
				}
			}
		}
		fail("Language DE in the SUBELEMENT can not be found!");
	}
	
	//Es ueberprueft, ob die SubEnumerationB21 korrekt in das ili file geschrieben wurde.
	@Test
	public void enumerationSubEnumB21WithLanguageFR() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2.a21")) {
					assertEquals("b21", ele.getName_fr());
					return;
				}
			}
		}
		fail("Language FR in the SUBELEMENT can not be found!");
	}
	
}
