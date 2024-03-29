package ro.brite.android.nehe06;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


public class GlApp extends Activity {
	
	private GLSurfaceView surface;
	private GlRenderer renderer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        surface = new GLSurfaceView(this);
        renderer = new GlRenderer(this);
        surface.setRenderer(renderer);
        setContentView(surface);
    }

	@Override
	protected void onPause() {
		super.onPause();
		surface.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		surface.onResume();
	}
	
}