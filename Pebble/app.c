/* Author: Luigi Vincent
* The companion Pebble WatchApp to control the Raspberry Pi Robot Car.
*/

#include <pebble.h>

#define UP_BUTTON_PRESS 0
#define DOWN_BUTTON_PRESS 1
#define UP_DOWN_BUTTON_RELEASE 2
#define SELECT_BUTTON_CLICK 3
#define BACK_BUTTON_CLICK 4

static Window *window;
static TextLayer *text_layer;

static void send(int key, int value) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);

  dict_write_int(iter, key, &value, sizeof(int), true);
  app_message_outbox_send();
}

static void handle_up_press() {
	text_layer_set_text(text_layer, "Forward");
	send(UP_BUTTON_PRESS, 0);
}

static void handle_down_press() {
	text_layer_set_text(text_layer, "Backward");
	send(DOWN_BUTTON_PRESS, 0);
}

static void handle_up_down_release() {
	text_layer_set_text(text_layer, "Stop");
	send(UP_DOWN_BUTTON_RELEASE, 0);
}

static void handle_select_click() {
	text_layer_set_text(text_layer, "Right");
	send(SELECT_BUTTON_CLICK, 0);
}

static void handle_back_click() {
	text_layer_set_text(text_layer, "Left");
	send(BACK_BUTTON_CLICK, 0);
}

static void outbox_sent_handler(DictionaryIterator *iter, void *context) {
	text_layer_set_text(text_layer, "Robot Control!");
}

static void outbox_failed_handler(DictionaryIterator *iter, AppMessageResult reason, void *context) {
	text_layer_set_text(text_layer, "Send failed!");
	APP_LOG(APP_LOG_LEVEL_ERROR, "Fail reason: %d", (int) reason);
}

static void configure_click_handlers() {
	window_long_click_subscribe(BUTTON_ID_UP, 1, handle_up_press, handle_up_down_release);
	window_long_click_subscribe(BUTTON_ID_DOWN, 1, handle_down_press, handle_up_down_release);
	window_single_click_subscribe(BUTTON_ID_SELECT, handle_select_click);
	window_single_click_subscribe(BUTTON_ID_BACK, handle_back_click);
}

static void load(Window *window) {
	// Set window properties
	window_set_background_color(window, GColorBlack);
	window_set_click_config_provider(window, configure_click_handlers);
	// Get information about the window
	Layer *window_layer = window_get_root_layer(window);
	GRect bounds = layer_get_bounds(window_layer);

	// Create layer with specific bounds
	text_layer = text_layer_create(GRect(0, PBL_IF_ROUND_ELSE(58, 52), bounds.size.w, 50));
	// Add centered, colored text
	text_layer_set_text(text_layer, "Robot Control!");
	text_layer_set_background_color(text_layer, GColorBlack);
	text_layer_set_text_color(text_layer, GColorRajah);
	text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);

	// Add to parent layer
	layer_add_child(window_layer, text_layer_get_layer(text_layer));
}

static void unload(Window *window) {
	text_layer_destroy(text_layer);
}

static void init(void) {
	window = window_create();
	window_set_window_handlers(window, (WindowHandlers) {
		.load = load,
		.unload = unload
	});
	window_stack_push(window, true);

	// Open AppMessage
	app_message_register_outbox_sent(outbox_sent_handler);
	app_message_register_outbox_failed(outbox_failed_handler);
	
	const int inbox_size = 128;
	const int outbox_size = 128;
	app_message_open(inbox_size, outbox_size);
}

static void deinit(void) {
	window_destroy(window);
}

int main(void) {
	init();
	app_event_loop();
	deinit();
}