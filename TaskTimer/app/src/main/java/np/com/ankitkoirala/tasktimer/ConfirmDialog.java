package np.com.ankitkoirala.tasktimer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    public static final String DIALOG_ID = "DIALOG_ID";
    public static final String DIALOG_TITLE = "DIALOG_TITLE";
    public static final String DIALOG_POS_STRING = "DIALOG_POS_STRING";
    public static final String DIALOG_NEG_STRING = "DIALOG_NEG_STRING";

    interface OnDialogResult {
        void onPositiveResult(int dialogId, Bundle args);
        void onNegativeResult(int dialogId, Bundle args);
        void onCancel(int dialogId);
    }

    private OnDialogResult listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(!(context instanceof OnDialogResult)) {
            throw new ClassCastException(context.toString() + " does not implement OnDialogResult interface");
        }

        listener = (OnDialogResult) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        int dialogId, positiveMessageId, negativeMesageId;
        String dialogTitle;

        if(args != null) {
            dialogId = args.getInt(DIALOG_ID);
            dialogTitle = getString(args.getInt(DIALOG_TITLE));
            positiveMessageId = args.getInt(DIALOG_POS_STRING);
            negativeMesageId = args.getInt(DIALOG_NEG_STRING);

            if(!args.containsKey(DIALOG_ID) || !args.containsKey(DIALOG_TITLE)) {
                throw new IllegalStateException("DIALOG_ID or DIALOG_TITLE cannot be null");
            }
        } else {
            throw new IllegalArgumentException("Argument Bundle cannot be null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle)
                .setPositiveButton(getString(positiveMessageId), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(listener != null) {
                            listener.onPositiveResult(dialogId, args);
                        }
                    }
                })
                .setNegativeButton(getString(negativeMesageId), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(listener != null) {
                            listener.onNegativeResult(dialogId, args);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if(listener != null) {
            listener.onCancel(getArguments().getInt(DIALOG_ID));
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
