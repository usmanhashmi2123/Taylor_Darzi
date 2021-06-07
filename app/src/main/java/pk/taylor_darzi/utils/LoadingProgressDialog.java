package pk.taylor_darzi.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import pk.taylor_darzi.R;

public class LoadingProgressDialog extends Dialog {
    private static LoadingProgressDialog dialog;
    private static ProgressBar progressBar;
    private static Context mContext;
    private LoadingProgressDialog(Context context) {
        super(context, R.style.MyProgressDialog);
    }

    public static LoadingProgressDialog getDialog(Context context) {
        if(dialog == null || mContext== null || context!=mContext)
        {
            disMiss();
            mContext = context;
            dialog = new LoadingProgressDialog(mContext);
            dialog.setCancelable(false);
            progressBar = new ProgressBar(mContext);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.gray), PorterDuff.Mode.SRC_IN);
            dialog.addContentView(progressBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return dialog;
    }

    public static boolean isLoading()
    {
        return dialog != null && dialog.isShowing();
    }
    public static void show(Context context)
    {
       getDialog(context).show();
    }

    public static void disMiss()
    {
        if(dialog!= null)
            dialog.dismiss();
        dialog = null;
    }
    public static void destroy(Context context)
    {
       if(mContext == null ||context == mContext)
       {
           disMiss();
           dialog= null;
       }
    }
}