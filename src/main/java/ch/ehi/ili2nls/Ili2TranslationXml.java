package ch.ehi.ili2nls;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Constraint;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.ExpressionSelection;
import ch.interlis.ili2c.metamodel.Function;
import ch.interlis.ili2c.metamodel.Graphic;
import ch.interlis.ili2c.metamodel.LineForm;
import ch.interlis.ili2c.metamodel.MetaDataUseDef;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.Parameter;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SignAttribute;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Unit;
import ch.interlis.ili2c.metamodel.View;

/**
 * Reads data from the ili file and creates a new XML document..
 *
 */

public class Ili2TranslationXml {

	private ModelElements elements = new ModelElements();

	public ModelElements readAllModel(TransferDescription td, File iliFile) {

		td = compileIliModel(iliFile);

		if (td == null) {
			System.out.println("No Records Found");
			return null;
		}

		printAllModels(td, iliFile.getName());
		return elements;
	}

	/**
	 * All models are read in this method
	 * 
	 * @param iliFile,
	 *            path of the source file
	 * @return elements, all collected elements are returned
	 */
	public ModelElements readAllModels(File iliFile) {

		TransferDescription td = compileIliModel(iliFile);

		if (td == null) {
			System.out.println("No Records Found");
			return null;
		}

		printAllModels(td, iliFile.getName());
		return elements;
	}

	/**
	 * with transferdescription we can get the ili data from the source file.
	 * 
	 * @param iliFile,
	 *            The path of the source file.
	 * @return TransferDescription, it include all configirations, settings and all
	 *         models.
	 */
	public static TransferDescription compileIliModel(File iliFile) {
		return compileIliModel(iliFile, null);
	}

	public static TransferDescription compileIliModel(File iliFile1, File iliFile2) {
		Configuration ili2cConfig = new ch.interlis.ili2c.config.Configuration();
		ili2cConfig.setAutoCompleteModelList(true);
		ili2cConfig.addFileEntry(new FileEntry(iliFile1.getAbsolutePath(), FileEntryKind.ILIMODELFILE));
		if (iliFile2 != null) {
			ili2cConfig.addFileEntry(new FileEntry(iliFile2.getAbsolutePath(), FileEntryKind.ILIMODELFILE));
		}

		Settings settings = new Settings();
		String currentDir = iliFile1.getAbsoluteFile().getParent();
		settings.setValue(ch.interlis.ili2c.gui.UserSettings.ILIDIRS, ch.interlis.ili2c.Main.DEFAULT_ILIDIRS);
		HashMap<String, String> pathMap = new HashMap<String, String>();
		pathMap.put(ch.interlis.ili2c.Main.ILI_DIR, currentDir);
		settings.setTransientObject(ch.interlis.ili2c.gui.UserSettings.ILIDIRS_PATHMAP, pathMap);
		TransferDescription td = null;
		td = ch.interlis.ili2c.Main.runCompiler(ili2cConfig, settings);
		return td;
	}

	private void printAllModels(TransferDescription td, String file) {
		Iterator<Model> modeli = td.iterator();
		boolean hasABaseLanguage = false;

		List<Model> list = new ArrayList<Model>();
		while (modeli.hasNext()) {
			list.add(0, modeli.next());
		}

		modeli = list.iterator();
		while (modeli.hasNext()) {
			Model model = modeli.next();

			String pathName = model.getFileName();
			if (pathName == null)
				continue;

			if (!pathName.contains(file)) {
				continue;
			}

			// Control of the another Language
			if (hasABaseLanguage == true) {
				continue;
			}

			hasABaseLanguage = hasABaseLanguage(model);
			TranslationElement text = new TranslationElement();
			text = allFieldsWithSetTOEmpty(text);
			text.setScopedName(getElementInRootLanguage(model).getScopedName());

			text.setElementType(getElementType(model));
			setModelElementAllLanguages(text, model);
			printModelElement(text);

			visitAllElements(model);
		}
	}

	private TranslationElement allFieldsWithSetTOEmpty(TranslationElement text) {
		text.setDocumentation_de("");
		text.setDocumentation_en("");
		text.setDocumentation_fr("");
		text.setDocumentation_it("");
		text.setDocumentation_rm("");

		text.setName_de("");
		text.setName_en("");
		text.setName_fr("");
		text.setName_it("");
		text.setName_rm("");

		text.setScopedName("");
		return text;
	}

