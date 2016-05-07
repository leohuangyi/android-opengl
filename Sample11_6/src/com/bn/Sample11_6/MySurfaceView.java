package com.bn.Sample11_6;
import static com.bn.Sample11_6.Constant.*;
import static com.bn.Sample11_6.Sample11_6Activity.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.bn.Sample11_6.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	static float direction=0;//���߷���
    static float cx=0;//�����x���� 
    static float cz=20;//�����z����
    
    static float tx=0;//�۲�Ŀ���x����
    static float tz=0;//�۲�Ŀ���z����   
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//�����ÿ��ת���ĽǶ�
    //�߳�ѭ���ı�־λ
    boolean flag=true;
    float x;
    float y;
    float Offset=20;
	SceneRenderer mRender;
	float preX;
	float preY;
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRender = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRender);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread()
				{
					@Override
					public void run()
					{
						while(flag)
						{
							if(x>0&&x<WIDTH/2&&y>0&&y<HEIGHT/2)
							{//��ǰ
								cx=cx-(float)Math.sin(direction)*1.0f;
								cz=cz-(float)Math.cos(direction)*1.0f;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//���
								cx=cx+(float)Math.sin(direction)*1.0f;
								cz=cz+(float)Math.cos(direction)*1.0f;
							}
							else if(x>0&&x<WIDTH/2&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction+DEGREE_SPAN;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction-DEGREE_SPAN;
							}
							try
							{
								Thread.sleep(100);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
			break;
			case MotionEvent.ACTION_UP:
				flag=false;
			break;
		}
		
		//�����µĹ۲�Ŀ���XZ����
		tx=(float)(cx-Math.sin(direction)*Offset);//�۲�Ŀ���x���� 
        tz=(float)(cz-Math.cos(direction)*Offset);//�۲�Ŀ���z����
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Mountion mountion;
		//ɽ������id
		int mountionId;
		int rockId;
		Sky sky;
		int skyId;
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //�����µ������λ��
            MatrixState.setCamera(cx,5,cz,tx,2,tz,0,1,0);
            MatrixState.pushMatrix();
            mountion.drawSelf(mountionId,rockId);
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            sky.drawSelf(skyId);
            MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ��߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 3000);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(cx,5,cz,tx,2,tz,0,1,0);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//������Ļ����ɫRGBA
            GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
    		yArray=loadLandforms(MySurfaceView.this.getResources(), R.drawable.land);
           
            mountion=new Mountion(MySurfaceView.this,yArray,yArray.length-1,yArray[0].length-1);
            sky=new Sky(MySurfaceView.this);
            //��ʼ������
            skyId=initTexture(R.drawable.sky,false); 
            mountionId=initTexture(R.drawable.grass,true);
            rockId=initTexture(R.drawable.rock,true);
		}  
    }
	//��������Id�ķ���
	public int initTexture(int drawableId,boolean isMipmap)
	{
		//��������ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		if(isMipmap)
		{
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);   
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		}
		else
		{
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);   
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		}
		//ST�����������췽ʽ
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);		
        
        //ͨ������������ͼƬ
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try   
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);        	
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }   
        
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //�����Ĳ�Σ�0��ʾ����ͼ��㣬��������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //�����߿�ߴ�
        );   
        //�Զ�����Mipmap����
        if(isMipmap)
        {
        	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }
        //�ͷ�����ͼ
        bitmapTmp.recycle();
        //��������ID
        return textureId;
	}
}