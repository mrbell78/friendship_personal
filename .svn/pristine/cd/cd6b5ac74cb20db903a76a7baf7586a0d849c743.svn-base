package ngo.friendship.satellite.views;
import android.text.InputFilter;
import android.text.Spanned;
/**
 * Created by Yeasin Ali on 8/31/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */


public class CharacterInputFilter implements InputFilter {
    private String mAllowedCharacters;

    public CharacterInputFilter(String allowedCharacters) {
        mAllowedCharacters = allowedCharacters;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);
            if (mAllowedCharacters.contains(String.valueOf(character))) {
                builder.append(character);
            }
        }
        return builder.toString();
    }
}
