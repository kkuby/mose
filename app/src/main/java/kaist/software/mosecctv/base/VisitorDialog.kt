package kaist.software.mosecctv.base

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.R
import kaist.software.mosecctv.databinding.DialogVisitorBinding
import kaist.software.mosecctv.ui.visitor.VisitorData
import kotlin.math.roundToInt

class VisitorDialog(context: Context, private var visitorData: VisitorData) : AppCompatDialog(context) {

    private var binding: DialogVisitorBinding
    private var delete: Button
    private var update: Button
    private var radioGroup: RadioGroup
    private var name: TextInputLayout
    private var nameEdit: TextInputEditText
    private var schedule: Spinner

    private var group = 0L
    private var scheduleList = ArrayList<Long>()
    private var selectedSchedule = 0L

    init {
        setCancelable(true)
        binding = DialogVisitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutParams = window?.attributes
        if (layoutParams != null) {
            layoutParams.width = Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                300F, context.resources.displayMetrics))

            layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                400F, context.resources.displayMetrics
            ).roundToInt()
            window?.attributes = layoutParams
        }

        radioGroup = binding.radioGroup
        radioGroup.check(R.id.basic_radio)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            run {
                when (checkedId) {
                    R.id.basic_radio -> this.group = 0L
                    R.id.black_radio -> this.group = 1L
                }
            }
        }

        name = binding.name
        nameEdit =binding.nameEdit

        schedule = binding.schedule

        for(i: Long in 12L..24L)
            scheduleList.add(i)

        val arrayAdapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, scheduleList)
        schedule.adapter = arrayAdapter
        schedule.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSchedule = scheduleList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val db = Firebase.firestore

        delete = binding.delete
        delete.setOnClickListener {
            visitorData.docId?.let { it1 ->
                db.collection("Visitor_android")
                    .document(it1)
                    .delete()
            }
            dismiss()
        }
        update = binding.update

        update.setOnClickListener {
            visitorData.group = this.group
            visitorData.name = this.name.editText?.text.toString()
            visitorData.schedule = this.selectedSchedule
            if(visitorData.docId==null){
                db.collection("Visitor_android")
                    .add(visitorData)
                    .addOnSuccessListener {
                        visitorData.docId = it.id
                        db.collection("Visitor_android")
                            .document(it.id)
                            .set(visitorData)
                    }
            }else{
                db.collection("Visitor_android")
                    .document(visitorData.docId!!)
                    .set(visitorData)
            }

            dismiss()

        }

        if(visitorData.docId==null) {
            update.text = "Create"
            delete.text = "Close"
        }else{
            update.text = "Update"
            delete.text = "Delete"

            when(visitorData.group){
                0L->radioGroup.check(R.id.basic_radio)
                1L->radioGroup.check(R.id.black_radio)
            }

            nameEdit.setText(visitorData.name)
            (visitorData.schedule?.minus(12))?.let { schedule.setSelection(it.toInt()) }
        }


    }

}