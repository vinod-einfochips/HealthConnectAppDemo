# Chip Inflation Crash Fix

## Problem

The app was crashing during activity launch with the following error:

```
android.view.InflateException: Binary XML file line #91 in layout/layout_filter_panel: 
Error inflating class com.google.android.material.chip.Chip
Caused by: java.lang.reflect.InvocationTargetException
```

## Root Cause

The `Chip` widget from Material Components requires specific theme attributes that were missing from the application theme. When the layout inflater tried to create the Chip views in `layout_filter_panel.xml`, it couldn't find the required theme attributes, resulting in an `InvocationTargetException`.

**Affected Files:**
- `app/src/main/res/layout/layout_filter_panel.xml` (lines 85-211) - Contains 12 Chip widgets
- `app/src/main/res/values/themes.xml` - Missing Material Components theme attributes

## Solution

Added missing Material Components theme attributes to `themes.xml`:

### Changes Made

**File:** `app/src/main/res/values/themes.xml`

Added the following attributes to `Theme.HealthConnectDemo`:

```xml
<!-- Surface colors for Material Components -->
<item name="colorSurface">@color/white</item>
<item name="colorOnSurface">@color/black</item>

<!-- Material Components attributes for Chips -->
<item name="chipStyle">@style/Widget.MaterialComponents.Chip.Filter</item>
<item name="chipGroupStyle">@style/Widget.MaterialComponents.ChipGroup</item>
```

### Why This Works

1. **colorSurface & colorOnSurface**: Material Components widgets use these color attributes for their default appearance. Chips need these to determine their background and text colors.

2. **chipStyle**: Defines the default style for all Chip widgets in the app. By setting it to `Widget.MaterialComponents.Chip.Filter`, we ensure all Chips have the correct Material Components styling.

3. **chipGroupStyle**: Defines the default style for ChipGroup containers, ensuring proper layout and behavior.

## Verification

Build completed successfully:
```bash
./gradlew assembleQaDebug
BUILD SUCCESSFUL in 22s
```

## Testing Recommendations

1. **Launch TemperatureHistoryActivity** - Verify the filter panel inflates without crashes
2. **Toggle filter panel** - Ensure all 12 Chip widgets are visible and functional
3. **Test chip interactions** - Click chips to verify selection behavior works correctly
4. **Test on different Android versions** - Verify on API 28+ (minSdk)
5. **Test dark mode** - Since we use `DayNight` theme, verify chips work in both light and dark modes

## Related Components

The filter panel contains three ChipGroups with 12 total Chips:

### Sort By (4 chips)
- Date (Newest) - default selected
- Date (Oldest)
- Temp (Highest)
- Temp (Lowest)

### Date Range (4 chips)
- All Time - default selected
- Today
- Last 7 Days
- Last 30 Days

### Temperature Range (4 chips)
- All - default selected
- Low (< 36.5°C)
- Normal (36.5-37.5°C)
- High (> 37.5°C)

## Prevention

To prevent similar issues in the future:

1. **Always use Material Components theme** - Ensure `Theme.MaterialComponents.*` is the parent theme
2. **Define required color attributes** - Include `colorSurface`, `colorOnSurface`, etc.
3. **Test on real devices early** - Layout inflation errors don't always show in Android Studio preview
4. **Use Material Components widgets consistently** - Don't mix Material Components with AppCompat widgets

## References

- [Material Components for Android - Theming](https://material.io/develop/android/theming/color)
- [Chip Documentation](https://material.io/components/chips/android)
- Material Components version: `1.11.0`
