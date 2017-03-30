/**
* The PebbleAction enumeration encapsulates the possible action triggers coming from the companion Pebble Watchapp.
* Each of these events correlate to a command to be executed on the Raspberry Pi Robot Car.
* 
* @author Luigi Vincent
*/

enum PebbleAction {
	UP_PRESS(0, "forward"),
	DOWN_PRESS(1, "backward"),
	UP_DOWN_RELEASE(2, "stop"),
	SELECT_CLICK(3, "right"),
	BACK_CLICK(4, "left");

	private final int key;
	private final String action;

	PebbleAction(int key, String action) {
		this.key = key;
		this.action = action;
	}

	public int key() {
		return key;
	}

	public String action() {
		return action;
	}
}