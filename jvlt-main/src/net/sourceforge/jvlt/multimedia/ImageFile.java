package net.sourceforge.jvlt.multimedia;

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.sourceforge.jvlt.ui.utils.GUIUtils;
import net.sourceforge.jvlt.utils.I18nService;

/**
 * This class represents an image file that can be viewed with jVLT. 
 * Other image files are represented by CustomMultimediaFile.
 * @author Maithu
 */
public class ImageFile extends MultimediaFile {
	public ImageFile(String file_name) {
		super(file_name, IMAGE_FILE);
	}

	public ImageFile() {
		this("");
	}

	/**
	 * This deals with allowing the user to view the image read into jVLT.
	 * @param parent jVLT Window
	 * @throws IOException if any of the checks fail
	 */
	public void show(Frame parent) throws IOException {
		File f = getFile();
		if (!f.exists() || !f.isFile()) {
			String msg = "File " + f.getAbsolutePath() + " does not exist "
					+ "or cannot be opened.";
			throw new IOException(msg);
		}

		ImageIcon icon = new ImageIcon(f.getAbsolutePath());
		if (isImageTooLarge(icon, parent.getBounds())) {
			String msg = "Image is too large. Please choose an image smaller than the size of the jVLT window or enlarge the jVLT window to view image.";
			throw new IOException(msg);
		}
		JDialog dlg = new JDialog(parent,
				I18nService.getString("Labels", "image"), false);
		JLabel lbl = new JLabel(icon);
		dlg.setContentPane(lbl);
		GUIUtils.showDialog(parent, dlg);
	}

	/**
	 * Checks if dimensions of image file are larger than the dimensions of the jVLT window.
	 * @param icon The icon to be checked
	 * @param bounds The bounds of the jVLT window
	 * @return true if image is larger than size of jVLT window, else returns false
	 */
	private boolean isImageTooLarge(ImageIcon icon, Rectangle bounds){
		return (icon.getIconHeight() > bounds.getHeight()
				|| icon.getIconWidth() > bounds.getWidth());
	}
}
