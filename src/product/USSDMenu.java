package product;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class USSDMenu {

	public USSDMenu() {

	}

	public Document getContent(int root) {
		// attributes : type = [text|number|msisdn|static] ; network = [on|off] ; ton = [National|International] ; value = [value_to_fixe]
		// in case type equals to msisdn, set attribute <<ton>> required
		// in case type equals to msisdn, set attribute <<network>> if needed (<<ton>> must be set to 'National')
		// in case type equals to static, set attribute <<value>> required

		Element racine = new Element("SERVICE-CODE-"  + root);
		Document document = new Document(racine);

		// body
		Element body = new Element("menu");
		racine.addContent(body);

		body.addContent(new Element("choice-0").addContent(new Element("choice-1")).addContent(new Element("choice-2")));
		body.addContent(new Element("choice-1").addContent(new Element("choice-1")).addContent(new Element("choice-2")));
		body.addContent(new Element("choice-2"));
		body.addContent(new Element("choice-3"));

	    return document;
	}

	public void afficher(Document document) {
		try {
	      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	      sortie.output(document, System.out);

	    } catch (IOException e) {

	    }
	  }

}