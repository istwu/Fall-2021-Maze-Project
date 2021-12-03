package edu.wm.cs.cs301.isabellawu.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 *
 * @author Isabella Wu
 *
 */
public class MazePanel extends View implements P5PanelF21 {
	private static final long serialVersionUID = 2787329533730973905L;
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	// bufferImage can only be initialized if the container is displayable,
	// uses a delayed initialization and relies on client class to call initBufferImage()
	// before first use
//	private Image bufferImage;
//	private Graphics2D graphics; // obtained from bufferImage,
	// graphics is stored to allow clients to draw on the same graphics object repeatedly
	// has benefits if color settings should be remembered for subsequent drawing operations

	private Canvas canvas;
	private Bitmap bitmap;
	private Paint paint;

	private static final String TAG = "MazePanel";

	/**
	 * Constructor. Object is not focusable.
	 */
	public MazePanel(Context context) {
		super(context);
		Log.v(TAG, "Constructor 1");
		init();
		setFocusable(false);
//		bufferImage = null; // bufferImage initialized separately and later
//		graphics = null;	// same for graphics
//		myTestImage(canvas);
	}

	public MazePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.v(TAG, "Constructor 2");
		init();
		setFocusable(false);
//		myTestImage(canvas);
	}

	public void init() {
		bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint = new Paint();
	}

	@Override
	public void onDraw(Canvas UIcanvas) {
		// draw private bitmap on canvas passed in
		super.onDraw(UIcanvas);
//		paint.setColor(0xffff0000);
//		UIcanvas.drawRect(0, 0, 1200, 1200, paint);
//		myTestImage(UIcanvas);

		UIcanvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int minw = getPaddingLeft() + getPaddingRight() + Constants.VIEW_WIDTH;
		int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
		setMeasuredDimension(w, w);
	}

	private void myTestImage(Canvas c) {
		Log.v(TAG, "Running myTestImage()");
		// red ball
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		c.drawCircle(100, 100 , 100, paint);
		// green ball
		paint.setColor(Color.GREEN);
		c.drawCircle(180, 320, 40, paint);
		// yellow rectangle
		paint.setColor(Color.YELLOW);
		c.drawRect(400, 200, 800, 600, paint);
		// blue polygon
		int[] x = {240, 490, 603, 520};
		int[] y = {580, 684, 302, 438};
		Path polygon = new Path();
		for(int i = 0; i < 4; i++) {
			polygon.lineTo(x[i], y[i]);
		}
		polygon.lineTo(x[0], y[0]);
		paint.setColor(Color.BLUE);
		c.drawPath(polygon, paint);
		// lines
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		c.drawLine(160, 800, 200, 1000, paint);
		c.drawLine(300, 900, 600, 800, paint);
		invalidate();
	}

	// --------------------------------------------- //

//	@Override
	public void update(Canvas c) {
//		paint(g);
		paint(c);
	}
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass.
	 * Warning: do not override getGraphics() or drawing might fail.
	 */
	public void update() {
//		paint(getGraphics());
//		paint(canvas);
		if (canvas == null) {
			System.out.println("StatePlaying.start: warning: no panel, dry-run game without graphics!");
			return;
		}
//		// draw the first person view and the map view if wanted
//		firstPersonView.draw(panel, px, py, walkStep, angle,
//				getPercentageForDistanceToExit()) ;
//		if (isInMapMode()) {
//			mapView.draw(panel, px, py, angle, walkStep,
//					isInShowMazeMode(),isInShowSolutionMode()) ;
//		}
		// update the screen with the buffer graphics
		paint(canvas);
	}

	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 * The given graphics object is the one that actually shows
	 * on the screen.
	 */
