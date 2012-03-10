package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.util.Log;
import android.util.Pair;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Costume;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actors.Image;
import com.thoughtworks.xstream.XStream;

public class StageRecorder {

	private static StageRecorder instance;
	private ArrayList projectExecutionList = new ArrayList();
	private long startTime;
	private long pausedTime;
	private XStream xStream;

	private StageRecorder() {
		xStream = new XStream();
		xStream.alias("Pair", Pair.class);
		xStream.alias("Costume", Costume.class);
		xStream.alias("SoundInfo", SoundInfo.class);
		xStream.alias("Volume", Volume.class);
		xStream.omitField(Actor.class, "actions");
		xStream.omitField(Actor.class, "color");
		xStream.omitField(Actor.class, "parent");
		xStream.omitField(Actor.class, "touchable");
		//		xStream.omitField(Actor.class, "name");
		xStream.omitField(Actor.class, "originX");
		xStream.omitField(Actor.class, "originY");
		xStream.omitField(Actor.class, "toRemove");
		xStream.omitField(Actor.class, "width");
		xStream.omitField(Actor.class, "height");
		xStream.omitField(Image.class, "region");
		xStream.omitField(Costume.class, "xYWidthHeightLock");
		xStream.omitField(Costume.class, "imageLock");
		xStream.omitField(Costume.class, "scaleLock");
		xStream.omitField(Costume.class, "alphaValueLock");
		xStream.omitField(Costume.class, "brightnessLock");
		xStream.omitField(Costume.class, "disposeTexturesLock");
		xStream.omitField(Costume.class, "imageChanged");
		xStream.omitField(Costume.class, "sprite");
		xStream.omitField(Costume.class, "currentAlphaPixmap");
		xStream.omitField(Costume.class, "internalPath");
		xStream.omitField(Costume.class, "costumeChanged");
		pausedTime = 0;
	}

	public static StageRecorder getInstance() {
		if (instance == null) {
			instance = new StageRecorder();
		}
		return instance;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		projectExecutionList.clear();
	}

	public void pause() {
		if (pausedTime != 0) {
			pausedTime = System.currentTimeMillis();
		}
	}

	public void resume() {
		if (pausedTime > 0) {
			startTime += System.currentTimeMillis() - pausedTime;
		}
		pausedTime = 0;
	}

	public void updateCostume(Costume costume) {
		try {
			projectExecutionList.add(new Pair<Costume, Long>(costume.clone(), System.currentTimeMillis() - startTime));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void updateSound(SoundInfo soundInfo) {
		try {
			projectExecutionList.add(new Pair<SoundInfo, Long>(soundInfo.clone(), System.currentTimeMillis()
					- startTime));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void updateVolume(double volume) {
		projectExecutionList.add(new Pair<Volume, Long>(new Volume(volume), System.currentTimeMillis() - startTime));
	}

	public String getXml() {
		for (Object pair : projectExecutionList) {
			Log.d("!!", "!  " + ((Pair<Object, Long>) pair).second);
		}
		return xStream.toXML(projectExecutionList.toArray());
	}
}

class Volume {
	private double volume;

	Volume(double volume) {
		this.volume = volume;
	}
}
