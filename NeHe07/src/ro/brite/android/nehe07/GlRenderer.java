package ro.brite.android.nehe07;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.brite.android.nehe07.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;


public class GlRenderer implements Renderer {

	private Context context;
	
	public GlRenderer(Context context)
	{
		this.context = context;
	}
	
	private final static float[][] cubeVertexCoords = new float[][] {
		new float[] { // top
			 1, 1,-1,
			-1, 1,-1,
			-1, 1, 1,
			 1, 1, 1
		},
		new float[] { // bottom
			 1,-1, 1,
			-1,-1, 1,
			-1,-1,-1,
			 1,-1,-1
		},
		new float[] { // front
			 1, 1, 1,
			-1, 1, 1,
			-1,-1, 1,
			 1,-1, 1
		},
		new float[] { // back
			 1,-1,-1,
			-1,-1,-1,
			-1, 1,-1,
			 1, 1,-1
		},
		new float[] { // left
			-1, 1, 1,
			-1, 1,-1,
			-1,-1,-1,
			-1,-1, 1
		},
		new float[] { // right
			 1, 1,-1,
			 1, 1, 1,
			 1,-1, 1,
			 1,-1,-1
		},
	};

	private final static float[][] cubeNormalCoords = new float[][] {
		new float[] { // top
			 0, 1, 0,
			 0, 1, 0,
			 0, 1, 0,
			 0, 1, 0
		},
		new float[] { // bottom
			 0,-1, 0,
			 0,-1, 0,
			 0,-1, 0,
			 0,-1, 0
		},
		new float[] { // front
			 0, 0, 1,
			 0, 0, 1,
			 0, 0, 1,
			 0, 0, 1
		},
		new float[] { // back
			 0, 0,-1,
			 0, 0,-1,
			 0, 0,-1,
			 0, 0,-1
		},
		new float[] { // left
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0
		},
		new float[] { // right
			 1, 0, 0,
			 1, 0, 0,
			 1, 0, 0,
			 1, 0, 0
		},
	};
	
	private final static float[][] cubeTextureCoords = new float[][] {
		new float[] { // top
			0,1,
			0,0,
			1,0,
			1,1
		},
		new float[] { // bottom
			1,1,
			0,1,
			0,0,
			1,0
		},
		new float[] { // front
			0,0,
			1,0,
			1,1,
			0,1
		},
		new float[] { // back
			1,0,
			1,1,
			0,1,
			0,0
		},
		new float[] { // left
			0,0,
			1,0,
			1,1,
			0,1
		},
		new float[] { // right
			1,0,
			1,1,
			0,1,
			0,0
		},
	};
	
	private final static float lightAmb[]= { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float lightDif[]= { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float lightPos[]= { 0.0f, 0.0f, 2.0f, 1.0f };
	
	private final static FloatBuffer[] cubeVertexBfr;
	private final static FloatBuffer[] cubeNormalBfr;
	private final static FloatBuffer[] cubeTextureBfr;

	private final static FloatBuffer lightAmbBfr;
	private final static FloatBuffer lightDifBfr;
	private final static FloatBuffer lightPosBfr;
	
	private IntBuffer texturesBuffer;
	
	private float cubeRotX;
	private float cubeRotY;
	private float cubeRotZ;
	
	static
	{
		cubeVertexBfr = new FloatBuffer[6];
		cubeNormalBfr = new FloatBuffer[6];
		cubeTextureBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++)
		{
			cubeVertexBfr[i] = FloatBuffer.wrap(cubeVertexCoords[i]);
			cubeNormalBfr[i] = FloatBuffer.wrap(cubeNormalCoords[i]);
			cubeTextureBfr[i] = FloatBuffer.wrap(cubeTextureCoords[i]);
		}
		
		lightAmbBfr = FloatBuffer.wrap(lightAmb);
		lightDifBfr = FloatBuffer.wrap(lightDif);
		lightPosBfr = FloatBuffer.wrap(lightPos);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		// lighting
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDifBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBfr);
		gl.glEnable(GL10.GL_LIGHTING);
		
		// create texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, texturesBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		
		// setup texture parameters and build texture
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		Bitmap texture = BitmapFactory.decodeResource(context.getResources(), R.drawable.crate);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// draw cube
		
		gl.glTranslatef(0, 0, -6);
		gl.glRotatef(cubeRotX, 1, 0, 0);
		gl.glRotatef(cubeRotY, 0, 1, 0);
		gl.glRotatef(cubeRotZ, 0, 0, 1);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		for (int i = 0; i < 6; i++) // draw each face
		{
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBfr[i]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTextureBfr[i]);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, cubeNormalBfr[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// update rotations
		cubeRotX += 1.2f;
		cubeRotY += 0.8f;
		cubeRotZ += 0.6f;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// avoid division by zero
		if (height == 0) height = 1;
		// draw on the entire screen
		gl.glViewport(0, 0, width, height);
		// setup projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 1.0f, 100.0f);
	}

}