#https://android.googlesource.com/platform/frameworks/support.git/+/master/design/proguard-rules.pro

# CoordinatorLayout resolves the behaviors of its child components with reflection.
-keep public class * extends android.support.design.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}
# Make sure we keep annotations for CoordinatorLayout's DefaultBehavior
-keepattributes *Annotation*