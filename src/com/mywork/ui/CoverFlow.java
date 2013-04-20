package com.mywork.ui;

import android.content.Context;

import android.graphics.Camera;

import android.graphics.Matrix;

import android.util.AttributeSet;

import android.view.View;

import android.view.animation.Transformation;

import android.widget.Gallery;

import android.widget.ImageView;

public class CoverFlow extends Gallery {

	/**
	 * 036. Graphics Camera used for transforming the matrix of ImageViews 037.
	 */

	private Camera mCamera = new Camera();

	/**
	 * 041. The maximum angle the Child ImageView will be rotated by 042.
	 */

	private int mMaxRotationAngle = 60;

	/**
	 * 046. The maximum zoom on the centre Child 047.
	 */

	private int mMaxZoom = -50;

	/**
	 * 051. The Centre of the Coverflow 052.
	 **/
	private int mCoveflowCenter;

	public CoverFlow(Context context) {

		super(context);

		this.setStaticTransformationsEnabled(true);

	}

	public CoverFlow(Context context, AttributeSet attrs) {

		super(context, attrs);

		this.setStaticTransformationsEnabled(true);

	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		this.setStaticTransformationsEnabled(true);

	}

	/**
	 * 071. Get the max rotational angle of the image 072.
	 * 
	 * @return the mMaxRotationAngle 073.
	 */

	public int getMaxRotationAngle() {

		return mMaxRotationAngle;

	}

	/**
	 * 079. Set the max rotational angle of each image 080.
	 * 
	 * @param maxRotationAngle
	 *            the mMaxRotationAngle to set 081.
	 */

	public void setMaxRotationAngle(int maxRotationAngle) {

		mMaxRotationAngle = maxRotationAngle;

	}

	/**
	 * 087. Get the Max zoom of the centre image 088.
	 * 
	 * @return the mMaxZoom 089.
	 */

	public int getMaxZoom() {

		return mMaxZoom;

	}

	/**
	 * 095. Set the max zoom of the centre image 096.
	 * 
	 * @param maxZoom
	 *            the mMaxZoom to set 097.
	 */

	public void setMaxZoom(int maxZoom) {

		mMaxZoom = maxZoom;

	}

	/**
	 * 103. Get the Centre of the Coverflow 104.
	 * 
	 * @return The centre of this Coverflow. 105.
	 */

	private int getCenterOfCoverflow() {

		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();

	}

	/**
	 * 111. Get the Centre of the View 112.
	 * 
	 * @return The centre of the given view. 113.
	 */

	private static int getCenterOfView(View view) {

		return view.getLeft() + view.getWidth() / 2;

	}

	/**
	 * 118. {@inheritDoc} 119.
	 * 
	 * 120.
	 * 
	 * @see #setStaticTransformationsEnabled(boolean) 121.
	 */

	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int childCenter = getCenterOfView(child);

		final int childWidth = child.getWidth();

		int rotationAngle = 0;

		t.clear();

		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {

			transformImageBitmap((ImageView) child, t, 0);

		} else {

			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);

			if (Math.abs(rotationAngle) > mMaxRotationAngle) {

				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;

			}

			transformImageBitmap((ImageView) child, t, rotationAngle);

		}

		return true;

	}

	/**
	 * This is called during layout when the size of this view has changed.
	 * If you were just added to the view hierarchy, you're called with the
	 * old values of 0.
	 * 
	 * 
	 * @param w
	 *            Current width of this view. 
	 * @param h
	 *            Current height of this view. 
	 * @param oldw
	 *            Old width of this view. 
	 * @param oldh
	 *            Old height of this view. 
	 */

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		mCoveflowCenter = getCenterOfCoverflow();

		super.onSizeChanged(w, h, oldw, oldh);

	}

	/**
	 * Transform the Image Bitmap by the Angle passed 
	 * 
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate 
	 * @param t
	 *            transformation 
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */

	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {

		mCamera.save();

		final Matrix imageMatrix = t.getMatrix();
		;

		final int imageHeight = child.getLayoutParams().height;
		;

		final int imageWidth = child.getLayoutParams().width;

		final int rotation = Math.abs(rotationAngle);

		mCamera.translate(0.0f, 0.0f, 100.0f);

		// As the angle of the view gets less, zoom in

		if (rotation < mMaxRotationAngle) {

			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));

			mCamera.translate(0.0f, 0.0f, zoomAmount);

		}

		mCamera.rotateY(rotationAngle);

		mCamera.getMatrix(imageMatrix);

		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));

		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));

		mCamera.restore();

	}

}

