package party.treesquaredcode.android.util.espressotestutils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.text.SpannableString;
import android.text.method.TransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by rht on 9/7/15.
 */
public class ViewMatchers {
    private static final String ALL_CAPS_TRANSFORMATION_METHOD_CLASS_CANONICAL_NAME = "android.text.method.AllCapsTransformationMethod";

    public static Matcher<View> withBitmap(final int bitmapDrawableResId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView imageView) {
                Bitmap bitmap = BitmapFactory.decodeResource(imageView.getResources(), bitmapDrawableResId);
                Drawable drawable = imageView.getDrawable();
                Bitmap imageViewBitmap;
                if (drawable instanceof BitmapDrawable) {
                    imageViewBitmap = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    imageViewBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(imageViewBitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
                return bitmap.sameAs(imageViewBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with bitmap from resource id: ");
                description.appendValue(bitmapDrawableResId);
            }
        };
    }

    public static Matcher<View> withTextWithClickableSpan(final int textResId, final int clickableSpanStartResId, final int clickableSpanStopResId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView textView) {
                Resources resources = textView.getResources();
                String text = resources.getString(textResId);
                if (!textView.getText().toString().equals(text)) {
                    return false;
                }
                if (!(textView.getText() instanceof SpannableString)) {
                    return false;
                }
                int spanStart = resources.getInteger(clickableSpanStartResId);
                int spanStop = resources.getInteger(clickableSpanStopResId);
                SpannableString textViewSpannableString = (SpannableString) textView.getText();
                Object[] spans = textViewSpannableString.getSpans(0, textViewSpannableString.length(), Object.class);
                for (Object span : spans) {
                    if (span instanceof ClickableSpan && textViewSpannableString.getSpanStart(span) == spanStart && textViewSpannableString.getSpanEnd(span) == spanStop) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text from resource id: ");
                description.appendValue(textResId);
                description.appendText(", clickable span starting from integer from resource id: ");
                description.appendValue(clickableSpanStartResId);
                description.appendText(" and ending at integer from resource id: ");
                description.appendValue(clickableSpanStopResId);
            }
        };
    }

    public static Matcher<View> withTextAllCaps(final boolean textAllCaps) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView textView) {
                return isTextAllCaps(textView) == textAllCaps;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(textAllCaps ? "with all caps" : "without all caps");
            }

            private boolean isTextAllCaps(TextView textView) {
                TransformationMethod transformationMethod = textView.getTransformationMethod();
                return transformationMethod != null && ((transformationMethod instanceof AllCapsTransformationMethod) || (transformationMethod.getClass().getCanonicalName().equals(ALL_CAPS_TRANSFORMATION_METHOD_CLASS_CANONICAL_NAME)));
            }
        };
    }

    public static Matcher<View> withoutAllCaps() {
        return withTextAllCaps(false);
    }
}
