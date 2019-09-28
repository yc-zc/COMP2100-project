package cs.anu.edu.au.comp2100.weiming;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventClickListener,
        WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener
{

    private WeekView mWeekView;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ArrayList<WeekViewEvent> events = new ArrayList<>();
    private int defaultEventLength = 60;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Pop intro slides if first started
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = sp.getBoolean("firstStart", true);
        if (isFirstStart) {
            Intent intent = new Intent(MainActivity.this, AppintroActivity.class); // Call the AppIntro java class
            startActivity(intent);
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("firstStart", false);
            e.apply();
        }


        //navigator setup
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //timetable
        events = EventsFileHelper.readData(this);
        mWeekView = findViewById(R.id.weekView);
        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);
        // Set empty long press listener
        mWeekView.setEmptyViewLongPressListener(this);
        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        inflater = getLayoutInflater();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit) {
            Intent infoIntent = new Intent(this, LoginActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_course) {
            Intent infoIntent = new Intent(this, CourseActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_tutorial) {
            Intent infoIntent = new Intent(this, TutorialActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_manage) {
            Intent infoIntent = new Intent(this, ManageActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_settings) {
            Intent infoIntent = new Intent(this, SettingsActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, AppintroActivity.class); // Call the AppIntro java class
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else if (id == R.id.nav_about) {
            Intent infoIntent = new Intent(this, AboutActivity.class);
            startActivity(infoIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void imgbtnPress(View view){
        Intent infoIntent = new Intent(this, LoginActivity.class);
        startActivity(infoIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    public String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events
        if (newMonth == Calendar.getInstance().get(Calendar.MONTH))
            return events;
        else {
            return new ArrayList<>();
        }
    }


    // Add event
    public void addEvent(final Calendar startTime){
        final Calendar endTime = Calendar.getInstance();

        //time info
        final int startHr = startTime.get(Calendar.HOUR_OF_DAY);
        final int startMin = startTime.get(Calendar.MINUTE);
        final int startMon = startTime.get(Calendar.MONTH);
        final int startDt = startTime.get(Calendar.DATE);
        final int startYr = startTime.get(Calendar.YEAR);
        String startTimeStr = (startHr < 10 ? "0" : "") + startHr + ":" + (startMin < 10 ? "0" : "") + startMin;
        String startDateStr = (startDt < 10 ? "0" : "") + startDt + "/" + (startMon < 10 ? "0" : "") + startMon + "/" + startYr;

        endTime.set(startYr, startMon, startDt, startHr, startMin);
        endTime.add(Calendar.MINUTE, defaultEventLength);
        final int endHr = endTime.get(Calendar.HOUR_OF_DAY);
        final int endMin = endTime.get(Calendar.MINUTE);
        final int endMon = endTime.get(Calendar.MONTH);
        final int endDt = endTime.get(Calendar.DATE);
        final int endYr = endTime.get(Calendar.YEAR);
        String endTimeStr = (endHr < 10 ? "0" : "") + endHr + ":" + (endMin < 10 ? "0" : "") + endMin;
        String endDateStr = (endDt < 10 ? "0" : "") + endDt + "/" + (endMon < 10 ? "0" : "") + endMon + "/" + endYr;


        // Build a alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Add an event");


        //start and end time hint
        View view = inflater.inflate(R.layout.dialog_add_event, null);
        final EditText enterName = view.findViewById(R.id.pick_name_txt);
        final EditText enterLocation = view.findViewById(R.id.pick_location_txt);

        final TextView enterStartTime = view.findViewById(R.id.pick_starttime_txt);
        enterStartTime.setHint(startTimeStr);
        final TextView enterStartDate = view.findViewById(R.id.pick_startdate_txt);
        enterStartDate.setHint(startDateStr);

        final TextView enterEndTime = view.findViewById(R.id.pick_endtime_txt);
        enterEndTime.setHint(endTimeStr);
        final TextView enterEndDate = view.findViewById(R.id.pick_enddate_txt);
        enterEndDate.setHint(endDateStr);

        //select time
        enterStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(MainActivity.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHrStr = (selectedHour < 10 ? "0" : "") + selectedHour;
                        String selectedMinStr = (selectedMinute < 10 ? "0" : "") + selectedMinute;
                        enterStartTime.setText(selectedHrStr + ":" + selectedMinStr);
                        startTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        startTime.set(Calendar.MINUTE, selectedMinute);
                    }}, startHr, startMin, true);
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        enterEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(MainActivity.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHrStr = (selectedHour < 10 ? "0" : "") + selectedHour;
                        String selectedMinStr = (selectedMinute < 10 ? "0" : "") + selectedMinute;
                        enterEndTime.setText(selectedHrStr + ":" + selectedMinStr);
                        endTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        endTime.set(Calendar.MINUTE, selectedMinute);
                    }}, endHr, endMin, true);
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });

        //select date
        enterStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedMonth = (month < 10 ? "0" : "") + month;
                        String selectedDate = (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
                        enterStartDate.setText(selectedDate + "/" + selectedMonth + "/" + year);
                        startTime.set(year, month, dayOfMonth);
                    }
                }, startYr, startMon, startDt);
                mDatePicker.show();
            }
        });

        enterEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedMonth = (month < 10 ? "0" : "") + month;
                        String selectedDate = (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
                        enterEndDate.setText(selectedDate + "/" + selectedMonth + "/" + year);
                        endTime.set(year, month, dayOfMonth);
                    }
                }, endYr, endMon, endDt);
                mDatePicker.show();
            }
        });


        //color
        final Button colorbtn = view.findViewById(R.id.event_color_picker);
        final int[] eventColor = {getResources().getColor(R.color.colorAccent)};
        colorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker(MainActivity.this);
                colorPicker.setRoundColorButton(true)
                        .setTitle("Choose Event Color")
                        .disableDefaultButtons(false)
                        .setColorButtonMargin(10,7,10,7)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                // put code
                                if(color != 0){
                                    eventColor[0] = color;
                                    colorbtn.setBackgroundColor(color);
                                }
                            }

                            @Override
                            public void onCancel(){
                            }
                        });
                colorPicker.show();
            }
        });

        //dialog settings
        builder.setView(view);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WeekViewEvent event = new WeekViewEvent(0, enterName.getText().toString(), enterLocation.getText().toString(), startTime, endTime);
                event.setColor(eventColor[0]);
                events.add(event);

                onMonthChange(startYr, startMon);
                mWeekView.notifyDatasetChanged();

                //file_helper
                EventsFileHelper.writeData(events, getApplicationContext());
            }
        });


        builder.show();
    }


    // Click event to show detail
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Event Clicked: Detail?" + event.getName(), Toast.LENGTH_SHORT).show();
    }


    // Long press event to delete
    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Event Long pressed: Edit? " + event.getName(), Toast.LENGTH_SHORT).show();

        String eventStr = event.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Delete");
        builder.setMessage("Delete event "+ eventStr +" ?");

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                events.remove(event);
                System.out.println(events.size());
                mWeekView.notifyDatasetChanged();

                //file_helper
                EventsFileHelper.writeData(events, getApplicationContext());
            }
        });

        builder.show();
    }


    // Long press empty view to add an event
    // Set start time as where it is pressed
    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty place Long pressed: Add?" + getEventTitle(time), Toast.LENGTH_SHORT).show();
        addEvent(time);
    }


    //Different View-options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_now:
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                mWeekView.goToHour(hour);
                mWeekView.goToToday();
                System.out.println(hour);
                return true;

            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;

            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;

            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;

            case R.id.go_to:
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                //pop up a calendar picker
                final DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, mDateSetListener,
                        year, month, day);

                //set a listener
                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    }
                };

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "GO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        DatePicker datePicker = dialog.getDatePicker();
                        mDateSetListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        Calendar calNew = Calendar.getInstance();
                        calNew.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        mWeekView.goToDate(calNew);
                    }
                });

                dialog.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_weekview, menu);
        return true;
    }

    public void addPressed(View view) {
        Calendar now = Calendar.getInstance();
        addEvent(now);
    }
}
