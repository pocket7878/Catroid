package at.tugraz.ist.catroid.web;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.tugraz.ist.catroid.common.Consts;

public class ProgressInputStream extends InputStream {

	private InputStream inputStream;
	private Handler handler;
	private long progress;
	private long updateTime;
	private boolean closed;
	private int totalSize;

	public ProgressInputStream(InputStream inputStream, Handler handler) {
		this.inputStream = inputStream;
		this.handler = handler;
		this.progress = 0;
		this.closed = false;
		updateTime = System.currentTimeMillis();
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
		maybeUpdateDisplay(progress);
		return count;
	}

	private void maybeUpdateDisplay(long progress) {
		if (System.currentTimeMillis() - updateTime > Consts.UPLOAD_REFRESH_MS) {
			Message message = new Message();
			Bundle bundle = new Bundle();
			double progressPercent = (100 / (double) totalSize) * progress;
			bundle.putInt(Consts.UPLOAD_PROGRESS_KEY, (int) progressPercent);
			message.setData(bundle);
			handler.sendMessage(message);
			updateTime = System.currentTimeMillis();
		}
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
			throw new IOException();
		}
		closed = true;
	}
}
