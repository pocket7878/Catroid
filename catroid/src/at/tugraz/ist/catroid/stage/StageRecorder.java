package at.tugraz.ist.catroid.stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.utils.Utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class StageRecorder {
	private static StageRecorder instance;
	private long startTime;
	private long pausedTime;
	private JsonGenerator jsonGenerator;
	private ArrayList<RecordedSound> soundList;

	public static StageRecorder getInstance() {
		if (instance == null) {
			instance = new StageRecorder();
		}
		return instance;
	}

	public void start() {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File recordedFile = new File(Utils.buildPath(Constants.DEFAULT_ROOT, currentProject), "record.json");
		if (recordedFile.exists()) {
			recordedFile.delete();
		}
		startTime = System.currentTimeMillis();
		pausedTime = 0;
		soundList = new ArrayList<RecordedSound>();

		JsonFactory jsonFactory = new JsonFactory();

		try {
			jsonGenerator = jsonFactory.createJsonGenerator(new FileOutputStream(new File(Utils.buildPath(
					Constants.DEFAULT_ROOT, currentProject), "record.json")));
			jsonGenerator.writeStartObject();
			jsonGenerator.writeNumberField("screenWidth",
					ProjectManager.getInstance().getCurrentProject().virtualScreenWidth);
			jsonGenerator.writeNumberField("screenHeight",
					ProjectManager.getInstance().getCurrentProject().virtualScreenHeight);
			jsonGenerator.writeArrayFieldStart("costumes");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

	public void finishAndSave() {
		try {
			jsonGenerator.writeEndArray();
			jsonGenerator.writeArrayFieldStart("sounds");
			for (RecordedSound recordedSound : soundList) {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeBooleanField("isPlaying", recordedSound.isPlaying);
				jsonGenerator.writeStringField("filename", recordedSound.filename);
				jsonGenerator.writeNumberField("timestamp", recordedSound.timestamp);
				jsonGenerator.writeEndObject();
			}
			jsonGenerator.writeEndArray();
			jsonGenerator.writeNumberField("duration", getTime());
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void recordCostume(Costume costume) {
		try {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("name", costume.getSprite().getName());
			if (costume.getCostumeData() != null) {
				jsonGenerator.writeStringField("filename", costume.getCostumeData().getCostumeFileName());
			}
			jsonGenerator.writeNumberField("timestamp", getTime());
			jsonGenerator.writeNumberField("alphaValue", costume.getAlphaValue());
			jsonGenerator.writeNumberField("rotation", costume.rotation);
			jsonGenerator.writeNumberField("scaleX", costume.scaleX);
			jsonGenerator.writeNumberField("scaleY", costume.scaleY);
			jsonGenerator.writeBooleanField("show", costume.show);
			jsonGenerator.writeBooleanField("visible", costume.visible);
			jsonGenerator.writeNumberField("brightnessValue", costume.getBrightnessValue());
			jsonGenerator.writeNumberField("x", costume.x);
			jsonGenerator.writeNumberField("y", costume.y);
			jsonGenerator.writeNumberField("zPosition", costume.zPosition);
			jsonGenerator.writeEndObject();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void recordSound(SoundInfo soundInfo) {
		soundList.add(new RecordedSound(soundInfo.getSoundFileName(), soundInfo.isPlaying, getTime()));
	}

	public void recordTts(TextToSpeech textToSpeech, String text, HashMap<String, String> speakParameter) {
		String filename = "TTS_" + text.hashCode() + ".wav";
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(filename);
		soundInfo.setTitle(filename);
		soundInfo.isPlaying = true;
		textToSpeech.synthesizeToFile(text, speakParameter, Constants.DEFAULT_ROOT + "/"
				+ ProjectManager.getInstance().getCurrentProject().getName() + "/" + Constants.SOUND_DIRECTORY + "/"
				+ filename);
		recordSound(soundInfo);
	}

	public long getTime() {
		return System.currentTimeMillis() - startTime;
	}

	public long getStartTime() {
		return startTime;
	}
}

class RecordedSound {
	String filename;
	boolean isPlaying;
	long timestamp;

	public RecordedSound(String fileName, boolean isPlaying, long timestamp) {
		this.filename = fileName;
		this.isPlaying = isPlaying;
		this.timestamp = timestamp;
	}
}