//	@Override
	public void paint(Canvas c) {
//		if (null == g) {
//			System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
//		}
//		else {
//			g.drawImage(bufferImage,0,0,null);
//		}

		if (null == c) {
			Log.v(TAG, "MazePanel.paint: no canvas object, skipping drawImage operation");
		}
		else {
			c.drawBitmap(bitmap, 0, 0, paint);
			invalidate();
		}
	}

	/**
	 * Obtains a graphics object that can be used for drawing.
	 * This MazePanel object internally stores the graphics object
	 * and will return the same graphics object over multiple method calls.
	 * The graphics object acts like a notepad where all clients draw
	 * on to store their contribution to the overall image that is to be
	 * delivered later.
	 * To make the drawing visible on screen, one needs to trigger
	 * a call of the paint method, which happens
	 * when calling the update method.
	 * @return graphics object to draw on, null if impossible to obtain image
	 */
	public Canvas getBufferGraphics() {
//		// if necessary instantiate and store a graphics object for later use
//		if (null == graphics) {
//			if (null == bufferImage) {
//				bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
//				if (null == bufferImage)
//				{
//					System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
//					return null; // still no buffer image, give up
//				}
//			}
//			graphics = (Graphics2D) bufferImage.getGraphics();
//			if (null == graphics) {
//				System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
//			}
//			else {
//				// System.out.println("MazePanel: Using Rendering Hint");
//				// For drawing in FirstPersonDrawer, setting rendering hint
//				// became necessary when lines of polygons
//				// that were not horizontal or vertical looked ragged
//				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						RenderingHints.VALUE_ANTIALIAS_ON);
//				graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//			}
//		}
//		return graphics;

		return canvas;
	}

	@Override
	public void commit() {
		update();
	}

	@Override
	public boolean isOperational() {
//		return (graphics != null);
		return canvas != null;
	}

	// ---------- COLOR METHODS ---------- //

	@Override
	public void setColor(int rgb) {
//		graphics.setColor(new Color(rgb));
		paint.setColor(rgb);
	}

	/**
	 * Sets the color for future drawing requests. The color setting
	 * will remain in effect till this method is called again and
	 * with a different color. Accounts for an alpha value.
	 * Substitute for Graphics.setColor method.
	 * @param r red value in the range 0.0 - 1.0
	 * @param g green value in the range 0.0 - 1.0
	 * @param b blue value in the range 0.0 - 1.0
	 * @param a alpha value in the range 0.0 - 1.0
	 */
	public void setColor(float r, float g, float b, float a) {
		paint.setColor(Color.argb(a, r, g, b));
	}

	@Override
	public int getColor() {
//		return graphics.getColor().getRGB();
		return paint.getColor();
	}

	@Override
	public void addBackground(float percentToExit) {
//		Color greenWM = Color.decode("#115740");
//		Color goldWM = Color.decode("#916f41");
//		Color yellowWM = Color.decode("#FFFF99");
//
//		// black rectangle in upper half of screen
//		// graphics.setColor(Color.black);
//		// dynamic color setting:
//		graphics.setColor(blend(yellowWM, goldWM, percentToExit));
//		graphics.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);
//		// grey rectangle in lower half of screen
//		// graphics.setColor(Color.darkGray);
//		// dynamic color setting:
//		graphics.setColor(blend(Color.lightGray, greenWM, percentToExit));
//		graphics.fillRect(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);

// 		Color greenWM = Color.valueOf(0x115740);
//		Color goldWM = Color.valueOf(0x916f41);
//		Color yellowWM = Color.valueOf(0xFFFF99);
		int greenWM = Color.parseColor("#115740");
		int goldWM = Color.parseColor("#916f41");
		int yellowWM = Color.parseColor("#FFFF99");

		// black rectangle in upper half of screen
		// graphics.setColor(Color.black);
		// dynamic color setting:
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(blend(yellowWM, goldWM, percentToExit));
		canvas.drawRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2, paint);
		// grey rectangle in lower half of screen
		// graphics.setColor(Color.darkGray);
		// dynamic color setting:
		paint.setColor(blend(Color.parseColor("lightgray"), greenWM, percentToExit));
		canvas.drawRect(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2, paint);
	}

	/**
	 * Calculates the weighted average of the two given colors.
	 * The weight for the first color is expected to be between
	 * 0 and 1. The weight for the other color is then 1-weight0.
	 * The result is the weighted average of the red, green, and
	 * blue components of the colors. The resulting alpha value
	 * for transparency is the max of the alpha values of both colors.
	 * @param fstColor is the first color
	 * @param sndColor is the second color
	 * @param weightFstColor is the weight of fstColor, {@code 0.0 <= weightFstColor <= 1.0}
	 * @return blend of both colors as weighted average of their rgb values
	 */
