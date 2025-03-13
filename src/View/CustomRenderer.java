package View;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class CustomRenderer extends JLabel implements TableCellRenderer
{
	String formattedString;
	DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column
	) {
		DefaultTableCellRenderer    renderer = new DefaultTableCellRenderer();
		Component c = renderer.getTableCellRendererComponent(
			table, value, isSelected, hasFocus, row, column );
		String s = value.toString();
		if (value != null && value instanceof Double) {
			s = String.format("%3.1f", value);
		}//end if we need to restrict decimal places
		if (value != null && value instanceof Integer) {
			s = String.format("%d", value);
		}//end if we want a plain integer
		c = renderer.getTableCellRendererComponent(
			table, s, isSelected, hasFocus, row, column );
		((JLabel)c).setHorizontalAlignment( SwingConstants.CENTER );
		return c;
	}
}
