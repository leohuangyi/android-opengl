package com.bn.Sample8_1;

import static com.bn.Sample8_1.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

//Բ��
public class Circle 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������
    int maTexCoorHandle; //��������������������
    int muMMatrixHandle;
    
    int maCameraHandle; //�����λ���������� 
    int maNormalHandle; //���㷨������������ 
    int maLightLocationHandle;//��Դλ����������  
    
    
    String mVertexShader;//������ɫ������ű�  	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//�����������ݻ���
	FloatBuffer   mTexCoorBuffer;//���������������ݻ���
	FloatBuffer   mNormalBuffer;//���㷨�������ݻ���
    int vCount=0;   
    float xAngle=0;//��x����ת�ĽǶ�
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
    
    public Circle(MySurfaceView mv,float scale,float r,int n)
    {
    	//���ó�ʼ���������ݵ�initVertexData����
    	initVertexData(scale,r,n);
    	//���ó�ʼ����ɫ����intShader����
    	initShader(mv);
    }
    
    //�Զ���ĳ�ʼ���������ݵķ���
    public void initVertexData(
    		float scale,	//��С
    		float r,		//�뾶
    		int n)		//�зֵķ���
    {
    	r=r*scale;
		float angdegSpan=360.0f/n;	//���ǵĶ���
		vCount=3*n;//�������������n�������Σ�ÿ�������ζ�����������
		
		float[] vertices=new float[vCount*3];//��������
		float[] textures=new float[vCount*2];//��������S��T����ֵ����
		//�������ݳ�ʼ��
		int count=0;
		int stCount=0;
		for(float angdeg=0;Math.ceil(angdeg)<360;angdeg+=angdegSpan)
		{
			double angrad=Math.toRadians(angdeg);//��ǰ����
			double angradNext=Math.toRadians(angdeg+angdegSpan);//��һ����
			//���ĵ�
			vertices[count++]=0;//��������
			vertices[count++]=0; 
			vertices[count++]=0;
			
			textures[stCount++]=0.5f;//st����
			textures[stCount++]=0.5f;
			//��ǰ��
			vertices[count++]=(float) (-r*Math.sin(angrad));//��������
			vertices[count++]=(float) (r*Math.cos(angrad));
			vertices[count++]=0;
			
			textures[stCount++]=(float) (0.5f-0.5f*Math.sin(angrad));//st����
			textures[stCount++]=(float) (0.5f-0.5f*Math.cos(angrad));
			//��һ��
			vertices[count++]=(float) (-r*Math.sin(angradNext));//��������
			vertices[count++]=(float) (r*Math.cos(angradNext));
			vertices[count++]=0;
			
			textures[stCount++]=(float) (0.5f-0.5f*Math.sin(angradNext));//st����
			textures[stCount++]=(float) (0.5f-0.5f*Math.cos(angradNext));
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//���������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //���������ݳ�ʼ�� 
        float[] normals=new float[vertices.length];
        for(int i=0;i<normals.length;i+=3){
        	normals[i]=0;
        	normals[i+1]=0;
        	normals[i+2]=1;
        }
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//�������㷨�������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        //�����������ݳ�ʼ��
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);//���������������ݻ���
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mTexCoorBuffer.put(textures);//�򻺳����з��붥����������
        mTexCoorBuffer.position(0);//���û�������ʼλ�� 
    }

    //�Զ����ʼ����ɫ��initShader����
    public void initShader(MySurfaceView mv){
    	//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal"); 
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation"); 
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
    }
    
    public void drawSelf(int texId)
    {        
    	 //�ƶ�ʹ��ĳ��shader����
    	 GLES20.glUseProgram(mProgram);        
         
         //�����ձ任������shader����
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
         //��λ�á���ת�任������shader����
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //�������λ�ô���shader����   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //����Դλ�ô���shader����   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         
         //���Ͷ���λ������
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //���Ͷ���������������
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         //���Ͷ��㷨��������
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
         
         //���ö���λ������
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         //���ö�����������
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //���ö��㷨��������
         GLES20.glEnableVertexAttribArray(maNormalHandle);
         
         
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //������������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount); 
    }
}