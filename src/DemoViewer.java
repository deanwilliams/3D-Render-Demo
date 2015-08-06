import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class DemoViewer {

	private JFrame frame;

	public DemoViewer() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());

		JSlider headingSlider = new JSlider(SwingConstants.HORIZONTAL, 360, 180);
		pane.add(headingSlider, BorderLayout.SOUTH);

		JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
		pane.add(pitchSlider, BorderLayout.EAST);

		JPanel renderPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getWidth(), getHeight());

				double heading = Math.toRadians(headingSlider.getValue());
				Matrix3 headingTransform = new Matrix3(new double[] {
						Math.cos(heading), 0, -Math.sin(heading), 0, 1, 0,
						Math.sin(heading), 0, Math.cos(heading) });
				double pitch = Math.toRadians(pitchSlider.getValue());
				Matrix3 pitchTransform = new Matrix3(new double[] { 1, 0, 0, 0,
						Math.cos(pitch), Math.sin(pitch), 0, -Math.sin(pitch),
						Math.cos(pitch) });

				Matrix3 transform = headingTransform.multiply(pitchTransform);

				BufferedImage img = new BufferedImage(getWidth(), getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				double[] zBuffer = new double[img.getWidth() * img.getHeight()];
				for (int q = 0; q < zBuffer.length; q++) {
					zBuffer[q] = Double.NEGATIVE_INFINITY;
				}

				Scene scene = new Scene();

				for (Triangle t : scene.getTris()) {
					Vertex v1 = transform.transform(t.getV1());
					Vertex v2 = transform.transform(t.getV2());
					Vertex v3 = transform.transform(t.getV3());

					v1.addX(getWidth() / 2);
					v1.addY(getHeight() / 2);
					v2.addX(getWidth() / 2);
					v2.addY(getHeight() / 2);
					v3.addX(getWidth() / 2);
					v3.addY(getHeight() / 2);

					int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x(),
							Math.min(v2.x(), v3.x()))));
					int maxX = (int) Math.min(
							img.getWidth() - 1,
							Math.floor(Math.max(v1.x(),
									Math.max(v2.x(), v3.x()))));
					int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y(),
							Math.min(v2.y(), v3.y()))));
					int maxY = (int) Math.min(
							img.getHeight() - 1,
							Math.floor(Math.max(v1.y(),
									Math.max(v2.y(), v3.y()))));

					double triangleArea = (v1.y() - v3.y()) * (v2.x() - v3.x())
							+ (v2.y() - v3.y()) * (v3.x() - v1.x());

					for (int y = minY; y <= maxY; y++) {
						for (int x = minX; x <= maxX; x++) {
							double b1 = ((y - v3.y()) * (v2.x() - v3.x()) + (v2
									.y() - v3.y()) * (v3.x() - x))
									/ triangleArea;
							double b2 = ((y - v1.y()) * (v3.x() - v1.x()) + (v3
									.y() - v1.y()) * (v1.x() - x))
									/ triangleArea;
							double b3 = ((y - v2.y()) * (v1.x() - v2.x()) + (v1
									.y() - v2.y()) * (v2.x() - x))
									/ triangleArea;
							if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1
									&& b3 >= 0 && b3 <= 1) {
								double depth = b1 * v1.z() + b2 * v2.z() + b3
										* v3.z();
								int zIndex = y * img.getWidth() + x;
								if (zBuffer[zIndex] < depth) {
									img.setRGB(x, y, t.getColor().getRGB());
									zBuffer[zIndex] = depth;
								}
							}
						}
					}
				}
				g2.drawImage(img, 0, 0, null);
			}
		};
		headingSlider.addChangeListener(e -> renderPanel.repaint());
		pitchSlider.addChangeListener(e -> renderPanel.repaint());

		pane.add(renderPanel, BorderLayout.CENTER);

		frame.setSize(400, 400);
	}

	private void start() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				DemoViewer demoViewer = new DemoViewer();
				demoViewer.start();
			}
		});
	}
}
