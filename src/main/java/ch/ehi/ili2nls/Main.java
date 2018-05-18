package ch.ehi.ili2nls;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ch.interlis.ili2c.metamodel.TransferDescription;

public class Main {

	public static void main(String[] args) {

		String file = "";
		Ili2TranslationXml dateiLesen = new Ili2TranslationXml();
		for (String s : args) {
			file = s;
		}
		ModelElements eles = dateiLesen.readAllModels(new File(file));
		Class jaxbContextPath[] = { ModelElements.class };
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(jaxbContextPath);
			Marshaller ms = jaxbContext.createMarshaller();
			ms.marshal(eles, new File("out.xml"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
