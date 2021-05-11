package com.xiekang.bluetooths.bluetooths.oxgen.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.creative.base.BaseDate;
import com.xiekang.bluetooths.bluetooths.oxgen.Oxgen_Bluetooth_Utlis;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths2.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 绘制血氧波形
 * draw pod wave by view
 */
public class DrawThreadNW extends BaseDraw {

	/** 血氧波形高度的最大值 可控制波形高度 */
	private final int ySpo2Max = 200; //130
	/** 当前波形增益 */
	protected int gain = 2;
	/** 血氧波形高度缩放比例 */
	private float zoomSpo2 = 0.0f;
	private String msg;
  private int drawColor= R.color.color_val_5fc079;
	/**
	 * 设置波形数据
	 * @param SPO_WAVE
	 */
	public void setSPO_WAVE(BaseDate.Wave SPO_WAVE) {
		this.SPO_WAVE.add(SPO_WAVE);
	}

	/**
	 * 设置波形颜色
	 * @param drawColor
	 */
	public void setDrawColor(@ColorRes int drawColor){
		 this.drawColor=drawColor;
		 postInvalidate();
	}

	private List<BaseDate.Wave> SPO_WAVE = new ArrayList<BaseDate.Wave>();

	public DrawThreadNW(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DrawThreadNW(Context context) {
		super(context);
	}

	public DrawThreadNW(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public synchronized void Continue() {
		super.Continue();
		cleanWaveData();
	}

	@Override
	public void cleanWaveData() {
		SPO_WAVE.clear();
		super.cleanWaveData();
	}

	@Override
	public void run() {
		super.run();
		synchronized (this) {
			while (!stop) {
				try {
					if (pause) {
						this.wait();
					}
					if (SPO_WAVE.size() > 0) {
						BaseDate.Wave data = SPO_WAVE.remove(0);
						addData(data.data);
						if (data.flag == 1) { //发送搏动标记
							mHandler.sendEmptyMessage(Oxgen_Bluetooth_Utlis.MSG_DATA_PULSE);
						}

						//设置参数，调整波形，adjust wave
						if (SPO_WAVE.size() > 20) {
							Thread.sleep(12); //18
						} else {
							Thread.sleep(25); //25
						}
					} else {
						Thread.sleep(500);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			cleanWaveData();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//如果是eclipse、androidstudio的编辑模式下,跳过以下代码
		if(isInEditMode()){
			return; 
		}
		
		if (msg != null && !msg.equals(""))
			drawMsg(canvas);
		paint.setPathEffect(cornerPathEffect);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(10);
		paint.setColor(drawColor);
		paint.setStrokeWidth(dm.density);
		Path path = new Path();
		path.moveTo(0, gethPx(data2draw[0]));
		for (int i = 0; i < data2draw.length; i++) {
			path.lineTo(i * stepx, gethPx(data2draw[i]));
		}
		canvas.drawPath(path, paint);
//		paint.setColor(Color.WHITE);
//		paint.setStrokeWidth(5);
//		canvas.drawLine(arraycnt * stepx, 0, arraycnt * stepx, height, paint);
	}

	private void drawMsg(Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(dm.density * 2);
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(dm.density * 20);
		canvas.drawText(msg, (weight - mPaint.measureText(msg)) / 2,
				height / 2, mPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		zoomSpo2 = (float) (height / ySpo2Max);
	}

	/**
	 * 获取该点在Y轴上的像素坐标
	 */
	private float gethPx(int data) {
		return height - zoomSpo2 * data;
	}

	/**
	 * 设置波形增益
	 * 
	 * @param gain
	 */
	private void setGain(int gain) {
		this.gain = gain == 0 ? 2 : gain;
	}

	private void drawMsg(String msg) {
		this.msg = msg;
		postInvalidate();
	}
}
