package ch.ehi.ili2nls;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.TransferDescription;

public class IliDateiLesen {

	private ModelElements elements = new ModelElements();

	public ModelElements readAllModels(File iliFile) {

		Configuration ili2cConfig = new ch.interlis.ili2c.config.Configuration();
		ili2cConfig.setAutoCompleteModelList(true);
		ili2cConfig.addFileEntry(new FileEntry(iliFile.getAbsolutePath(), FileEntryKind.ILIMODELFILE));

		Settings settings = new Settings();
		String currentDir = iliFile.getAbsoluteFile().getParent();
		settings.setValue(ch.interlis.ili2c.gui.UserSettings.ILIDIRS, ch.interlis.ili2c.Main.DEFAULT_ILIDIRS);
		HashMap<String, String> pathMap = new HashMap<String, String>();
		pathMap.put(ch.interlis.ili2c.Main.ILI_DIR, currentDir);
		settings.setTransientObject(ch.interlis.ili2c.gui.UserSettings.ILIDIRS_PATHMAP, pathMap);
		TransferDescription td = null;
		td = ch.interlis.ili2c.Main.runCompiler(ili2cConfig, settings);

		if (td == null) {
			System.out.println("No Records Found");
			return null;
		}

		printAllModels(td, iliFile.getName());
		return elements;
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
			ModelElement text = new ModelElement();
			text.setScopedName(model.getScopedName());
			setModelElementAllLanguages(text, model);
			printModelElement(text);

			visitAllElements(model);
		}
	}

	private boolean hasABaseLanguage(Model model) {
		Element baseLanguageElement = model.getTranslationOf();
		if (baseLanguageElement != null) {
			return true;
		}
		return false;
	}

	private void printModelElement(ModelElement text) {
		elements.add(text);
	}

	private void setModelElementAllLanguages(ModelElement text, Element model) {
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

	private String getLanguage(Element ele) {
		if (ele instanceof Model) {
			return ((Model) ele).getLanguage();
		}
		return ((Model) ele.getContainer(Model.class)).getLanguage();
	}

	private void setModelElement(ModelElement text, Element model, String language) {
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

	private void setModelElementEnum(ModelElement text, ch.interlis.ili2c.metamodel.Enumeration.Element element,
			String language) {
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

	private void visitAllElements(Container model) {
		Iterator<Element> funcI = model.iterator();
		while (funcI.hasNext()) {
			Element ele = funcI.next();
			ModelElement dto = new ModelElement();
			dto.setScopedName(ele.getScopedName());
			setModelElementAllLanguages(dto, ele);
			printModelElement(dto);

			if (ele instanceof Container) {
				visitAllElements((Container) ele);
			} else if (ele instanceof AttributeDef) {
				AttributeDef attr = (AttributeDef) ele;
				// If exist
				if (attr.getDomain() instanceof EnumerationType) {
					String text = ele.getScopedName();
					printAllEnumaration((EnumerationType) attr.getDomain(), text, attr);
				}
			} else if (ele instanceof Domain) {
				Domain domain = (Domain) ele;
				if (domain.getType() instanceof EnumerationType) {
					String text = ele.getScopedName();
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
			ModelElement text = new ModelElement();

			// ScopedName
			String scopedName = scopedNamePrefix + "." + enumEle.getName();
			text.setScopedName((scopedName));

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

			printModelElement(text);
			if (enumEle.getSubEnumeration() != null) {
				printAllEnumaration(enumEle.getSubEnumeration(), scopedName, modelEle);
			}
		}
	}

}
