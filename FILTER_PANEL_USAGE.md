# Filter Panel - Quick Reference Guide

## Overview
The Temperature History page now includes a comprehensive sorting and filtering panel that allows users to organize and filter their temperature records dynamically.

## How to Use

### 1. Accessing the Filter Panel
- Open the **Temperature History** page
- The filter panel appears at the top, collapsed by default
- **Tap the header** (with "Sort & Filter" text) to expand/collapse the panel

### 2. Sorting Records

Choose from 4 sorting options:
- **Date (Newest)** - Most recent records first (default)
- **Date (Oldest)** - Oldest records first
- **Temp (Highest)** - Highest temperature first
- **Temp (Lowest)** - Lowest temperature first

### 3. Filtering by Date Range

Filter records by time period:
- **All Time** - Shows all records (default)
- **Today** - Records from the last 24 hours
- **Last 7 Days** - Records from the past week
- **Last 30 Days** - Records from the past month

### 4. Filtering by Temperature Range

Filter by temperature level (ranges in Celsius):
- **All** - Shows all temperatures (default)
- **Low (< 36.5°C)** - Hypothermia range
- **Normal (36.5-37.5°C)** - Normal body temperature
- **High (> 37.5°C)** - Fever range

### 5. Clearing Filters

- The **"Clear All Filters"** button appears when any filter is active
- Tap it to reset all filters and sorting to default values
- The button is hidden when no filters are active

## Features

### Dynamic Updates
- Records update **instantly** when you change any filter or sort option
- No need to tap "Apply" or "Search" buttons

### Visual Feedback
- Selected chips are highlighted
- Filter panel expands/collapses with smooth animation
- Toggle icon rotates when panel state changes
- Clear button appears/disappears based on filter state

### Smart Filtering
- Multiple filters work together (AND logic)
- Example: "Last 7 Days" + "High Temperature" shows only high-temp records from the past week
- Temperature comparisons work correctly for both Celsius and Fahrenheit records

## Use Cases

### Finding Recent Fever Records
1. Expand filter panel
2. Select **"Last 7 Days"** in Date Range
3. Select **"High (> 37.5°C)"** in Temperature Range
4. View only fever records from the past week

### Tracking Temperature Trends
1. Select **"Last 30 Days"** in Date Range
2. Select **"Date (Oldest)"** in Sort By
3. View chronological progression of temperatures

### Identifying Low Temperatures
1. Select **"Temperature (Lowest)"** in Sort By
2. Select **"Low (< 36.5°C)"** in Temperature Range
3. View all hypothermia-range readings, lowest first

### Viewing Today's Readings
1. Select **"Today"** in Date Range
2. Select **"Date (Newest)"** in Sort By
3. View today's readings in reverse chronological order

## Tips

- **Collapse the panel** when not needed to maximize screen space for records
- **Use multiple filters** together for precise results
- **Clear filters** quickly to return to the default view
- **Temperature ranges** are based on medical standards for body temperature
- **Date filters** use your device's current time zone

## Empty Results

If no records match your filters:
- The empty state message will appear
- Try adjusting or clearing filters
- Check that you have records in the selected time range

## Technical Notes

- Temperature comparisons automatically convert Fahrenheit to Celsius
- Date filters use instant-based comparison (timezone-aware)
- Sorting is stable (maintains relative order for equal values)
- Filters are applied in-memory for instant performance
