package GUI;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

/**
 * 
 * IPanel is a simple JPanel but with a modified paintComponent that
 * allows for the JPanel to have an image background.
 *
 */
public class IPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Image imageOrg = null;
	private Image image = null;
	{
		addComponentListener(new ComponentAdapter() {
			// This allows for window scaling to be done correctly
			@Override
			public void componentResized(final ComponentEvent e) {
				final int w = IPanel.this.getWidth();
				final int h = IPanel.this.getHeight();
				image = w > 0 && h > 0 ? imageOrg.getScaledInstance(w, h,
						Image.SCALE_SMOOTH) : imageOrg;
				IPanel.this.repaint();
			}
		});
	}

	public IPanel(final Image i) {
		imageOrg = i;
		image = i;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (image != null)
			// Draw the background
			g.drawImage(image, 0, 0, null);
	}
}