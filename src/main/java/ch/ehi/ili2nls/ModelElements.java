package ch.ehi.ili2nls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="IliModelElements")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModelElements implements Iterable<ModelElement> {
    private List<ModelElement> element=new ArrayList<ModelElement>();
	@Override
	public Iterator<ModelElement> iterator() {
		return element.listIterator();
	}
	public boolean add(ModelElement ele) {
		return element.add(ele);
	}

}
