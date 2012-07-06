/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

public class StorageHandler {
	private static final String SIMON_TUTORIAL_CAT = "SIMON";

	private static final int JPG_COMPRESSION_SETTING = 95;
	private static final String TAG = StorageHandler.class.getSimpleName();
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";

	private static final String BACKGROUND = null;

	private static final String NORMAL_CAT = null;

	private static final String BANZAI_CAT = null;

	private static final String CHESHIRE_CAT = null;
	private static StorageHandler instance;
	private XStream xstream;

	private StorageHandler() throws IOException {

		xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new CatroidFieldKeySorter())));
		xstream.processAnnotations(Project.class);
		xstream.aliasPackage("Bricks", "at.tugraz.ist.catroid.content.bricks");
		xstream.aliasPackage("Common", "at.tugraz.ist.catroid.common");
		xstream.aliasPackage("Content", "at.tugraz.ist.catroid.content");

		if (!Utils.hasSdCard()) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();
	}

	private void createCatroidRoot() {
		File catroidRoot = new File(Constants.DEFAULT_ROOT);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
	}

	public synchronized static StorageHandler getInstance() {
		if (instance == null) {
			try {
				instance = new StorageHandler();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Exception in Storagehandler, please refer to the StackTrace");
			}
		}
		return instance;
	}

	public Project loadProject(String projectName) {
		createCatroidRoot();
		try {
			if (NativeAppActivity.isRunning()) {
				InputStream spfFileStream = NativeAppActivity.getContext().getAssets().open(projectName);
				return (Project) xstream.fromXML(spfFileStream);
			}

			File projectDirectory = new File(Utils.buildProjectPath(projectName));

			if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				return (Project) xstream.fromXML(projectFileStream);
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean saveProject(Project project) {
		createCatroidRoot();
		if (project == null) {
			return false;
		}
		try {
			String projectFile = xstream.toXML(project);

			String projectDirectoryName = Utils.buildProjectPath(project.getName());
			File projectDirectory = new File(projectDirectoryName);

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

				File imageDirectory = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY));
				imageDirectory.mkdir();

				File noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY,
						Constants.NO_MEDIA_FILE));
				noMediaFile.createNewFile();

				File soundDirectory = new File(projectDirectoryName + "/" + Constants.SOUND_DIRECTORY);
				soundDirectory.mkdir();

				noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.SOUND_DIRECTORY,
						Constants.NO_MEDIA_FILE));
				noMediaFile.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(Utils.buildPath(projectDirectoryName,
					Constants.PROJECTCODE_NAME)), Constants.BUFFER_8K);

			writer.write(XML_HEADER.concat(projectFile));
			writer.flush();
			writer.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "saveProject threw an exception and failed.");
			return false;
		}
	}

	public boolean deleteProject(Project project) {
		if (project != null) {
			return UtilFile.deleteDirectory(new File(Utils.buildProjectPath(project.getName())));
		}
		return false;
	}

	public boolean projectExists(String projectName) {
		File projectDirectory = new File(Utils.buildProjectPath(projectName));
		if (!projectDirectory.exists()) {
			return false;
		}
		return true;
	}

	public File copySoundFile(String path) throws IOException {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File soundDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProject),
				Constants.SOUND_DIRECTORY));

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}
		String inputFileChecksum = Utils.md5Checksum(inputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
		if (fileChecksumContainer.containsChecksum(inputFileChecksum)) {
			fileChecksumContainer.addChecksum(inputFileChecksum, null);
			return new File(fileChecksumContainer.getPath(inputFileChecksum));
		}
		File outputFile = new File(Utils.buildPath(soundDirectory.getAbsolutePath(), inputFileChecksum + "_"
				+ inputFile.getName()));

		return copyFile(outputFile, inputFile, soundDirectory);
	}

	public File copyImage(String currentProjectName, String inputFilePath, String newName) throws IOException {
		String newFilePath;
		File imageDirectory = new File(Utils.buildPath(Utils.buildProjectPath(currentProjectName),
				Constants.IMAGE_DIRECTORY));

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
		FileChecksumContainer checksumCont = ProjectManager.getInstance().fileChecksumContainer;

		Project project = ProjectManager.getInstance().getCurrentProject();
		if ((imageDimensions[0] <= project.virtualScreenWidth) && (imageDimensions[1] <= project.virtualScreenHeight)) {
			String checksumSource = Utils.md5Checksum(inputFile);

			if (newName != null) {
				newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(), checksumSource + "_" + newName);
			} else {
				newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(),
						checksumSource + "_" + inputFile.getName());
				if (checksumCont.containsChecksum(checksumSource)) {
					checksumCont.addChecksum(checksumSource, newFilePath);
					return new File(checksumCont.getPath(checksumSource));
				}
			}
			File outputFile = new File(newFilePath);
			return copyFile(outputFile, inputFile, imageDirectory);
		} else {
			File outputFile = new File(Utils.buildPath(imageDirectory.getAbsolutePath(), inputFile.getName()));
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
		}
	}

	private File copyAndResizeImage(File outputFile, File inputFile, File imageDirectory) throws IOException {
		Project project = ProjectManager.getInstance().getCurrentProject();
		Bitmap bitmap = ImageEditing.getScaledBitmapFromPath(inputFile.getAbsolutePath(), project.virtualScreenWidth,
				project.virtualScreenHeight, true);

		saveBitmapToImageFile(outputFile, bitmap);

		String checksumCompressedFile = Utils.md5Checksum(outputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
		String newFilePath = Utils.buildPath(imageDirectory.getAbsolutePath(),
				checksumCompressedFile + "_" + inputFile.getName());

		if (!fileChecksumContainer.addChecksum(checksumCompressedFile, newFilePath)) {
			outputFile.delete();
			return new File(fileChecksumContainer.getPath(checksumCompressedFile));
		}

		File compressedFile = new File(newFilePath);
		outputFile.renameTo(compressedFile);

		return compressedFile;
	}

	public static void saveBitmapToImageFile(File outputFile, Bitmap bitmap) throws FileNotFoundException {
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		try {
			if (outputFile.getName().endsWith(".jpg") || outputFile.getName().endsWith(".jpeg")
					|| outputFile.getName().endsWith(".JPG") || outputFile.getName().endsWith(".JPEG")) {
				bitmap.compress(CompressFormat.JPEG, JPG_COMPRESSION_SETTING, outputStream);
			} else {
				bitmap.compress(CompressFormat.PNG, 0, outputStream);
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {

		}
	}

	private File copyFile(File destinationFile, File sourceFile, File directory) throws IOException {
		FileInputStream inputStream = new FileInputStream(sourceFile);
		FileChannel inputChannel = inputStream.getChannel();
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		FileChannel outputChannel = outputStream.getChannel();

		String checksumSource = Utils.md5Checksum(sourceFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;

		try {
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
			return destinationFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputChannel != null) {
				inputChannel.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputChannel != null) {
				outputChannel.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public void deleteFile(String filepath) {
		FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;
		try {
			if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				toDelete.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the default project and saves it to the filesystem
	 * 
	 * @return the default project object if successful, else null
	 * @throws IOException
	 */
	public Project createDefaultProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createDefaultProject(projectName, context);
	}

	/**
	 * Creates the default project and saves it to the file system
	 * 
	 * @return the default project object if successful, else null
	 * @throws IOException
	 */
	public Project createDefaultProject(String projectName, Context context) throws IOException {
		Project defaultProject = new Project(context, projectName);
		saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite("Catroid");
		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		Script backgroundStartScript = new StartScript(backgroundSprite);
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);

		File normalCatTemp = savePictureFromResourceInProject(projectName, NORMAL_CAT, R.drawable.catroid, context);
		File banzaiCatTemp = savePictureFromResourceInProject(projectName, BANZAI_CAT, R.drawable.catroid_banzai,
				context);
		File cheshireCatTemp = savePictureFromResourceInProject(projectName, CHESHIRE_CAT, R.drawable.catroid_cheshire,
				context);
		File backgroundTemp = savePictureFromResourceInProject(projectName, BACKGROUND, R.drawable.background_blueish,
				context);

		String directoryName = Utils.buildPath(Constants.DEFAULT_ROOT, projectName, Constants.IMAGE_DIRECTORY);
		File normalCat = new File(Utils.buildPath(directoryName,
				Utils.md5Checksum(normalCatTemp) + "_" + normalCatTemp.getName()));
		File banzaiCat = new File(Utils.buildPath(directoryName,
				Utils.md5Checksum(banzaiCatTemp) + "_" + banzaiCatTemp.getName()));
		File cheshireCat = new File(Utils.buildPath(directoryName, Utils.md5Checksum(cheshireCatTemp) + "_"
				+ cheshireCatTemp.getName()));
		File background = new File(Utils.buildPath(directoryName, Utils.md5Checksum(backgroundTemp) + "_"
				+ backgroundTemp.getName()));

		normalCatTemp.renameTo(normalCat);
		banzaiCatTemp.renameTo(banzaiCat);
		cheshireCatTemp.renameTo(cheshireCat);
		backgroundTemp.renameTo(background);

		CostumeData normalCatCostumeData = new CostumeData();
		normalCatCostumeData.setCostumeName(NORMAL_CAT);
		normalCatCostumeData.setCostumeFilename(normalCat.getName());

		CostumeData banzaiCatCostumeData = new CostumeData();
		banzaiCatCostumeData.setCostumeName(BANZAI_CAT);
		banzaiCatCostumeData.setCostumeFilename(banzaiCat.getName());

		CostumeData cheshireCatCostumeData = new CostumeData();
		cheshireCatCostumeData.setCostumeName(CHESHIRE_CAT);
		cheshireCatCostumeData.setCostumeFilename(cheshireCat.getName());

		CostumeData backgroundCostumeData = new CostumeData();
		backgroundCostumeData.setCostumeName(BACKGROUND);
		backgroundCostumeData.setCostumeFilename(background.getName());

		ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();
		costumeDataList.add(normalCatCostumeData);
		costumeDataList.add(banzaiCatCostumeData);
		costumeDataList.add(cheshireCatCostumeData);
		ArrayList<CostumeData> costumeDataList2 = backgroundSprite.getCostumeDataList();
		costumeDataList2.add(backgroundCostumeData);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite);
		setCostumeBrick1.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite);
		setCostumeBrick2.setCostume(banzaiCatCostumeData);

		SetCostumeBrick setCostumeBrick3 = new SetCostumeBrick(sprite);
		setCostumeBrick3.setCostume(cheshireCatCostumeData);

		SetCostumeBrick backgroundBrick = new SetCostumeBrick(backgroundSprite);
		backgroundBrick.setCostume(backgroundCostumeData);

		WaitBrick waitBrick1 = new WaitBrick(sprite, 500);
		WaitBrick waitBrick2 = new WaitBrick(sprite, 500);

		startScript.addBrick(setCostumeBrick);

		whenScript.addBrick(setCostumeBrick2);
		whenScript.addBrick(waitBrick1);
		whenScript.addBrick(setCostumeBrick3);
		whenScript.addBrick(waitBrick2);
		whenScript.addBrick(setCostumeBrick1);
		backgroundStartScript.addBrick(backgroundBrick);

		defaultProject.addSprite(sprite);
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		backgroundSprite.addScript(backgroundStartScript);

		this.saveProject(defaultProject);

		return defaultProject;
	}

	/**
	 * Creates the default project and saves it to the file system
	 * 
	 * @return the default project object if successful, else null
	 * @throws IOException
	 */
	public Project createThumbTutorialProject(Context context) throws IOException {
		String projectName = "thumb_tutorial";
		Project defaultProject = new Project(context, projectName);
		saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite("Catroid");
		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		Script backgroundStartScript = new StartScript(backgroundSprite);
		Script startScript = new StartScript(sprite);

		String directoryName = Utils.buildPath(Constants.DEFAULT_ROOT, projectName, Constants.IMAGE_DIRECTORY);

		ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();

		for (int count = 1; count < 15; count++) {
			int drawableId = context.getResources().getIdentifier("simon_tut_" + count, "drawable",
					"at.tugraz.ist.catroid");
			File simonTemp = savePictureFromResourceInProject(projectName, SIMON_TUTORIAL_CAT + "_" + count,
					drawableId, context);
			File simon = new File(Utils.buildPath(directoryName,
					Utils.md5Checksum(simonTemp) + "_" + simonTemp.getName()));
			simonTemp.renameTo(simon);
			CostumeData simonCostumeData = new CostumeData();
			simonCostumeData.setCostumeName(SIMON_TUTORIAL_CAT + "_" + count);
			simonCostumeData.setCostumeFilename(simon.getName());
			SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
			setCostumeBrick.setCostume(simonCostumeData);
			startScript.addBrick(setCostumeBrick);
			WaitBrick waitBrick = new WaitBrick(sprite, 200);
			startScript.addBrick(waitBrick);
			costumeDataList.add(simonCostumeData);
		}

		File backgroundTemp = savePictureFromResourceInProject(projectName, BACKGROUND, R.drawable.background_blueish,
				context);

		File background = new File(Utils.buildPath(directoryName, Utils.md5Checksum(backgroundTemp) + "_"
				+ backgroundTemp.getName()));

		backgroundTemp.renameTo(background);

		CostumeData backgroundCostumeData = new CostumeData();
		backgroundCostumeData.setCostumeName(BACKGROUND);
		backgroundCostumeData.setCostumeFilename(background.getName());

		ArrayList<CostumeData> costumeDataList2 = backgroundSprite.getCostumeDataList();
		costumeDataList2.add(backgroundCostumeData);

		SetCostumeBrick backgroundBrick = new SetCostumeBrick(backgroundSprite);
		backgroundBrick.setCostume(backgroundCostumeData);

		backgroundStartScript.addBrick(backgroundBrick);

		defaultProject.addSprite(sprite);
		sprite.addScript(startScript);
		backgroundSprite.addScript(backgroundStartScript);

		this.saveProject(defaultProject);

		return defaultProject;
	}

	private File savePictureFromResourceInProject(String project, String name, int fileId, Context context)
			throws IOException {

		final String imagePath = Utils.buildPath(Constants.DEFAULT_ROOT, project, Constants.IMAGE_DIRECTORY, name);
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}
}
