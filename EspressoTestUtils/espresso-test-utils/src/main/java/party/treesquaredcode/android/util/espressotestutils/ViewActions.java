package party.treesquaredcode.android.util.espressotestutils;

import android.content.res.Resources;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rht on 9/7/15.
 */
public class ViewActions {
    public static ViewAction clickClickableSpan(final int spanStartResId, final int spanStopResId) {
        CoordinatesProvider coordinatesProvider = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                if (!(view instanceof TextView)) {
                    return new float[0];
                }
                TextView textView = (TextView) view;
                Resources resources = textView.getResources();
                int spanStart = resources.getInteger(spanStartResId);
                int spanStop = resources.getInteger(spanStopResId);
                Layout textViewLayout = textView.getLayout();
                int startLine = textViewLayout.getLineForOffset(spanStart);
                float startX = textViewLayout.getPrimaryHorizontal(spanStart);
                int stopLine = textViewLayout.getLineForOffset(spanStop - 1);
                float stopX = textViewLayout.getPrimaryHorizontal(spanStop - 1);
                int[] viewPosition = new int[2];
                textView.getLocationOnScreen(viewPosition);
                float[] result = new float[2];
                result[0] = (float) viewPosition[0];
                result[1] = (float) viewPosition[1];
                if (startLine == stopLine) {
                    result[0] += 0.5f * (startX + stopX);
                    int lineTop = textViewLayout.getLineTop(startLine);
                    int lineBottom = textViewLayout.getLineBottom(stopLine);
                    result[1] += 0.5f * (lineTop + lineBottom);
                } else {
                    result[0] += 0.5f * stopX;
                    int lineTop = textViewLayout.getLineTop(stopLine);
                    int lineBottom = textViewLayout.getLineBottom(stopLine);
                    result[1] += 0.5f * (lineTop + lineBottom);
                }
                return result;
            }
        };
        return new GeneralClickAction(Tap.SINGLE, coordinatesProvider, Press.FINGER);
    }
}
