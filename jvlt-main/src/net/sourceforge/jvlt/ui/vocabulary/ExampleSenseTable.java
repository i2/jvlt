package net.sourceforge.jvlt.ui.vocabulary;

import java.awt.Font;
import java.util.List;

import net.sourceforge.jvlt.JVLT;
import net.sourceforge.jvlt.core.Example;
import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.metadata.DefaultAttribute;
import net.sourceforge.jvlt.metadata.MetaData;
import net.sourceforge.jvlt.ui.table.CustomFontCellRenderer;
import net.sourceforge.jvlt.ui.table.SortableTable;
import net.sourceforge.jvlt.ui.table.SortableTableModel;
import net.sourceforge.jvlt.utils.Utils;

public class ExampleSenseTable extends SortableTable<Sense> {
	private static class SenseMetaData extends MetaData {
		private static class OriginalAttribute extends DefaultAttribute {
			public OriginalAttribute() {
				super("Original", String.class);
			}

			@Override
			public Object getValue(Object o) {
				return ((Sense) o).getParent().getOrthography();
			}
		}

		private static class PronunciationAttribute extends DefaultAttribute {
			public PronunciationAttribute() {
				super("Pronunciations", String.class);
			}

			@Override
			public Object getValue(Object o) {
				return Utils.arrayToString(((Sense) o).getParent()
						.getPronunciations());
			}
		}

		private static class SenseAttribute extends DefaultAttribute {
			public SenseAttribute() {
				super("Sense", String.class);
			}

			@Override
			public Object getValue(Object o) {
				return o.toString();
			}
		}

		public SenseMetaData() {
			super(Sense.class);
		}

		@Override
		protected void init() {
			addAttribute(new OriginalAttribute());
			addAttribute(new PronunciationAttribute());
			addAttribute(new SenseAttribute());
		}
	}

	private static final long serialVersionUID = 1L;

	private static final CustomFontCellRenderer ORIGINAL_RENDERER;
	private static final CustomFontCellRenderer PRONUNCIATION_RENDERER;

	static {
		Font font;
		ORIGINAL_RENDERER = new CustomFontCellRenderer();
		PRONUNCIATION_RENDERER = new CustomFontCellRenderer();
		font = JVLT.getConfig().getFontProperty("ui_orth_font");
		if (font != null)
			ORIGINAL_RENDERER.setCustomFont(font);
		font = JVLT.getConfig().getFontProperty("ui_pron_font");
		if (font != null)
			PRONUNCIATION_RENDERER.setCustomFont(font);
	}

	private Example example = null;

	public ExampleSenseTable(Example example) {
		super(new SortableTableModel<Sense>(new SenseMetaData()));

		this.example = example;

		setCellRenderer("Original", ORIGINAL_RENDERER);
		setCellRenderer("Pronunciations", PRONUNCIATION_RENDERER);
		_model.setColumnNames(_model.getMetaData().getAttributeNames());
		update();
	}

	public Example.TextFragment getSelectedTextFragment() {
		List<Sense> senses = getSelectedObjects();
		if (senses.size() == 0)
			return null;

		for (Example.TextFragment f : example.getTextFragments())
			if (f.getSense() == senses.get(0))
				return f;

		return null;
	}

	public void update() {
		_model.clear();

		for (Example.TextFragment f : example.getTextFragments())
			if (f.getSense() != null)
				_model.addObject(f.getSense());
	}
}