//	private Color blend(Color fstColor, Color sndColor, double weightFstColor) {
//		if (weightFstColor < 0.1)
//			return sndColor;
//		if (weightFstColor > 0.95)
//			return fstColor;
//		double r = weightFstColor * fstColor.getRed() + (1-weightFstColor) * sndColor.getRed();
//		double g = weightFstColor * fstColor.getGreen() + (1-weightFstColor) * sndColor.getGreen();
//		double b = weightFstColor * fstColor.getBlue() + (1-weightFstColor) * sndColor.getBlue();
//		double a = Math.max(fstColor.getAlpha(), sndColor.getAlpha());
//
//		return new Color((int) r, (int) g, (int) b, (int) a);
//	}

	private int blend(int fstColor, int sndColor, double weightFstColor) {
		if (weightFstColor < 0.1)
			return sndColor;
		if (weightFstColor > 0.95)
			return fstColor;
		double r = weightFstColor * Color.red(fstColor) + (1-weightFstColor) * Color.red(sndColor);
		double g = weightFstColor * Color.green(fstColor) + (1-weightFstColor) * Color.green(sndColor);
		double b = weightFstColor * Color.blue(fstColor) + (1-weightFstColor) * Color.blue(sndColor);
		double a = Math.max(Color.alpha(fstColor), Color.alpha(sndColor));

		return Color.argb((int) r, (int) g, (int) b, (int) a);
	}

	@Override
	public void addFilledRectangle(int x, int y, int width, int height) {
//		graphics.fillRect(x, y, width, height);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, width, height, paint);
	}

	@Override
	public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//		graphics.fillPolygon(xPoints, yPoints, nPoints);
		paint.setStyle(Paint.Style.FILL);
		Path polygon = new Path();
		for(int i = 0; i < nPoints; i++) {
			polygon.lineTo(xPoints[i], yPoints[i]);
		}
		polygon.lineTo(xPoints[0], yPoints[0]);
		canvas.drawPath(polygon, paint);
	}

	@Override
	public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//		graphics.drawPolygon(xPoints, yPoints, nPoints);
		paint.setStyle(Paint.Style.STROKE);
		Path polygon = new Path();
		for(int i = 0; i < nPoints; i++) {
			polygon.lineTo(xPoints[i], yPoints[i]);
		}
		polygon.lineTo(xPoints[0], yPoints[0]);
		canvas.drawPath(polygon, paint);
	}

	@Override
	public void addLine(int startX, int startY, int endX, int endY) {
//		graphics.drawLine(startX, startY, endX, endY);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawLine(startX, startY, endX, endY, paint);
	}

	@Override
	public void addFilledOval(int x, int y, int width, int height) {
//		graphics.fillOval(x, y, width, height);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawOval(x, y, width, height, paint);
	}

	@Override
	public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
//		graphics.drawArc(x, y, width, height, startAngle, arcAngle);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawArc(x, y, width, height, startAngle, arcAngle, true, paint);	// usecenter = true
	}

	@Override
	public void addMarker(float x, float y, String str) {
//		GlyphVector gv = Font.decode("Serif-PLAIN-16").createGlyphVector(graphics.getFontRenderContext(), str);
//		Rectangle2D rect = gv.getVisualBounds();
//		// need to update x, y by half of rectangle width, height
//		// to serve as x, y coordinates for drawing a GlyphVector
//		x -= rect.getWidth() / 2;
//		y += rect.getHeight() / 2;
//
//		graphics.drawGlyphVector(gv, x, y);

		paint.setTypeface(Typeface.create("Serif-PLAIN-16", Typeface.NORMAL));
		canvas.drawText(str, x, y, paint);
	}

	@Override
	public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {
//		RenderingHints.Key key = null;
//		Object val = null;
//		switch(hintKey) {
//			case KEY_RENDERING: key = RenderingHints.KEY_RENDERING;
//				break;
//			case KEY_ANTIALIASING: key = RenderingHints.KEY_ANTIALIASING;
//				break;
//			case KEY_INTERPOLATION: key = RenderingHints.KEY_INTERPOLATION;
//			default:
//				break;
//		}
//		switch(hintValue) {
//			case VALUE_RENDER_QUALITY: val = RenderingHints.VALUE_RENDER_QUALITY;
//				break;
//			case VALUE_ANTIALIAS_ON: val = RenderingHints.VALUE_ANTIALIAS_ON;
//				break;
//			case VALUE_INTERPOLATION_BILINEAR: val = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
//				break;
//			default:
//				break;
//		}
//
//		java.util.Map<RenderingHints.Key, Object> hints = Map.of(key, val);
//		graphics.addRenderingHints(hints);

		Log.v(TAG, "setRenderingHint() not yet implemented");
	}

}
