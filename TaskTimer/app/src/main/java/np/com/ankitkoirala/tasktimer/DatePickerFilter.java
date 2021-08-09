package np.com.ankitkoirala.tasktimer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFilter extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFilter";

    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    int dialogId = 0;
    DatePickerDialog.OnDateSetListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if(!(getActivity() instanceof  DatePickerDialog.OnDateSetListener)) {
            throw new ClassCastException(getActivity().getComponentName() + " should implement " +
                    DatePickerDialog.OnDateSetListener.class.getCanonicalName());
        }
        mListener = (DatePickerDialog.OnDateSetListener) getActivity();

        Bundle bundle = getArguments();
        int year, month, dayOfMonth;
        String title = null;
        Date date = null;

        if(bundle != null) {
            dialogId = bundle.getInt(DATE_PICKER_ID);
            title = bundle.getString(DATE_PICKER_TITLE);
            date = (Date) bundle.getSerializable(DATE_PICKER_DATE);
        }
        if(title == null) title = "Pick Date";

        GregorianCalendar gc = new GregorianCalendar();
        if(date != null) {
            gc.setTime(date);
        }
        year = gc.get(GregorianCalendar.YEAR);
        month = gc.get(GregorianCalendar.MONTH);
        dayOfMonth = gc.get(GregorianCalendar.DAY_OF_MONTH);

        Log.d(TAG, "onCreateDialog: title = " + title);
        Log.d(TAG, "onCreateDialog: year = " + year);
        Log.d(TAG, "onCreateDialog: month = " + month);
        Log.d(TAG, "onCreateDialog: dayOfMonth = " + dayOfMonth);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), DatePickerFilter.this, year, month, dayOfMonth);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        view.setTag(dialogId);
        if(mListener != null) {
            mListener.onDateSet(view, year, month, dayOfMonth);
        }
    }
}
