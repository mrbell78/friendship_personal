package ngo.friendship.satellite.interfaces
// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onSeekBarPreferenceButtonClick events.
 * The class that is interested in processing a onSeekBarPreferenceButtonClick
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's `addOnSeekBarPreferenceButtonClickListener` method. When
 * the onSeekBarPreferenceButtonClick event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnSeekBarPreferenceButtonClickEvent
`` */
interface OnSeekBarPreferenceButtonClickListener {
    /**
     * On seek bar preference button click.
     *
     * @param isPositiveResult the is positive result
     * @param progress the progress
     */
    fun onSeekBarPreferenceButtonClick(isPositiveResult: Boolean, progress: Int)
}