/**
 * 
 */
package com.charles.actions;

/**
 * Represents the action which will be shown in {@link HorizontalActionBar} with
 * an image view.
 * 
 * @author charles
 * 
 */
public abstract class AbstractImageAction implements IAction {

	protected int mImageResId;

	/**
	 * Constructor.
	 * 
	 * @param resId
	 *            the image resource id.
	 */
	public AbstractImageAction(int resId) {
		this.mImageResId = resId;
	}
	
	/**
	 * Returns the image resource id.
	 * @return
	 */
	public int getImageResourceId() {
		return mImageResId;
	}

}
