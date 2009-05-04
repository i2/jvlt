package net.sourceforge.jvlt.hibernate;

import java.util.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class DictManager {
	private SessionFactory sessionFactory = null;
	private net.sourceforge.jvlt.Dict currentDict = null;
	private Map<net.sourceforge.jvlt.Sense, Sense> hibSenseMap
		= new HashMap<net.sourceforge.jvlt.Sense, Sense>();
	private Map<Sense, net.sourceforge.jvlt.Sense> senseMap
		= new HashMap<Sense, net.sourceforge.jvlt.Sense>();
	
	public void storeDict(net.sourceforge.jvlt.Dict dict)
		throws HibernateException {
		/* Reset sense map */
		this.hibSenseMap.clear();
		
		/* Create session */
		Configuration config = new Configuration();
		config.configure();
		config.setProperty("hibernate.hbm2ddl.auto", "create");
		this.sessionFactory = config.buildSessionFactory();
		Session session = this.sessionFactory.getCurrentSession();
	
		/* Convert dictionary */
		Dict hibDict = dictToHibDict(dict);
        session.beginTransaction();
        
        session.save(hibDict);
		
		/* Store entries */
		Set<Entry> entries = hibDict.getEntries();
        for (Iterator<Entry> it = entries.iterator(); it.hasNext(); )
        	storeEntry(session, it.next());
        
        /* Store examples */
		Set<Example> examples = hibDict.getExamples();
        for (Iterator<Example> it = examples.iterator(); it.hasNext(); )
        	storeExample(session, it.next());

        session.getTransaction().commit();
        this.sessionFactory.close();
	}
	
	@SuppressWarnings("unchecked")
	public net.sourceforge.jvlt.Dict readDict() throws HibernateException {
		/* Clear sense map */
		this.senseMap.clear();

		/* Create session */
		Configuration config = new Configuration();
		config.configure();
		this.sessionFactory = config.buildSessionFactory();
		Session session = this.sessionFactory.getCurrentSession();

        session.beginTransaction();
        List dictList = session.createQuery("from Dict").list();
        
		this.currentDict = new net.sourceforge.jvlt.Dict();
		try {
			if (dictList.size() == 1) {
				Dict hibDict = (Dict) dictList.get(0);
				
				this.currentDict.setLanguage(hibDict.getLanguage());
				
				// Convert entries
				Set<Entry> entries = hibDict.getEntries();
				for (Iterator<Entry> it = entries.iterator(); it.hasNext(); )
					this.currentDict.addEntry(hibEntryToEntry(it.next()));
				
				// Convert examples
				Set<Example> examples = hibDict.getExamples();
				for (Iterator<Example> it = examples.iterator(); it.hasNext(); )
					this.currentDict.addExample(hibExampleToExample(it.next()));
			}
			
	        session.getTransaction().commit();
	        this.sessionFactory.close();
	        
			return this.currentDict;
		} catch (net.sourceforge.jvlt.DictException e) {
			throw new HibernateException(e);
		} 
	}
	
	private void storeEntry(Session session, Entry entry) {
		session.save(entry);
		if (entry.getStats() != null)
			session.save(entry.getStats());
		if (entry.getEntryClass() != null) {
			EntryClass ec = entry.getEntryClass();
			for (Iterator<SchemaAttribute> it = ec.getAttributes().iterator();
				it.hasNext(); )
				session.save(it.next());
			
			session.save(entry.getEntryClass());
		}
		for (Iterator<Sense> it = entry.getSenses().iterator(); it.hasNext(); )
			session.save(it.next());
	}
	
	private void storeExample(Session session, Example example) {
		session.save(example);
		
		Iterator<Example.TextFragment> it
			= example.getTextFragments().iterator();
		while (it.hasNext())
			session.save(it.next());
			// No need to store sense, already stored in storeEntry()
	}
	
	private net.sourceforge.jvlt.Entry hibEntryToEntry(Entry hibEntry)
		throws HibernateException {
		net.sourceforge.jvlt.Entry entry = new net.sourceforge.jvlt.Entry(
				"e" + String.valueOf(hibEntry.getId()));
		
		entry.setOrthography(hibEntry.getOrthography());
		entry.setLesson(hibEntry.getLesson());
		
		/* Set entry stats */
		if (hibEntry.getStats() != null) {
			entry.setBatch(hibEntry.getStats().getBatch());
			entry.setNumQueried(hibEntry.getStats().getNumQuizzed());
			entry.setNumMistakes(hibEntry.getStats().getNumMistakes());
			GregorianCalendar lastQueried = new GregorianCalendar();
			lastQueried.setTime(hibEntry.getStats().getLastQuizzed());
			entry.setLastQueried(lastQueried);
			GregorianCalendar dateAdded = new GregorianCalendar();
			dateAdded.setTime(hibEntry.getStats().getDateAdded());
			entry.setDateAdded(dateAdded);
		}
		
		/* Set entry class and language-specific attributes */
		entry.setEntryClass(
				hibEntryClassToEntryClass(hibEntry.getEntryClass()));
		
		/* Set categories, pronunciations and multimedia files */
		entry.setCategories(hibEntry.getCategories().toArray(new String[0]));
		entry.setPronunciations(
				hibEntry.getPronunciations().toArray(new String[0]));
		entry.setMultimediaFiles(
				hibEntry.getMultimediaFiles().toArray(new String[0]));
		
		/* Set senses */
		try {
			for (Iterator<Sense> it = hibEntry.getSenses().iterator();
				it.hasNext(); ) {
				Sense hibSense = it.next();
				net.sourceforge.jvlt.Sense sense
					= new net.sourceforge.jvlt.Sense(hibSense.getTranslation(),
							hibSense.getDefinition());
				entry.addSense(sense);
				this.senseMap.put(hibSense, sense);
			}

			return entry;
		} catch (net.sourceforge.jvlt.DictException e) {
			throw new HibernateException(e);
		}
	}
	
	private net.sourceforge.jvlt.EntryClass hibEntryClassToEntryClass(
			EntryClass hibEntryClass) throws HibernateException {
		if (hibEntryClass == null)
			return null;
		
		net.sourceforge.jvlt.EntryAttributeSchema schema
			= this.currentDict.getEntryAttributeSchema();
		if (schema == null)
			throw new HibernateException("No language set.");
		
		net.sourceforge.jvlt.EntryClass schemaClass
			= schema.getEntryClass(hibEntryClass.getName());
		if (schemaClass == null)
			throw new HibernateException("Unknown word class: '"
					+ hibEntryClass.getName() + "'");
		
		net.sourceforge.jvlt.EntryClass ec
			= (net.sourceforge.jvlt.EntryClass) schemaClass.clone();
		Iterator<SchemaAttribute> it = hibEntryClass.getAttributes().iterator();
		while (it.hasNext()) {
			SchemaAttribute hibAttr = it.next();
			net.sourceforge.jvlt.SchemaAttribute attr
				= ec.getAttribute(hibAttr.getName());
			if (attr == null)
				throw new HibernateException("No such attribute: '"
						+ hibAttr.getName() + "'");
			
			if (attr instanceof net.sourceforge.jvlt.ArraySchemaAttribute) {
				ArrayList<net.sourceforge.jvlt.AttributeChoice> vals
					= new ArrayList<net.sourceforge.jvlt.AttributeChoice>();
				Iterator<String> valIt
					= ((ArraySchemaAttribute) hibAttr).getValues().iterator();
				while (valIt.hasNext())
					vals.add(stringToAttributeChoice(
							(net.sourceforge.jvlt.ArraySchemaAttribute) attr,
							valIt.next()));
				
				attr.setValue(vals.toArray(
						new net.sourceforge.jvlt.AttributeChoice[0]));
			} else if (attr instanceof
					net.sourceforge.jvlt.ChoiceSchemaAttribute) {
				net.sourceforge.jvlt.ChoiceSchemaAttribute csa
					= (net.sourceforge.jvlt.ChoiceSchemaAttribute) attr;
				attr.setValue(stringToAttributeChoice(csa,
						((DefaultSchemaAttribute) hibAttr).getValue()));
			} else {
				attr.setValue(((DefaultSchemaAttribute) hibAttr).getValue());
			}
		}
		
		return ec;
	}

	private Dict dictToHibDict(net.sourceforge.jvlt.Dict dict)
		throws HibernateException {
		Dict hibDict = new Dict();
		hibDict.setLanguage(dict.getLanguage());
		
		Collection<net.sourceforge.jvlt.Entry> entries = dict.getEntries();
		for (Iterator<net.sourceforge.jvlt.Entry> it = entries.iterator();
			it.hasNext(); )
			hibDict.getEntries().add(entryToHibEntry(it.next()));

		Collection<net.sourceforge.jvlt.Example> examples = dict.getExamples();
		for (Iterator<net.sourceforge.jvlt.Example> it = examples.iterator();
			it.hasNext(); )
			hibDict.getExamples().add(exampleToHibExample(it.next()));
		
		return hibDict;
	}
	
	private Entry entryToHibEntry(net.sourceforge.jvlt.Entry entry) {
		Entry hibEntry = new Entry();
		
		hibEntry.setOrthography(entry.getOrthography());
		hibEntry.setLesson(entry.getLesson());
		
		/* Set entry stats */
		if (entry.getNumQueried() > 0) {
			EntryStats hibEntryStats = new EntryStats();
			hibEntryStats.setBatch(entry.getBatch());
			hibEntryStats.setNumQuizzed(entry.getNumQueried());
			hibEntryStats.setNumMistakes(entry.getNumMistakes());
			if (entry.getLastQueried() != null)
				hibEntryStats.setLastQuizzed(entry.getLastQueried().getTime());
			if (entry.getDateAdded() != null)
				hibEntryStats.setDateAdded(entry.getDateAdded().getTime());
			hibEntry.setStats(hibEntryStats);
		}
		
		/* Set entry class and language-specific attributes */
		net.sourceforge.jvlt.EntryClass entryClass = entry.getEntryClass();
		if (entryClass != null) {
			EntryClass hibEntryClass = new EntryClass();
			hibEntryClass.setName(entryClass.getName());
			
			net.sourceforge.jvlt.SchemaAttribute[] attrs
				= entryClass.getAttributes();
			for (int i=0; i<attrs.length; i++) {
				SchemaAttribute attr
					= schemaAttributeToHibSchemaAttribute(attrs[i]);
				if (attr != null)
					hibEntryClass.getAttributes().add(attr);
			}
			
			hibEntry.setEntryClass(hibEntryClass);
		}
		
		/* Set categories, pronunciations and multimedia files */
		hibEntry.getCategories().addAll(Arrays.asList(entry.getCategories()));
		hibEntry.getPronunciations().addAll(
				Arrays.asList(entry.getPronunciations()));
		hibEntry.getMultimediaFiles().addAll(
				Arrays.asList(entry.getMultimediaFiles()));
		
		/* Set senses */
		net.sourceforge.jvlt.Sense[] senses = entry.getSenses();
		for (int i=0; i<senses.length; i++) {
			Sense hibSense = new Sense();
			hibSense.setTranslation(senses[i].getTranslation());
			hibSense.setDefinition(senses[i].getDefinition());
			hibEntry.getSenses().add(hibSense);
			this.hibSenseMap.put(senses[i], hibSense);
		}
		
		return hibEntry;
	}
	
	private SchemaAttribute schemaAttributeToHibSchemaAttribute(
			net.sourceforge.jvlt.SchemaAttribute attr) {
		if (attr.getValue() == null)
			return null;

		if (attr instanceof
				net.sourceforge.jvlt.ArraySchemaAttribute) {
			ArraySchemaAttribute a = new ArraySchemaAttribute();
			a.setName(attr.getName());
			net.sourceforge.jvlt.AttributeChoice[] vals
				= (net.sourceforge.jvlt.AttributeChoice[])
					attr.getValue();
			
			if (vals.length == 0)
				return null;
			else {
				for (int j=0; j<vals.length; j++)
					a.getValues().add(vals[j].getName());
				
				return a;
			}
		} else {
			DefaultSchemaAttribute a = new DefaultSchemaAttribute();
			a.setName(attr.getName());
			a.setValue(attr.getValue().toString());
			
			return a;
		}
	}
	
	private net.sourceforge.jvlt.AttributeChoice stringToAttributeChoice(
			net.sourceforge.jvlt.ChoiceSchemaAttribute attr, String val)
				throws HibernateException {
		net.sourceforge.jvlt.AttributeChoice c = attr.getChoice(val);
		if (c == null)
			throw new HibernateException("No such choice: '"
					+ val + "'");
		else
			return c;
	}
	
	private net.sourceforge.jvlt.Example hibExampleToExample(
			Example hibExample) throws HibernateException {
		net.sourceforge.jvlt.Example example = new net.sourceforge.jvlt.Example(
				"x" + String.valueOf(hibExample.getId()));
		
		example.setTranslation(hibExample.getTranslation());
		
		Iterator<Example.TextFragment> it
			= hibExample.getTextFragments().iterator();
		while (it.hasNext()) {
			Example.TextFragment hibFragment = it.next();
			net.sourceforge.jvlt.Example.TextFragment fragment
				= new net.sourceforge.jvlt.Example.TextFragment(
						hibFragment.getText());
			Sense hibSense = hibFragment.getSense();
			if (hibSense != null)
				if (! this.senseMap.containsKey(hibSense))
					throw new HibernateException("Invalid sense in example");
				else
					fragment.setSense(this.senseMap.get(hibSense));
			example.addTextFragment(fragment);
		}
		
		return example;
	}
	
	private Example exampleToHibExample(net.sourceforge.jvlt.Example example)
		throws HibernateException {
		Example hibExample = new Example();
		
		hibExample.setTranslation(example.getTranslation());
		
		net.sourceforge.jvlt.Example.TextFragment[] fragments
			= example.getTextFragments();
		for (int i=0; i<fragments.length; i++) {
			Example.TextFragment hibFragment = new Example.TextFragment();
			hibFragment.setText(fragments[i].getText());
			hibExample.getTextFragments().add(hibFragment);
			
			net.sourceforge.jvlt.Sense sense = fragments[i].getSense();
			if (sense != null) {
				if (! this.hibSenseMap.containsKey(fragments[i].getSense()))
					throw new HibernateException("Invalid sense in example");
				else
					hibFragment.setSense(this.hibSenseMap.get(sense));
			}
		}
		
		return hibExample;
	}
}
