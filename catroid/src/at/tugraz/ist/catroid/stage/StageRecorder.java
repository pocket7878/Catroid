package at.tugraz.ist.catroid.stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;
import android.util.Pair;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actors.Image;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class StageRecorder {

	private static StageRecorder instance;
	private Recording recording;
	private long startTime;
	private long pausedTime;
	private XStream xStream;

	private StageRecorder() {
		xStream = new XStream();
		xStream.setMode(XStream.NO_REFERENCES);
		xStream.alias("Recording", Recording.class);
		xStream.alias("Pair", Pair.class);
		xStream.alias("Costume", Costume.class);
		xStream.alias("SoundInfo", SoundInfo.class);
		xStream.omitField(Actor.class, "actions");
		xStream.omitField(Actor.class, "color");
		xStream.omitField(Actor.class, "parent");
		xStream.omitField(Actor.class, "touchable");
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
		xStream.registerConverter(new SoundInfoConverter());
	}

	public static StageRecorder getInstance() {
		if (instance == null) {
			instance = new StageRecorder();
		}
		return instance;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		pausedTime = 0;
		recording = new Recording();
		recording.screenWidth = Values.SCREEN_WIDTH;
		recording.screenHeight = Values.SCREEN_HEIGHT;
	}

	public void pause() {
		if (pausedTime == 0) {
			pausedTime = System.currentTimeMillis();
		}
	}

	public void resume() {
		if (pausedTime > 0) {
			startTime += System.currentTimeMillis() - pausedTime;
		}
		pausedTime = 0;
	}

	public String finishAndSave() {
		recording.duration = getTime();
		String xml = xStream.toXML(recording);

		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File outputFile = new File(Utils.buildPath(Consts.DEFAULT_ROOT, currentProject), "record.xml");
		try {
			FileOutputStream outputstream = new FileOutputStream(outputFile);
			outputstream.write(xml.getBytes());
			outputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	public void recordCostume(Costume costume) {
		try {
			recording.costumeList.add(new Pair<Costume, Long>(costume.clone(), getTime()));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void recordSound(SoundInfo soundInfo) {
		try {
			recording.soundList.add(new Pair<SoundInfo, Long>(soundInfo.clone(), getTime()));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void recordTts(TextToSpeech textToSpeech, String text, HashMap<String, String> speakParameter) {
		String filename = "TTS_" + text.hashCode() + ".wav";
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(filename);
		soundInfo.setTitle(filename);
		soundInfo.isPlaying = true;
		textToSpeech.synthesizeToFile(text, speakParameter, Consts.DEFAULT_ROOT + "/"
				+ ProjectManager.getInstance().getCurrentProject().getName() + "/" + Consts.SOUND_DIRECTORY + "/"
				+ filename);
		recordSound(soundInfo);
	}

	public Recording getRecording() {
		return recording;
	}

	public long getTime() {
		return System.currentTimeMillis() - startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public static class Recording {
		public int screenWidth;
		public int screenHeight;
		public long duration;
		public ArrayList<Pair<Costume, Long>> costumeList = new ArrayList<Pair<Costume, Long>>();
		public ArrayList<Pair<SoundInfo, Long>> soundList = new ArrayList<Pair<SoundInfo, Long>>();
	}
}

class SoundInfoConverter implements Converter {

	public boolean canConvert(Class type) {
		return type.equals(SoundInfo.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		SoundInfo soundInfo = (SoundInfo) source;
		writer.startNode("name");
		writer.setValue(soundInfo.getTitle());
		writer.endNode();

		writer.startNode("fileName");
		writer.setValue(soundInfo.getSoundFileName());
		writer.endNode();

		writer.startNode("isPlaying");
		writer.setValue("" + soundInfo.isPlaying);
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}

}
