/**
* The PebbleTrigger enumeration encapsulates the possible events coming from the companion Pebble Watchapp.
* Each of these actions correlate to a command to be executed on the Raspberry Pi Robot Car.
* 
* @author Luigi Vincent
* @version 1.0
*/

package com.luigivincent.raspirobotcarcontroller;

enum PebbleTrigger {
	UP_PRESS(0, "forward"),
	DOWN_PRESS(1, "backward"),
	UP_DOWN_RELEASE(2, "stop"),
	SELECT_CLICK(3, "right"),
	BACK_CLICK(4, "left");

	private final int key;
	private final String action;

	/**
	* @param key 		The actual data received from the pebble.
	* @param action 	The action indicated by the matching key.
	*/
	PebbleTrigger(int key, String action) {
		this.key = key;
		this.action = action;
	}

	/**
	* Returns the PebbleTrigger constant's specified key.
	*
	* @return Key to represent and ascertain received.
	*/
	public int key() {
		return key;
	}

	/**
	* Returns the PebbleTrigger constant's specified action.
	*
	* @return String to indicate which command matches trigger key.
	*/
	public String action() {
		return action;
	}
}