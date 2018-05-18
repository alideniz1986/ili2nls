package ch.ehi.ili2nls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.DataModel;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;

public class Interlis2GeneratorTest {

	private static final String FR = "fr";
	private static final String FILEPATH = "src/test/data/interlis2generator/EnumOk_de.ili";
	private static final String NEWFILEPATH = "out.ili";

	// Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	@Test
	public void modelWithLanguageFR() throws Exception {
		// ili lesen
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(FILEPATH));
		// xml lesen
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(FILEPATH + ".xml"));
		// neues ili mit namen aus xml schreiben
		FileWriter out = new FileWriter(new File(NEWFILEPATH));
		new Interlis2Generator().generate(out, td, modelElements, FR, FILEPATH);
		out.close();
		// neues ili lesen
		TransferDescription newTd = Ili2TranslationXml.compileIliModel(new File(NEWFILEPATH), new File(FILEPATH));
		assertNotNull(newTd);

		// testen, dass im neuen ili fuer das model der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB");
		assertNotNull(modelEle);
		assertEquals(DataModel.class, modelEle.getClass());
		assertEquals(FR, ((DataModel) modelEle).getLanguage());
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA", modelEle.getTranslationOf().getScopedName());
	}

	// Es ueberprueft, ob die TopicB korrekt in das ili file geschrieben wurde.
	@Test
	public void topicWithLanguageFR() throws Exception {
		// ili lesen
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(FILEPATH));
		// xml lesen
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(FILEPATH + ".xml"));
		// neues ili mit namen aus xml schreiben
		FileWriter out = new FileWriter(new File(NEWFILEPATH));
		new Interlis2Generator().generate(out, td, modelElements, FR, FILEPATH);
		out.close();
		// neues ili lesen
		TransferDescription newTd = Ili2TranslationXml.compileIliModel(new File(NEWFILEPATH), new File(FILEPATH));
		assertNotNull(newTd);

		// testen, dass im neuen ili fuer das model der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB");
		assertNotNull(modelEle);
		assertEquals(Topic.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA", modelEle.getTranslationOf().getScopedName());
	}

	// Es ueberprueft, ob die ClassB korrekt in das ili file geschrieben wurde.
	@Test
	public void classWithLanguageFR() throws Exception {
		// ili lesen
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(FILEPATH));
		// xml lesen
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(FILEPATH + ".xml"));
		// neues ili mit namen aus xml schreiben
		FileWriter out = new FileWriter(new File(NEWFILEPATH));
		new Interlis2Generator().generate(out, td, modelElements, FR, FILEPATH);
		out.close();
		// neues ili lesen
		TransferDescription newTd = Ili2TranslationXml.compileIliModel(new File(NEWFILEPATH), new File(FILEPATH));
		assertNotNull(newTd);

		// testen, dass im neuen ili fuer das model der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB");
		assertNotNull(modelEle);
		assertEquals(Table.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA", modelEle.getTranslationOf().getScopedName());
	}

	// Es ueberprueft, ob die Attribute korrekt in das ili file geschrieben wurde.
	@Test
	public void attributeWithLanguageFR() throws Exception {
		// ili lesen
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(FILEPATH));
		// xml lesen
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(FILEPATH + ".xml"));
		// neues ili mit namen aus xml schreiben
		FileWriter out = new FileWriter(new File(NEWFILEPATH));
		new Interlis2Generator().generate(out, td, modelElements, FR, FILEPATH);
		out.close();
		// neues ili lesen
		TransferDescription newTd = Ili2TranslationXml.compileIliModel(new File(NEWFILEPATH), new File(FILEPATH));
		assertNotNull(newTd);

		// testen, dass im neuen ili fuer das model der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB.attrB");
		assertNotNull(modelEle);
		assertEquals(LocalAttribute.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA.attrA", modelEle.getTranslationOf().getScopedName());
	}

	// Es ueberprueft, ob die SubEnumerationB21 korrekt in das ili file geschrieben
	// wurde.
	@Test
	public void enumELementB21WithLanguageFR() throws Exception {
		// ili lesen
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(FILEPATH));
		// xml lesen
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(FILEPATH + ".xml"));
		// neues ili mit namen aus xml schreiben
		FileWriter out = new FileWriter(new File(NEWFILEPATH));
		new Interlis2Generator().generate(out, td, modelElements, FR, FILEPATH);
		out.close();
		// neues ili lesen
		TransferDescription newTd = Ili2TranslationXml.compileIliModel(new File(NEWFILEPATH), new File(FILEPATH));
		assertNotNull(newTd);

		// testen, dass im neuen ili fuer das enumElement der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB.attrB");
		assertEquals(LocalAttribute.class, modelEle.getClass());
		assertTrue(hasEnumElement(modelEle, "b2.b21"));
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertTrue(hasEnumElement(modelEle.getTranslationOf(), "a2.a21"));
	}

	private boolean hasEnumElement(Element modelEle, String enumElementName) {
		boolean isTrue = false;
		if (modelEle instanceof Domain) {
			Domain domain = (Domain) modelEle;
			if (domain.getType() instanceof EnumerationType) {
				isTrue = hasEnumElement((EnumerationType) domain.getType(), enumElementName);
			}
		} else if (modelEle instanceof AttributeDef) {
			AttributeDef attr = (AttributeDef) modelEle;
			if (attr.getDomain() instanceof EnumerationType) {
				isTrue = hasEnumElement((EnumerationType) attr.getDomain(), enumElementName);
			}
		}
		return isTrue;
	}

	private boolean hasEnumElement(EnumerationType et, String enumElementName) {
		Enumeration enumeration = et.getEnumeration();
		ArrayList<String> elements=new ArrayList<String>();
		EnumerationType.buildEnumList(elements, "", enumeration);
		return elements.contains(enumElementName);
	}


}