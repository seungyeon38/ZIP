package com.ssafy.zip.android
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ssafy.zip.android.data.Calendar
import com.google.android.material.timepicker.TimeFormat
import java.text.DateFormat
import java.text.DateFormat.getInstance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.util.Calendar.*
import java.util.Currency.getInstance
import android.annotation.SuppressLint;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;


class CalendarFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dialogRecyclerView: RecyclerView
    private lateinit var calendarList: ArrayList<Calendar>
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var activity: MainActivity
    private lateinit var customAlertDialogView: View
    private lateinit var customChoiceDialogView: View
    private lateinit var calendarTextField: TextInputLayout
    private lateinit var memberList: ArrayList<Member>
    private lateinit var calendarDialogAdapter: CalendarDialogAdapter


    companion object {
        fun newInstance(): CalendarFragment = CalendarFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_calendar)


    }

    private fun setContentView(dialogAddCalendar: Int) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setContentView(R.layout.fragment_calendar)

        recyclerView = view.findViewById(R.id.calendar_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(activity, 1)

        calendarList = ArrayList()
        memberList = ArrayList()
        addDataToList()

        calendarAdapter = CalendarAdapter(calendarList)
        recyclerView.adapter = calendarAdapter


        val dayText: TextView = view.findViewById(R.id.day_text)
        val calendarView: CalendarView = view.findViewById(R.id.calendarView)

        val dataFormat: DateFormat = SimpleDateFormat("MM월 dd일")

        val date: Date = Date(calendarView.date)

        dayText.text = dataFormat.format(date)

        calendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            var day: String = "${month + 1}월 ${dayOfMonth}일"

            dayText.text = day

        }

        val fab: View = view.findViewById(R.id.add_calendar_fab)
        fab.setOnClickListener { view ->
            customAlertDialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_add_calendar, null, false)

            // dialog recycler view
            dialogRecyclerView = customAlertDialogView.findViewById(R.id.family_recycler_view)
            dialogRecyclerView.setHasFixedSize(true)
            calendarDialogAdapter = CalendarDialogAdapter(memberList)

            val cnt = when (calendarDialogAdapter.itemCount) {
                in 1..4 -> calendarDialogAdapter.itemCount
                in 5..6 -> 3
                else -> 4
            }

            dialogRecyclerView.adapter = calendarDialogAdapter
            dialogRecyclerView.layoutManager = GridLayoutManager(activity, cnt)

            MaterialAlertDialogBuilder(activity)
                .setView(customAlertDialogView)
                .setTitle(resources.getString(R.string.new_calendar))
                .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                    val calendarTitle = calendarTextField.editText?.text.toString()

                    dialog.dismiss()
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()


            var startPickDateButton: TextView? = null
            var startShowSelectedDateText: TextView? = null

            startPickDateButton = customAlertDialogView.findViewById(R.id.btn_startDate)
            startShowSelectedDateText = customAlertDialogView.findViewById(R.id.btn_startDate)

            var materialDateBuilder: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()

            materialDateBuilder.setTitleText("SELECT A DATE")

            var materialDatePicker = materialDateBuilder.build()

            // material design date picker
            startPickDateButton.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        materialDatePicker.show(
                            activity!!.supportFragmentManager,
                            "MATERIAL_DATE_PICKER"
                        )
                    }
                })


            materialDatePicker.addOnPositiveButtonClickListener {
                startShowSelectedDateText.setText(materialDatePicker.headerText)
            }

            var endPickDateButton: TextView? = null
            var endShowSelectedDateText: TextView? = null

            endPickDateButton = customAlertDialogView.findViewById(R.id.btn_endDate)
            endShowSelectedDateText = customAlertDialogView.findViewById(R.id.btn_endDate)

            var materialDateBuilder2: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()

            materialDateBuilder2.setTitleText("SELECT A DATE")

            var materialDatePicker2 = materialDateBuilder2.build()

            // material design date picker
            endPickDateButton.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        materialDatePicker2.show(
                            activity!!.supportFragmentManager,
                            "MATERIAL_DATE_PICKER"
                        )
                    }
                })


            materialDatePicker2.addOnPositiveButtonClickListener {
                endShowSelectedDateText.setText(materialDatePicker2.headerText)
            }

            var startPickTimeButton: TextView? = null
            var startShowSelectedTimeText: TextView? = null

            startPickTimeButton = customAlertDialogView.findViewById(R.id.btn_startTime)
            startShowSelectedTimeText = customAlertDialogView.findViewById(R.id.btn_startTime)

            startPickTimeButton.setOnClickListener {

                val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()

                    .setTitleText("SELECT YOUR TIMING")

                    .setHour(12)
                    .setMinute(10)
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .build()

                materialTimePicker.show(activity!!.supportFragmentManager, "MainActivity")

                materialTimePicker.addOnPositiveButtonClickListener {

                    val pickedHour: Int = materialTimePicker.hour
                    val pickedMinute: Int = materialTimePicker.minute

                    val formattedTime: String = when {
                        pickedHour > 12 -> {
                            if (pickedMinute < 10) {
                                "${materialTimePicker.hour - 12}:0${materialTimePicker.minute} pm"
                            } else {
                                "${materialTimePicker.hour - 12}:${materialTimePicker.minute} pm"
                            }
                        }
                        pickedHour == 12 -> {
                            if (pickedMinute < 10) {
                                "${materialTimePicker.hour}:0${materialTimePicker.minute} pm"
                            } else {
                                "${materialTimePicker.hour}:${materialTimePicker.minute} pm"
                            }
                        }
                        pickedHour == 0 -> {
                            if (pickedMinute < 10) {
                                "${materialTimePicker.hour + 12}:0${materialTimePicker.minute} am"
                            } else {
                                "${materialTimePicker.hour + 12}:${materialTimePicker.minute} am"
                            }
                        }
                        else -> {
                            if (pickedMinute < 10) {
                                "${materialTimePicker.hour}:0${materialTimePicker.minute} am"
                            } else {
                                "${materialTimePicker.hour}:${materialTimePicker.minute} am"
                            }
                        }
                    }

                    // then update the preview TextView
                    startShowSelectedTimeText.text = formattedTime
                }

                var endPickTimeButton: TextView? = null
                var endShowSelectedTimeText: TextView? = null

                endPickTimeButton = customAlertDialogView.findViewById(R.id.btn_endTime)
                endShowSelectedTimeText = customAlertDialogView.findViewById(R.id.btn_endTime)

                endPickTimeButton.setOnClickListener {

                    val materialTimePicker2: MaterialTimePicker = MaterialTimePicker.Builder()

                        .setTitleText("SELECT YOUR TIMING")

                        .setHour(12)
                        .setMinute(10)
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .build()

                    materialTimePicker2.show(activity!!.supportFragmentManager, "MainActivity")

                    materialTimePicker2.addOnPositiveButtonClickListener {

                        val pickedHour2: Int = materialTimePicker2.hour
                        val pickedMinute2: Int = materialTimePicker2.minute

                        val formattedTime: String = when {
                            pickedHour2 > 12 -> {
                                if (pickedMinute2 < 10) {
                                    "${materialTimePicker2.hour - 12}:0${materialTimePicker2.minute} pm"
                                } else {
                                    "${materialTimePicker2.hour - 12}:${materialTimePicker2.minute} pm"
                                }
                            }
                            pickedHour2 == 12 -> {
                                if (pickedMinute2 < 10) {
                                    "${materialTimePicker2.hour}:0${materialTimePicker2.minute} pm"
                                } else {
                                    "${materialTimePicker2.hour}:${materialTimePicker2.minute} pm"
                                }
                            }
                            pickedHour2 == 0 -> {
                                if (pickedMinute2 < 10) {
                                    "${materialTimePicker2.hour + 12}:0${materialTimePicker2.minute} am"
                                } else {
                                    "${materialTimePicker2.hour + 12}:${materialTimePicker2.minute} am"
                                }
                            }
                            else -> {
                                if (pickedMinute2 < 10) {
                                    "${materialTimePicker2.hour}:0${materialTimePicker2.minute} am"
                                } else {
                                    "${materialTimePicker2.hour}:${materialTimePicker2.minute} am"
                                }
                            }
                        }

                        // then update the preview TextView
                        endShowSelectedTimeText.text = formattedTime
                    }

                }
            }


    }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun addDataToList() {
        calendarList.add(Calendar(1, R.drawable.ex, "아침엔 티타임~", 10))
        calendarList.add(Calendar(2, R.drawable.ex2, "점심엔 짜장면~", 12))
        calendarList.add(Calendar(3, R.drawable.ex3, "저녁엔 삼겹살~", 6))

        memberList.add(Member(1,"류현수", "행복한 우리 가조쿠", "귀요미 막둥이 현수", R.drawable.member1))
        memberList.add(Member(2,"김민균", "행복한 우리 가조쿠", "귀요미 첫째 민균", R.drawable.member2))
        memberList.add(Member(3,"이승연", "행복한 우리 가조쿠", "귀요미 둘째 승연", R.drawable.member3))
        memberList.add(Member(4,"이보나", "행복한 우리 가조쿠", "귀요미 셋째 보나", R.drawable.member4))
    }
}


