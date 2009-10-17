package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	
	public static class UserFlagsAttribute extends DefaultAttribute {
		public UserFlagsAttribute() { super("UserFlags", Integer.class); }
		
		public String getFormattedValue(Object o) {
			Integer value = (Integer) getValue(o);
			List<Entry.Stats.UserFlag> flags =
				new ArrayList<Entry.Stats.UserFlag>();
			for (Entry.Stats.UserFlag f: Entry.Stats.UserFlag.values())
				if ((value & f.getValue()) != 0)
					flags.add(f);
			
			String[] string_list = new String[flags.size()];
			for (int i=0; i<string_list.length; i++)
				string_list[i] = GUIUtils.getString(
						"Labels", flags.get(i).getShortName());
			
			return Utils.arrayToString(string_list, ", ");
		}
	}
	
	private EntryAttributeSchema _schema = null;
	private Vector<CustomAttribute> _custom_attributes;
	
	public EntryMetaData() {
		super(Entry.class);
		
		_custom_attributes = new Vector<CustomAttribute>();
		
		// Remove attributes that shouldn't be visible for the user
		removeAttribute("ID");
		removeAttribute("Stats");

		addAttribute(new SensesAttribute());
		addAttribute(new DefaultChoiceAttribute("Lesson", String.class));
		addAttribute(new ArrayChoiceAttribute("Categories", String[].class));
		addAttribute(new EntryClassAttribute());
		addAttribute(new UserFlagsAttribute());
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

