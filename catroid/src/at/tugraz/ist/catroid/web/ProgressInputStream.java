package at.tugraz.ist.catroid.web;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.tugraz.ist.catroid.common.Consts;

public class ProgressInputStream extends InputStream {

	/* Key to retrieve progress value from message bundle passed to handler */
	public static final String PROGRESS_UPDATE = "progress_update";

	private static final int TEN_KILOBYTES = 1024 * 10;

	private InputStream inputStream;
	private Handler handler;
	private long progress;
	private long lastUpdate;
	private boolean closed;
	private int totalSize;

	public ProgressInputStream(InputStream inputStream, Handler handler) {
		this.inputStream = inputStream;
		this.handler = handler;
		this.progress = 0;
		this.lastUpdate = 0;
		this.closed = false;
		try {
			this.totalSize = inputStream.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int count = inputStream.read(b, off, len);
		if (count > 0) {
			progress += count;
		}
		lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
		return count;
	}

	private long maybeUpdateDisplay(long progress, long lastUpdate) {
		if (progress - lastUpdate > TEN_KILOBYTES) {
			lastUpdate = progress;
			Message message = new Message();
			Bundle bundle = new Bundle();
			double progressPercent = (100 / (double) totalSize) * progress;
			bundle.putInt(Consts.UPLOAD_PROGRESS_KEY, (int) progressPercent);
			message.setData(bundle);
			handler.sendMessage(message);
		}
		return lastUpdate;
	}

	@Override
	public int read() throws IOException {
		int count = inputStream.read();
		return count;
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (closed) {
			throw new IOException("already closed");
		}
		closed = true;
	}
}
