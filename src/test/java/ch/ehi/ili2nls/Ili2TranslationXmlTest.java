package ch.ehi.ili2nls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;

public class Ili2TranslationXmlTest {

	private static final String ILIFILENAME = "src/test/data/ili2translationxml/EnumOk.ili";

	//Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	@Test
	public void model() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		int modelAcount=0;
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA")) {
					assertEquals("EnumOkA", ele.getName_de());
					assertEquals("EnumOkB", ele.getName_fr());
					assertEquals("Das ist Dokumentation zum Modell in DE", ele.getDocumentation_de());
					assertEquals("Das ist Dokumentation zum Modell in FR", ele.getDocumentation_fr());
					modelAcount++;
				}
			}
		}
		assertEquals(1,modelAcount);
	}
	@Test
	public void model2() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkBasis")) {
					assertEquals("EnumOkBasis", ele.getName_de());
					return;
				}
			}
		}
		fail("MODEL EnumOkBasis can not be found!");
	}
	
	//Es ueberprueft, ob die Topic korrekt in das ili file geschrieben wurde.
	@Test
	public void topic() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA")) {
					assertEquals("TopicA", ele.getName_de());
					assertEquals("TopicB", ele.getName_fr());
					return;
				}
			}
		}
		fail("TOPIC can not be found!");
	}
	
	//Es ueberprueft, ob die MetaAttribute korrekt in das ili file geschrieben wurde.
	@Test
	public void topicMetaAttribute() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.METAOBJECT.dispName")) {
					assertEquals("Topic A", ele.getName_de());
					assertEquals("Topic B", ele.getName_fr());
					return;
				}
			}
		}
		fail("MetaAttribute can not be found!");
	}
	
	//Es ueberprueft, ob die Class korrekt in das ili file geschrieben wurde.
	@Test
	public void classTest() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA")) {
					assertEquals("ClassA", ele.getName_de());
					assertEquals("ClassB", ele.getName_fr());
					return;
				}
			}
		}
		fail("CLASS can not be found!");
	}
	
	//Es ueberprueft, ob die Attributes korrekt in das ili file geschrieben wurde.
	@Test
	public void attribute() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA")) {
					assertEquals("attrA", ele.getName_de());
					assertEquals("attrB", ele.getName_fr());
					assertEquals("Das ist Dokumentation in DE", ele.getDocumentation_de());
					assertEquals("Das ist Dokumentation in FR", ele.getDocumentation_fr());
					return;
				}
			}
		}
		fail("ATTRIBUTES can not be found!");
	}
	
	//Es ueberprueft, ob die Enumerationsdocu korrekt in das ili file geschrieben wurde.
	@Test
	public void enumeration() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2")) {
					assertEquals("a2", ele.getName_de());
					assertEquals("b2", ele.getName_fr());
					assertEquals("enum docu zu a2", ele.getDocumentation_de());
					assertEquals("enum docu zu b2", ele.getDocumentation_fr());
					return;
				}
			}
		}
		fail("Element can not be found!");
	}
	
	//Es ueberprueft, ob die SubEnumerations korrekt in das ili file geschrieben wurde.
	@Test
	public void enumerationSubEnum() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2.a21")) {
					assertEquals("a21", ele.getName_de());
					assertEquals("b21", ele.getName_fr());
					return;
				}
			}
		}
		fail("Subelements of Enumeration can not be found!");
	}
	
	//Es ueberprueft, ob die MetaAttributes korrekt in das ili file geschrieben wurde.
	@Test
	public void enumerationElementMetaAttribute() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.attrA.a2.METAOBJECT.dispName")) {
					assertEquals("a 2", ele.getName_de());
					assertEquals("b 2", ele.getName_fr());
					return;
				}
			}
		}
		fail("Enumeration element of Meta Attributes can not be found!");
	}
	
	//Es ueberprueft, ob die Association korrekt in das ili file geschrieben wurde.
	@Test
	public void association() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.roleA1roleA2")) {
					assertEquals("roleA1roleA2", ele.getName_de());
					assertEquals("roleB1roleB2", ele.getName_fr());
					return;
				}
			}
		}
		fail("ASSOCIATION can not be found!");
	}
	
	//Es ueberprueft, ob die Rolle korrekt in das ili file geschrieben wurde.
	@Test
	public void role() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.roleA1roleA2.roleA1")) {
					assertEquals("roleA1", ele.getName_de());
					assertEquals("roleB1", ele.getName_fr());
					return;
				}
			}
		}
		fail("ROLLE can not be found!");
	}
		
	//Es ueberprueft, ob die Constraint korrekt in das ili file geschrieben wurde.
	@Test
	public void constraintWithoutExplicitName() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.Constraint1")) {
					assertEquals("Constraint1", ele.getName_de());
					assertEquals("Constraint1", ele.getName_fr());
					return;
				}
			}
		}
		fail("Constraint without explicit name can not be found!");
	}
	
	//Es ueberprueft, ob die Constraint korrekt in das ili file geschrieben wurde.
	@Test
	public void constraintWithExplicitName() {
		File file = new File(ILIFILENAME);
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		ModelElements eles = dateiLesen.readAllModels(file);
		for (TranslationElement ele : eles) {
			if (ele.getScopedName() != null) {
				if (ele.getScopedName().equals("EnumOkA.TopicA.ClassA.UniqueConstraintA")) {
					assertEquals("UniqueConstraintA", ele.getName_de());
					assertEquals("UniqueConstraintB", ele.getName_fr());
					return;
				}
			}
		}
		fail("Constraint with explicit name can not be found!");
	}
	
}
