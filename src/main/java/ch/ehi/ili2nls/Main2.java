package ch.ehi.ili2nls;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.interlis.ili2c.metamodel.TransferDescription;

public class Main2 {

	public static void main(String[] args) throws JAXBException, IOException {

		String file = "";
		for (String s : args) {
			file = s;
		}
		
		Unmarshaller um = Ili2TranslationXml.createUnmarshaller();
		ModelElements modelElements = Ili2TranslationXml.readModelElementsXml(um, new File(file + ".xml"));
		TransferDescription td = Ili2TranslationXml.compileIliModel(new File(file));
		FileWriter out = new FileWriter(new File("out.ili"));
		new Interlis2Generator().generate(out, td, modelElements, "fr", file);
		out.close();
	}


}
