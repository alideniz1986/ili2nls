package ch.ehi.ili2nls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="IliModelElements")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModelElements implements Iterable<TranslationElement> {
    private List<TranslationElement> element=new ArrayList<TranslationElement>();
	@Override
	public Iterator<TranslationElement> iterator() {
		return element.listIterator();
	}
	public boolean add(TranslationElement ele) {
		return element.add(ele);
	}

}