	private String getElementType(Object model) {
		if(model instanceof Model) {
			return "MODEL";
		} else if (model instanceof AttributeDef) {
			return "ATTRIBUTE";
		} else if (model instanceof RoleDef) {
			return "ROLE";
		} else if (model instanceof Function) {
			return "FUNCTION";
		} else if (model instanceof Parameter) {
			return "PARAMETER";
		} else if (model instanceof Domain) {
			return "DOMAIN";
		} else if (model instanceof LineForm) {
			return "LINE FORM";
		} else if (model instanceof Unit) {
			return "UNIT";
		} else if (model instanceof Model) {
			return "MODEL";
		} else if (model instanceof Topic) {
			return "TOPIC";
		} else if (model instanceof MetaDataUseDef) {
			return "META DATA BASKET";
		} else if (model instanceof Table) {
			return "TABLE";
		} else if (model instanceof AssociationDef) {
			return "ASSOCIATION";
		} else if (model instanceof View) {
			return "VIEW";
		} else if (model instanceof Graphic) {
			return "GRAPHIC";
		} else if (model instanceof Constraint) {
			return "CONSTRAINT";
		} else if (model instanceof ExpressionSelection) {
			return "EXPRESSION SELECTION";
		} else if (model instanceof SignAttribute) {
			return "SIGN ATTRIBUTE";
		} else if (model instanceof Enumeration.Element) {
			return "ENUMERATION ELEMENT";
		} else { return ""; }
		
	}

	/**
	 * check if element contains a root language.
	 * 
	 * @param ele,
	 *            check the Element if it contains the root elements.
	 * @return Element, root language element
	 */
	public static Element getElementInRootLanguage(Element ele) {
		Element baseLanguageElement = ele.getTranslationOf();
		if (baseLanguageElement != null) {
			ele = baseLanguageElement;
			baseLanguageElement = ele.getTranslationOf();
		}

		return ele;
	}

	private boolean hasABaseLanguage(Model model) {
		Element baseLanguageElement = model.getTranslationOf();
		if (baseLanguageElement != null) {
			return true;
		}
		return false;
	}

	private void printModelElement(TranslationElement text) {
		elements.add(text);
	}

	private void setModelElementAllLanguages(TranslationElement text, Element model) {
		String language = getLanguage(model);
		setModelElement(text, model, language);
		Element baseLanguageElement = model.getTranslationOf();
		// alle sprachen zu model
		while (baseLanguageElement != null) {
			language = getLanguage(baseLanguageElement);
			setModelElement(text, baseLanguageElement, language);
			Element translatedElement = baseLanguageElement;
			baseLanguageElement = translatedElement.getTranslationOf();
		}
	}

	public static String getLanguage(Element ele) {
		if (ele instanceof Model) {
			return ((Model) ele).getLanguage();
		}
		return ((Model) ele.getContainer(Model.class)).getLanguage();
	}

	private void setModelElement(TranslationElement text, Element model, String language) {
		if (language == null) {
			System.out.println(model.getName());
			text.setName_de(model.getName());
			System.out.println(model.getDocumentation());
			text.setDocumentation_de(model.getDocumentation());
		} else {
			if (language.equals("de")) {
				text.setName_de(model.getName());
				text.setDocumentation_de(model.getDocumentation());
			} else if (language.equals("fr")) {
				text.setName_fr(model.getName());
				text.setDocumentation_fr(model.getDocumentation());
			} else if (language.equals("it")) {
				text.setName_it(model.getName());
				text.setDocumentation_it(model.getDocumentation());
			} else if (language.equals("en")) {
				text.setName_en(model.getName());
				text.setDocumentation_en(model.getDocumentation());
			}
		}
	}

	private void setModelElementEnum(TranslationElement text, ch.interlis.ili2c.metamodel.Enumeration.Element element,
			String language) {
		if (language == null) {
			text.setName_de(element.getName());
			text.setDocumentation_de(element.getDocumentation());
		} else {
			if (language.equals("de")) {
				text.setName_de(element.getName());
				text.setDocumentation_de(element.getDocumentation());
			} else if (language.equals("fr")) {
				text.setName_fr(element.getName());
				text.setDocumentation_fr(element.getDocumentation());
			} else if (language.equals("it")) {
				text.setName_it(element.getName());
				text.setDocumentation_it(element.getDocumentation());
			} else if (language.equals("en")) {
				text.setName_en(element.getName());
				text.setDocumentation_en(element.getDocumentation());
			}
		}
	}

