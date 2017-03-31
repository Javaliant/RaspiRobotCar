/**
* The ControlActivity class is the primary screen of the application.
* It contains the buttons which control the Raspberry Pi Robot Car.
* 
* @author Luigi Vincent
* @version 1.0
*/

package com.luigivincent.raspirobotcarcontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlActivity extends Activity {
	private static final String TAG = "ControlActivity";
	private static final String DEFAULT_HOST = "10.0.0.25";
	private static final int PORT = 21505;
	private static final UUID appID = UUID.fromString("51813ebb-9446-4521-8a71-892e1b3fe2cf");
	private static PrintWriter out;
	private static final String STOP_COMMAND = "stop";
	private static final Map<Integer, String> COMMANDS;

	static {
		Map<Integer, String> commandMap = new HashMap<>();
		commandMap.put(R.id.forwardButton, "forward");
		commandMap.put(R.id.backwardButton, "backward");
		commandMap.put(R.id.leftButton, "left");
		commandMap.put(R.id.rightButton, "right");
		COMMANDS = Collections.unmodifiableMap(commandMap);
	}

	/**
	* The object to receive, acknowledge and process data from the companion Pebble WatchApp.
	*/
	PebbleDataReceiver dataReceiver = new PebbleDataReceiver(appID) {
		@Override
		public void receiveData(Context context, int id, PebbleDictionary dictionary) {
			PebbleKit.sendAckToPebble(context, id);
			for (PebbleTrigger trigger : PebbleTrigger.values()) {
				if (dictionary.getInteger(trigger.key()) != null) {
					send(trigger.action());
					break;
				}
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataReceiver);
		PebbleKit.startAppOnPebble(getApplicationContext(), appID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connect(DEFAULT_HOST, PORT);

		View.OnTouchListener touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				int action = e.getAction();
				int id = v.getId();

				if (action == MotionEvent.ACTION_UP && (id == R.id.forwardButton || id == R.id.backwardButton)) {
					send(STOP_COMMAND);
				} else if (action == MotionEvent.ACTION_DOWN) {
					send(COMMANDS.get(id));
				}

				return true;
			}
		};

		int[] movementIds = {R.id.forwardButton, R.id.backwardButton, R.id.rightButton, R.id.leftButton};
		for (int movementId : movementIds) {
			Button movementButton = (Button) findViewById(movementId);
			movementButton.setOnTouchListener(touchListener);
		}
	}

	/**
	* Attempts to connect to the Raspberry Pi Robot Car.
	*
	* @param host 			The host name to connect to.
	* @param port 			The port to connect with.
	*/
	private void connect(final String host, final int port) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket(host, port);
					out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				} catch (IOException ioe) {
					Toast.makeText(getApplicationContext(), "Could not connect", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
	}

	/**
	* Sends the specified string to the Raspberry Pi Robot Car.
	*
	* @param message	The data to be sent to the Raspberry Pi Robot Car.
	*/
	private static void send(final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				out.prinf("%s", message);
			}
		}).start();
	}
}