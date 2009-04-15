package net.sourceforge.jvlt;

import java.util.Iterator;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntryMetaData extends MetaData {
	public static class SensesAttribute extends ArrayAttribute {
		private MetaData _sense_data = new MetaData(Sense.class);
		
		public SensesAttribute() {
			super("Senses", Sense[].class);
			setDelimiter("; ");
			setEnableNumbering(true);
		}
		
		public Element getXMLElement(Document doc, Object o) {
			DictObjectFormatter formatter = new DictObjectFormatter(doc);
			Element elem = doc.createElement("Senses");
			Sense[] senses = (Sense[]) getValue(o);
			for (int i=0; i<senses.length; i++)
				elem.appendChild(formatter.getElementForObject(
					senses[i], _sense_data.getAttributes()));

			return elem;
		}
	}
	
	public static class EntryClassAttribute extends DefaultAttribute {
		public EntryClassAttribute() { super("EntryClass", EntryClass.class); }
		
		public String getFormattedValue(Object o) {
			EntryClass cl = (EntryClass) getValue(o);
			if (cl == null)
				return "";
			else {
				AttributeResources resources = new AttributeResources();
				return resources.getString(cl.getName());
			}
		}
	}
	
	private EntryAttributeSchema _schema = null;
	private Vector<CustomAttribute> _custom_attributes;
	
	public EntryMetaData() {
		super(Entry.class);
		
		_custom_attributes = new Vector<CustomAttribute>();
		
		addAttribute(new SensesAttribute());
		addAttribute(new DefaultChoiceAttribute("Lesson", String.class));
		addAttribute(new ArrayChoiceAttribute("Categories", String[].class));
		addAttribute(new EntryClassAttribute());
	}
	
	public EntryAttributeSchema getAttributeSchema() { return _schema; }
	
	public void setAttributeSchema(EntryAttributeSchema schema) {
		_schema = schema;
		for (Iterator<CustomAttribute> it=_custom_attributes.iterator();
				it.hasNext(); )
			removeAttribute(it.next().getName());
		_custom_attributes.clear();
		
		if (schema == null)
			return;
		
		EntryClass[] ecs = schema.getEntryClasses();
		for (int i=0; i<ecs.length; i++) {
			SchemaAttribute[] attrs = ecs[i].getAttributes();
			for (int j=0; j<attrs.length; j++) {
				CustomAttribute ca;
				if (attrs[j] instanceof ChoiceSchemaAttribute) {
					ChoiceSchemaAttribute csa=(ChoiceSchemaAttribute) attrs[j];
					CustomChoiceAttribute cca;
					if (attrs[j] instanceof ArraySchemaAttribute)
						cca = new CustomArrayAttribute(csa.getName());
					else
						cca = new CustomChoiceAttribute(csa.getName());

					cca.setValues(csa.getChoices());
					ca = cca;
				}
				else {
					ca = new CustomAttribute(attrs[j].getName());
				}
				_custom_attributes.add(ca);
				addAttribute(ca);
			}
		}
	}
}