	private void visitAllElements(Container model) {
		Iterator<Element> funcI = model.iterator();
		while (funcI.hasNext()) {
			Element ele = funcI.next();
			TranslationElement dto = new TranslationElement();
			dto.setScopedName(getElementInRootLanguage(ele).getScopedName());
			setModelElementAllLanguages(dto, ele);
			dto.setElementType(getElementType(ele));
			printModelElement(dto);
			for(String metaAttrName:ele.getMetaValues().getValues()) {
				String metaAttrValue=ele.getMetaValue(metaAttrName);
				System.out.println(metaAttrName+"="+metaAttrValue);
				String metaAttrScopedName=ele.getScopedName()+".METAOBJECT."+metaAttrName;
				TranslationElement dto2=new TranslationElement();
				dto2.setScopedName(metaAttrScopedName);
				elements.add(dto2);
			}

			if (ele instanceof Container) {
				visitAllElements((Container) ele);
			} else if (ele instanceof AttributeDef) {
				AttributeDef attr = (AttributeDef) ele;
				// If exist
				if (attr.getDomain() instanceof EnumerationType) {
					String text = getElementInRootLanguage(ele).getScopedName();
					printAllEnumaration((EnumerationType) attr.getDomain(), text, attr);
				}
			} else if (ele instanceof Domain) {
				Domain domain = (Domain) ele;
				if (domain.getType() instanceof EnumerationType) {
					String text = getElementInRootLanguage(ele).getScopedName();
					printAllEnumaration((EnumerationType) domain.getType(), text, domain);
				}
			}
		}

	}

	private void printAllEnumaration(EnumerationType et, String scopedNamePrefix, Element modelElement) {
		Enumeration enumeration = et.getEnumeration();
		printAllEnumaration(enumeration, scopedNamePrefix, modelElement);
	}

	private void printAllEnumaration(Enumeration enumeration, String scopedNamePrefix, Element modelEle) {
		Iterator<ch.interlis.ili2c.metamodel.Enumeration.Element> enumarationIterator = enumeration.getElements();

		while (enumarationIterator.hasNext()) {
			Enumeration.Element enumEle = enumarationIterator.next();
			TranslationElement text = new TranslationElement();

			// ScopedName
			String scopedName = scopedNamePrefix + "." + getEnumerationElementInRootLanguage(enumEle).getName();
			text.setScopedName(scopedName);

			String language = getLanguage(modelEle);
			setModelElementEnum(text, enumEle, language);
			Enumeration.Element baseLanguageElement = enumEle.getTranslationOf();
			Element baseLanguageModelElement = modelEle.getTranslationOf();
			while (baseLanguageElement != null) {
				String baseLanguage = getLanguage(baseLanguageModelElement);
				setModelElementEnum(text, baseLanguageElement, baseLanguage);
				baseLanguageElement = baseLanguageElement.getTranslationOf();
				baseLanguageModelElement = baseLanguageModelElement.getTranslationOf();
			}
			
			text.setElementType(getElementType(enumEle));
			printModelElement(text);
			if (enumEle.getSubEnumeration() != null) {
				printAllEnumaration(enumEle.getSubEnumeration(), scopedName, modelEle);
			}
		}
	}

	public static ch.interlis.ili2c.metamodel.Enumeration.Element getEnumerationElementInRootLanguage(
			Enumeration.Element ele) {
		ch.interlis.ili2c.metamodel.Enumeration.Element baseLanguageElement = ele.getTranslationOf();
		if (baseLanguageElement != null) {
			ele = baseLanguageElement;
			baseLanguageElement = ele.getTranslationOf();
		}

		return ele;
	}

	public static ModelElements readModelElementsXml(Unmarshaller um, File file) throws JAXBException {
		ModelElements modelElements = (ModelElements) um.unmarshal(file);
		return modelElements;
	}

	public static Unmarshaller createUnmarshaller() throws JAXBException {
		Class jaxbContextPath[] = { ModelElements.class };
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbContextPath);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		return um;
	}

}
