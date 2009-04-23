package net.sourceforge.jvlt.hibernate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.jvlt.DictReaderException;
import net.sourceforge.jvlt.DictWriter;
import net.sourceforge.jvlt.DictXMLWriter;
import net.sourceforge.jvlt.JVLT;

public class HibernateUtil {
	private JVLT jvlt = null;
	
	public HibernateUtil() {
		this.jvlt = new JVLT();
		this.jvlt.init();
	}
	
	public static void main(String[] args) {
		if (args.length < 2)
			return;
		
		if (args[0].equals("--load-from-db")) {
			try {
				DictManager manager = new DictManager();
				net.sourceforge.jvlt.Dict dict = manager.readDict();
				HibernateUtil ctrl = new HibernateUtil();
				ctrl.storeDictToFile(dict, args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("--store-into-db")) {
			HibernateUtil ctrl = new HibernateUtil();
			try {
				net.sourceforge.jvlt.Dict dict = ctrl.readDictFromFile(args[1]);
				DictManager manager = new DictManager();
				manager.storeDict(dict);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private net.sourceforge.jvlt.Dict readDictFromFile(String fileName)
		throws IOException, DictReaderException {
		this.jvlt.getModel().load(fileName);
		
		return this.jvlt.getModel().getDict();
	}
	
	private void storeDictToFile(net.sourceforge.jvlt.Dict dict,
			String filename) throws FileNotFoundException, IOException {
		File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file, false);
		DictWriter writer = new DictXMLWriter(dict, fos);
		writer.write();
	}
}
