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

import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.DataModel;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.MandatoryConstraint;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.UniquenessConstraint;
import static ch.ehi.ili2nls.Consts.*;

public class Interlis2GeneratorTest {

	/**
	 * DE -> Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Model was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Model korrekt in das ili file geschrieben wurde.
	@Test
	public void model() throws Exception {
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

	/**
	 * DE -> Es ueberprueft, ob die Topic korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Topic was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die TopicB korrekt in das ili file geschrieben wurde.
	@Test
	public void topic() throws Exception {
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

		// testen, dass im neuen ili fuer das Topic der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB");
		assertNotNull(modelEle);
		assertEquals(Topic.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die Class korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Class was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die ClassB korrekt in das ili file geschrieben wurde.
	@Test
	public void classTest() throws Exception {
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

		// testen, dass im neuen ili fuer das Class der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB");
		assertNotNull(modelEle);
		assertEquals(Table.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die Attribute korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Attribute was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Attribute korrekt in das ili file geschrieben wurde.
	@Test
	public void attribute() throws Exception {
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

		// testen, dass im neuen ili fuer das Attribute der name_fr aus dem xml steht
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB.attrB");
		assertNotNull(modelEle);
		assertEquals(LocalAttribute.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA.attrA", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die SubEnumerationB21 korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the SubEnumerationB21 was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die SubEnumerationB21 korrekt in das ili file geschrieben wurde.
	@Test
	public void enumElementB21() throws Exception {
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
	/**
	 * DE -> Es ueberprueft, ob die TopicMetaAttribute korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the TopicMetaAttribute was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die TopicMetaAttribute korrekt in das ili file geschrieben wurde.
	@Test
	public void topicMetaAttribute() throws Exception {
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
		assertTrue(hasMetaElement(modelEle,"Topic B", "dispName"));		
		assertEquals(Topic.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("Topic A", modelEle.getTranslationOf().getMetaValues().getValue("dispName"));
	}
	/**
	 * DE -> Es ueberprueft, ob die Constraint ohne expliziten Namen korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Constraint without Explicit Name was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Constraint korrekt in das ili file geschrieben wurde.
	@Test
	public void constraintWithoutExplicitName() throws Exception {
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
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB.Constraint1");
		assertNotNull(modelEle);
		assertEquals(MandatoryConstraint.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA.Constraint1", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die Constraint mit expliziten Namen korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Constraint with explicit name was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Constraint korrekt in das ili file geschrieben wurde.
	@Test
	public void constraintWithExplicitName() throws Exception {
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
		Element modelEle = newTd.getElement("EnumOkB.TopicB.ClassB.UniqueConstraintB");
		assertNotNull(modelEle);
		assertEquals(UniquenessConstraint.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.ClassA.UniqueConstraintA", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die Role korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Role was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Rolle korrekt in das ili file geschrieben wurde.
	@Test
	public void role() throws Exception {
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
		Element modelEle = newTd.getElement("EnumOkB.TopicB.roleB1roleB2.roleB1");
		assertNotNull(modelEle);
		assertEquals(RoleDef.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.roleA1roleA2.roleA1", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die Association korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the Association was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Association korrekt in das ili file geschrieben wurde.
	@Test
	public void association() throws Exception {
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
		Element modelEle = newTd.getElement("EnumOkB.TopicB.roleB1roleB2");
		assertNotNull(modelEle);
		assertEquals(AssociationDef.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.roleA1roleA2", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die ConstraintInAssociation korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the ConstraintInAssociation was written correctly in the ili file.
	 * */
	@Test
	public void constraintInAssociation() throws Exception {
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
		Element modelEle = newTd.getElement("EnumOkB.TopicB.roleB1roleB2.Constraint1");
		assertNotNull(modelEle);
		assertEquals(MandatoryConstraint.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertEquals("EnumOkA.TopicA.roleA1roleA2.Constraint1", modelEle.getTranslationOf().getScopedName());
	}
	/**
	 * DE -> Es ueberprueft, ob die EnumerationELement MetaAttribute korrekt in das ili file geschrieben wurde.
	 * EN -> It checks if the EnumerationELement MetaAttribute was written correctly in the ili file.
	 * */
	// Es ueberprueft, ob die Association korrekt in das ili file geschrieben wurde.
	@Test
	public void enumElementMetaAttribute() throws Exception {
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
		assertTrue(getMetaValueFromEnumerationElement(modelEle, "b 2", "b2"));
		assertEquals(LocalAttribute.class, modelEle.getClass());
		assertEquals(FR, Ili2TranslationXml.getLanguage(modelEle));
		assertNotNull(modelEle.getTranslationOf());
		assertTrue(getMetaValueFromEnumerationElement(modelEle.getTranslationOf(), "a 2", "a2"));
	}
	
	/**
	 * Checking if MetaAttribute is in EnumerationElement
	 * 
	 * @param ModelEle - Related ModelEle
	 * @param ExpectedMetaValue - expected Meta Value
	 * @param wantedEnumerationElement - Enumeration Element
	 * */
	private boolean getMetaValueFromEnumerationElement(Element modelEle, String expectedMetaValue, String wantedEnumerationElement) {
		if (modelEle instanceof AttributeDef) {
			AttributeDef attr = (AttributeDef) modelEle;
			if (attr.getDomain() instanceof EnumerationType) {
				EnumerationType enumType = (EnumerationType) attr.getDomain();
				Enumeration enumeration = enumType.getEnumeration();
				Iterator<ch.interlis.ili2c.metamodel.Enumeration.Element> enumarationIterator = enumeration.getElements();
				
				while (enumarationIterator.hasNext()) {
					Enumeration.Element enumEle = enumarationIterator.next();
					if (enumEle.getName().equals(wantedEnumerationElement) && enumEle.getMetaValues().getValue("dispName") != null) {
						if (enumEle.getMetaValues().getValue("dispName").equals(expectedMetaValue)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether metaAttName and expectedMetaValue are the same.
	 * 
	 * @param Element - Related Model Element
	 * @param ExpectedMetaValue - expected Meta Value
	 * @param MetaAttributeName - Meta Attribute Name
	 * */
	private boolean hasMetaElement(Element modelEle, String expectedMetaValue, String metaAttName) {
		if (modelEle.getMetaValues().getValue(metaAttName).equals(expectedMetaValue)) {
			return true;
		}
		return false;
	}
	
	/**
	 * if ModelEle has a Enum Element then it check to same between enum Element Name and Enumeration Type 
	 * 
	 * @param Element - Related Model Element
	 * @param EnumElementName - expected Element Name
	 * */
	private boolean hasEnumElement(Element modelEle, String enumElementName) {
		if (modelEle instanceof Domain) {
			Domain domain = (Domain) modelEle;
			if (domain.getType() instanceof EnumerationType) {
				return hasEnumElement((EnumerationType) domain.getType(), enumElementName);
			}
		} else if (modelEle instanceof AttributeDef) {
			AttributeDef attr = (AttributeDef) modelEle;
			if (attr.getDomain() instanceof EnumerationType) {
				return hasEnumElement((EnumerationType) attr.getDomain(), enumElementName);
			}
		}
		return false;
	}
	
	/**
	 * check if EnumElement contains a EnumElementName.
	 * 
	 * @param enumElementName - expected Element Name
	 * @param EnumerationType - Related Enumeration Type
	 * */
	private boolean hasEnumElement(EnumerationType et, String enumElementName) {
		Enumeration enumeration = et.getEnumeration();
		ArrayList<String> elements=new ArrayList<String>();
		EnumerationType.buildEnumList(elements, NULL, enumeration);
		if (elements.contains(enumElementName) == true) {
			return true;
		}
		return false;
	}


}