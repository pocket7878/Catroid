package at.tugraz.ist.catroid.stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;

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
		xStream.alias("RecordedCostume", RecordedCostume.class);
		xStream.alias("RecordedSound", RecordedSound.class);
	}

	public static StageRecorder getInstance() {
		if (instance == null) {
			instance = new StageRecorder();
		}
		return instance;
	}

	public void start() {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File recordedFile = new File(Utils.buildPath(Consts.DEFAULT_ROOT, currentProject), "record.xml");
		if (recordedFile.exists()) {
			recordedFile.delete();
		}
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
		recording.costumeList.add(new RecordedCostume(costume.name, costume.rotation, costume.scaleX, costume.scaleY,
				costume.visible, costume.show, costume.x, costume.y, costume.getCostumeData() != null ? costume
						.getCostumeData().getCostumeFileName() : null, costume.getBrightnessValue(), costume
						.getAlphaValue(), costume.zPosition, getTime()));
	}

	public void recordSound(SoundInfo soundInfo) {
		recording.soundList.add(new RecordedSound(soundInfo.getSoundFileName(), soundInfo.isPlaying, getTime()));
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
		public ArrayList<RecordedCostume> costumeList = new ArrayList<RecordedCostume>();
		public ArrayList<RecordedSound> soundList = new ArrayList<RecordedSound>();
	}
}

class RecordedCostume {
	String name;
	float rotation;
	float scaleX;
	float scaleY;
	boolean visible;
	boolean show;
	float x;
	float y;
	String fileName;
	float brightnessValue;
	float alphaValue;
	int zPosition;
	long timestamp;

	public RecordedCostume(String name, float rotation, float scaleX, float scaleY, boolean visible, boolean show,
			float x, float y, String fileName, float brightnessValue, float alphaValue, int zPosition, long timestamp) {
		this.name = name;
		this.rotation = rotation;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.visible = visible;
		this.show = show;
		this.x = x;
		this.y = y;
		this.fileName = fileName;
		this.brightnessValue = brightnessValue;
		this.alphaValue = alphaValue;
		this.zPosition = zPosition;
		this.timestamp = timestamp;
	}
}

class RecordedSound {
	String fileName;
	boolean isPlaying;
	long timestamp;

	public RecordedSound(String fileName, boolean isPlaying, long timestamp) {
		this.fileName = fileName;
		this.isPlaying = isPlaying;
		this.timestamp = timestamp;
	}
}
