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

	private Canvas canvas;
	private Bitmap bitmap;
	private Paint paint;

	private static final String TAG = "MazePanel";

	/**
	 * Constructor. Object is not focusable.
	 */
	public MazePanel(Context context) {
		super(context);
		init();
		setFocusable(false);
//		bufferImage = null; // bufferImage initialized separately and later
//		graphics = null;	// same for graphics
//		myTestImage(canvas);
	}

	public MazePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
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
//		Log.v(TAG, "Redrawing MazePanel");
		paint(UIcanvas);
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
		paint.setStrokeWidth(1);
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
		paint.setStrokeWidth(6);
		paint.setColor(Color.BLACK);
		c.drawLine(160, 800, 200, 1000, paint);
		c.drawLine(300, 900, 600, 800, paint);
		invalidate();
	}

	// --------------------------------------------- //

	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 * The given graphics object is the one that actually shows
	 * on the screen.
	 */
//	@Override
	public void paint(Canvas c) {
		if (null == c) {
			Log.v(TAG, "MazePanel.paint: no canvas object, skipping drawImage operation");
		}
		else {
			c.drawBitmap(bitmap, 0, 0, paint);
//			invalidate();
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
		return canvas;
	}

	@Override
	public void commit() {
		invalidate();
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
		int sky = Color.parseColor("#88D2E5");
		int ground = Color.parseColor("#2F7C3A");;
		int ceiling = Color.parseColor("#A27842");

		// black rectangle in upper half of screen
		// graphics.setColor(Color.black);
		// dynamic color setting:
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(0);
		paint.setColor(blend(ceiling, sky, percentToExit));
		canvas.drawRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2, paint);
		// grey rectangle in lower half of screen
		// graphics.setColor(Color.darkGray);
		// dynamic color setting:
		paint.setColor(blend(Color.rgb(232, 226, 220), ground, percentToExit));
		canvas.drawRect(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, paint);
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
	private int blend(int fstColor, int sndColor, double weightFstColor) {
		if (weightFstColor < 0.1)
			return sndColor;
		if (weightFstColor > 0.95)
			return fstColor;
		double r = weightFstColor * Color.red(fstColor) + (1-weightFstColor) * Color.red(sndColor);
		double g = weightFstColor * Color.green(fstColor) + (1-weightFstColor) * Color.green(sndColor);
		double b = weightFstColor * Color.blue(fstColor) + (1-weightFstColor) * Color.blue(sndColor);
		double a = Math.max(Color.alpha(fstColor), Color.alpha(sndColor));

		return Color.argb((int) a, (int) r, (int) g, (int) b);
	}

	@Override
	public void addFilledRectangle(int x, int y, int width, int height) {
//		graphics.fillRect(x, y, width, height);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(0);
		canvas.drawRect(x, y, x+width, y+height, paint);
	}

	@Override
	public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//		graphics.fillPolygon(xPoints, yPoints, nPoints);
		Path polygon = new Path();
		polygon.moveTo(xPoints[0], yPoints[0]);
		for(int i = 0; i < nPoints; i++) {
			polygon.lineTo(xPoints[i], yPoints[i]);
		}
		polygon.lineTo(xPoints[0], yPoints[0]);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(0);
		canvas.drawPath(polygon, paint);
	}

	@Override
	public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//		graphics.drawPolygon(xPoints, yPoints, nPoints);
		Path polygon = new Path();
		polygon.moveTo(xPoints[0], yPoints[0]);
		for(int i = 0; i < nPoints; i++) {
			polygon.lineTo(xPoints[i], yPoints[i]);
		}
		polygon.lineTo(xPoints[0], yPoints[0]);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawPath(polygon, paint);
	}

	@Override
	public void addLine(int startX, int startY, int endX, int endY) {
//		graphics.drawLine(startX, startY, endX, endY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		canvas.drawLine(startX, startY, endX, endY, paint);
	}

	@Override
	public void addFilledOval(int x, int y, int width, int height) {
//		graphics.fillOval(x, y, width, height);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(0);
		canvas.drawOval(x, y, x+width, y+height, paint);
	}

	@Override
	public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
//		graphics.drawArc(x, y, width, height, startAngle, arcAngle);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(0);
		canvas.drawArc(x, y, x+width, y+height, startAngle, arcAngle, false, paint);	// usecenter = true
	}

	@Override
	public void addMarker(float x, float y, String str) {
		int textSize = 34;
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.create("Serif-PLAIN-16", Typeface.NORMAL));
		canvas.drawText(str, (float) (x-(.25)*textSize), (float) (y+(.25)*textSize), paint);
	}

	@Override
	public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {
		Log.v(TAG, "setRenderingHint() not implemented");
	}

}
