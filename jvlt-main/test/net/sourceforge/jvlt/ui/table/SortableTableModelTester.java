package net.sourceforge.jvlt.ui.table; // NOPMD static imports (mocks/asserts)

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import net.sourceforge.jvlt.ui.table.SortableTableModel.Directive;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Contains tests related to the {@link SortableTableModel} class.
 * 
 * @author thrar
 */
public class SortableTableModelTester {
	private SortableTableModel<Object> model;

	/**
	 * Sets up the table model including a spy.
	 */
	@BeforeMethod
	public void buildTableModel() {
		model = spy(new SortableTableModel<Object>(null));
		when(model.getColumnCount()).thenReturn(1);
	}

	/**
	 * Builds pairs of comparable row contents with their expected result.
	 * 
	 * @return pairs of comparable row contents with their expected result
	 */
	@DataProvider(name = "sortableContentProvider")
	public Object[][] provideSortableContent() {
		return new Object[][] { { null, null, 0 }, { null, "a", -1 },
				{ "a", null, 1 }, { "a", "a", 0 }, { "a", "aa", -1 },
				{ "a", "b", -1 }, { "b", "a", 1 }, { true, true, 0 },
				{ true, false, 1 }, { false, true, -1 }, { 0, 0, 0 },
				{ 0, 1, -1 }, { 1, 0, 1 },
				{ Double.MIN_VALUE, Double.MAX_VALUE, -1 },
				{ new Object(), new Object(), 0 }, { "a", new Object(), 0 },
				{ true, new Object(), 0 }, { 1, new Object(), 0 } };
	}

	/**
	 * Verifies that two rows are compared correctly to each other. It tests
	 * both ascending and descending order, inverting the expected result for
	 * the latter.
	 * 
	 * @param current content of the comparer row
	 * @param other content of the comparee row
	 * @param expected the expected result of the comparison when ascending
	 */
	@Test(dataProvider = "sortableContentProvider")
	public void testAscendingRowSort(Object current, Object other, int expected) {
		SortableTableModel<Object>.Row currentRow = buildRow(0, current);
		SortableTableModel<Object>.Row otherRow = buildRow(1, other);

		model.setSortingDirective(new Directive(0, 1));
		assertEquals(currentRow.compareTo(otherRow), expected,
				"Unexpected comparison result");
		model.setSortingDirective(new Directive(0, -1));
		assertEquals(currentRow.compareTo(otherRow), -expected,
				"Unexpected comparison result");
	}

	/**
	 * Builds a table row pretending to have the given index with the given
	 * value in all columns.
	 * 
	 * @param index the index of this row's data in the model
	 * @param value the value to store in the model for this row
	 * @return a newly created row
	 */
	private SortableTableModel<Object>.Row buildRow(int index, Object value) {
		SortableTableModel<Object>.Row row = model.new Row(index);
		doReturn(value).when(model).getValue(eq(index), anyInt());
		return row;
	}
}
